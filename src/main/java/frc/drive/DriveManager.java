package frc.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.controllers.XBoxController;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import org.jetbrains.annotations.NotNull;


public class DriveManager implements ISubsystem {
    private final ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    private final NetworkTableEntry driveRotMult = tab2.add("Rotation Factor", RobotNumbers.TURN_SCALE).getEntry();
    private final NetworkTableEntry driveScaleMult = tab2.add("Speed Factor", RobotNumbers.DRIVE_SCALE).getEntry();
    private final PigeonIMU pigeon = new PigeonIMU(RobotMap.PIGEON);
    // private Logger logger = new Logger("drive");
    // private Logger posLogger = new Logger("positions");
    // private Permalogger odo = new Permalogger("distance");
    // wheelbase 27"

    private final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Units.inchesToMeters(22));
    private final boolean invert = true;
    public double[] ypr = new double[3];
    // private BallChameleon chameleon = new BallChameleon();
    public double[] startypr = new double[3];
    public double currentOmega;
    public int autoStage = 0;
    //public Pose2d robotPose;
    /*SpeedControllerGroup talonLeft = new SpeedControllerGroup(leaderLTalon, followerLTalon1);
    SpeedControllerGroup talonRight = new SpeedControllerGroup(leaderRTalon, followerRTalon1);
    DifferentialDrive differentialDrive = new DifferentialDrive(talonLeft, talonRight);*/
    //public Translation2d robotTranslation;
    //public Rotation2d robotRotation;
    public boolean autoComplete = false;
    // private double targetHeading;
    //DifferentialDriveOdometry odometer;
    private XBoxController controller;
    private CANSparkMax leaderL, leaderR;
    // private boolean chaseBall;
    //private boolean pointBall;
    private SparkFollowerMotors followerL, followerR;
    private WPI_TalonFX leaderLTalon, leaderRTalon;


    //private WPI_TalonFX followerLTalon1, followerLTalon2, followerRTalon1, followerRTalon2;
    private TalonFollowerMotors followerLTalon, followerRTalon;
    //private double relLeft;
    //private double relRight;
    private CANPIDController leftPID;
    private CANPIDController rightPID;

    //private double feetDriven = 0;
    // private ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    // private NetworkTableEntry driveP = tab2.add("P",
    // RobotNumbers.drivebaseP).getEntry();
    // private NetworkTableEntry driveI = tab2.add("I",
    // RobotNumbers.drivebaseI).getEntry();
    // private NetworkTableEntry driveD = tab2.add("D",
    // RobotNumbers.drivebaseD).getEntry();
    // private NetworkTableEntry driveF = tab2.add("F",
    // RobotNumbers.drivebaseF).getEntry();
    // private NetworkTableEntry driveRotMult = tab2.add("Rotation Factor",
    // RobotNumbers.turnScale).getEntry();
    // Pigeon IMU
    private double startYaw;

    public DriveManager() throws RuntimeException {
        init();
    }

    /**
     *  Configures motors
     *
     * @param motor - motor
     * @param idx - PID loop, by default 0
     * @param kF - Feed forward
     * @param kP - Proportional constant
     * @param kI - Integral constant
     * @param kD - Derivative constant
     */
    private static void configureTalon(@NotNull WPI_TalonFX motor, int idx, double kF, double kP, double kI, double kD) {
        int timeout = RobotNumbers.DRIVE_TIMEOUT_MS;

        motor.config_kF(idx, kF, timeout);
        motor.config_kF(idx, kP, timeout);
        motor.config_kI(idx, kI, timeout);
        motor.config_kD(idx, kD, timeout);
    }

    /**
    *
    *
    * @param input
    *
    */
    private static double adjustedDrive(double input) {
        return input * RobotNumbers.MAX_SPEED;
    }

    private static double adjustedRotation(double input) {
        return input * RobotNumbers.MAX_ROTATION;
    }

    private static double convertFPStoRPM(double FPS) {
        return FPS * (RobotNumbers.MAX_MOTOR_SPEED / RobotNumbers.MAX_SPEED);
    }

    private static double getTargetVelocity(double FPS) {
        return convertFPStoRPM(FPS) * RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION / 600.0;
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
        initIMU();
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
                throw new InitializationFailureException("An error has occured linking follower drive motors to leaders", "Make sure the motors are plugged in and id'd properly");
            }

            try {
                leaderL.setInverted(RobotToggles.DRIVE_INVERT_LEFT);
                leaderR.setInverted(RobotToggles.DRIVE_INVERT_RIGHT);
            } catch (Exception e) {
                throw new InitializationFailureException("An error has occured inverting leader drivetrain motors", "Start debugging");
            }

            setAllMotorCurrentLimits(50);
        } else {
            leaderLTalon = new WPI_TalonFX(RobotMap.DRIVE_LEADER_L);
            leaderRTalon = new WPI_TalonFX(RobotMap.DRIVE_LEADER_R);
            followerLTalon = new TalonFollowerMotors().createFollowers(RobotMap.DRIVE_FOLLOWERS_L);
            followerRTalon = new TalonFollowerMotors().createFollowers(RobotMap.DRIVE_FOLLOWERS_R);

            // followerLTalon.follow(leaderLTalon);
            // followerRTalon.follow(leaderRTalon);

            try {
                followerLTalon.follow(leaderLTalon);
                followerRTalon.follow(leaderRTalon);
            } catch (Exception e) {
                throw new InitializationFailureException("An error has occured linking follower drive motors to leaders", "Make sure the motors are plugged in and id'd properly");
            }

            try {
                leaderLTalon.setInverted(RobotToggles.DRIVE_INVERT_LEFT);
                leaderRTalon.setInverted(RobotToggles.DRIVE_INVERT_RIGHT);
            } catch (Exception e) {
                throw new InitializationFailureException("An error has occured inverting leader drivetrain motors", "Start debugging");
            }
        }
    }

    /**
     * Initialize the IMU
     *
     * @throws InitializationFailureException When the Pigeon IMU fails to init
     */
    private void initIMU() throws InitializationFailureException {
        try {
            if (RobotToggles.ENABLE_IMU) {
                resetPigeon();
                updatePigeon();
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
            setPID(RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D, RobotNumbers.DRIVEBASE_F);
        } else {
            DriveManager.configureTalon(leaderLTalon, 0, RobotNumbers.DRIVEBASE_F, RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D);
            DriveManager.configureTalon(leaderRTalon, 0, RobotNumbers.DRIVEBASE_F, RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D);
            followerLTalon.configureMotors(0, RobotNumbers.DRIVEBASE_F, RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D);
            followerRTalon.configureMotors(0, RobotNumbers.DRIVEBASE_F, RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D);
        }
    }

    /**
     * Creates xbox controller n stuff
     */
    private void initMisc() {
        controller = new XBoxController(RobotNumbers.XBOX_CONTROLLER_SLOT);
    }

    /**
     * Set all motor current limits
     *
     * @param limit Current limit in amps
     */
    public void setAllMotorCurrentLimits(int limit) {
        leaderL.setSmartCurrentLimit(limit);
        leaderR.setSmartCurrentLimit(limit);
        followerL.setCurrentLimit(limit);
        followerR.setCurrentLimit(limit);
    }

    /**
     * Resets the Pigeon IMU
     */
    public void resetPigeon() {
        updatePigeon();
        startypr = ypr;
        startYaw = yawAbs();
    }

    /**
     * Updates the Pigeon IMU
     */
    public void updatePigeon() {
        pigeon.getYawPitchRoll(ypr);
    }


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

        }
    }

    public double yawAbs() { // return absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }

    @Override
    public void updateTeleop() {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            double invertedDrive = invert ? -1 : 1;
            double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
            double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
            if (!RobotToggles.EXPERIMENTAL_DRIVE) {
                drive(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
            } else {
                drive(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.LEFT_JOY_X));
            }
        } else {

        }
    }


    public void driveOne() {
        leaderLTalon.set(ControlMode.Velocity, 10000);
    }

    private void drive(double forward, double rotation) {
        drivePure(adjustedDrive(forward), adjustedRotation(rotation));
    }

    /**
     * This takes a speed in feet per second, a requested turn speed in radians/sec
     *
     * @param FPS   Speed in Feet per Second
     * @param omega Rotation in Radians per Second
     */
    private void drivePure(double FPS, double omega) {
        omega *= driveRotMult.getDouble(RobotNumbers.TURN_SCALE);
        FPS *= driveScaleMult.getDouble(RobotNumbers.DRIVE_SCALE);
        currentOmega = omega;
        double mult = 3.8 * 2.16 * RobotNumbers.DRIVE_SCALE;
        ChassisSpeeds chassisSpeeds = new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega);
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);
        double leftFPS = Units.metersToFeet(wheelSpeeds.leftMetersPerSecond);
        double rightFPS = Units.metersToFeet(wheelSpeeds.rightMetersPerSecond);

        if (RobotToggles.DRIVE_USE_SPARKS) {
            if (RobotToggles.DEBUG) {
                System.out.println("FPS: " + leftFPS + "  " + rightFPS + " RPM: " + convertFPStoRPM(leftFPS) + " " + convertFPStoRPM(rightFPS));
            }
            leftPID.setReference(convertFPStoRPM(leftFPS) * mult, ControlType.kVelocity);
            rightPID.setReference(convertFPStoRPM(rightFPS) * mult, ControlType.kVelocity);
            /*
            if (RobotToggles.DEBUG) {
                System.out.println(leaderL.getEncoder().getVelocity()+" "+leaderR.getEncoder().getVelocity());
            }
            */
        } else {
            //leaderLTalon.set(ControlMode.Velocity, (controller.get(XboxAxes.LEFT_JOY_Y)+controller.get(XboxAxes.RIGHT_JOY_X)*0.5)*12000);
            //leaderRTalon.set(ControlMode.Velocity, (controller.get(XboxAxes.LEFT_JOY_Y)-controller.get(XboxAxes.RIGHT_JOY_X)*0.5)*12000);
        }
    }

    @Override
    public void updateTest() {
        if (RobotToggles.DEBUG) {
            if (!RobotToggles.DRIVE_USE_SPARKS) {
                System.out.println(leaderLTalon.getSelectedSensorVelocity() + " | " + leaderRTalon.getSelectedSensorVelocity());
            }
        }
        leaderLTalon.set(ControlMode.Velocity, (controller.get(XboxAxes.LEFT_JOY_Y) + controller.get(XboxAxes.RIGHT_JOY_X) * 0.5) * 12000);
        leaderRTalon.set(ControlMode.Velocity, (controller.get(XboxAxes.LEFT_JOY_Y) - controller.get(XboxAxes.RIGHT_JOY_X) * 0.5) * 12000);

        //leaderLTalon.set(ControlMode.PercentOutput, 0.1);
        //leaderRTalon.set(ControlMode.PercentOutput, 0.1);
    }

    @Override
    public void updateGeneric() {

    }

    public void drivePIDSparks(double left, double right) {
        leftPID.setReference(left * RobotNumbers.MAX_MOTOR_SPEED, ControlType.kVelocity);
        rightPID.setReference(right * RobotNumbers.MAX_MOTOR_SPEED, ControlType.kVelocity);
    }

    @Override
    public void updateAuton() {

    }

    /**
     * Any Operation that you do on one follower motor, implement in here so that a seamless transition can occur
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
         * Creates Spark Motor followers based on
         *
         * @param motorType Brushless or brushed motor
         * @param ids       The id's of the motors to be used. Must match RobotToggles.DRIVE_USE_6_MOTORS
         * @return this object (factory style construction)
         * @throws IllegalArgumentException if RobotToggles.DRIVE_USE_6_MOTORS motor count != #of id's passed in
         */
        public SparkFollowerMotors createFollowers(MotorType motorType, @NotNull int... ids) throws IllegalArgumentException {
            if ((this.USE_TWO_MOTORS) != (ids.length == 2)) {
                throw new IllegalArgumentException("I need to have an equal number of motor IDs as motors in use");
            }
            for (int i = 0; i < ids.length; i++) {
                this.motors[i] = new CANSparkMax(ids[i], motorType);
            }
            return this;
        }

        public void follow(@NotNull CANSparkMax leader) {
            for (CANSparkMax follower : this.motors) {
                follower.follow(leader);
            }
        }

        public void brake(boolean brake) {
            for (CANSparkMax follower : this.motors) {
                if (!brake) {
                    follower.setIdleMode(IdleMode.kCoast);
                } else {
                    follower.setIdleMode(IdleMode.kBrake);
                }
            }
        }

        public void setCurrentLimit(int limit) {
            for (CANSparkMax follower : this.motors) {
                follower.setSmartCurrentLimit(limit);
            }
        }
    }

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
        public TalonFollowerMotors createFollowers(@NotNull int... ids) throws IllegalArgumentException {
            if ((this.USE_TWO_MOTORS) != (ids.length == 2)) {
                throw new IllegalArgumentException("I need to have an equal number of motor IDs as motors in use");
            }
            for (int i = 0; i < ids.length; i++) {
                this.motors[i] = new WPI_TalonFX(ids[i]);
            }
            return this;
        }

        public void follow(@NotNull WPI_TalonFX leader) {
            for (WPI_TalonFX follower : this.motors) {
                follower.follow(leader);
            }
        }

        public void setInverted(InvertType followMaster) {
            for (WPI_TalonFX follower : this.motors) {
                follower.setInverted(followMaster);
            }
        }

        public void configureMotors(int idx, double kF, double kP, double kI, double kD) {
            for (WPI_TalonFX follower : this.motors) {
                DriveManager.configureTalon(follower, idx, kF, kP, kI, kD);
            }
        }
    }
}