package frc.drive;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
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
import frc.misc.PID;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.motors.followers.AbstractFollowerMotorController;
import frc.motors.followers.SparkFollowerMotorsController;
import frc.motors.followers.TalonFollowerMotorController;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.telemetry.RobotTelemetry;

import java.util.Map;

import static frc.robot.Robot.robotSettings;

/**
 * Everything that has to do with driving is in here. There are a lot of auxilairy helpers and {@link
 * frc.robot.Robot#robotSettings} that feed in here.
 *
 * @see RobotTelemetry
 */
public class DriveManager implements ISubsystem {
    private static final boolean DEBUG = false;
    public final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(robotSettings.DRIVEBASE_DISTANCE_BETWEEN_WHEELS);
    private final NetworkTableEntry driveRotMult = UserInterface.DRIVE_ROT_MULT.getEntry(),
            driveScaleMult = UserInterface.DRIVE_SCALE_MULT.getEntry(),
            P = UserInterface.DRIVE_P.getEntry(),
            I = UserInterface.DRIVE_I.getEntry(),
            D = UserInterface.DRIVE_D.getEntry(),
            F = UserInterface.DRIVE_F.getEntry(),
            calibratePid = UserInterface.DRIVE_CALIBRATE_PID.getEntry(),
            coast = UserInterface.DRIVE_COAST.getEntry(),
            rumblecontroller = UserInterface.DRIVE_RUMBLE_NEAR_MAX.getEntry();
    public AbstractMotorController leaderL, leaderR;
    public RobotTelemetry guidance;
    public AbstractFollowerMotorController followerL, followerR;
    public SimpleWidget driveSpeed;
    private BaseController controller;
    private PID lastPID = PID.EMPTY_PID;

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
        switch (robotSettings.DRIVE_MOTOR_TYPE) {
            case CAN_SPARK_MAX: {
                leaderL = new SparkMotorController(robotSettings.DRIVE_LEADER_L_ID);
                leaderR = new SparkMotorController(robotSettings.DRIVE_LEADER_R_ID);
                followerL = new SparkFollowerMotorsController(robotSettings.DRIVE_FOLLOWERS_L_IDS);
                followerR = new SparkFollowerMotorsController(robotSettings.DRIVE_FOLLOWERS_R_IDS);
                //rpm <=> rps <=> gearing <=> wheel circumference
                final double s2rf = robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER / 12 * Math.PI) / 60;
                leaderL.setSensorToRealDistanceFactor(s2rf);
                leaderR.setSensorToRealDistanceFactor(s2rf);
                break;
            }
            case TALON_FX: {
                leaderL = new TalonMotorController(robotSettings.DRIVE_LEADER_L_ID);
                leaderR = new TalonMotorController(robotSettings.DRIVE_LEADER_R_ID);
                followerL = new TalonFollowerMotorController(robotSettings.DRIVE_FOLLOWERS_L_IDS);
                followerR = new TalonFollowerMotorController(robotSettings.DRIVE_FOLLOWERS_R_IDS);
                //Sens units / 100ms <=> rps <=> gearing <=> wheel circumference
                final double s2rf = (10.0 / robotSettings.DRIVEBASE_SENSOR_UNITS_PER_ROTATION) * robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER * Math.PI / 12);
                leaderL.setSensorToRealDistanceFactor(s2rf);
                leaderR.setSensorToRealDistanceFactor(s2rf);
                break;
            }
            default:
                throw new InitializationFailureException("DriveManager does not have a suitible constructor for " + robotSettings.DRIVE_MOTOR_TYPE.name(), "Add an implementation in the init for drive manager");
        }
        try {
            followerL.follow(leaderL);
            followerR.follow(leaderR);
        } catch (Exception e) {
            throw new InitializationFailureException("An error has occurred linking follower drive motors to leaders", "Make sure the motors are plugged in and id'd properly");
        }
        leaderL.setInverted(robotSettings.DRIVE_INVERT_LEFT).resetEncoder();
        leaderR.setInverted(robotSettings.DRIVE_INVERT_RIGHT).resetEncoder();

        setAllMotorCurrentLimits(50);

        followerL.invert(robotSettings.DRIVE_INVERT_LEFT);
        followerR.invert(robotSettings.DRIVE_INVERT_RIGHT);
    }

    /**
     * Initialize the IMU and telemetry
     */
    private void initGuidance() {
        guidance = new RobotTelemetry(this);
        guidance.resetOdometry();
    }

    /**
     * Initialize the PID for the motor controllers.
     */
    private void initPID() {
        setPID(robotSettings.DRIVEBASE_PID);
    }

    /**
     * Creates xbox controller n stuff
     *
     * @throws IllegalStateException when there is no configuration for {@link frc.robot.Robot#robotSettings#DRIVE_STYLE}
     */
    private void initMisc() throws IllegalStateException {
        driveSpeed = UserInterface.DRIVE_TAB.add("Drivebase Speed", 0).withWidget(BuiltInWidgets.kDial).withProperties(Map.of("Min", 0, "Max", 20));
        System.out.println("THE XBOX CONTROLLER IS ON " + robotSettings.XBOX_CONTROLLER_USB_SLOT);
        switch (robotSettings.DRIVE_STYLE) {
            case STANDARD:
            case EXPERIMENTAL:
                controller = XBoxController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT);
                break;
            case MARIO_KART:
                controller = WiiController.createOrGet(0);
                break;
            case GUITAR:
                controller = SixButtonGuitarController.createOrGet(0);
                break;
            case DRUM_TIME:
                controller = DrumTimeController.createOrGet(0);
                break;
            case BOP_IT:
                controller = BopItBasicController.createOrGet(0);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.DRIVE_STYLE.name() + " to control the drivetrain. Please implement me");
        }
        if (robotSettings.DEBUG && DEBUG)
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
     * @param pid the {@link PID} object that contains pertinent pidf data
     */
    private void setPID(PID pid) {
        leaderL.setPid(pid);
        leaderR.setPid(pid);
    }

    /**
     * Put any experimental stuff to do with the drivetrain here
     */
    @Override
    public void updateTest() {

    }

    /**
     * This is where driving happens. Call this every tick to drive and set {@link frc.robot.Robot#robotSettings#DRIVE_STYLE}
     * to change the drive stype
     *
     * @throws IllegalArgumentException if {@link frc.robot.Robot#robotSettings#DRIVE_STYLE} is not implemented here or
     *                                  if you missed a break statement
     */
    @Override
    public void updateTeleop() throws IllegalArgumentException {
        updateGeneric();
        double avgSpeedInFPS = Math.abs((leaderL.getSpeed() + leaderR.getSpeed()) / 2);
        driveSpeed.getEntry().setNumber(avgSpeedInFPS);
        switch (robotSettings.DRIVE_STYLE) {
            case EXPERIMENTAL: {

                double invertedDrive = robotSettings.DRIVE_INVERT_LEFT ? -1 : 1;
                if (Math.abs(controller.get(XboxAxes.LEFT_JOY_Y)) > 0.9) {
                    double dir = controller.get(XboxAxes.LEFT_JOY_Y) > 0 ? 1 : -1;
                    driveFPS(100 * dir * invertedDrive * driveScaleMult.getDouble(robotSettings.DRIVE_SCALE), 100 * dir * invertedDrive * driveScaleMult.getDouble(robotSettings.DRIVE_SCALE));
                    break;
                }
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                    //System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                }
                if (rumblecontroller.getBoolean(false)) {
                    controller.rumble(Math.max(0, Math.min(1, (avgSpeedInFPS - robotSettings.RUMBLE_TOLERANCE_FPS) / (robotSettings.MAX_SPEED - robotSettings.RUMBLE_TOLERANCE_FPS))));
                }
                drive(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
            }
            break;
            case STANDARD: {
                double invertedDrive = robotSettings.DRIVE_INVERT_LEFT ? -1 : 1;
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                    //System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                }
                if (rumblecontroller.getBoolean(false)) {
                    controller.rumble(Math.max(0, Math.min(1, (avgSpeedInFPS - robotSettings.RUMBLE_TOLERANCE_FPS) / (robotSettings.MAX_SPEED - robotSettings.RUMBLE_TOLERANCE_FPS))));
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
    }

    @Override
    public void updateAuton() {

    }

    /**
     * updates telemetry and if calibrating pid, does that
     */
    @Override
    public void updateGeneric() {
        if (leaderL.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, robotSettings.DRIVE_LEADER_L_ID, leaderL.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, robotSettings.DRIVE_LEADER_L_ID);
        if (leaderR.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, robotSettings.DRIVE_LEADER_R_ID, leaderR.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, robotSettings.DRIVE_LEADER_L_ID);
        if (followerL.failureFlag() || followerR.failureFlag())
            MotorDisconnectedIssue.reportIssue(this, -1);
        setBrake(!coast.getBoolean(false));
        if (calibratePid.getBoolean(false)) {
            PID readPid = new PID(P.getDouble(robotSettings.DRIVEBASE_PID.getP()), I.getDouble(robotSettings.DRIVEBASE_PID.getI()), D.getDouble(robotSettings.DRIVEBASE_PID.getD()), F.getDouble(robotSettings.DRIVEBASE_PID.getF()));
            if (!lastPID.equals(readPid)) {
                lastPID = readPid;
                setPID(lastPID);
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Set drive pid to " + lastPID);
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
        setBrake(false);
        guidance.resetEncoders();
    }

    @Override
    public void initDisabled() {
        setBrake(true);
    }

    @Override
    public void initGeneric() {
        setBrake(true);
    }

    @Override
    public String getSubsystemName() {
        return "Drivetrain";
    }

    /**
     * Drives the bot based on the requested left and right speed
     *
     * @param leftFPS  Left drivetrain speed in feet per second
     * @param rightFPS Right drivetrain speed in feet per second
     */
    public void driveFPS(double leftFPS, double rightFPS) {
        double mult = 3.8 * 2.16 * robotSettings.DRIVE_SCALE;
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("FPS: " + leftFPS + "  " + rightFPS + " (" + mult + ")");
        }
        leaderL.moveAtVelocity((leftFPS) * mult);
        leaderR.moveAtVelocity((rightFPS) * mult);
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

    public void setBrake(boolean braking) {
        coast.setBoolean(!braking);
        leaderL.setBrake(braking);
        leaderR.setBrake(braking);
        followerL.setBrake(braking);
        followerR.setBrake(braking);
        //System.out.println("Set brake: " + braking + " at " + System.currentTimeMillis() + " \r");
    }

    /**
     * This takes a speed in feet per second, a requested turn speed in radians/sec
     *
     * @param FPS   Speed in Feet per Second
     * @param omega Rotation in Radians per Second
     */
    public void drivePure(double FPS, double omega) {
        omega *= driveRotMult.getDouble(robotSettings.TURN_SCALE);
        FPS *= driveScaleMult.getDouble(robotSettings.DRIVE_SCALE);
        ChassisSpeeds chassisSpeeds = new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega);
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);
        driveMPS(wheelSpeeds.leftMetersPerSecond, wheelSpeeds.rightMetersPerSecond);
    }

    /**
     * Takes a -1 to 1 scaled value and returns it scaled based on the max sped
     *
     * @param input -1 to 1 drive amount
     * @return input scaled based on the bot's max speed
     */
    private static double adjustedDrive(double input) {
        return input * robotSettings.MAX_SPEED;
    }

    /**
     * Takes a -1 to 1 scaled value and returns it scaled based on the max turning
     *
     * @param input -1 to 1 drive amount
     * @return input scaled based on max turning
     */
    private static double adjustedRotation(double input) {
        return input * robotSettings.MAX_ROTATION;
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