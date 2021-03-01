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
import frc.misc.ISubsystem;
import frc.misc.PID;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.RobotSettings;
import frc.vision.GoalPhoton;
import frc.vision.IVision;

import static frc.robot.Robot.hopper;

/**
 * Shooter pertains to spinning the flywheel that actually makes the balls go really fast
 */
public class Shooter implements ISubsystem {
    private final NetworkTableEntry P = UserInterface.shooterP.getEntry(),
            I = UserInterface.SHOOTER_I.getEntry(),
            D = UserInterface.SHOOTER_D.getEntry(),
            F = UserInterface.SHOOTER_F.getEntry(),
            constSpeed = UserInterface.SHOOTER_CONST_SPEED.getEntry(),
            calibratePID = UserInterface.SHOOTER_CALIBRATE_PID.getEntry();
    public double speed = 4200, shooting;
    BaseController panel, joystickController;
    private AbstractMotorController leader, follower;
    private IVision goalPhoton;
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
            case STANDARD:
                joystickController = new JoystickController(RobotSettings.FLIGHT_STICK_USB_SLOT);
                panel = new ButtonPanelController(RobotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joystickController = new BopItBasicController(1);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        createAndInitMotors();
        if (RobotSettings.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
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

        leader.setInverted(RobotSettings.SHOOTER_INVERTED).setPid(RobotSettings.SHOOTER_PID);
        if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
            follower.follow(leader).setInverted(!RobotSettings.SHOOTER_INVERTED).setCurrentLimit(80).setBrake(false);
            //TODO test if braking leader brakes follower
        }
        leader.setCurrentLimit(80).setBrake(false).setOpenLoopRampRate(40).resetEncoder();
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {
        //updateGeneric();
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
        updateShuffleboard();
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD: {
                if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                } else if (isValidTarget() && panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                } else {
                    hopper.setAll(false);
                    leader.moveAtPercent(0);
                    //setSpeed(constSpeed.getDouble(0));
                }
                break;
            }
            case BOP_IT: {
                if (joystickController.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                }
                break;
            }
            default:
                throw new IllegalStateException("This UI not implemented for this controller");
        }
    }

    private void updateShuffleboard() {
        if (calibratePID.getBoolean(false)) {
            PID readPid = new PID(P.getDouble(RobotSettings.DRIVEBASE_PID.getP()), I.getDouble(RobotSettings.DRIVEBASE_PID.getI()), D.getDouble(RobotSettings.DRIVEBASE_PID.getD()), F.getDouble(RobotSettings.DRIVEBASE_PID.getF()));
            if (!lastPID.equals(readPid)) {
                lastPID = readPid;
                leader.setPid(lastPID);
                if (RobotSettings.DEBUG) {
                    System.out.println("Set shooter pid to " + lastPID);
                }
            }
        }
        UserInterface.putNumber("RPM", leader.getSpeed());
        UserInterface.putNumber("Target RPM", speed);
        UserInterface.putBoolean("atSpeed", isAtSpeed());
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        if (RobotSettings.DEBUG) {
            System.out.println("set shooter speed to " + rpm);
        }
        leader.moveAtVelocity(rpm);
        //leader.moveAtPercent(rpm == 0 ? 0 : rpm > 0 ? .75 : 0);
    }

    /**
     * if the shooter is actually at the requested speed
     *
     * @return if the shooter is actually at the requested speed
     */
    public boolean isAtSpeed() {
        return leader.getSpeed() > speed - 80;
    }

    /**
     * if the goal photon is in use and has a valid target in its sights
     *
     * @return if the goal photon is in use and has a valid target in its sights
     */
    public boolean isValidTarget() {
        return RobotSettings.ENABLE_VISION && goalPhoton.hasValidTarget();
    }
}
