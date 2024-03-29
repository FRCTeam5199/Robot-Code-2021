package frc.drive;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.misc.InitializationFailureException;
import frc.misc.PID;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.motors.followers.AbstractFollowerMotorController;
import frc.motors.followers.SparkFollowerMotorsController;
import frc.motors.followers.TalonFollowerMotorController;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.telemetry.RobotTelemetryStandard;

import static frc.robot.Robot.pneumatics;
import static frc.robot.Robot.robotSettings;

/**
 * Everything that has to do with driving is in here. There are a lot of auxilairy helpers and {@link
 * frc.robot.Robot#robotSettings} that feed in here.
 *
 * @see RobotTelemetryStandard
 */
public class DriveManagerStandard extends AbstractDriveManager {
    private static final boolean DEBUG = true;
    public final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(robotSettings.DRIVEBASE_DISTANCE_BETWEEN_WHEELS);
    private final NetworkTableEntry
            P = UserInterface.DRIVE_P.getEntry(),
            I = UserInterface.DRIVE_I.getEntry(),
            D = UserInterface.DRIVE_D.getEntry(),
            F = UserInterface.DRIVE_F.getEntry(),
            calibratePid = UserInterface.DRIVE_CALIBRATE_PID.getEntry(),
            coast = UserInterface.DRIVE_COAST.getEntry(),
            rumbleController = UserInterface.DRIVE_RUMBLE_NEAR_MAX.getEntry(),
            driveRPM = UserInterface.DRIVE_SPEED_RPM.getEntry();
    public AbstractMotorController leaderL, leaderR;
    public AbstractFollowerMotorController followerL, followerR;
    private BaseController controller;
    private PID lastPID = PID.EMPTY_PID;
    private boolean ballShifterEnabled = false;

    public DriveManagerStandard() throws UnsupportedOperationException, InitializationFailureException {
        super();
    }

