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
import frc.robot.RobotSettings;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.vision.camera.GoalLimelight;
import frc.vision.camera.GoalPhoton;
import frc.vision.camera.IVision;

import static frc.robot.Robot.hopper;

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
    public double speed = 4200, shooting;
    public IVision goalCamera;
    public boolean singleShot = false;
    public boolean isShooting = false;
    public int ballsShot = 0;
    public int ticksPassed = 0;
    BaseController panel, joystickController;
    private AbstractMotorController leader, follower;
    private PID lastPID = PID.EMPTY_PID;

    public Shooter() {
        addToMetaList();
        init();
    }

    /**
     * Initialize the Shooter object including the controller and the cameras and timers
     */
    @Override
    public void init() throws IllegalStateException {
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD:
                joystickController = new JoystickController(RobotSettings.FLIGHT_STICK_USB_SLOT);
                panel = new ButtonPanelController(RobotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joystickController = new BopItBasicController(1);
                break;
            case XBOX_CONTROLLER:
                joystickController = new XBoxController(1);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        createAndInitMotors();
        if (RobotSettings.ENABLE_VISION) {
            switch(RobotSettings.GOAL_CAMERA_TYPE){
            case LIMELIGHT:
                goalCamera = new GoalLimelight();
                goalCamera.init();
                break;
            case PHOTON:
                goalCamera = new GoalPhoton();
                goalCamera.init();
                break;
            default:
                throw new IllegalStateException("You must have a camera type set.");
            }
        }
    }

    /**
     * Initialize the motors. Checks for SHOOTER_USE_SPARKS and SHOOTER_USE_TWO_MOTORS to allow modularity.
     */
    private void createAndInitMotors() {
        switch (RobotSettings.SHOOTER_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                leader = new SparkMotorController(RobotSettings.SHOOTER_LEADER_ID);
                if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new SparkMotorController(RobotSettings.SHOOTER_FOLLOWER_ID);
                }
                leader.setSensorToRealDistanceFactor(1);
                break;
            case TALON_FX:
                leader = new TalonMotorController(RobotSettings.SHOOTER_LEADER_ID);
                if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new TalonMotorController(RobotSettings.SHOOTER_FOLLOWER_ID);
                }
                leader.setSensorToRealDistanceFactor(600 / RobotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                break;
            default:
                throw new IllegalStateException("No such supported shooter motor config for " + RobotSettings.SHOOTER_MOTOR_TYPE.name());
        }

        leader.setInverted(RobotSettings.SHOOTER_INVERTED);
        if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
            follower.follow(leader, true).setInverted(!RobotSettings.SHOOTER_INVERTED).setCurrentLimit(80).setBrake(false);
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
     * Input is parsed and shooter object maintained appropriately
     */
    @Override
    public void updateGeneric() {

        if (leader.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, RobotSettings.SHOOTER_LEADER_ID, leader.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, RobotSettings.SHOOTER_LEADER_ID);
        if (follower != null && follower.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, RobotSettings.SHOOTER_FOLLOWER_ID, follower.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, RobotSettings.SHOOTER_FOLLOWER_ID);
        updateShuffleboard();
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD: {
                if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                } else {
                    if (RobotSettings.ENABLE_HOPPER) {
                        hopper.setAll(false);
                    }
                    leader.moveAtPercent(0);
                    isShooting = false;
                    ballsShot = 0;
                }
                break;
            }
            case ACCURACY_2021: {
                if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                } else if (Robot.articulatedHood.unTargeted) {
                    shooterDefault();
                } else if (panel.get(ButtonPanelButtons.HOPPER_IN) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                } else if (singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                } else if (panel.get(ButtonPanelButtons.INTAKE_UP) == ButtonStatus.DOWN) {
                    singleShot = true;
                } else {
                    shooterDefault();
                }
                break;
            }
            case SPEED_2021: {
                if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_WITH_NO_REGARD_TO_ACCURACY.shoot(this);
                } else if (panel.get(ButtonPanelButtons.HOPPER_IN) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                } else {
                    shooterDefault();
                }
                break;
            }
            case BOP_IT: {
                if (joystickController.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                } else {
                    isShooting = false;
                    ballsShot = 0;
                }
                break;
            }
            case XBOX_CONTROLLER: {
                if (joystickController.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1) {
                    ShootingEnums.FIRE_TEST_SPEED.shoot(this);
                } else {
                    leader.moveAtPercent(0);
                    isShooting = false;
                    ballsShot = 0;
                }
                break;
            }
            default:
                throw new IllegalStateException("This UI not implemented for this controller");
        }
    }

    private void shooterDefault() {
        if (RobotSettings.ENABLE_HOPPER) {
            hopper.setAll(false);
        }
        double speedYouWant = constSpeed.getDouble(0);
        if (speedYouWant != 0) {
            leader.moveAtVelocity(speedYouWant);
        } else {
            leader.moveAtPercent(0);
        }
        isShooting = false;
        ballsShot = 0;
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
        singleShot = false;
        if (RobotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.SPEED_2021) {
            leader.setPid(new PID(0.0025, 0.0000007, 0.03, 0));
        } else {
            leader.setPid(RobotSettings.SHOOTER_PID);
        }
    }

    @Override
    public String getSubsystemName() {
        return "Shooter";
    }

    private void updateShuffleboard() {
        if (calibratePID.getBoolean(false)) {
            PID readPid = new PID(P.getDouble(RobotSettings.SHOOTER_PID.getP()), I.getDouble(RobotSettings.SHOOTER_PID.getI()), D.getDouble(RobotSettings.SHOOTER_PID.getD()), F.getDouble(RobotSettings.SHOOTER_PID.getF()));
            if (!lastPID.equals(readPid)) {
                lastPID = readPid;
                leader.setPid(lastPID);
                if (RobotSettings.DEBUG && DEBUG) {
                    System.out.println("Set shooter pid to " + lastPID);
                }
            }
        }
        UserInterface.smartDashboardPutNumber("RPM", leader.getSpeed());
        UserInterface.smartDashboardPutNumber("Target RPM", speed);
        UserInterface.smartDashboardPutBoolean("atSpeed", isAtSpeed());
        UserInterface.smartDashboardPutBoolean("IS SHOOTING?", isShooting);
    }

    /**
     * if the goal photon is in use and has a valid target in its sights
     *
     * @return if the goal photon is in use and has a valid target in its sights
     */
    public boolean isValidTarget() {
        return RobotSettings.ENABLE_VISION && goalCamera.hasValidTarget();
    }

    /**
     * if the shooter is actually at the requested speed
     *
     * @return if the shooter is actually at the requested speed
     */
    public boolean isAtSpeed() {
        return Math.abs(leader.getSpeed() - speed) < 50;
    }

    public double getSpeed() {
        return leader.getSpeed();
    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        if (RobotSettings.DEBUG && DEBUG) {
            System.out.println("set shooter speed to " + rpm);
        }
        leader.moveAtVelocity(rpm);
    }

    public void setPercentSpeed(double percentSpeed) {
        leader.moveAtPercent(percentSpeed);
    }
}
