package frc.ballstuff.shooting;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
import frc.controllers.ButtonPanel;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.JoystickController;
import frc.telemetry.RobotTelemetry;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.vision.GoalPhoton;

/**
 * Turret refers to the shooty thing that spinny spinny in the yaw direction
 */
public class Turret implements ISubsystem {
    private final ShuffleboardTab tab = Shuffleboard.getTab("Turret");
    private final NetworkTableEntry fMult = tab.add("F Multiplier", 0).getEntry();
    private final NetworkTableEntry pos = tab.add("Position", 0).getEntry();
    private final NetworkTableEntry arbDriveMult = tab.add("drive omega mult", -0.25).getEntry();
    private final NetworkTableEntry angleOffset = tab.add("angle offset", -2.9).getEntry();
    private final NetworkTableEntry rotSpeed = tab.add("rotationSpeed", 0).getEntry();
    public boolean track;
    public boolean atTarget = false;
    public boolean chasingTarget = false;
    private BaseController joy;
    private BaseController panel;
    private CANSparkMax motor;
    private CANEncoder encoder;
    private CANPIDController controller;
    private PIDController positionControl;
    private RobotTelemetry guidance;
    private GoalPhoton goalPhoton;
    private int scanDirection = -1;

    public Turret() {
        init();
    }

    /**
     * Creates a plethora of new objects
     */
    @Override
    public void init() {
        joy = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
        if (RobotToggles.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
            goalPhoton.init();
        }
        motor = new CANSparkMax(RobotMap.TURRET_YAW, MotorType.kBrushless);
        encoder = motor.getEncoder();
        panel = new ButtonPanel(RobotNumbers.BUTTON_PANEL_SLOT);
        encoder.setPositionConversionFactor(360 / 77.7);
        encoder.setPositionConversionFactor(360 / (RobotNumbers.TURRET_SPROCKET_SIZE * RobotNumbers.TURRET_GEAR_RATIO));
        controller = motor.getPIDController();
        positionControl = new PIDController(0, 0, 0);
        motor.setInverted(false);
        encoder.setPosition(270);
        setMotorPID(0.5, 0, 0);
        setPosPID(0.02, 0, 0);
        motor.setIdleMode(IdleMode.kBrake);
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
        if (RobotToggles.DEBUG) {
            System.out.println("Turret degrees:" + turretDegrees());
        }
        //!!!!! THE TURRET ZERO IS THE PHYSICAL STOP CLOSEST TO THE GOAL

        double omegaSetpoint = 0;
        if (RobotToggles.ENABLE_VISION) {
            if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN) { //Check if the Target button is held down
                if (RobotToggles.DEBUG) {
                    System.out.println("I'm looking. Target is valid? " + goalPhoton.hasValidTarget());
                }
                if (goalPhoton.hasValidTarget()) {
                    omegaSetpoint = -goalPhoton.getAngle() / 30;
                } else {
                    omegaSetpoint = scan();
                }
            }
        }
        //If holding down the manual rotation button, then rotate the turret based on the Z rotation of the joystick.
        if (joy.get(ControllerEnums.JoystickButtons.TWO) == ControllerEnums.ButtonStatus.DOWN) {
            if (RobotToggles.DEBUG) {
                System.out.println("Joystick is at " + joy.get(ControllerEnums.JoystickAxis.Z_ROTATE));
            }
            omegaSetpoint = joy.get(ControllerEnums.JoystickAxis.Z_ROTATE) * -2;
        }