    /**
     * Initializes the driver
     *
     * @throws UnsupportedOperationException  When a setting does not have a valid configuration defined
     * @throws InitializationFailureException When something fails to init properly
     */
    @Override
    public void init() throws UnsupportedOperationException, InitializationFailureException {
        createDriveMotors();
        initPID();
        initMisc();
        createTelem();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return !leaderL.isFailed() && !leaderR.isFailed() && !followerL.failureFlag() && !followerR.failureFlag() ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    /**
     * Put any experimental stuff to do with the drivetrain here
     */
    @Override
    public void updateTest() {
        driveRPM.setNumber(leaderL.getSpeed() + leaderR.getSpeed() / 2);

    }

    /**
     * This is where driving happens. Call this every tick to drive and set {@link frc.robot.robotconfigs.DefaultConfig#DRIVE_STYLE}
     * to change the drive stype
     *
     * @throws IllegalArgumentException if {@link frc.robot.robotconfigs.DefaultConfig#DRIVE_STYLE} is not implemented
     *                                  here or if you missed a break statement
     */
    @Override
    public void updateTeleop() throws IllegalArgumentException {
        updateGeneric();
        driveRPM.setNumber(leaderL.getSpeed() + leaderR.getSpeed() / 2);
        double avgSpeedInFPS = Math.abs((leaderL.getSpeed() + leaderR.getSpeed()) / 2);
        UserInterface.DRIVE_SPEED.getEntry().setNumber(avgSpeedInFPS);
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
                if (rumbleController.getBoolean(false) && !ballShifterEnabled) {
                    controller.rumble(Math.max(0, Math.min(1, (avgSpeedInFPS - robotSettings.RUMBLE_TOLERANCE_FPS) / (robotSettings.MAX_SPEED - robotSettings.RUMBLE_TOLERANCE_FPS))));
                } else {
                    controller.rumble(0);
                }
                drive(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
            }
            break;
            case BALL_SHIFTING_STANDARD: {
                if (controller.get(ControllerEnums.XBoxPOVButtons.DOWN) == ButtonStatus.DOWN) {
                    pneumatics.ballShifter.set(DoubleSolenoid.Value.kForward);
                    ballShifterEnabled = true;
                } else if (controller.get(ControllerEnums.XBoxPOVButtons.UP) == ButtonStatus.DOWN) {
                    pneumatics.ballShifter.set(DoubleSolenoid.Value.kReverse);
                    ballShifterEnabled = false;
                }
            }
            case STANDARD: {
                double invertedDrive = robotSettings.DRIVE_INVERT_LEFT ? -1 : 1;
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                    //System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                }
                if (rumbleController.getBoolean(false)) {
                    controller.rumble(Math.max(0, Math.min(1, (avgSpeedInFPS - robotSettings.RUMBLE_TOLERANCE_FPS) / (robotSettings.MAX_SPEED - robotSettings.RUMBLE_TOLERANCE_FPS))));
                }
                driveCringe(invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y), dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X));
            }
            break;
            case OPENLOOP_BALL_SHIFTING_STANDARD: {
                if (controller.get(ControllerEnums.XBoxPOVButtons.DOWN) == ButtonStatus.DOWN) {
                    pneumatics.ballShifter.set(DoubleSolenoid.Value.kForward);
                    ballShifterEnabled = true;
                } else if (controller.get(ControllerEnums.XBoxPOVButtons.UP) == ButtonStatus.DOWN) {
                    pneumatics.ballShifter.set(DoubleSolenoid.Value.kReverse);
                    ballShifterEnabled = false;
                } 
                double invertedDrive = robotSettings.DRIVE_INVERT_LEFT ? -1 : 1;
                double dynamic_gear_R = controller.get(XBoxButtons.RIGHT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                double dynamic_gear_L = controller.get(XBoxButtons.LEFT_BUMPER) == ButtonStatus.DOWN ? 0.25 : 1;
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                    //System.out.println("Forward: " + (invertedDrive * dynamic_gear_L * controller.get(XboxAxes.LEFT_JOY_Y)) + " Turn: " + (dynamic_gear_R * -controller.get(XboxAxes.RIGHT_JOY_X)));
                }
                if (rumbleController.getBoolean(false)) {
                    controller.rumble(Math.max(0, Math.min(1, (avgSpeedInFPS - robotSettings.RUMBLE_TOLERANCE_FPS) / (robotSettings.MAX_SPEED - robotSettings.RUMBLE_TOLERANCE_FPS))));
                }
                drivePercent(controller.get(XboxAxes.LEFT_JOY_Y), controller.get(XboxAxes.RIGHT_JOY_X));
                break;
            }
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
                double driveamt = (controller.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN ? 1 * driveScaleMult.getDouble(robotSettings.DRIVE_SCALE) : 0) * (controller.get(ControllerEnums.BopItButtons.BOPIT) == ButtonStatus.DOWN ? -1 : 1);
                double turnamt = (controller.get(ControllerEnums.BopItButtons.TWISTIT) == ButtonStatus.DOWN ? 1 * driveScaleMult.getDouble(robotSettings.DRIVE_SCALE) : 0) * (controller.get(ControllerEnums.BopItButtons.BOPIT) == ButtonStatus.DOWN ? -1 : 1);
                //System.out.println("bop it says: " + driveamt + ", " + turnamt);
                drive(driveamt, turnamt);
                break;
            }
            default:
                throw new IllegalStateException("Invalid drive type");
        }
    }

    @Override
    public void updateAuton() {
        updateGeneric();
        driveRPM.setNumber(leaderL.getSpeed() + leaderR.getSpeed() / 2);
    }

    @Override
    public void initTest() {
        initGeneric();
        setBrake(false);
        resetDriveEncoders();
    }

    @Override
    public void initTeleop() {
        initGeneric();
    }

    @Override
    public void initAuton() {
        initGeneric();
        setBrake(false);
        resetDriveEncoders();
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
    public void resetDriveEncoders() {
        leaderL.resetEncoder();
        leaderR.resetEncoder();
    }

    public void setBrake(boolean braking) {
        coast.setBoolean(!braking);
        leaderL.setBrake(braking);
        leaderR.setBrake(braking);
        followerL.setBrake(braking);
        followerR.setBrake(braking);
    }

    @Override
    public void driveMPS(double xMeters, double yMeters, double rotation) {
        drivePure(Units.metersToFeet(xMeters), rotation);
    }

    @Override
    public void driveWithChassisSpeeds(ChassisSpeeds speeds) { //speeds in mps
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(speeds);
        driveMPS(wheelSpeeds.leftMetersPerSecond, wheelSpeeds.rightMetersPerSecond);
    }

    /**
     * updates telemetry and if calibrating pid, does that
     */
    @Override
    public void updateGeneric() {
        super.updateGeneric();
        if (robotSettings.ENABLE_IMU) {
            guidance.updateGeneric();
        }
        MotorDisconnectedIssue.handleIssue(this, leaderL, leaderR);
        MotorDisconnectedIssue.handleIssue(this, followerL, followerR);
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
    protected void onControlChange() {
        //recreate controllers
        initMisc();
    }

    /**
     * Drives the bot based on the requested left and right speed
     *
     * @param leftFPS  Left drivetrain speed in feet per second
     * @param rightFPS Right drivetrain speed in feet per second
     */
    public void driveFPS(double leftFPS, double rightFPS) {
        //todo get rid of this
        double gearRatio = 28.6472 * 12;
        if (/*robotSettings.DEBUG &&*/ DEBUG) {
            System.out.println("FPS: " + leftFPS + "  " + rightFPS + " (" + gearRatio + ")");
            UserInterface.smartDashboardPutNumber("Left Wheel RPM", leaderL.getSpeed());
            UserInterface.smartDashboardPutNumber("Left Wheel Voltage", leaderL.getVoltage());
        }
        leaderL.moveAtVelocity((leftFPS) * gearRatio * robotSettings.DRIVE_SCALE);
        leaderR.moveAtVelocity((rightFPS) * gearRatio * robotSettings.DRIVE_SCALE);
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

    public void drivePercent(double leftPercent, double rightPercent) {
        leaderL.moveAtPercent(leftPercent);
        leaderR.moveAtPercent(rightPercent);
    }

    /**
     * This takes a speed in feet per second, a requested turn speed in radians/sec
     *
     * @param FPS   Speed in Feet per Second
     * @param omega Rotation in Radians per Second
     */
    public void drivePure(double FPS, double omega) {
        driveWithChassisSpeeds(new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega));
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
     * Creates the drive motors
     *
     * @throws InitializationFailureException When follower drive motors fail to link to leaders or when leader
     *                                        drivetrain motors fail to invert
     */
    private void createDriveMotors() throws InitializationFailureException {
        double s2rf;
        switch (robotSettings.DRIVE_MOTOR_TYPE) {
            case CAN_SPARK_MAX: {
                leaderL = new SparkMotorController(robotSettings.DRIVE_LEADER_L_ID);
                leaderR = new SparkMotorController(robotSettings.DRIVE_LEADER_R_ID);
                followerL = new SparkFollowerMotorsController(robotSettings.DRIVE_FOLLOWERS_L_IDS);
                followerR = new SparkFollowerMotorsController(robotSettings.DRIVE_FOLLOWERS_R_IDS);
                //rpm <=> rps <=> gearing <=> wheel circumference
                s2rf = robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER * Math.PI);
                break;
            }
            case TALON_FX: {
                leaderL = new TalonMotorController(robotSettings.DRIVE_LEADER_L_ID);
                leaderR = new TalonMotorController(robotSettings.DRIVE_LEADER_R_ID);
                followerL = new TalonFollowerMotorController(robotSettings.DRIVE_FOLLOWERS_L_IDS);
                followerR = new TalonFollowerMotorController(robotSettings.DRIVE_FOLLOWERS_R_IDS);
                //Sens units / 100ms <=> rps <=> gearing <=> wheel circumference
                s2rf = (10.0 / robotSettings.DRIVEBASE_SENSOR_UNITS_PER_ROTATION) * robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER * Math.PI / 12);
                break;
            }
            default:
                throw new InitializationFailureException("DriveManager does not have a suitible constructor for " + robotSettings.DRIVE_MOTOR_TYPE.name(), "Add an implementation in the init for drive manager");
        }
        System.out.println(s2rf + " !!!!!!!!!!!! ");
        leaderL.setSensorToRealDistanceFactor(s2rf);
        leaderR.setSensorToRealDistanceFactor(s2rf);

        followerL.follow(leaderL);
        followerR.follow(leaderR);

        leaderL.setInverted(robotSettings.DRIVE_INVERT_LEFT).resetEncoder();
        leaderR.setInverted(robotSettings.DRIVE_INVERT_RIGHT).resetEncoder();

        setAllMotorCurrentLimits(35);

        followerL.invert(robotSettings.DRIVE_INVERT_LEFT);
        followerR.invert(robotSettings.DRIVE_INVERT_RIGHT);
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
     * @throws UnsupportedOperationException when there is no configuration for {@link frc.robot.robotconfigs.DefaultConfig#DRIVE_STYLE}
     */
    private void initMisc() throws UnsupportedOperationException {
        System.out.println("THE XBOX CONTROLLER IS ON " + robotSettings.XBOX_CONTROLLER_USB_SLOT);
        switch (robotSettings.DRIVE_STYLE) {
            case EXPERIMENTAL:
            case BALL_SHIFTING_STANDARD:
            case STANDARD:
                controller = BaseController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT, BaseController.Controllers.XBOX_CONTROLLER);
                break;
            case FLIGHT_STICK:
                controller = BaseController.createOrGet(1, BaseController.Controllers.JOYSTICK_CONTROLLER);
                break;
            case MARIO_KART:
                controller = BaseController.createOrGet(4, BaseController.Controllers.WII_CONTROLLER);
                break;
            case GUITAR:
                controller = BaseController.createOrGet(6, BaseController.Controllers.SIX_BUTTON_GUITAR_CONTROLLER);
                break;
            case DRUM_TIME:
                controller = BaseController.createOrGet(5, BaseController.Controllers.DRUM_CONTROLLER);
                break;
            case BOP_IT:
                controller = BaseController.createOrGet(3, BaseController.Controllers.BOP_IT_CONTROLLER);
                break;
            default:
                throw new UnsupportedOperationException("There is no UI configuration for " + robotSettings.DRIVE_STYLE.name() + " to control the drivetrain. Please implement me");
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

    public void driveCringe(double forward, double rotation) {
        /*
        drivePure(adjustedDrive(forward), adjustedRotation(rotation)); //double FPS, double omega

        driveWithChassisSpeeds(new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega));

        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(speeds);
        driveMPS(wheelSpeeds.leftMetersPerSecond, wheelSpeeds.rightMetersPerSecond);

        driveFPS(Units.metersToFeet(leftMPS), Units.metersToFeet(rightMPS));
         */
        double FPS = adjustedDrive(forward);
        double omega = adjustedRotation(rotation);
        ChassisSpeeds cringChassis = new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega);
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(cringChassis);
        double leftFPS = Units.metersToFeet(wheelSpeeds.leftMetersPerSecond);
        double rightFPS = Units.metersToFeet(wheelSpeeds.rightMetersPerSecond);
        //todo get rid of this
        double gearRatio = 28.6472 * 12;
        if (/*robotSettings.DEBUG &&*/ DEBUG) {
            System.out.println("FPS: " + leftFPS + "  " + rightFPS + " (" + gearRatio + ")");
            UserInterface.smartDashboardPutNumber("Left Wheel RPM", leaderL.getSpeed());
            UserInterface.smartDashboardPutNumber("Left Wheel Voltage", leaderL.getVoltage());
        }
        //I like to call this one driveCringe
        leaderL.moveAtVoltage(adjustedDriveVoltage((leftFPS) * gearRatio * robotSettings.DRIVE_SCALE));
        leaderR.moveAtVoltage(adjustedDriveVoltage((rightFPS) * gearRatio * robotSettings.DRIVE_SCALE));
    }
}