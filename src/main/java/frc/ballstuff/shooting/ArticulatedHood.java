package frc.ballstuff.shooting;

import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.networktables.NetworkTableEntry;
import frc.controllers.BaseController;
import frc.misc.ISubsystem;
import frc.misc.PID;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;

import static frc.controllers.ControllerEnums.*;
import static frc.misc.UtilFunctions.weightedAverage;
import static frc.robot.Robot.*;

enum HoodSpecialAction {
    MANUAL_MOVEMENT, OUT_OF_BOUNDS, NOT_MOVING, AIMING
}

/**
 * Articulated hood refers to the moving top section of {@link Shooter}. Theres a lot going on here so maybe check out
 * the settings.
 */
public class ArticulatedHood implements ISubsystem {
    private static final boolean DEBUG = false;
    private final NetworkTableEntry HOOD_HEIGHT = UserInterface.HOOD_HEIGHT.getEntry(),
            HEIGHT_OVERRIDE = UserInterface.HOOD_OVERRIDE_HEIGHT.getEntry(),
            HOOD_OVERRIDE = UserInterface.HOOD_OVERRIDE_POSITION.getEntry(),
            VISION_SIZE = UserInterface.VISION_SIZE.getEntry(),
            VISION_ESTIMATE_HEIGHT = UserInterface.VISION_CALCULATED_HEIGHT.getEntry();
    public boolean unTargeted = true;
    public double moveTo = 0.0;
    public double lastSeenCameraArea = 0.0;
    public boolean isAtWantedPosition = false;
    public AbstractMotorController hoodMotor;
    BaseController joystickController, panel;

