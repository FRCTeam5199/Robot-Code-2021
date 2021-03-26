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
    private static final boolean DEBUG = false;
    private double moveTo = 0.0;
    private final double[][] sizeEncoderPositionArray = {
            {2.792, 0},
            {1.292, 0.58},
            {0.849, 1},
            {0.451, 1.05},
    };
    BaseController joystickController, panel;
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
        double currentPos = hoodMotor.getRotations();
        if (DEBUG && RobotSettings.DEBUG) {
            UserInterface.smartDashboardPutNumber("Hood Pos", hoodMotor.getRotations());
        }
        if (RobotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.COMP_2021){
            if (currentPos > 1.2) {
                hoodMotor.moveAtPercent(0.1);
            } else if (currentPos < 0) {
                hoodMotor.moveAtPercent(-0.1);
            }else if (joystickController.get(ControllerEnums.JoystickButtons.FIVE) == ControllerEnums.ButtonStatus.DOWN) {
                moveTo = -2;
                hoodMotor.moveAtPercent(-0.3);
            } else if (joystickController.get(ControllerEnums.JoystickButtons.THREE) == ControllerEnums.ButtonStatus.DOWN) {
                moveTo = -2;
                hoodMotor.moveAtPercent(0.3);
            } else if (panel.get(ControllerEnums.ButtonPanelButtons.AUX_BOTTOM) == ControllerEnums.ButtonStatus.DOWN){
                moveTo = 0.0; //POS 1
            } else if (panel.get(ControllerEnums.ButtonPanelButtons.INTAKE_DOWN) == ControllerEnums.ButtonStatus.DOWN){
                moveTo = 0.58; //POS 2
            } else if (panel.get(ControllerEnums.ButtonPanelButtons.HOPPER_OUT) == ControllerEnums.ButtonStatus.DOWN){
                moveTo = 1; //POS 3
            } else if (panel.get(ControllerEnums.ButtonPanelButtons.SOLID_SPEED) == ControllerEnums.ButtonStatus.DOWN){
                moveTo = 1.05; //POS 4
            }
        }

        if (RobotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.STANDARD) {
            if (currentPos > 1.2) {
                moveTo = -2;
                hoodMotor.moveAtPercent(0.1);
            } else if (currentPos < 0) {
                moveTo = -2;
                hoodMotor.moveAtPercent(-0.1);
            } else {
                if ((panel.get(ControllerEnums.ButtonPanelButtons.TARGET) == ControllerEnums.ButtonStatus.DOWN) && Robot.shooter.goalCamera.hasValidTarget()) {
                    moveTo = requiredArticulationForTargetSize(Robot.shooter.goalCamera.getSize());
                } else if (joystickController.get(ControllerEnums.JoystickButtons.FIVE) == ControllerEnums.ButtonStatus.DOWN) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(-0.3);
                } else if (joystickController.get(ControllerEnums.JoystickButtons.THREE) == ControllerEnums.ButtonStatus.DOWN) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(0.3);
                } else {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(0);
                }
            }
        } else {
            throw new IllegalStateException("You can't articulate the hood without the panel.");
        }
        moveToPos(moveTo, currentPos);
    }

    private void moveToPos(double moveTo, double currentPos){
        if (moveTo != -2) {
            double distanceNeededToTravel = currentPos - moveTo;
            double hoodPercent = distanceNeededToTravel > 0 ? 0.3 : -0.3;
            if (Math.abs(distanceNeededToTravel) < 0.05) {
                hoodPercent = 0;
            }
            hoodMotor.moveAtPercent(hoodPercent);
            if (DEBUG && RobotSettings.DEBUG) {
                UserInterface.smartDashboardPutNumber("Moving to", moveTo);
                UserInterface.smartDashboardPutNumber("Distance from target", distanceNeededToTravel);
            }
        } else {
            if (DEBUG && RobotSettings.DEBUG) {
                UserInterface.smartDashboardPutNumber("Moving to", -2);
                UserInterface.smartDashboardPutNumber("Distance from target", -2);
            }
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

    public double requiredArticulationForTargetSize(double size) {
        if (size <= sizeEncoderPositionArray[sizeEncoderPositionArray.length - 1][0]) {
            return sizeEncoderPositionArray[sizeEncoderPositionArray.length - 1][1];
        }
        if (size >= sizeEncoderPositionArray[0][0]) {
            return sizeEncoderPositionArray[0][1];
        }
        for (int i = 1; i < sizeEncoderPositionArray.length - 1; i++) {
            if (size > sizeEncoderPositionArray[i][0]) {
                return weightedAverage(size, sizeEncoderPositionArray[i - 1], sizeEncoderPositionArray[i]);
            }
        }
        throw new IllegalStateException("The only way to get here is to not have sizeEncoderPositionArray sorted in ascending order based on the first value of each entry. Please ensure that it is sorted as such and try again.");
        //return -2;
    }
}
