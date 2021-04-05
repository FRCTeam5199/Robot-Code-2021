package frc.ballstuff.shooting;

import edu.wpi.first.networktables.NetworkTableEntry;
import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ButtonPanelController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.controllers.JoystickController;
import frc.controllers.XBoxController;
import frc.misc.ISubsystem;
import frc.misc.PID;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.vision.camera.IVision;

import static frc.robot.Robot.hopper;
import static frc.robot.Robot.robotSettings;

/**
 * Shooter pertains to spinning the flywheel that actually makes the balls go really fast
 */
public class Shooter implements ISubsystem {
    public static final boolean DEBUG = true;
    private final NetworkTableEntry P = UserInterface.SHOOTER_P.getEntry(),
            I = UserInterface.SHOOTER_I.getEntry(),
            D = UserInterface.SHOOTER_D.getEntry(),
            F = UserInterface.SHOOTER_F.getEntry(),
            constSpeed = UserInterface.SHOOTER_CONST_SPEED.getEntry(),
            calibratePID = UserInterface.SHOOTER_CALIBRATE_PID.getEntry();
    public double speed = 4200;
    public int ballsShot = 0, ticksPassed = 0;
    //Yes this needs to be package private
    boolean singleShot = false;
    IVision goalCamera;
    BaseController panel, joystickController;
    private AbstractMotorController leader, follower;
    private PID lastPID = PID.EMPTY_PID;
    private boolean isConstSpeed, isConstSpeedLast = false, shooting = false;

    public Shooter() {
        addToMetaList();
        init();
    }

