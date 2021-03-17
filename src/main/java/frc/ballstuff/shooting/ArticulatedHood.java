package frc.ballstuff.shooting;

import com.revrobotics.CANSparkMaxLowLevel;
import frc.controllers.*;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;
import frc.robot.RobotSettings;

import static frc.misc.UtilFunctions.weightedAverage;

public class ArticulatedHood implements ISubsystem {
    private static final boolean DEBUG = true;
    BaseController joystickController, panel;
    private AbstractMotorController hoodMotor;

    private final double[][] sizeEncoderPositionArray = {
            {0, 0},
            {45, 4100},
            {55, 4150},
            {65, 4170},
            {85, 4500},
    };

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
        hoodMotor.setBrake(true);
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
        if (DEBUG && RobotSettings.DEBUG) {
            UserInterface.smartDashboardPutNumber("Hood Pos", hoodMotor.getRotations());
        }
        double currentPos = hoodMotor.getRotations();
        if (currentPos > 0.9){
            hoodMotor.moveAtPercent(-0.1);
        } else if (currentPos < 0){
            hoodMotor.moveAtPercent(0.1);
        }

        if (RobotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.STANDARD) {
            if (panel.get(ControllerEnums.ButtonPanelButtons.TARGET) == ControllerEnums.ButtonStatus.DOWN) {
                //Adjust based on distance
                double moveTo = fetchEncoderPos(Robot.shooter.goalCamera.getSize());

                if (DEBUG && RobotSettings.DEBUG) {
                    UserInterface.smartDashboardPutNumber("Moving to", moveTo);
                }
            } else if (joystickController.get(ControllerEnums.JoystickButtons.FIVE) == ControllerEnums.ButtonStatus.DOWN) {
                hoodMotor.moveAtPercent(0.3);
            } else if (joystickController.get(ControllerEnums.JoystickButtons.THREE) == ControllerEnums.ButtonStatus.DOWN) {
                hoodMotor.moveAtPercent(-0.3);
            } else {
                hoodMotor.moveAtPercent(0);
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
        initGeneric();
    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {
        hoodMotor.resetEncoder();
    }

    @Override
    public String getSubsystemName() {
        return "ShooterHood";
    }

    public double fetchEncoderPos(double size) {
        if (size > sizeEncoderPositionArray[sizeEncoderPositionArray.length - 1][0]) {
            return sizeEncoderPositionArray[sizeEncoderPositionArray.length - 1][1];
        }
        if (size < sizeEncoderPositionArray[0][0]) {
            return sizeEncoderPositionArray[0][1];
        }
        for (int i = sizeEncoderPositionArray.length - 2; i >= 0; i--) {
            if (size > sizeEncoderPositionArray[i][0]) {
                return weightedAverage(size, sizeEncoderPositionArray[i + 1], sizeEncoderPositionArray[i]);
            }
        }
        return -2;
        //throw new IllegalStateException("The only way to get here is to not have sizeEncoderPositionArray sorted in ascending order based on the first value of each entry. Please ensure that it is sorted as such and try again.");
    }
}
