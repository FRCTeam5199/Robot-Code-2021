package frc.ballstuff.shooting;

import frc.controllers.*;
import frc.misc.ISubsystem;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.RobotSettings;

import static frc.robot.Robot.hopper;

public class ArticulatedHood implements ISubsystem {
    private static final boolean DEBUG = false;
    BaseController panel, joystickController;
    private AbstractMotorController hoodMotor;

    public ArticulatedHood() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
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
                throw new IllegalStateException("There is no UI configuration for " + RobotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the articulated hood. Please implement me");
        }
        createAndInitMotors();
    }

    /**
     * Initialize the motors.
     */
    private void createAndInitMotors() {
        switch (RobotSettings.HOOD_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                hoodMotor = new SparkMotorController(RobotSettings.SHOOTER_HOOD_ID);
                hoodMotor.setSensorToRealDistanceFactor(1);
                break;
            case TALON_FX:
                hoodMotor = new TalonMotorController(RobotSettings.SHOOTER_HOOD_ID);
                hoodMotor.setSensorToRealDistanceFactor(600 / RobotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                break;
            default:
                throw new IllegalStateException("No such supported hood config for " + RobotSettings.HOOD_MOTOR_TYPE.name());
        }
        hoodMotor.setCurrentLimit(80).setBrake(false).setOpenLoopRampRate(40).resetEncoder();
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {
        if (RobotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.STANDARD) {
            if (panel.get(ControllerEnums.ButtonPanelButtons.AUX_TOP) == ControllerEnums.ButtonStatus.DOWN) {
                hoodMotor.moveAtPercent(0.1);
            } else if (panel.get(ControllerEnums.ButtonPanelButtons.AUX_BOTTOM) == ControllerEnums.ButtonStatus.DOWN) {
                hoodMotor.moveAtPercent(-0.1);
            }
        } else {
            throw new IllegalStateException("You can't articulate the hood without the panel.");
        }
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

    @Override
    public String getSubsystemName() {
        return null;
    }
}
