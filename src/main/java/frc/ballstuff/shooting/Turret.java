package frc.ballstuff.shooting;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.telemetry.AbstractRobotTelemetry;
import frc.vision.camera.IVision;

import static frc.robot.Robot.*;

/**
 * Turret refers to the shooty thing that spinny spinny in the yaw direction
 */
public class Turret implements ISubsystem {
    private static final boolean DEBUG = false;
    private final NetworkTableEntry isMorganne = UserInterface.MORGANNE_MODE.getEntry();
    private final PIDController HEADING_PID;
    public AbstractMotorController turretMotor;
    public IVision visionCamera;
    private BaseController joy, panel;
    private AbstractRobotTelemetry guidance;
    private int scanDirection = -1;

    public Turret() {
        addToMetaList();
        init();
        HEADING_PID = new PIDController(robotSettings.TURRET_HEADING_PID.P, robotSettings.TURRET_HEADING_PID.I, robotSettings.TURRET_HEADING_PID.D);
    }

    /**
     * Creates a plethora of new objects
     *
     * @throws UnsupportedOperationException if the configuration is not supported
     * @see frc.robot.robotconfigs.DefaultConfig
     */
    @Override
    public void init() throws UnsupportedOperationException {
        createControllers();

        switch (robotSettings.TURRET_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                turretMotor = new SparkMotorController(robotSettings.TURRET_YAW_ID);
                turretMotor.setSensorToRealDistanceFactor(robotSettings.TURRET_SPROCKET_SIZE * robotSettings.TURRET_GEAR_RATIO * Math.PI / 30.0);
                break;
            case TALON_FX:
                turretMotor = new TalonMotorController(robotSettings.TURRET_YAW_ID);
                turretMotor.setSensorToRealDistanceFactor(robotSettings.TURRET_SPROCKET_SIZE * robotSettings.TURRET_GEAR_RATIO * Math.PI / 30 * 600.0 / 2048.0);
                break;
            default:
                throw new UnsupportedOperationException("This motor is not supported here in TurretLand inc.");
        }
        if (robotSettings.ENABLE_VISION) {
            visionCamera = IVision.manufactureGoalCamera(robotSettings.GOAL_CAMERA_TYPE);
        }
        turretMotor.setInverted(false).setPid(robotSettings.TURRET_PID).setBrake(true);

        setBrake(true);
        turretMotor.resetEncoder();
    }

