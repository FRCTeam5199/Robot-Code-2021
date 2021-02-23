package frc.ballstuff.shooting;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ButtonPanelController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.JoystickController;
import frc.misc.ISubsystem;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.robot.RobotSettings;
import frc.telemetry.RobotTelemetry;
import frc.vision.GoalPhoton;
import frc.vision.IVision;

/**
 * Turret refers to the shooty thing that spinny spinny in the yaw direction
 */
public class Turret implements ISubsystem {
    //Still required for debug prints?
    private final ShuffleboardTab tab = Shuffleboard.getTab("Turret");
    /*private final NetworkTableEntry fMult = tab.add("F Multiplier", 0).getEntry(),
            pos = tab.add("Position", 0).getEntry(),
            arbDriveMult = tab.add("drive omega mult", -0.25).getEntry(),
            angleOffset = tab.add("angle offset", -2.9).getEntry(),
            rotSpeed = tab.add("rotationSpeed", 0).getEntry();*/
    public boolean track, atTarget;
    private BaseController joy, panel;
    private AbstractMotorController motor;
    private RobotTelemetry guidance;
    private IVision goalPhoton;
    private int scanDirection = -1;

    public Turret() {
        addToMetaList();
        init();
    }

    /**
     * Creates a plethora of new objects
     */
    @Override
    public void init() {
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD:
                joy = new JoystickController(RobotSettings.FLIGHT_STICK_USB_SLOT);
                panel = new ButtonPanelController(RobotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joy = new BopItBasicController(1);
                break;
        }
        if (RobotSettings.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
            goalPhoton.init();
        }
        motor = new SparkMotorController(RobotSettings.TURRET_YAW);
        motor.setSensorToRevolutionFactor((RobotSettings.TURRET_SPROCKET_SIZE * RobotSettings.TURRET_GEAR_RATIO * Math.PI / 30));
        motor.setInverted(false);
        motor.setPid(RobotSettings.TURRET_PID);
        motor.setBrake(true);
        setBrake(true);
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {
        updateGeneric();
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
        if (RobotSettings.DEBUG) {
            System.out.println("Turret degrees:" + turretDegrees());
        }
        double omegaSetpoint = 0;
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD:
                if (RobotSettings.ENABLE_VISION) {
                    if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN) {
                        if (RobotSettings.DEBUG) {
                            System.out.println("I'm looking. Target is valid? " + goalPhoton.hasValidTarget());
                        }
                        if (goalPhoton.hasValidTarget()) {
                            omegaSetpoint = -goalPhoton.getAngle() / 30;
                        } else {
                            omegaSetpoint = 0;//scan();
                        }
                    }
                }
                //If holding down the manual rotation button, then rotate the turret based on the Z rotation of the joystick.
                if (joy.get(ControllerEnums.JoystickButtons.TWO) == ControllerEnums.ButtonStatus.DOWN) {
                    if (RobotSettings.DEBUG) {
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

        if (isSafe()) {
            //System.out.println("SAFE");
            rotateTurret(omegaSetpoint);
            if (RobotSettings.DEBUG) {
                System.out.println("Attempting to rotate the POS at" + omegaSetpoint);
            }
        } else {
            //System.out.println("Unsafe " + turretDegrees());
            if (turretDegrees() > 270) {
                rotateTurret(0.25);
            } else if (turretDegrees() < 0) {
                rotateTurret(-0.25);
            } else {
                rotateTurret(0);
            }
        }
        if (RobotSettings.DEBUG) {
            //SmartDashboard.putNumber("Turret DB Omega offset", -driveOmega * arbDriveMult.getDouble(-0.28));
            SmartDashboard.putNumber("Turret Omega", omegaSetpoint);
            SmartDashboard.putNumber("Turret Position", turretDegrees());
            SmartDashboard.putNumber("Turret Speed", motor.getRotations());
            SmartDashboard.putBoolean("Turret Safe", isSafe());
            if (RobotSettings.ENABLE_IMU && guidance != null) {
                //no warranties
                SmartDashboard.putNumber("YawWrap", guidance.imu.yawWraparoundAhead() - 360);
                SmartDashboard.putNumber("Turret North", limitAngle(235 + guidance.imu.yawWraparoundAhead() - 360));
            }
            SmartDashboard.putBoolean("Turret At Target", atTarget);
            SmartDashboard.putBoolean("Turret Track", track);
            SmartDashboard.putBoolean("Turret at Target", atTarget);
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
        if (turretDegrees() >= 260) {
            scanDirection = 1;
        } else if (turretDegrees() <= 100) {
            scanDirection = -1;
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
        return turretDeg <= 270 && turretDeg >= 0;
    }

    /**
     * Rotate the turret at a certain rad/sec
     *
     * @param speed - % max speed to rotate at (too fast and the gremlins gonna eat u)
     */
    private void rotateTurret(double speed) {
        if (RobotSettings.DEBUG) {
            System.out.println("Set to " + (speed * (RobotSettings.TURRET_SPROCKET_SIZE * RobotSettings.TURRET_GEAR_RATIO * Math.PI / 30)) + " from " + speed);
        }
        //Dont overcook it pls
        motor.moveAtPercent(speed * 0.1);
    }

    /**
     * If the angle is greater than the acceptable max, or less than the acceptable min, returns the nearest bound, else
     * bounces input
     *
     * @param angle the current angle of the turret
     * @return angle at the minimum or maximum angle
     */
    private double limitAngle(double angle) {
        return Math.max(Math.min(angle, RobotSettings.TURRET_MAX_POS), RobotSettings.TURRET_MIN_POS);
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