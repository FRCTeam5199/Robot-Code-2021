package frc.drive;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.controllers.DrumTimeController;
import frc.controllers.SixButtonGuitarController;
import frc.controllers.WiiController;
import frc.controllers.XBoxController;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.misc.UtilFunctions;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.motors.followers.AbstractFollowerMotorController;
import frc.motors.followers.SparkFollowerMotorsController;
import frc.motors.followers.TalonFollowerMotorController;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.telemetry.RobotTelemetry;

/**
 * Everything that has to do with driving is in here. There are a lot of auxilairy helpers and {@link RobotToggles} that
 * feed in here.
 *
 * @see RobotTelemetry
 */
public class DriveManager implements ISubsystem {
    public final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Units.inchesToMeters(22));
    private final ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    private final boolean invert = true;
    private final NetworkTableEntry driveRotMult = tab2.add("Rotation Factor", RobotNumbers.TURN_SCALE).getEntry(),
            driveScaleMult = tab2.add("Speed Factor", RobotNumbers.DRIVE_SCALE).getEntry(),
            P = tab2.add("P", RobotNumbers.DRIVEBASE_P).getEntry(),
            I = tab2.add("I", RobotNumbers.DRIVEBASE_I).getEntry(),
            D = tab2.add("D", RobotNumbers.DRIVEBASE_D).getEntry(),
            F = tab2.add("F", RobotNumbers.DRIVEBASE_F).getEntry(),
            calibratePid = tab2.add("Calibrate PID", false).getEntry();
    private final NetworkTableEntry coast = tab2.add("Coast", true).getEntry();
    public boolean autoComplete = false;
    public AbstractMotorController leaderL, leaderR;
    public RobotTelemetry guidance;
    public AbstractFollowerMotorController followerL, followerR;
    private BaseController controller;
    private double lastP = 0, lastI = 0, lastD = 0, lastF = 0;

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
     * Gets the target velocity based on the speed requested
     *
     * @param FPS speed in feet/second
     * @return speed in rotations/minute
     */
    private static double getTargetVelocity(double FPS) {
        return UtilFunctions.convertDriveFPStoRPM(FPS) * RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION / 600.0;
    }

    public DriveManager() throws RuntimeException {
        addToMetaList();
        init();
    }

    /**
     * Initializes the driver
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
     * @throws InitializationFailureException When follower drive motors fail to link to leaders or when leader
     *                                        drivetrain motors fail to invert
     */
    private void createDriveMotors() throws InitializationFailureException, IllegalArgumentException {
        switch (RobotToggles.DRIVE_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                leaderL = new SparkMotorController(RobotMap.DRIVE_LEADER_L);
                leaderR = new SparkMotorController(RobotMap.DRIVE_LEADER_R);
                followerL = new SparkFollowerMotorsController(RobotMap.DRIVE_FOLLOWERS_L);
                followerR = new SparkFollowerMotorsController(RobotMap.DRIVE_FOLLOWERS_R);
                leaderL.setSensorToRevolutionFactor(RobotNumbers.DRIVE_GEARING);
                leaderR.setSensorToRevolutionFactor(RobotNumbers.DRIVE_GEARING);
                break;
            case TALON_FX:
                leaderL = new TalonMotorController(RobotMap.DRIVE_LEADER_L);
                leaderR = new TalonMotorController(RobotMap.DRIVE_LEADER_R);
                followerL = new TalonFollowerMotorController(RobotMap.DRIVE_FOLLOWERS_L);
                followerR = new TalonFollowerMotorController(RobotMap.DRIVE_FOLLOWERS_R);
                leaderL.setSensorToRevolutionFactor((600.0 / RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION) * RobotNumbers.DRIVE_GEARING);
                leaderR.setSensorToRevolutionFactor((600.0 / RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION) * RobotNumbers.DRIVE_GEARING);
                break;
            default:
                throw new InitializationFailureException("DriveManager does not have a suitible constructor for " + RobotToggles.DRIVE_MOTOR_TYPE.name(), "Add an implementation in the init for drive manager");
        }
        try {
            followerL.follow(leaderL);
            followerR.follow(leaderR);
        } catch (Exception e) {
            throw new InitializationFailureException("An error has occurred linking follower drive motors to leaders", "Make sure the motors are plugged in and id'd properly");
        }
        leaderL.setInverted(RobotToggles.DRIVE_INVERT_LEFT);
        leaderR.setInverted(RobotToggles.DRIVE_INVERT_RIGHT);

        leaderL.resetEncoder();
        leaderR.resetEncoder();

        setAllMotorCurrentLimits(50);

        followerL.invert(RobotToggles.DRIVE_INVERT_LEFT);
        followerR.invert(RobotToggles.DRIVE_INVERT_RIGHT);
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
        setPID(RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D, RobotNumbers.DRIVEBASE_F);
    }

    /**
     * Creates xbox controller n stuff
     *
     * @throws IllegalStateException when there is no configuration for {@link RobotToggles#DRIVE_STYLE}
     */
    private void initMisc() throws IllegalStateException {
        System.out.println("THE XBOX CONTROLLER IS ON " + RobotNumbers.XBOX_CONTROLLER_SLOT);
        switch (RobotToggles.DRIVE_STYLE) {
            case STANDARD:
            case EXPERIMENTAL:
                controller = new XBoxController(RobotNumbers.XBOX_CONTROLLER_SLOT);
                break;
            case MARIO_KART:
                controller = new WiiController(0);
                break;
            case GUITAR:
                controller = new SixButtonGuitarController(0);
                break;
            case DRUM_TIME:
                controller = new DrumTimeController(0);
                break;
            case BOP_IT:
                controller = new BopItBasicController(0);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotToggles.DRIVE_STYLE.name() + " to control the drivetrain. Please implement me");
        }
        if (RobotToggles.DEBUG)
            System.out.println("Created a " + controller.toString());
    }

    /**
     * Set all motor current limits
     *
     * @param limit Current limit in amps
     */
    public void setAllMotorCurrentLimits(int limit) {
        leaderL.setCurrentLimit(limit);
        leaderR.setCurrentLimit(limit);
        followerL.setCurrentLimit(limit);
        followerR.setCurrentLimit(limit);
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
        leaderL.setPid(P, I, D, F);
        leaderR.setPid(P, I, D, F);
    }

    /**
     * Put any experimental stuff to do with the drivetrain here
     */
    @Override
    public void updateTest() {
        //updateTeleop();
    }

    /**
     * This is where driving happens. Call this every tick to drive and set {@link RobotToggles#DRIVE_STYLE} to change
     * the drive stype
     *
     * @throws IllegalArgumentException if {@link RobotToggles#DRIVE_STYLE} is not implemented here or if you missed a
     *                                  break statement
     */
    @Override
    public void updateTeleop() throws IllegalArgumentException {
        updateGeneric();

        switch (RobotToggles.DRIVE_STYLE) {
            case EXPERIMENTAL: {
                /*setBrake(controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN);
                double precision = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double invertedDrive = invert ? -1 : 1;
                drive(invertedDrive * controller.get(XboxAxes.LEFT_JOY_Y) * precision, -controller.get(XboxAxes.RIGHT_JOY_X) * precision);*/
                double invertedDrive = invert ? -1 : 1;
                if (Math.abs(controller.get(XboxAxes.LEFT_JOY_Y)) > 0.9) {
                    double dir = controller.get(XboxAxes.LEFT_JOY_Y) > 0 ? 1 : -1;
                    driveFPS(100 * dir * invertedDrive * driveScaleMult.getDouble(RobotNumbers.DRIVE_SCALE), 100 * dir * invertedDrive * driveScaleMult.getDouble(RobotNumbers.DRIVE_SCALE));
                    break;
                }
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                if (RobotToggles.DEBUG) {
                    System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                    //System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                }
                drive(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
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
            case DRUM_TIME: {
                double speedFactor = controller.get(ControllerEnums.DrumButton.PEDAL) == ButtonStatus.DOWN ? 2 : 0.5;
                double goLeft = controller.get(ControllerEnums.Drums.RED) == ButtonStatus.DOWN ? 2 : controller.get(ControllerEnums.Drums.YELLOW) == ButtonStatus.DOWN ? -2 : 0;
                double goRight = controller.get(ControllerEnums.Drums.GREEN) == ButtonStatus.DOWN ? 2 : controller.get(ControllerEnums.Drums.BLUE) == ButtonStatus.DOWN ? -2 : 0;
                driveFPS(goLeft * speedFactor, goRight * speedFactor);
                break;
            }
            case BOP_IT: {
                double driveamt = (controller.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN ? 1 : 0) * (controller.get(ControllerEnums.BopItButtons.BOPIT) == ButtonStatus.DOWN ? -1 : 1);
                double turnamt = (controller.get(ControllerEnums.BopItButtons.TWISTIT) == ButtonStatus.DOWN ? 1 : 0) * (controller.get(ControllerEnums.BopItButtons.BOPIT) == ButtonStatus.DOWN ? -1 : 1);
                System.out.println("bop it says: " + driveamt + ", " + turnamt);
                drive(driveamt, turnamt);
                break;
            }
            default:
                throw new IllegalStateException("Invalid drive type");
        }
        //System.out.println(guidance.imu.yawWraparoundAhead());
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
        setBrake(!coast.getBoolean(false));
        if (calibratePid.getBoolean(false)) {
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

    @Override
    public void initTest() {
        initGeneric();
    }

    @Override
    public void initTeleop() {
        initGeneric();
    }

    @Override
    public void initAuton() {
        initGeneric();
        guidance.resetEncoders();
    }

    @Override
    public void initDisabled() {
        setBrake(true);
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
        driveMPS(wheelSpeeds.leftMetersPerSecond, wheelSpeeds.rightMetersPerSecond);
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
        leaderL.moveAtVelocity(UtilFunctions.convertDriveFPStoRPM(leftFPS) * mult);
        leaderR.moveAtVelocity(UtilFunctions.convertDriveFPStoRPM(rightFPS) * mult);
    }

    @Override
    public void initGeneric() {
        setBrake(true);
    }

    public void setBrake(boolean braking) {
        coast.setBoolean(!braking);
        leaderL.setBrake(braking);
        leaderR.setBrake(braking);
        followerL.setBrake(braking);
        followerR.setBrake(braking);
        //System.out.println("Set brake: " + braking + " at " + System.currentTimeMillis() + " \r");
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
}