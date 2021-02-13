package frc.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.music.Orchestra;
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
import frc.controllers.*;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.telemetry.RobotTelemetry;
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
    private final NetworkTableEntry P = tab2.add("P", RobotNumbers.DRIVEBASE_P).getEntry();
    private final NetworkTableEntry I = tab2.add("I", RobotNumbers.DRIVEBASE_I).getEntry();
    private final NetworkTableEntry D = tab2.add("D", RobotNumbers.DRIVEBASE_D).getEntry();
    private final NetworkTableEntry F = tab2.add("F", RobotNumbers.DRIVEBASE_F).getEntry();
    public boolean autoComplete = false;
    public CANSparkMax leaderL, leaderR;
    public RobotTelemetry guidance;
    public WPI_TalonFX leaderLTalon, leaderRTalon;
    public TalonFollowerMotors followerLTalon, followerRTalon;
    private BaseController controller;
    private SparkFollowerMotors followerL, followerR;
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
            followerLTalon.invert(RobotToggles.DRIVE_INVERT_LEFT);
            followerRTalon.invert(RobotToggles.DRIVE_INVERT_RIGHT);
        }
    }

    /**
     * Initialize the IMU and telemetry
     */
    private void initGuidance() {
        guidance = new RobotTelemetry(this);
        guidance.resetOdometry(null, null);
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
            case GUITAR:
                controller = new SixButtonGuitar(0);
                break;
            case DRUM_TIME:
                controller = new DrumTime(0);
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
                if (RobotToggles.DEBUG) {
                    System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
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
            case GUITAR: {
                double turn = controller.get(ControllerEnums.SixKeyGuitarAxis.PITCH);
                double gogo = controller.get(ControllerEnums.SixKeyGuitarAxis.STRUM);
                drive(gogo, turn);
                break;
            }
            case DRUM_TIME:{
                double speedFactor = controller.get(ControllerEnums.DrumButton.PEDAL) == ButtonStatus.DOWN ? 2 : 0.5;
                double goLeft = controller.get(ControllerEnums.Drums.RED) == ButtonStatus.DOWN ? 2 : controller.get(ControllerEnums.Drums.YELLOW) == ButtonStatus.DOWN ? -2 : 0;
                double goRight = controller.get(ControllerEnums.Drums.GREEN) == ButtonStatus.DOWN ? 2 : controller.get(ControllerEnums.Drums.BLUE) == ButtonStatus.DOWN ? -2 : 0;
                driveFPS(goLeft * speedFactor, goRight * speedFactor);
                break;
            }
            default:
                throw new IllegalStateException("Invalid drive type");
        }
        //System.out.println(guidance.imu.yawWraparoundAhead());
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
        if (leftFPS != 0)
        System.out.println(leftFPS + ", " + rightFPS);
        double mult = 3.8 * 2.16 * RobotNumbers.DRIVE_SCALE;
        if (RobotToggles.DEBUG) {
            System.out.println("FPS: " + leftFPS + "  " + rightFPS + " RPM: " + UtilFunctions.convertDriveFPStoRPM(leftFPS) + " " + UtilFunctions.convertDriveFPStoRPM(rightFPS));
            System.out.println("Req left: " + (getTargetVelocity(leftFPS) * mult) + " Req Right: " + (getTargetVelocity(rightFPS) * mult));
        }
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leftPID.setReference(UtilFunctions.convertDriveFPStoRPM(leftFPS) * mult, ControlType.kVelocity);
            rightPID.setReference(UtilFunctions.convertDriveFPStoRPM(rightFPS) * mult, ControlType.kVelocity);
        } else {
            leaderLTalon.set(ControlMode.Velocity, getTargetVelocity(leftFPS) * mult);
            leaderRTalon.set(ControlMode.Velocity, getTargetVelocity(rightFPS) * mult);
        }
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
    public void updateAuton() {
    }

    /**
     * updates telemetry and if calibrating pid, does that
     */
    @Override
    public void updateGeneric() {
        guidance.updateGeneric();
        if (RobotToggles.CALIBRATE_DRIVE_PID) {
            if (lastP != P.getDouble(RobotNumbers.DRIVEBASE_P) || lastI != I.getDouble(RobotNumbers.DRIVEBASE_I) || lastD != D.getDouble(RobotNumbers.DRIVEBASE_D) || lastF != F.getDouble(RobotNumbers.DRIVEBASE_P)) {
                lastP = P.getDouble(RobotNumbers.DRIVEBASE_P);
                lastI = I.getDouble(RobotNumbers.DRIVEBASE_I);
                lastD = D.getDouble(RobotNumbers.DRIVEBASE_D);
                lastF = F.getDouble(RobotNumbers.DRIVEBASE_F);
                setPID(lastP, lastI, lastD, lastF);
                if (RobotToggles.DEBUG) {
                    System.out.println("Set drive pid to P: " + lastP + " I: " + lastI + " D: " + lastD + " F: " + lastF);
                }
            }
        }
    }

    public void initGeneric() {
        setBrake(false);
    }

    public void setBrake(boolean braking) {
        if (!braking) {
            if (RobotToggles.DRIVE_USE_SPARKS) {
                leaderL.setIdleMode(CANSparkMax.IdleMode.kCoast);
                leaderR.setIdleMode(CANSparkMax.IdleMode.kCoast);
                followerL.setIdleMode(CANSparkMax.IdleMode.kCoast);
                followerR.setIdleMode(CANSparkMax.IdleMode.kCoast);
            } else {
                leaderLTalon.setNeutralMode(NeutralMode.Coast);
                leaderRTalon.setNeutralMode(NeutralMode.Coast);
                followerLTalon.setNeutralMode(NeutralMode.Coast);
                followerRTalon.setNeutralMode(NeutralMode.Coast);
            }
        } else {
            if (RobotToggles.DRIVE_USE_SPARKS) {
                leaderL.setIdleMode(CANSparkMax.IdleMode.kBrake);
                leaderR.setIdleMode(CANSparkMax.IdleMode.kBrake);
                followerL.setIdleMode(CANSparkMax.IdleMode.kBrake);
                followerR.setIdleMode(CANSparkMax.IdleMode.kBrake);
            } else {
                leaderLTalon.setNeutralMode(NeutralMode.Brake);
                leaderRTalon.setNeutralMode(NeutralMode.Brake);
                followerLTalon.setNeutralMode(NeutralMode.Brake);
                followerRTalon.setNeutralMode(NeutralMode.Brake);
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

        public void setIdleMode(CANSparkMax.IdleMode idleMode) {
            for (CANSparkMax follower : this.motors) {
                follower.setIdleMode(idleMode);
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

        public void invert(boolean invert){
            for (WPI_TalonFX follower : this.motors) {
                follower.setInverted(invert);
            }
        }

        public void setNeutralMode(NeutralMode mode) {
            for (WPI_TalonFX follower : this.motors) {
                follower.setNeutralMode(mode);
            }
        }

        public void addInstrument(Orchestra orchestra) {
            for (WPI_TalonFX follower : this.motors) {
                orchestra.addInstrument(follower);
            }
        }
    }
}