package frc.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.controllers.WiiController;
import frc.controllers.XBoxController;
import frc.drive.auton.RobotTelemetry;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.misc.UtilFunctions;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import org.jetbrains.annotations.NotNull;

/**
 * Everything that has to do with driving is in here.
 * There are a lot of auxilairy helpers and {@link RobotToggles} that feed in here.
 *
 * @see RobotTelemetry
 * @see SparkFollowerMotors
 * @see TalonFollowerMotors
 */
public class DriveManager implements ISubsystem {
    public final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Units.inchesToMeters(22));
    private final ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    private final NetworkTableEntry driveRotMult = tab2.add("Rotation Factor", RobotNumbers.TURN_SCALE).getEntry();
    private final NetworkTableEntry driveScaleMult = tab2.add("Speed Factor", RobotNumbers.DRIVE_SCALE).getEntry();
    private final boolean invert = true;
    private final NetworkTableEntry P = tab2.add("P", 0).getEntry();
    private final NetworkTableEntry I = tab2.add("I", 0).getEntry();
    private final NetworkTableEntry D = tab2.add("D", 0).getEntry();
    private final NetworkTableEntry F = tab2.add("F", 0).getEntry();
    public boolean autoComplete = false;
    public CANSparkMax leaderL, leaderR;
    public RobotTelemetry guidance;
    private BaseController controller;
    private SparkFollowerMotors followerL, followerR;
    public WPI_TalonFX leaderLTalon, leaderRTalon;
    public TalonFollowerMotors followerLTalon, followerRTalon;
    private CANPIDController leftPID, rightPID;
    private double lastP = 0;
    private double lastI = 0;
    private double lastD = 0;
    private double lastF = 0;

    public DriveManager() throws RuntimeException {
        init();
    }

    /**
     * Initialize the driver
     *
     * @throws IllegalArgumentException       When IDs for follower motors are too few or too many
     * @throws InitializationFailureException When something fails to init properly
     */
    @Override
    public void init() throws IllegalArgumentException, InitializationFailureException {
        createDriveMotors();
        initGuidance();
        initPID();
        initMisc();
    }

    /**
     * Creates the drive motors
     *
     * @throws IllegalArgumentException       When IDs for follower motors are too few or too many
     * @throws InitializationFailureException When follower drive motors fail to link to leaders or when leader drivetrain motors fail to invert
     */
    private void createDriveMotors() throws InitializationFailureException, IllegalArgumentException {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leaderL = new CANSparkMax(RobotMap.DRIVE_LEADER_L, MotorType.kBrushless);
            leaderR = new CANSparkMax(RobotMap.DRIVE_LEADER_R, MotorType.kBrushless);

            followerL = new SparkFollowerMotors().createFollowers(MotorType.kBrushless, RobotMap.DRIVE_FOLLOWERS_L);
            followerR = new SparkFollowerMotors().createFollowers(MotorType.kBrushless, RobotMap.DRIVE_FOLLOWERS_R);
            try {
                followerL.follow(leaderL);
                followerR.follow(leaderR);
            } catch (Exception e) {
                throw new InitializationFailureException("An error has occurred linking follower drive motors to leaders", "Make sure the motors are plugged in and id'd properly");
            }
            leaderL.setInverted(RobotToggles.DRIVE_INVERT_LEFT);
            leaderR.setInverted(RobotToggles.DRIVE_INVERT_RIGHT);

            leaderL.getEncoder().setPosition(0);
            leaderR.getEncoder().setPosition(0);
            setAllMotorCurrentLimits(50);
        } else {
            leaderLTalon = new WPI_TalonFX(RobotMap.DRIVE_LEADER_L);
            leaderRTalon = new WPI_TalonFX(RobotMap.DRIVE_LEADER_R);
            followerLTalon = new TalonFollowerMotors().createFollowers(RobotMap.DRIVE_FOLLOWERS_L);
            followerRTalon = new TalonFollowerMotors().createFollowers(RobotMap.DRIVE_FOLLOWERS_R);
            try {
                followerLTalon.follow(leaderLTalon);
                followerRTalon.follow(leaderRTalon);
            } catch (Exception e) {
                throw new InitializationFailureException("An error has occured linking follower drive motors to leaders", "Make sure the motors are plugged in and id'd properly");
            }
            leaderLTalon.setInverted(RobotToggles.DRIVE_INVERT_LEFT);
            leaderRTalon.setInverted(RobotToggles.DRIVE_INVERT_RIGHT);
        }
    }

    /**
     * Initialize the IMU and telemetry
     *
     * @throws InitializationFailureException When the Pigeon IMU fails to init
     */
    private void initGuidance() throws InitializationFailureException {
        guidance = new RobotTelemetry(this);
        try {
            if (RobotToggles.ENABLE_IMU) {
                guidance.resetPigeon();
                guidance.updatePigeon();
            }
        } catch (Exception e) {
            throw new InitializationFailureException("Pigeon IMU Failed to init", "Ensure the pigeon is plugged in and other hardware is operating nomially. Can also disable RobotToggles.ENABLE_IMU");
        }
    }

    /**
     * Initialize the PID for the motor controllers.
     */
    private void initPID() {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leftPID = leaderL.getPIDController();
            rightPID = leaderR.getPIDController();
        }
        setPID(RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D, RobotNumbers.DRIVEBASE_F);
    }

    /**
     * Creates xbox controller n stuff
     *
     * @throws IllegalStateException when there is no configuration for {@link RobotToggles#EXPERIMENTAL_DRIVE}
     */
    private void initMisc() throws IllegalStateException {
        System.out.println("THE XBOX CONTROLLER IS ON " + RobotNumbers.XBOX_CONTROLLER_SLOT);
        switch (RobotToggles.EXPERIMENTAL_DRIVE) {
            case STANDARD:
            case EXPERIMENTAL:
                controller = new XBoxController(RobotNumbers.XBOX_CONTROLLER_SLOT);
                break;
            case MARIO_KART:
                controller = new WiiController(0);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotToggles.EXPERIMENTAL_DRIVE.name() + " to control the drivetrain. Please implement me");
        }
    }

    /**
     * Set all motor current limits
     *
     * @param limit Current limit in amps
     */
    public void setAllMotorCurrentLimits(int limit) {
        leaderL.setSmartCurrentLimit(limit);
        leaderR.setSmartCurrentLimit(limit);
        followerL.setSmartCurrentLimit(limit);
        followerR.setSmartCurrentLimit(limit);
    }

    /**
     * Sets the pid for all the motors that need pid setting
     *
     * @param P proportional gain
     * @param I integral gain
     * @param D derivative gain
     * @param F feed-forward gain
     */
    private void setPID(double P, double I, double D, double F) {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leftPID.setP(P);
            leftPID.setI(I);
            leftPID.setD(D);
            leftPID.setFF(F);
            rightPID.setP(P);
            rightPID.setI(I);
            rightPID.setD(D);
            rightPID.setFF(F);

            leftPID.setOutputRange(-1, 1);
            rightPID.setOutputRange(-1, 1);
        } else {
            int timeout = RobotNumbers.DRIVE_TIMEOUT_MS;
            leaderLTalon.config_kF(0, F, timeout);
            leaderLTalon.config_kP(0, P, timeout);
            leaderLTalon.config_kI(0, I, timeout);
            leaderLTalon.config_kD(0, D, timeout);
            leaderRTalon.config_kF(0, F, timeout);
            leaderRTalon.config_kP(0, P, timeout);
            leaderRTalon.config_kI(0, I, timeout);
            leaderRTalon.config_kD(0, D, timeout);
        }
    }

    /**
     * Configures talon motor pid
     *
     * @param motor - motor
     * @param idx   - PID loop, by default 0
     * @param kF    - Feed forward
     * @param kP    - Proportional constant
     * @param kI    - Integral constant
     * @param kD    - Derivative constant
     * @deprecated {@link #setPID(double, double, double, double)}
     */
    @Deprecated
    private static void configureTalon(@NotNull WPI_TalonFX motor, int idx, double kF, double kP, double kI, double kD) {
        int timeout = RobotNumbers.DRIVE_TIMEOUT_MS;
        motor.config_kF(idx, kF, timeout);
        motor.config_kP(idx, kP, timeout);
        motor.config_kI(idx, kI, timeout);
        motor.config_kD(idx, kD, timeout);
    }

    /**
     * Put any experimental stuff to do with the drivetrain here
     */
    @Override
    public void updateTest() {
        if (RobotToggles.DEBUG) {
            if (!RobotToggles.DRIVE_USE_SPARKS) {
                System.out.println(leaderLTalon.getSelectedSensorVelocity() + " | " + leaderRTalon.getSelectedSensorVelocity());
            }
        }
    }

    /**
     * This is where driving happens. Call this every tick to drive and set {@link RobotToggles#EXPERIMENTAL_DRIVE} to change the drive stype
     *
     * @throws IllegalArgumentException if {@link RobotToggles#EXPERIMENTAL_DRIVE} is not implemented here or if you missed a break statement
     */
    @Override
    public void updateTeleop() throws IllegalArgumentException {
        updateGeneric();
        switch (RobotToggles.EXPERIMENTAL_DRIVE) {
            case EXPERIMENTAL: {
                double invertedDrive = invert ? -1 : 1;
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                drive(invertedDrive / dynamic_gear_L * dynamic_gear_R * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
            }
            break;
            case STANDARD: {
                double invertedDrive = invert ? -1 : 1;
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                if (RobotToggles.DEBUG) {
                    //System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                }
                drive(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
            }
            break;
            case MARIO_KART: {
                double gogoTime = controller.get(ControllerEnums.WiiButton.ONE) == ButtonStatus.DOWN ? -1 : controller.get(ControllerEnums.WiiButton.TWO) == ButtonStatus.DOWN ? 1 : 0;
                drive(0.75 * gogoTime, -0.5 * controller.get(ControllerEnums.WiiAxis.ROTATIONAL_TILT) * gogoTime);
            }
            break;
            default:
                throw new IllegalStateException("Invalid drive type");
        }
    }

    /**
     * drives the robot based on -1 / 1 inputs (ie 100% forward and 100% turning)
     *
     * @param forward  the percentage of max forward to do
     * @param rotation the percentage of max turn speed to do
     */
    public void drive(double forward, double rotation) {
        drivePure(adjustedDrive(forward), adjustedRotation(rotation));
    }

    /**
     * This takes a speed in feet per second, a requested turn speed in radians/sec
     *
     * @param FPS   Speed in Feet per Second
     * @param omega Rotation in Radians per Second
     */
    public void drivePure(double FPS, double omega) {
        omega *= driveRotMult.getDouble(RobotNumbers.TURN_SCALE);
        FPS *= driveScaleMult.getDouble(RobotNumbers.DRIVE_SCALE);
        ChassisSpeeds chassisSpeeds = new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega);
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);
        double leftFPS = Units.metersToFeet(wheelSpeeds.leftMetersPerSecond);
        double rightFPS = Units.metersToFeet(wheelSpeeds.rightMetersPerSecond);
        driveFPS(leftFPS, rightFPS);
    }

    /**
     * Takes a -1 to 1 scaled value and returns it scaled based on the max sped
     *
     * @param input -1 to 1 drive amount
     * @return input scaled based on the bot's max speed
     */
    private static double adjustedDrive(double input) {
        return input * RobotNumbers.MAX_SPEED;
    }

    /**
     * Takes a -1 to 1 scaled value and returns it scaled based on the max turning
     *
     * @param input -1 to 1 drive amount
     * @return input scaled based on max turning
     */
    private static double adjustedRotation(double input) {
        return input * RobotNumbers.MAX_ROTATION;
    }

    /**
     * Drives the bot based on the requested left and right speed
     *
     * @param leftFPS  Left drivetrain speed in feet per second
     * @param rightFPS Right drivetrain speed in feet per second
     */
    public void driveFPS(double leftFPS, double rightFPS) {
        double mult = 3.8 * 2.16 * RobotNumbers.DRIVE_SCALE;
        if (RobotToggles.DEBUG) {
            System.out.println("FPS: " + leftFPS + "  " + rightFPS + " RPM: " + convertFPStoRPM(leftFPS) + " " + convertFPStoRPM(rightFPS));
            System.out.println("Req left: " + (getTargetVelocity(leftFPS) * mult) + " Req Right: " + (getTargetVelocity(rightFPS) * mult));
        }
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leftPID.setReference(convertFPStoRPM(leftFPS) * mult, ControlType.kVelocity);
            rightPID.setReference(convertFPStoRPM(rightFPS) * mult, ControlType.kVelocity);
        } else {
            leaderLTalon.set(ControlMode.Velocity, getTargetVelocity(leftFPS) * mult);
            leaderRTalon.set(ControlMode.Velocity, getTargetVelocity(rightFPS) * mult);
        }
    }

    /**
     * Can you read the name?
     *
     * @param FPS speed in feet/second
     * @return fps converted to rpm based on max speds
     */
    private static double convertFPStoRPM(double FPS) {
        return FPS * (RobotNumbers.MAX_MOTOR_SPEED / RobotNumbers.MAX_SPEED);
    }

    /**
     * Gets the target velocity based on the speed requested
     *
     * @param FPS speed in feet/second
     * @return speed in rotations/minute
     */
    private static double getTargetVelocity(double FPS) {
        return UtilFunctions.convertDriveFPStoRPM(FPS) * RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION / 600.0;
    }

    @Override
    public void updateAuton() { }

    /**
     * updates telemetry and if calibrating pid, does that
     */
    @Override
    public void updateGeneric() {
        guidance.updateGeneric();
        if (RobotToggles.CALIBRATE_DRIVE_PID) {
            System.out.println("P: " + P.getDouble(0) + " from " + lastP);
            if (lastP != P.getDouble(0) || lastI != I.getDouble(0) || lastD != D.getDouble(0) || lastF != F.getDouble(0)) {
                lastP = P.getDouble(0);
                lastI = I.getDouble(0);
                lastD = D.getDouble(0);
                lastF = F.getDouble(0);
                if (RobotToggles.DRIVE_USE_SPARKS){
                    setPID(lastP, lastI, lastD, lastF);
                }else {
                    configureTalon(leaderLTalon, 0, lastF, lastP, lastI, lastD);
                    configureTalon(leaderRTalon, 0, lastF, lastP, lastI, lastD);
                }
            }
        }
    }

    /**
     * Drives the bot based on the requested left and right speed
     *
     * @param leftMPS  Left drivetrain speed in meters per second
     * @param rightMPS Right drivetrain speed in meters per second
     */
    public void driveMPS(double leftMPS, double rightMPS) {
        driveFPS(Units.metersToFeet(leftMPS), Units.metersToFeet(rightMPS));
    }

    /**
     * Drives the bot based on the requested left and right drivetrain voltages
     *
     * @param leftVolts  Left drivetrain volts in ... uh ... volts ig
     * @param rightVolts Right drivetrain volts in ... uh ... volts ig
     */
    public void driveVoltage(double leftVolts, double rightVolts) {
        double invertLeft = RobotToggles.DRIVE_INVERT_LEFT ? -1 : 1;
        double invertRight = RobotToggles.DRIVE_INVERT_RIGHT ? -1 : 1;
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leaderL.setVoltage(leftVolts * invertLeft);
            leaderR.setVoltage(leftVolts * invertLeft);
        } else {
            leaderLTalon.setVoltage(leftVolts * invertLeft);
            leaderRTalon.setVoltage(rightVolts * invertRight);
        }
    }

    /**
     * Any Operation that you do on one follower motor, implement in here so that a seamless transition can occur
     * This is a wrapped class that makes an easy interface to access the follower motors
     */
    public static class SparkFollowerMotors {
        private final boolean USE_TWO_MOTORS;

        private final CANSparkMax[] motors;

        public SparkFollowerMotors() {
            this.USE_TWO_MOTORS = RobotToggles.DRIVE_USE_6_MOTORS;
            this.motors = new CANSparkMax[USE_TWO_MOTORS ? 2 : 1];
        }

        // I assume that both motors are of the same type
        // if using two followers, the first int is the first motor id, and the second
        // the second

        /**
         * Creates Spark Motor followers.
         *
         * @param motorType Brushless or brushed motor
         * @param ids       The id's of the motors to be used. Length must match RobotToggles.DRIVE_USE_6_MOTORS
         * @return this object (factory style construction)
         * @throws IllegalArgumentException if RobotToggles.DRIVE_USE_6_MOTORS motor count != #of id's passed in
         */
        public SparkFollowerMotors createFollowers(MotorType motorType, int... ids) throws IllegalArgumentException {
            if ((this.USE_TWO_MOTORS) != (ids.length == 2)) {
                throw new IllegalArgumentException("I need to have an equal number of motor IDs as motors in use");
            }
            for (int i = 0; i < ids.length; i++) {
                this.motors[i] = new CANSparkMax(ids[i], motorType);
            }
            return this;
        }

        /**
         * makes followers follow the passed leader
         *
         * @param leader main motor
         */
        public void follow(@NotNull CANSparkMax leader) {
            for (CANSparkMax follower : this.motors) {
                follower.follow(leader);
            }
        }

        /**
         * limit the current draw for each follower
         *
         * @param limit current limit in amps
         */
        public void setSmartCurrentLimit(int limit) {
            for (CANSparkMax follower : this.motors) {
                follower.setSmartCurrentLimit(limit);
            }
        }
    }

    /**
     * Any Operation that you do on one follower motor, implement in here so that a seamless transition can occur
     * This is a wrapped class that makes an easy interface to access the follower motors
     */
    public static class TalonFollowerMotors {
        private final boolean USE_TWO_MOTORS;

        private final WPI_TalonFX[] motors;

        public TalonFollowerMotors() {
            this.USE_TWO_MOTORS = RobotToggles.DRIVE_USE_6_MOTORS;
            this.motors = new WPI_TalonFX[USE_TWO_MOTORS ? 2 : 1];
        }

        // I assume that both motors are of the same type
        // if using two followers, the first int is the first motor id, and the second
        // the second

        /**
         * Creates follower motor objects and stores them
         *
         * @param ids the ids of the motors to init
         * @return this motor wrapper for factory style construction
         * @throws IllegalArgumentException if the number of motors does not match the number of motor ids
         */
        public TalonFollowerMotors createFollowers(int... ids) throws IllegalArgumentException {
            if ((this.USE_TWO_MOTORS) != (ids.length == 2)) {
                throw new IllegalArgumentException("I need to have an equal number of motor IDs as motors in use");
            }
            for (int i = 0; i < ids.length; i++) {
                this.motors[i] = new WPI_TalonFX(ids[i]);
            }
            return this;
        }

        /**
         * tell the followers which leader to follow
         *
         * @param leader main motor
         */
        public void follow(@NotNull WPI_TalonFX leader) {
            for (WPI_TalonFX follower : this.motors) {
                follower.follow(leader);
            }
        }
    }
}