        if (isSafe()) {
            rotateTurret(omegaSetpoint);
            if (RobotToggles.DEBUG) {
                System.out.println("Attempting to rotate the POS at" + omegaSetpoint);
            }
        } else {
            if (turretDegrees() > 270) {
                rotateTurret(0.25);
            } else if (turretDegrees() < 100) {
                rotateTurret(-0.25);
            } else {
                rotateTurret(0);
            }
        }
        if (RobotToggles.DEBUG) {
            //SmartDashboard.putNumber("Turret DB Omega offset", -driveOmega * arbDriveMult.getDouble(-0.28));
            SmartDashboard.putNumber("Turret Omega", omegaSetpoint);
            SmartDashboard.putNumber("Turret Position", turretDegrees());
            SmartDashboard.putNumber("Turret Speed", encoder.getVelocity());
            SmartDashboard.putNumber("Turret FF", controller.getFF());
            SmartDashboard.putBoolean("Turret Safe", isSafe());
            if (RobotToggles.ENABLE_IMU && guidance != null) {
                //no warranties
                SmartDashboard.putNumber("YawWrap", guidance.imu.yawWraparoundAhead() - 360);
                SmartDashboard.putNumber("Turret North", limitAngle(235 + guidance.imu.yawWraparoundAhead() - 360));
            }
            SmartDashboard.putBoolean("Turret At Target", atTarget);
            SmartDashboard.putBoolean("Turret Track", track);
            SmartDashboard.putBoolean("Turret at Target", atTarget);
        }
    }

    /**
     * @return position of turret in degrees
     */
    private double turretDegrees() {
        return 270 - encoder.getPosition();
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
        return turretDeg <= 270 && turretDeg >= 100;
    }

    /**
     * Rotate the turret at a certain rad/sec
     *
     * @param speed - rad/sec to rotate the turret at
     */
    private void rotateTurret(double speed) {
        //1 Radians Per Second to Revolutions Per Minute = 9.5493 RPM
        double turretRPM = speed * 9.5493;
        double motorRPM = turretRPM * (RobotNumbers.TURRET_SPROCKET_SIZE / RobotNumbers.MOTOR_SPROCKET_SIZE) * RobotNumbers.TURRET_GEAR_RATIO;
        //controller.setReference(motorRPM, ControlType.kVelocity);
        double deadbandComp;
        if (track) { //make if true
            if (motorRPM < 0) { // make if <usual rpm
                deadbandComp = 0.01;
            } else if (motorRPM > 0) { //make if >usual rpm
                deadbandComp = -0.01;
            } else {
                deadbandComp = 0;
            }
        } else {
            deadbandComp = 0;
            //motorRPM = 0;
        }
        if (RobotToggles.DEBUG) {
            System.out.println("Set to " + (motorRPM / 5700 - deadbandComp) + " from " + speed);
        }
        motor.set(motorRPM / 5700 - deadbandComp);
        SmartDashboard.putNumber("Motor RPM out", motorRPM);
        SmartDashboard.putNumber("Turret RPM out", turretRPM);
        SmartDashboard.putNumber("Deadband Add", deadbandComp);
        SmartDashboard.putNumber("Turret out", motorRPM / 5700 - deadbandComp);
    }

    /**
     * If the angle is greater than the acceptable max, or less than the acceptable min, returns the nearest bound, else bounces input
     *
     * @param angle the current angle of the turret
     * @return angle at the minimum or maximum angle
     */
    private double limitAngle(double angle) {
        return Math.max(Math.min(angle, RobotNumbers.TURRET_MAX_POS), RobotNumbers.TURRET_MIN_POS);
    }

    private void setMotorPID(double P, double I, double D) {
        controller.setP(P);
        controller.setI(I);
        controller.setD(D);
    }

    private void setPosPID(double P, double I, double D) {
        positionControl.setP(P);
        positionControl.setI(I);
        positionControl.setD(D);
    }

    /**
     * Tells the motor if it should coast or slam da brakes
     *
     * @param brake should i brake when off? (Y/N)
     */
    public void setBrake(boolean brake) {
        if (brake) {
            motor.setIdleMode(IdleMode.kBrake);
        } else {
            motor.setIdleMode(IdleMode.kCoast);
        }
    }

    /**
     * If there is a telem object, set it here
     *
     * @param telem the RobotTelemtry object in use by the drivetrian
     */
    public void setTelemetry(RobotTelemetry telem) {
        guidance = telem;
    }

    /**
     * every time we enter teleop, reset encoder
     */
    public void teleopInit() {
        encoder = motor.getEncoder();
        encoder.setPosition(0);
    }

    /**
     * When we enter disabled, unlock the turret to be moved freely
     */
    public void disabledInit() {
        motor.setIdleMode(IdleMode.kCoast);
    }
}