    private void createControllers() {
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case EXPERIMENTAL_OFFSEASON_2021:
            case STANDARD_OFFSEASON_2021:
            case STANDARD:
                joy = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
                panel = BaseController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT, BaseController.Controllers.BUTTON_PANEL_CONTROLLER);
                break;
            case BOP_IT:
                joy = BaseController.createOrGet(3, BaseController.Controllers.BOP_IT_CONTROLLER);
                break;
            case DRUM_TIME:
                joy = BaseController.createOrGet(5, BaseController.Controllers.DRUM_CONTROLLER);
                break;
            case WII:
                joy = BaseController.createOrGet(4, BaseController.Controllers.WII_CONTROLLER);
                break;
            case GUITAR:
                joy = BaseController.createOrGet(6, BaseController.Controllers.SIX_BUTTON_GUITAR_CONTROLLER);
            default:
                throw new UnsupportedOperationException("This control style is not supported here in TurretLand inc.");
                //TODO add Xbox and (standalone) Flightstick
        }
    }

    /**
     * Tells the motor if it should coast or slam da brakes
     *
     * @param brake should i brake when off? (Y/N)
     */
    public void setBrake(boolean brake) {
        turretMotor.setBrake(brake);
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return turretMotor.isFailed() ? SubsystemStatus.FAILED : SubsystemStatus.NOMINAL;
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
        if (Shooter.ShootingControlStyles.getSendableChooser().getSelected() != null && robotSettings.SHOOTER_CONTROL_STYLE != Shooter.ShootingControlStyles.getSendableChooser().getSelected()) {
            robotSettings.SHOOTER_CONTROL_STYLE = Shooter.ShootingControlStyles.getSendableChooser().getSelected();
            if (Robot.shooter != null)
                Robot.shooter.createControllers();
            createControllers();
        }
        MotorDisconnectedIssue.handleIssue(this, turretMotor);
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Turret degrees:" + turretDegrees());
        }
        double omegaSetpoint = 0;
        double camoffset = 0;
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD_OFFSEASON_2021:
                if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN) { //trench
                    camoffset = 0;//-4;//-2;
                } else if (panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) { //init
                    camoffset = 0;
                } else {
                    camoffset = -4;
                }
                break;
            case ACCURACY_2021:
                camoffset = -3;
                break;
            case SPEED_2021:
                camoffset = -10;
                break;
            default:
                camoffset = 0;
                break;
        }
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case STANDARD: {
                if (robotSettings.ENABLE_VISION) {
                    if (panel.get(ButtonPanelButtons.BUDDY_CLIMB) == ButtonStatus.DOWN) {
                        visionCamera.setLedMode(IVision.VisionLEDMode.BLINK); //haha suffer
                    } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && !shooter.isShooting()) {
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
                            } //WHAT IS THIS DUDES WHERES THE PID
                            omegaSetpoint *= Math.min(Math.abs(angle * 1.5), 1);
                        } else {
                            omegaSetpoint = scan();
                        }
                        visionCamera.setLedMode(IVision.VisionLEDMode.ON); //If targeting, then use the LL
                    } else {
                        visionCamera.setLedMode(IVision.VisionLEDMode.OFF); //If not targeting, then stop using the LL
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
            }
            case EXPERIMENTAL_OFFSEASON_2021:
            case STANDARD_OFFSEASON_2021: {
                if (robotSettings.ENABLE_VISION) {
                    if (panel.get(ButtonPanelButtons.BUDDY_CLIMB) == ButtonStatus.DOWN) {
                        //visionCamera.setLedMode(IVision.VisionLEDMode.BLINK); //haha suffer
                        visionCamera.setLedMode(IVision.VisionLEDMode.ON);
                    } else if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN || panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) {
                        if (robotSettings.DEBUG && DEBUG) {
                            System.out.println("I'm looking. Target is valid? " + visionCamera.hasValidTarget());
                        }
                        if (robotSettings.ENABLE_HOOD_ARTICULATION)
                            Robot.articulatedHood.unTargeted = true;
                        if (visionCamera.hasValidTarget()) {
                            double angle = -visionCamera.getAngle() + camoffset;
                            omegaSetpoint = -HEADING_PID.calculate(angle);
                        } else {
                            omegaSetpoint = scan();
                        }
                        visionCamera.setLedMode(IVision.VisionLEDMode.ON); //If targeting, then use the LL
                    } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && !shooter.isShooting() && !shooter.tryFiringBalls) {
                        if (robotSettings.DEBUG && DEBUG) {
                            System.out.println("I'm looking. Target is valid? " + visionCamera.hasValidTarget());
                        }
                        if (robotSettings.ENABLE_HOOD_ARTICULATION)
                            Robot.articulatedHood.unTargeted = true;
                        if (visionCamera.hasValidTarget()) {
                            double angle = -visionCamera.getAngle() + camoffset;
                            omegaSetpoint = -HEADING_PID.calculate(angle);
                        } else {
                            omegaSetpoint = scan();
                        }
                        visionCamera.setLedMode(IVision.VisionLEDMode.ON); //If targeting, then use the LL
                    } else {
                        visionCamera.setLedMode(IVision.VisionLEDMode.OFF); //If not targeting, then stop using the LL
                    }
                }
                //If holding down the manual rotation button, then rotate the turret based on the Z rotation of the joystick.
                if (joy.get(ControllerEnums.JoystickButtons.TWO) == ControllerEnums.ButtonStatus.DOWN && !shooter.tryFiringBalls) {
                    if (robotSettings.DEBUG && DEBUG) {
                        System.out.println("Joystick is at " + joy.get(ControllerEnums.JoystickAxis.Z_ROTATE));
                    }
                    omegaSetpoint = joy.get(ControllerEnums.JoystickAxis.Z_ROTATE) * (isMorganne.getBoolean(true) ? -1.25 : -2);
                }
                break;
            }
            case BOP_IT:
                //System.out.println("Shooting bop it");
                if (joy.get(ControllerEnums.BopItButtons.TWISTIT) == ButtonStatus.DOWN)
                    omegaSetpoint = scan();
                break;
            case DRUM_TIME: {
                if (joy.get(ControllerEnums.DrumButton.PEDAL) == ButtonStatus.DOWN)
                    omegaSetpoint = scan();
                break;
            }
            case WII: {
                double rot = joy.get(ControllerEnums.WiiAxis.LEFT_RIGHT_NUMBERPAD);
                if (Math.abs(rot) >= 0.1) {
                    omegaSetpoint = rot;
                }
                break;
            }
            case GUITAR: {
                if (joy.get(ControllerEnums.SixKeyGuitarButtons.ONE) == ButtonStatus.DOWN) {
                    omegaSetpoint = 1;
                } else if (joy.get(ControllerEnums.SixKeyGuitarButtons.THREE) == ButtonStatus.DOWN) {
                    omegaSetpoint = -1;
                }
                break;
            }
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
            UserInterface.smartDashboardPutNumber("Turret Speed", turretMotor.getRotations());
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
        //turretMotor.resetEncoder();
        turretMotor.setBrake(true);
    }

    @Override
    public void initAuton() {
        turretMotor.resetEncoder();
    }

    /**
     * When we enter disabled, unlock the turret to be moved freely
     */
    @Override
    public void initDisabled() {
        turretMotor.setBrake(false);
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
        return turretMotor.getRotations();
    }

    /**
     * Scan the turret back and forth to find a target.
     *
     * @return an integer to determine the direction of turret scan
     */
    private double scan() {
        if (scanDirection == 1 && turretDegrees() >= robotSettings.TURRET_MAX_POS - 40) {
            scanDirection = -1;
        }
        if (scanDirection == -1 && turretDegrees() <= robotSettings.TURRET_MIN_POS + 40) {
            scanDirection = 1;
        }

        if (joy.get(ControllerEnums.JoystickButtons.ONE) == ButtonStatus.UP) {
            return scanDirection;
        } else {
            return 0;
        }
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
        turretMotor.moveAtPercent(speed * 0.15);
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

    public boolean resetShooter() {
        turretMotor.moveAtPosition(1);
        articulatedHood.moveToPos(0.0, articulatedHood.hoodMotor.getRotations());
        boolean condition = (Math.abs(turretDegrees()) <= 0.1 && Math.abs(articulatedHood.hoodMotor.getRotations()) <= 0.1);
        if (condition) {
            turretMotor.moveAtPercent(0);
        }
        return condition;
    }

    public boolean aimAtTarget() {
        return aimAtTarget(0);
    }

    public boolean aimAtTarget(double camoffset) {
        double omegaSetpoint = 0;
        double angle = 0;
        if (robotSettings.ENABLE_VISION)
            visionCamera.setLedMode(IVision.VisionLEDMode.ON); //If targeting, then use the LL
        if (robotSettings.ENABLE_HOOD_ARTICULATION)
            articulatedHood.autoHoodAngle();
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("I'm looking. Target is valid? " + visionCamera.hasValidTarget());
        }
        if (robotSettings.ENABLE_HOOD_ARTICULATION)
            Robot.articulatedHood.unTargeted = true;
        if (visionCamera.hasValidTarget()) {
            angle = -visionCamera.getAngle() + camoffset;
            omegaSetpoint = -HEADING_PID.calculate(angle);
        } else {
            omegaSetpoint = scan();
        }
        boolean criteria;
        if (robotSettings.ENABLE_HOOD_ARTICULATION) {
            criteria = Math.abs(angle) < 1.5 && articulatedHood.autoHoodAngle() && shooter.isValidTarget();
            UserInterface.smartDashboardPutBoolean("AutoHoodSet", articulatedHood.autoHoodAngle());
            UserInterface.smartDashboardPutBoolean("Valid Target", shooter.isValidTarget());
            UserInterface.smartDashboardPutNumber("Angle", angle);
        }
        else {
            criteria = Math.abs(angle) < 1.5;
        }

        if (criteria) {
            visionCamera.setLedMode(IVision.VisionLEDMode.OFF);
            rotateTurret(0);
        }

        if (isSafe() && !Robot.shooter.isShooting()) {
            if (!criteria)
                rotateTurret(omegaSetpoint);
            if (robotSettings.DEBUG && DEBUG) {
                System.out.println("Attempting to rotate the POS at" + omegaSetpoint);
            }
        } else {
            if (!criteria) {
                if (turretDegrees() > robotSettings.TURRET_MAX_POS) {
                    rotateTurret(-1);
                } else if (turretDegrees() < robotSettings.TURRET_MIN_POS) {
                    rotateTurret(1);
                } else {
                    rotateTurret(0);
                }
            }
        }
        return criteria;
    }

    public boolean aimAtTarget(double camoffset, double height) {
        double omegaSetpoint = 0;
        double angle = 0;
        if (robotSettings.ENABLE_VISION)
            visionCamera.setLedMode(IVision.VisionLEDMode.ON); //If targeting, then use the LL
        if (robotSettings.ENABLE_HOOD_ARTICULATION)
            articulatedHood.autoHoodAngle();
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("I'm looking. Target is valid? " + visionCamera.hasValidTarget());
        }
        if (robotSettings.ENABLE_HOOD_ARTICULATION)
            Robot.articulatedHood.unTargeted = true;
        if (visionCamera.hasValidTarget()) {
            angle = -visionCamera.getAngle() + camoffset;
            omegaSetpoint = -HEADING_PID.calculate(angle);
        } else {
            omegaSetpoint = scan();
        }
        boolean criteria;
        if (robotSettings.ENABLE_HOOD_ARTICULATION) {
            boolean check1 = articulatedHood.autoHoodAngle(height);
            criteria = Math.abs(angle) < 1.8 && check1 && shooter.isValidTarget();
            UserInterface.smartDashboardPutBoolean("AutoHoodSet", check1);
            UserInterface.smartDashboardPutBoolean("Valid Target", shooter.isValidTarget());
            UserInterface.smartDashboardPutNumber("Angle", angle);
        }
        else {
            criteria = Math.abs(angle) < 1.8;
        }
        if (criteria) {
            visionCamera.setLedMode(IVision.VisionLEDMode.OFF);
            rotateTurret(0);
        }

        if (isSafe() && !Robot.shooter.isShooting()) {
            if (!criteria)
                rotateTurret(omegaSetpoint);
            if (robotSettings.DEBUG && DEBUG) {
                System.out.println("Attempting to rotate the POS at" + omegaSetpoint);
            }
        } else {
            if (!criteria) {
                if (turretDegrees() > robotSettings.TURRET_MAX_POS) {
                    rotateTurret(-1);
                } else if (turretDegrees() < robotSettings.TURRET_MIN_POS) {
                    rotateTurret(1);
                } else {
                    rotateTurret(0);
                }
            }
        }
        return criteria;
    }

    /**
     * If there is a telem object, set it here
     *
     * @param telem the RobotTelemtry object in use by the drivetrian
     */
    public void setTelemetry(AbstractRobotTelemetry telem) {
        guidance = telem;
    }

    public void updateControl() {
        createControllers();
    }
}