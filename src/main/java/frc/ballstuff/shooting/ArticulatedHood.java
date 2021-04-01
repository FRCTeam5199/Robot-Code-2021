package frc.ballstuff.shooting;

import com.revrobotics.CANSparkMaxLowLevel;
import frc.controllers.*;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;
import static frc.robot.Robot.RobotSettings;

import static frc.misc.UtilFunctions.weightedAverage;

public class ArticulatedHood implements ISubsystem {
    private static final boolean DEBUG = false;
    /*
    private final double[][] sizeEncoderPositionArray = {
            {2.415, 0.05},
            {1.466, 0.77},
            {0.793, 0.95},
            {0.481, 1.235},
    };
    private final double[][] sizeEncoderPositionArrayStraight = {
            {2.415, 0.05},
            {1.466, 0.77},
            {0.925, 1.05},
            {0.481, 1.135},
    };
     */
    public double moveTo = 0.0;
    public boolean unTargeted = true;
    BaseController joystickController, panel;
    private AbstractMotorController hoodMotor;

    public ArticulatedHood() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD:
                joystickController = JoystickController.createOrGet(RobotSettings.FLIGHT_STICK_USB_SLOT);
                panel = ButtonPanelController.createOrGet(RobotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joystickController = BopItBasicController.createOrGet(1);
                break;
            case XBOX_CONTROLLER:
                joystickController = XBoxController.createOrGet(1);
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
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
                if (currentPos > 1.75) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(0.1);
                } else if (currentPos < 0) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(-0.1);
                } else if (joystickController.get(ControllerEnums.JoystickButtons.FIVE) == ControllerEnums.ButtonStatus.DOWN) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(-0.3);
                } else if (joystickController.get(ControllerEnums.JoystickButtons.THREE) == ControllerEnums.ButtonStatus.DOWN) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(0.3);
                } else if (panel.get(ControllerEnums.ButtonPanelButtons.TARGET) == ControllerEnums.ButtonStatus.DOWN) {
                    if (!Robot.shooter.isValidTarget()){
                        moveTo = 1.4;
                        unTargeted = true;
                    } else {
                        double[][] sizeEncoderPositionArrayStraight = {
                                {2.415, 0.05},
                                {1.466, 0.77},
                                {0.925, 1.05},
                                {0.481, 1.135},
                        };
                        moveTo = requiredArticulationForTargetSize(Robot.shooter.goalCamera.getSize(), sizeEncoderPositionArrayStraight);
                        unTargeted = false;
                    }
                } else {
                    moveToPosFromButtons();
                }
                break;
            case SPEED_2021:
                if (currentPos > 1.75) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(0.1);
                } else if (currentPos < 0) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(-0.1);
                } else if (joystickController.get(ControllerEnums.JoystickButtons.FIVE) == ControllerEnums.ButtonStatus.DOWN) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(-0.3);
                } else if (joystickController.get(ControllerEnums.JoystickButtons.THREE) == ControllerEnums.ButtonStatus.DOWN) {
                    moveTo = -1;
                    hoodMotor.moveAtPercent(0.3);
                } else if (panel.get(ControllerEnums.ButtonPanelButtons.TARGET) == ControllerEnums.ButtonStatus.DOWN) {
                    if (!Robot.shooter.isValidTarget()){
                        moveTo = 1.4;
                        unTargeted = true;
                    } else {
                        double[][] sizeEncoderPositionArrayAngled = {
                                {2.415, 0.05},
                                {1.466, 0.77},
                                {0.793, 0.95},
                                {0.481, 1.235},
                        };
                        moveTo = requiredArticulationForTargetSize(Robot.shooter.goalCamera.getSize(), sizeEncoderPositionArrayAngled);
                        unTargeted = false;
                    }
                } else moveToPosFromButtons();
                break;
            case STANDARD:
                if (currentPos > 1.75) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(0.1);
                } else if (currentPos < 0) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(-0.1);
                } else {
                    if ((panel.get(ControllerEnums.ButtonPanelButtons.TARGET) == ControllerEnums.ButtonStatus.DOWN) && Robot.shooter.goalCamera.hasValidTarget()) {
                        if (!Robot.shooter.isShooting) {
                            double[][] sizeEncoderPositionArrayStraight = {
                                    {2.415, 0.05},
                                    {1.466, 0.77},
                                    {0.925, 1.05},
                                    {0.481, 1.135},
                            };
                            moveTo = requiredArticulationForTargetSize(Robot.shooter.goalCamera.getSize(), sizeEncoderPositionArrayStraight);
                        }
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
                break;
            default:
                throw new IllegalStateException("You can't articulate the hood without the panel.");
        }
        if (DEBUG && RobotSettings.DEBUG) {
            UserInterface.smartDashboardPutNumber("Hood Pos", hoodMotor.getRotations());
            UserInterface.smartDashboardPutNumber("Moving to pos", moveTo);
        }
        moveToPos(moveTo, currentPos);
    }

    private void moveToPosFromButtons() {
        if (panel.get(ControllerEnums.ButtonPanelButtons.AUX_BOTTOM) == ControllerEnums.ButtonStatus.DOWN) {
            moveTo = 0.05; //POS 1
            unTargeted = false;
        } else if (panel.get(ControllerEnums.ButtonPanelButtons.INTAKE_DOWN) == ControllerEnums.ButtonStatus.DOWN) {
            moveTo = 0.77; //POS 2
            unTargeted = false;
        } else if (panel.get(ControllerEnums.ButtonPanelButtons.HOPPER_OUT) == ControllerEnums.ButtonStatus.DOWN) {
            moveTo = 1.05; //POS 3
            unTargeted = false;
        } else if (panel.get(ControllerEnums.ButtonPanelButtons.SOLID_SPEED) == ControllerEnums.ButtonStatus.DOWN) {
            moveTo = 1.135; //POS 4
            unTargeted = false;
        } else {
            hoodMotor.moveAtPercent(0);
        }
    }

    private void moveToPos(double moveTo, double currentPos) {
        if (DEBUG && RobotSettings.DEBUG) {
            UserInterface.smartDashboardPutNumber("Moving to", moveTo);
        }
        if (moveTo != -2 && moveTo != -1) {
            double distanceNeededToTravel = currentPos - moveTo;
            //double hoodPercent = distanceNeededToTravel > 0 ? 0.3 : -0.3;
            double hoodPercent = Math.min(Math.abs(distanceNeededToTravel), 0.3);
            hoodPercent *= distanceNeededToTravel > 0 ? 1 : -1;
            /*if (Math.abs(distanceNeededToTravel) < 0.035) {
                hoodPercent = 0;
            }*/
            hoodMotor.moveAtPercent(hoodPercent);
            if (DEBUG && RobotSettings.DEBUG) {
                UserInterface.smartDashboardPutNumber("Moving to", moveTo);
                UserInterface.smartDashboardPutNumber("Distance from target", distanceNeededToTravel);
            }
        } else if (moveTo == -2) {
            if (DEBUG && RobotSettings.DEBUG) {
                UserInterface.smartDashboardPutNumber("Distance from target", 0);
            }
            hoodMotor.moveAtPercent(0);
        } else {
            if (DEBUG && RobotSettings.DEBUG) {
                UserInterface.smartDashboardPutNumber("Distance from target", 0);
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

    public double requiredArticulationForTargetSize(double size, double[][] articulationArray) {
        System.out.println("SIZE:" + size);
        if (size <= articulationArray[articulationArray.length - 1][0]) {
            System.out.println("A " + articulationArray[articulationArray.length - 1][1]);
            return articulationArray[articulationArray.length - 1][1];
        }
        if (size >= articulationArray[0][0]) {
            System.out.println("B " + articulationArray[0][1]);
            return articulationArray[0][1];
        }
        for (int i = 1; i < articulationArray.length; i++) {
            if (size > articulationArray[i][0]) {
                System.out.println("C " + weightedAverage(size, articulationArray[i - 1], articulationArray[i]));
                return weightedAverage(size, articulationArray[i - 1], articulationArray[i]);
            }
        }
        throw new IllegalStateException("The only way to get here is to not have sizeEncoderPositionArray sorted in ascending order based on the first value of each entry. Please ensure that it is sorted as such and try again.");
        //return -2;
    }
}
