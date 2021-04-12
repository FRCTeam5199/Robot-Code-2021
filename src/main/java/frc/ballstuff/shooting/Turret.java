package frc.ballstuff.shooting;

import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ButtonPanelController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.JoystickController;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.telemetry.RobotTelemetry;
import frc.vision.camera.IVision;
import frc.vision.camera.VisionLEDMode;

import static frc.robot.Robot.robotSettings;

/**
 * Turret refers to the shooty thing that spinny spinny in the yaw direction
 */
public class Turret implements ISubsystem {
    private static final boolean DEBUG = false;
    private BaseController joy, panel;
    private AbstractMotorController motor;
    private RobotTelemetry guidance;
    private IVision visionCamera;
    private int scanDirection = -1;

    public Turret() {
        addToMetaList();
        init();
    }

    /**
     * Creates a plethora of new objects
     *
     * @throws UnsupportedOperationException if the configuration is not supported
     * @see frc.robot.robotconfigs.DefaultConfig
     */
    @Override
    public void init() throws UnsupportedOperationException {
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD:
                joy = JoystickController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT);
                panel = ButtonPanelController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joy = BopItBasicController.createOrGet(1);
                break;
            default:
                throw new UnsupportedOperationException("This control style is not supported here in TurretLand inc.");
        }

        switch (robotSettings.TURRET_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                motor = new SparkMotorController(robotSettings.TURRET_YAW_ID);
                motor.setSensorToRealDistanceFactor(robotSettings.TURRET_SPROCKET_SIZE * robotSettings.TURRET_GEAR_RATIO * Math.PI / 30);
                break;
            case TALON_FX:
                motor = new TalonMotorController(robotSettings.TURRET_YAW_ID);
                //TODO make a setting maybe
                motor.setSensorToRealDistanceFactor(robotSettings.TURRET_SPROCKET_SIZE * robotSettings.TURRET_GEAR_RATIO * Math.PI / 30 * 600 / 2048);
                break;
            default:
                throw new UnsupportedOperationException("This motor is not supported here in TurretLand inc.");
        }
        if (robotSettings.ENABLE_VISION) {
            visionCamera = IVision.manufactureGoalCamera(robotSettings.GOAL_CAMERA_TYPE);
        }
        motor.setInverted(false).setPid(robotSettings.TURRET_PID).setBrake(true);

        setBrake(true);
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
     * prevents the turret from rotating too far, does debug
     */
    @Override
    public void updateGeneric() {
        if (motor.failureFlag)
            MotorDisconnectedIssue.reportIssue(this, robotSettings.TURRET_YAW_ID, motor.getSuggestedFix());
        else
            MotorDisconnectedIssue.resolveIssue(this, robotSettings.TURRET_YAW_ID);
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Turret degrees:" + turretDegrees());
        }
        double omegaSetpoint = 0;
        double camoffset = 0;
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
                camoffset = -3;
                break;
            case SPEED_2021:
                camoffset = -0.75;
                break;
            default:
                camoffset = 0;
                break;
        }
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD:
                if (robotSettings.ENABLE_VISION) {
                    if (panel.get(ButtonPanelButtons.BUDDY_CLIMB) == ButtonStatus.DOWN) {
                        visionCamera.setLedMode(VisionLEDMode.BLINK); //haha suffer
                    } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && !Robot.shooter.isShooting()) {
                        if (robotSettings.DEBUG && DEBUG) {
                            System.out.println("I'm looking. Target is valid? " + visionCamera.hasValidTarget());
                        }
                        if (robotSettings.ENABLE_HOOD_ARTICULATION)
                            Robot.articulatedHood.unTargeted = true;
                        if (visionCamera.hasValidTarget()) {
                            double angle = -visionCamera.getAngle() + camoffset;
                            if (angle > 0.005) {
                                omegaSetpoint = 0.3;
                            } else if (angle < -0.005) {
                                omegaSetpoint = -0.3;
                            }
                            omegaSetpoint *= Math.min(Math.abs(angle * 2), 1);
                        } else {
                            omegaSetpoint = scan();
                        }
                        visionCamera.setLedMode(VisionLEDMode.ON); //If targeting, then use the LL
                    } else {
                        visionCamera.setLedMode(VisionLEDMode.OFF); //If not targeting, then stop using the LL
                    }
                }
                //If holding down the manual rotation button, then rotate the turret based on the Z rotation of the joystick.
                if (joy.get(ControllerEnums.JoystickButtons.TWO) == ControllerEnums.ButtonStatus.DOWN) {
                    if (robotSettings.DEBUG && DEBUG) {
                        System.out.println("Joystick is at " + joy.get(ControllerEnums.JoystickAxis.Z_ROTATE));
                    }
                    omegaSetpoint = joy.get(ControllerEnums.JoystickAxis.Z_ROTATE) * -2;
                }
                break;
            case BOP_IT:
                if (joy.get(ControllerEnums.BopItButtons.TWISTIT) == ButtonStatus.DOWN)
                    omegaSetpoint = scan();
                break;
        }

        if (isSafe() && !Robot.shooter.isShooting()) {
            rotateTurret(omegaSetpoint);
            if (robotSettings.DEBUG && DEBUG) {
                System.out.println("Attempting to rotate the POS at" + omegaSetpoint);
            }
        } else {
            if (turretDegrees() > robotSettings.TURRET_MAX_POS) {
                rotateTurret(-1);
            } else if (turretDegrees() < robotSettings.TURRET_MIN_POS) {
                rotateTurret(1);
            } else {
                rotateTurret(0);
            }
        }
        if (robotSettings.DEBUG && DEBUG) {
            //UserInterface.putNumber("Turret DB Omega offset", -driveOmega * arbDriveMult.getDouble(-0.28));
            UserInterface.smartDashboardPutNumber("Turret Omega", omegaSetpoint);
            UserInterface.smartDashboardPutNumber("Turret Position", turretDegrees());
            UserInterface.smartDashboardPutNumber("Turret Speed", motor.getRotations());
            UserInterface.smartDashboardPutBoolean("Turret Safe", isSafe());
            if (robotSettings.ENABLE_IMU && guidance != null) {
                //no warranties
                UserInterface.smartDashboardPutNumber("YawWrap", guidance.imu.yawWraparoundAhead() - 360);
                UserInterface.smartDashboardPutNumber("Turret North", limitAngle(235 + guidance.imu.yawWraparoundAhead() - 360));
            }
        }
    }

    @Override
    public void initTest() {

    }

    /**
     * every time we enter teleop, reset encoder
     */
    @Override
    public void initTeleop() {
        motor.resetEncoder();
        motor.setBrake(true);
    }

    @Override
    public void initAuton() {

    }

    /**
     * When we enter disabled, unlock the turret to be moved freely
     */
    @Override
    public void initDisabled() {
        motor.setBrake(false);
    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "Turret";
    }

    /**
     * @return position of turret in degrees
     */
    private double turretDegrees() {
        return motor.getRotations();
    }

    /**
     * Scan the turret back and forth to find a target.
     *
     * @return an integer to determine the direction of turret scan
     */
    private double scan() {
        if (turretDegrees() >= robotSettings.TURRET_MAX_POS - 40) {
            scanDirection = -1;
        } else if (turretDegrees() <= robotSettings.TURRET_MAX_POS + 40) {
            scanDirection = 1;
        }
        return scanDirection;
    }

    /**
     * Is the shooter overrotated?
     *
     * @return yes the shooter is overrotated or no the shooter is not overrotated
     */
    private boolean isSafe() {
        double turretDeg = turretDegrees();
        return turretDeg <= robotSettings.TURRET_MAX_POS && turretDeg >= robotSettings.TURRET_MIN_POS;
    }

    /**
     * Rotate the turret at a certain rad/sec
     *
     * @param speed - % max speed to rotate at (too fast and the gremlins gonna eat u)
     */
    private void rotateTurret(double speed) {
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Set to " + (speed * (robotSettings.TURRET_SPROCKET_SIZE * robotSettings.TURRET_GEAR_RATIO * Math.PI / 30)) + " from " + speed);
        }
        //Dont overcook it pls
        //if (!Robot.shooter.isShooting) {
        motor.moveAtPercent(speed * 0.15);
        //}
    }

    /**
     * If the angle is greater than the acceptable max, or less than the acceptable min, returns the nearest bound, else
     * bounces input
     *
     * @param angle the current angle of the turret
     * @return angle at the minimum or maximum angle
     */
    private double limitAngle(double angle) {
        return Math.max(Math.min(angle, robotSettings.TURRET_MAX_POS), robotSettings.TURRET_MIN_POS);
    }

    /**
     * Tells the motor if it should coast or slam da brakes
     *
     * @param brake should i brake when off? (Y/N)
     */
    public void setBrake(boolean brake) {
        motor.setBrake(brake);
    }

    /**
     * If there is a telem object, set it here
     *
     * @param telem the RobotTelemtry object in use by the drivetrian
     */
    public void setTelemetry(RobotTelemetry telem) {
        guidance = telem;
    }
}