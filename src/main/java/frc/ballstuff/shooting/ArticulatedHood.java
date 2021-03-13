package frc.ballstuff.shooting;

import com.revrobotics.CANSparkMaxLowLevel;
import frc.controllers.*;
import frc.misc.ISubsystem;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.RobotSettings;

public class ArticulatedHood implements ISubsystem {
    private static final boolean DEBUG = false;
    BaseController joystickController;
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
                hoodMotor = new SparkMotorController(RobotSettings.SHOOTER_HOOD_ID, CANSparkMaxLowLevel.MotorType.kBrushed);
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
        //updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {
        //updateGeneric();
    }

    @Override
    public void updateGeneric() { //FIVE UP THREE DOWN
        if (RobotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.STANDARD) {
            if (joystickController.get(ControllerEnums.JoystickButtons.FIVE) == ControllerEnums.ButtonStatus.DOWN) {
                hoodMotor.moveAtPercent(0.3);
            } else if (joystickController.get(ControllerEnums.JoystickButtons.THREE) == ControllerEnums.ButtonStatus.DOWN) {
                hoodMotor.moveAtPercent(-0.3);
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
        return "ShooterHood";
    }
}