    /**
     * Initialize the Shooter object including the controller and the cameras and timers
     */
    @Override
    public void init() throws IllegalStateException {
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD:
                joystickController = JoystickController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT);
                panel = ButtonPanelController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joystickController = BopItBasicController.createOrGet(1);
                break;
            case XBOX_CONTROLLER:
                joystickController = XBoxController.createOrGet(1);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        createAndInitMotors();
        if (robotSettings.ENABLE_VISION) {
            goalCamera = IVision.manufactureGoalCamera(robotSettings.GOAL_CAMERA_TYPE);
        }
    }

    /**
     * Initialize the motors. Checks for SHOOTER_USE_SPARKS and SHOOTER_USE_TWO_MOTORS to allow modularity.
     *
     * @throws IllegalStateException If the motor configuration is not implemented
     */
    private void createAndInitMotors() throws IllegalStateException {
        switch (robotSettings.SHOOTER_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                leader = new SparkMotorController(robotSettings.SHOOTER_LEADER_ID);
                if (robotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new SparkMotorController(robotSettings.SHOOTER_FOLLOWER_ID);
                }
                leader.setSensorToRealDistanceFactor(1);
                break;
            case TALON_FX:
                leader = new TalonMotorController(robotSettings.SHOOTER_LEADER_ID);
                if (robotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new TalonMotorController(robotSettings.SHOOTER_FOLLOWER_ID);
                }
                leader.setSensorToRealDistanceFactor(600 / robotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                break;
            default:
                throw new IllegalStateException("No such supported shooter motor config for " + robotSettings.SHOOTER_MOTOR_TYPE.name());
        }

        leader.setInverted(robotSettings.SHOOTER_INVERTED);
        if (robotSettings.SHOOTER_USE_TWO_MOTORS) {
            follower.follow(leader, true).setInverted(!robotSettings.SHOOTER_INVERTED).setCurrentLimit(80).setBrake(false);
            //TODO test if braking leader brakes follower
        }
        leader.setCurrentLimit(80).setBrake(false).setOpenLoopRampRate(40).resetEncoder();
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {

    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }

    /**
     * Input is parsed and shooter object maintained appropriately.
     *
     * @throws IllegalStateException if control is not implemented for {@link frc.robot.robotconfigs.DefaultConfig#SHOOTER_CONTROL_STYLE
     *                               current control style}
     * @see frc.robot.robotconfigs.DefaultConfig#SHOOTER_CONTROL_STYLE
     * @see ShootingControlStyles
     */
    @Override
    public void updateGeneric() throws IllegalStateException {
        if (leader.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, robotSettings.SHOOTER_LEADER_ID, leader.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, robotSettings.SHOOTER_LEADER_ID);
        if (follower != null && follower.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, robotSettings.SHOOTER_FOLLOWER_ID, follower.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, robotSettings.SHOOTER_FOLLOWER_ID);
        updateShuffleboard();
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD: {
                if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                    isConstSpeed = false;
                } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                    isConstSpeed = false;
                } else {
                    if (robotSettings.ENABLE_HOPPER) {
                        hopper.setAll(false);
                    }
                    leader.moveAtPercent(0);
                    shooting = false;
                    ballsShot = 0;
                }
                break;
            }
            case ACCURACY_2021: {
                if (Robot.articulatedHood.unTargeted) {
                    shooterDefault();
                } else if (panel.get(ControllerEnums.ButtonPanelTapedButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                    isConstSpeed = false;
                } else if (singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                    isConstSpeed = false;
                } else if (panel.get(ControllerEnums.ButtonPanelTapedButtons.SINGLE_SHOT) == ButtonStatus.DOWN) {
                    singleShot = true;
                } else {
                    shooterDefault();
                }
                break;
            }
            case SPEED_2021: {
                if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_WITH_HOPPER_CONTROLLED.shoot(this);
                    //ShootingEnums.FIRE_TIMED.shoot(this);
                    isConstSpeed = false;
                } else if (panel.get(ButtonPanelButtons.HOPPER_IN) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                    isConstSpeed = false;
                } else {
                    shooterDefault();
                }
                break;
            }
            case BOP_IT: {
                if (joystickController.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                } else {
                    shooting = false;
                    ballsShot = 0;
                }
                break;
            }
            case XBOX_CONTROLLER: {
                if (joystickController.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1) {
                    ShootingEnums.FIRE_TEST_SPEED.shoot(this);
                } else {
                    leader.moveAtPercent(0);
                    shooting = false;
                    ballsShot = 0;
                }
                break;
            }
            default:
                throw new IllegalStateException("This UI not implemented for this controller");
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
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {
        isConstSpeedLast = false;
        isConstSpeed = false;
        singleShot = false;
        if (robotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.SPEED_2021) {
            leader.setPid(new PID(0.0025, 0.0000007, 0.03, 0));
        } else {
            leader.setPid(robotSettings.SHOOTER_PID);
        }
    }

    @Override
    public String getSubsystemName() {
        return "Shooter";
    }

    private void updateShuffleboard() {
        if (calibratePID.getBoolean(false)) {
            PID readPid = new PID(P.getDouble(robotSettings.SHOOTER_PID.getP()), I.getDouble(robotSettings.SHOOTER_PID.getI()), D.getDouble(robotSettings.SHOOTER_PID.getD()), F.getDouble(robotSettings.SHOOTER_PID.getF()));
            if (!lastPID.equals(readPid)) {
                lastPID = readPid;
                leader.setPid(lastPID);
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Set shooter pid to " + lastPID);
                }
            }
        } else {
            if (!isConstSpeed && isConstSpeedLast) {
                leader.setPid(robotSettings.SHOOTER_PID);
                isConstSpeedLast = false;
                if (DEBUG && robotSettings.DEBUG) {
                    System.out.println("Normal shooter PID.");
                }
            } else {
                if (DEBUG && robotSettings.DEBUG) {
                    System.out.println("Running constant speed PID.");
                }
            }
        }
        UserInterface.smartDashboardPutNumber("RPM", leader.getSpeed());
        UserInterface.smartDashboardPutNumber("Target RPM", speed);
        UserInterface.smartDashboardPutBoolean("atSpeed", isAtSpeed());
        UserInterface.smartDashboardPutBoolean("IS SHOOTING?", shooting);
    }

    /**
     * Runs the default update which unsets hopper {@link frc.ballstuff.intaking.Hopper#setAll(boolean) active flags}
     * and sets speed to constant speed (or 0)
     */
    private void shooterDefault() {
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(false);
        }
        double speedYouWant = constSpeed.getDouble(0);
        if (speedYouWant != 0) {
            isConstSpeed = true;
            if (!isConstSpeedLast) {
                isConstSpeedLast = true;
                leader.setPid(robotSettings.SHOOTER_CONST_SPEED_PID);
            }
            leader.moveAtVelocity(speedYouWant);
        } else {
            leader.moveAtPercent(0);
        }
        shooting = false;
        ballsShot = 0;
    }

    /**
     * if the shooter is actually at the requested speed
     *
     * @return if the shooter is actually at the requested speed
     */
    public boolean isAtSpeed() {
        return Math.abs(leader.getSpeed() - speed) < 50;
    }

    /**
     * if the goal photon is in use and has a valid target in its sights
     *
     * @return if the goal photon is in use and has a valid target in its sights
     */
    public boolean isValidTarget() {
        return robotSettings.ENABLE_VISION && goalCamera.hasValidTarget();
    }

    /**
     * Gets the current speed of the leader motor
     *
     * @return the current speed of the leader motor based on the output units of {@link #leader}
     */
    public double getSpeed() {
        return leader.getSpeed();
    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("set shooter speed to " + rpm);
        }
        speed = rpm;
        leader.moveAtVelocity(rpm);
    }

    /**
     * Moves the shooter at a -1 to 1 percent speed
     *
     * @param percentSpeed percent from -1 to 1 to move the shooter at
     */
    public void setPercentSpeed(double percentSpeed) {
        leader.moveAtPercent(percentSpeed);
    }

    /**
     * Getter for {@link #shooting}
     *
     * @return whether the shooter is shooting or not
     */
    public boolean isShooting() {
        return shooting;
    }

    void setShooting(boolean shooting) {
        this.shooting = shooting;
    }
}