    public ArticulatedHood() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD_OFFSEASON_2021:
            case EXPERIMENTAL_OFFSEASON_2021:
            case STANDARD:
                joystickController = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
                panel = BaseController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT, BaseController.Controllers.BUTTON_PANEL_CONTROLLER);
                break;
            case BOP_IT:
                joystickController = BaseController.createOrGet(1, BaseController.Controllers.BOP_IT_CONTROLLER);
                break;
            case XBOX_CONTROLLER:
                joystickController = BaseController.createOrGet(1, BaseController.Controllers.XBOX_CONTROLLER);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the articulated hood. Please implement me");
        }
        createAndInitMotors();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return hoodMotor.isFailed() ? SubsystemStatus.FAILED : SubsystemStatus.NOMINAL;
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

    }

    @Override
    public void updateGeneric() { //FIVE UP THREE DOWN
        double currentPos = hoodMotor.getRotations();
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
                if (currentPos > robotSettings.SHOOTER_HOOD_MAX_POS) {
                    moveTo = -3;
                    hoodMotor.moveAtPercent(0.1);
                } else if (currentPos < robotSettings.SHOOTER_HOOD_MIN_POS) {
                    moveTo = -3;
                    hoodMotor.moveAtPercent(-0.1);
                } else if (joystickController.get(JoystickButtons.FIVE) == ButtonStatus.DOWN) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(-0.3);
                } else if (joystickController.get(JoystickButtons.THREE) == ButtonStatus.DOWN) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(0.3);
                } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN) {
                    if (!Robot.shooter.isValidTarget()) {
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
                if (currentPos > robotSettings.SHOOTER_HOOD_MAX_POS) {
                    moveTo = -3;
                    hoodMotor.moveAtPercent(0.1);
                } else if (currentPos < robotSettings.SHOOTER_HOOD_MIN_POS) {
                    moveTo = -3;
                    hoodMotor.moveAtPercent(-0.1);
                } else if (joystickController.get(JoystickButtons.FIVE) == ButtonStatus.DOWN) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(-0.3);
                } else if (joystickController.get(JoystickButtons.THREE) == ButtonStatus.DOWN) {
                    moveTo = -2;
                    hoodMotor.moveAtPercent(0.3);
                } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN) {
                    if (!Robot.shooter.isValidTarget()) {
                        moveTo = 0.95;
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
                if (HEIGHT_OVERRIDE.getBoolean(false)) {
                    moveToPos(HOOD_HEIGHT.getDouble(0), currentPos);
                } else if (currentPos > robotSettings.SHOOTER_HOOD_MAX_POS) {
                    moveToPos(HoodSpecialAction.OUT_OF_BOUNDS, -robotSettings.SHOOTER_HOOD_OUT_OF_BOUNDS_SPEED);
                } else if (currentPos < robotSettings.SHOOTER_HOOD_MIN_POS) {
                    moveToPos(HoodSpecialAction.OUT_OF_BOUNDS, robotSettings.SHOOTER_HOOD_OUT_OF_BOUNDS_SPEED);
                } else {
                    if ((panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN) && Robot.shooter.goalCamera.hasValidTarget()) {
                        if (!Robot.shooter.isShooting()) {
                            moveToPos(requiredArticulationForTargetSize(Robot.shooter.goalCamera.getSize(), robotSettings.CALIBRATED_HOOD_POSITION_ARRAY), currentPos);
                        }
                    } else if (joystickController.get(JoystickButtons.FIVE) == ButtonStatus.DOWN) {
                        moveToPos(HoodSpecialAction.MANUAL_MOVEMENT, robotSettings.SHOOTER_HOOD_CONTROL_SPEED);
                    } else if (joystickController.get(JoystickButtons.THREE) == ButtonStatus.DOWN) {
                        moveToPos(HoodSpecialAction.MANUAL_MOVEMENT, -robotSettings.SHOOTER_HOOD_CONTROL_SPEED);
                    } else {
                        if (!HOOD_OVERRIDE.getBoolean(false) && currentPos > 3 && !shooter.isShooting()) {
                            moveToPos(0, currentPos);
                        } else {
                            moveToPos(HoodSpecialAction.NOT_MOVING);
                        }
                    }
                }
                break;
            case EXPERIMENTAL_OFFSEASON_2021:
            case STANDARD_OFFSEASON_2021:
                if (!shooter.tryFiringBalls && shooter.isValidTarget()) {
                    lastSeenCameraArea = Robot.shooter.goalCamera.getSize();
                }
                if (currentPos > robotSettings.SHOOTER_HOOD_MAX_POS) {
                    moveToPos(HoodSpecialAction.OUT_OF_BOUNDS, -robotSettings.SHOOTER_HOOD_OUT_OF_BOUNDS_SPEED);
                } else if (currentPos < robotSettings.SHOOTER_HOOD_MIN_POS) {
                    moveToPos(HoodSpecialAction.OUT_OF_BOUNDS, robotSettings.SHOOTER_HOOD_OUT_OF_BOUNDS_SPEED);
                } else if (HEIGHT_OVERRIDE.getBoolean(false)) {
                    moveToPos(HOOD_HEIGHT.getDouble(0), currentPos);
                } else {
                    if ((panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN)) {
                        if (!Robot.shooter.isShooting()) {
                            if (shooter.tryFiringBalls) {
                                //moveToPos(requiredArticulationForTargetSize(lastSeenCameraArea, robotSettings.CALIBRATED_HOOD_POSITION_ARRAY), currentPos);
                                moveToPos(interpolateBetweenTwoPositions(lastSeenCameraArea), currentPos);
                            } else {
                                moveToPos(HoodSpecialAction.AIMING, robotSettings.SHOOTER_HOOD_MAX_POS / 2);
                            }
                        }
                    } else if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN) {
                        moveToPos(HoodSpecialAction.AIMING, robotSettings.TRENCH_FRONT_HOOD_POSITION);
                    } else if (panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) {
                        moveToPos(HoodSpecialAction.AIMING, robotSettings.INITIATION_LINE_HOOD_POSITION);
                    } else if (joystickController.get(JoystickButtons.FIVE) == ButtonStatus.DOWN) {
                        moveToPos(HoodSpecialAction.MANUAL_MOVEMENT, robotSettings.SHOOTER_HOOD_CONTROL_SPEED);
                    } else if (joystickController.get(JoystickButtons.THREE) == ButtonStatus.DOWN) {
                        moveToPos(HoodSpecialAction.MANUAL_MOVEMENT, -robotSettings.SHOOTER_HOOD_CONTROL_SPEED);
                    } else {
                        if (!HOOD_OVERRIDE.getBoolean(false) && currentPos > 3 && !shooter.isShooting()) {
                            moveToPos(0, currentPos);
                        } else {
                            moveToPos(HoodSpecialAction.NOT_MOVING);
                        }
                    }
                }
                UserInterface.smartDashboardPutNumber("ArticulatedHoodAngle", interpolateBetweenTwoPositions(lastSeenCameraArea));
                break;
            default:
                throw new IllegalStateException("You can't articulate the hood without the panel.");
        }
        if (DEBUG && robotSettings.DEBUG) {
            UserInterface.smartDashboardPutNumber("Hood Pos", hoodMotor.getRotations());
            UserInterface.smartDashboardPutNumber("Moving to pos", moveTo);
            if (robotSettings.ENABLE_VISION) {
                VISION_SIZE.setNumber(turret.visionCamera.getSize());
                VISION_ESTIMATE_HEIGHT.setNumber(requiredArticulationForTargetSize(shooter.goalCamera.getSize(), robotSettings.CALIBRATED_HOOD_POSITION_ARRAY));
            }
        }
        if (robotSettings.SHOOTER_CONTROL_STYLE != Shooter.ShootingControlStyles.STANDARD && robotSettings.SHOOTER_CONTROL_STYLE != Shooter.ShootingControlStyles.STANDARD_OFFSEASON_2021 && robotSettings.SHOOTER_CONTROL_STYLE != Shooter.ShootingControlStyles.EXPERIMENTAL_OFFSEASON_2021) //TODO update the old moveto value to use enums as it's kinda clunky
            moveToPos(moveTo, currentPos);
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
        hoodMotor.resetEncoder();
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {
        hoodMotor.setBrake(true);
    }

    @Override
    public String getSubsystemName() {
        return "ShooterHood";
    }

    /**
     * This method is supposed to take in camera size and run some math to determine the optimal hood articulation to
     * fire at that target.
     *
     * @param size              The percieved size of the target
     * @param articulationArray the metadata where the array is formatted [len = number of entries][size, articulation]
     * @return the optimal articulation given passed inputs
     */
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

    /**
     * Uses {@link ButtonPanelTapedButtons nonstandard mapping} and moves the hood based on those inputs
     */
    private void moveToPosFromButtons() {
        if (panel.get(ButtonPanelTapedButtons.HOOD_POS_1) == ButtonStatus.DOWN) {
            moveTo = 0.05; //POS 1
            unTargeted = false;
        } else if (panel.get(ButtonPanelTapedButtons.HOOD_POS_2) == ButtonStatus.DOWN) {
            moveTo = 0.77; //POS 2
            unTargeted = false;
        } else if (panel.get(ButtonPanelTapedButtons.HOOD_POS_3) == ButtonStatus.DOWN) {
            moveTo = 1.05; //POS 3
            unTargeted = false;
        } else if (panel.get(ButtonPanelTapedButtons.HOOD_POS_4) == ButtonStatus.DOWN) {
            moveTo = 1.135; //POS 4
            unTargeted = false;
        } else {
            moveTo = -1;
            hoodMotor.moveAtPercent(0);
        }
    }

    /**
     * Moves the hood to a specified point. If you pass in -1 then the hood remains still. If you pass in -2 then the
     * hood is in manual movement and doesn't move. If you pass in -3 then the hood is out of bounds and doesn't move.
     *
     * @param moveTo     where to move the hood to
     * @param currentPos where the hood is now
     */
    public void moveToPos(double moveTo, double currentPos) {
        if (DEBUG && robotSettings.DEBUG) {
            UserInterface.smartDashboardPutNumber("Moving to", moveTo);
        }
        if (moveTo > robotSettings.SHOOTER_HOOD_MIN_POS && moveTo < robotSettings.SHOOTER_HOOD_MAX_POS) {
            double distanceNeededToTravel = currentPos - moveTo;
            /*
            double hoodPercent = Math.min(Math.abs(distanceNeededToTravel), 0.2);
            hoodPercent *= distanceNeededToTravel > 0 ? -1 : 1;
            //hoodMotor.moveAtPercent(hoodPercent);
             */
            isAtWantedPosition = Math.abs(distanceNeededToTravel) < 0.5;
            hoodMotor.moveAtPosition(moveTo);
            if (DEBUG) {//&& robotSettings.DEBUG) {
                UserInterface.smartDashboardPutNumber("Moving to", moveTo);
                UserInterface.smartDashboardPutNumber("Distance from target", distanceNeededToTravel);
            }
        } else {
            hoodMotor.moveAtPercent(0);
            System.out.println("You shouldn't be here. How did you get a hood position of " + moveTo + "?");
        }
    }

    /**
     * There are some special things that the hood can do, such as manual aiming, not moving at all, and being out of
     * bounds. This overloaded function allows these things to work nicely.
     *
     * @param specialAction   The {@link HoodSpecialAction special action} to perform
     * @param speedOrPosition The speed the motor should run at OR the hood position to go to
     */
    public void moveToPos(HoodSpecialAction specialAction, double speedOrPosition) {
        switch (specialAction) {
            case MANUAL_MOVEMENT:
                UserInterface.smartDashboardPutNumber("Distance from target", 0);
                if (robotSettings.DEBUG && DEBUG)
                    System.out.println("Manually moving");
                hoodMotor.moveAtPercent(speedOrPosition);
                break;
            case OUT_OF_BOUNDS:
                UserInterface.smartDashboardPutNumber("Distance from target", 0);
                if (robotSettings.DEBUG && DEBUG)
                    System.out.println("Out of bounds");
                hoodMotor.moveAtPercent(speedOrPosition);
            case AIMING:
                if (moveTo > robotSettings.SHOOTER_HOOD_MIN_POS && moveTo < robotSettings.SHOOTER_HOOD_MAX_POS) {
                    isAtWantedPosition = false;
                    hoodMotor.moveAtPosition(speedOrPosition);
                } else {
                    hoodMotor.moveAtPercent(0);
                    System.out.println("You shouldn't be here. How did you get a hood position of " + moveTo + "?");
                }
        }
    }

    /**
     * There are some special things that the hood can do, such as manual aiming, not moving at all, and being out of
     * bounds. This overloaded function allows these things to work nicely.
     *
     * @param specialAction The {@link HoodSpecialAction special action} to perform
     */
    public void moveToPos(HoodSpecialAction specialAction) {
        if (specialAction == HoodSpecialAction.NOT_MOVING) {
            UserInterface.smartDashboardPutNumber("Distance from target", 0);
            if (robotSettings.DEBUG && DEBUG)
                System.out.println("Not moving.");
        } else {
            throw new IllegalArgumentException("You must provide a speed to run the motor at.");
        }
        hoodMotor.moveAtPercent(0);
    }

    public double interpolateBetweenTwoPositions(double size) {
        double closesize = 2.1; //actually max
        double farsize = 0.68; //actually min
        double closeangle = 0;
        double farangle = 19;
        if (size > closesize) {
            return closeangle;
        } else if (size < farsize) {
            return farangle;
        } else {
            double cringemath = 26 - (((farangle - closeangle) / (closesize - farsize)) * size);
            if (cringemath < robotSettings.SHOOTER_HOOD_MIN_POS)
                return closeangle;
            else if (cringemath > robotSettings.SHOOTER_HOOD_MAX_POS) {
                return farangle;
            } else {
                return cringemath;
            }
        }
    }

    /**
     * Initialize the motors.
     */
    private void createAndInitMotors() {
        switch (robotSettings.HOOD_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                hoodMotor = new SparkMotorController(robotSettings.SHOOTER_HOOD_ID, CANSparkMaxLowLevel.MotorType.kBrushless);
                hoodMotor.setSensorToRealDistanceFactor(1);
                ((SparkMotorController) hoodMotor).setAllowedClosedLoopError(.01);
                break;
            case TALON_FX:
                hoodMotor = new TalonMotorController(robotSettings.SHOOTER_HOOD_ID);
                hoodMotor.setSensorToRealDistanceFactor(600 / robotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                break;
            default:
                throw new IllegalStateException("No such supported hood config for " + robotSettings.HOOD_MOTOR_TYPE.name());
        }
        hoodMotor.setCurrentLimit(20).setBrake(false).setInverted(robotSettings.SHOOTER_HOOD_INVERT_MOTOR).resetEncoder();
        hoodMotor.setBrake(true);
        hoodMotor.setPid(new PID(.1, 0, 0.01, 0));
        hoodMotor.resetEncoder();
    }

    public boolean autoHoodAngle() {
        double currentPos = hoodMotor.getRotations();
        if (!Robot.shooter.isValidTarget()) {
            moveTo = robotSettings.SHOOTER_HOOD_MAX_POS * 0.9;
            lastSeenCameraArea = 0;
            unTargeted = true;
        } else {
            if (unTargeted)
                lastSeenCameraArea = shooter.goalCamera.getSize();
            moveTo = requiredArticulationForTargetSize(Robot.shooter.goalCamera.getSize(), robotSettings.CALIBRATED_HOOD_POSITION_ARRAY);
            unTargeted = false;
        }
        moveToPos(Math.max(0, moveTo), currentPos);
        boolean condition = 1 >= Math.abs(currentPos - moveTo) && moveTo != robotSettings.SHOOTER_HOOD_MAX_POS * 0.9;
        if (condition)
            lastSeenCameraArea = 0;
        return condition;
    }

    public boolean autoHoodAngle(double height) {
        double currentPos = hoodMotor.getRotations();
        moveTo = height;
        unTargeted = false;
        moveToPos(Math.max(0, height - 2), currentPos);
        return 1 >= Math.abs(currentPos - (moveTo - 2));
    }
}