package frc.ballstuff.shooting;

import com.ctre.phoenix.sensors.PigeonIMU;
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
import frc.controllers.ButtonPanel;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.JoystickController;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.vision.GoalPhoton;

public class Turret implements ISubsystem {
    public boolean track;
    public boolean atTarget = false;
    public boolean chasingTarget = false;
    private double sprocketRatio = 1; //replace 1 with ratio between motor and turret sprocket(turret/motor)
    private double gearingRatio = 1; //replace with whatever number
    private JoystickController joy;
    private ButtonPanel panel;
    private CANSparkMax motor;
    private CANEncoder encoder;
    private CANPIDController controller;
    private PIDController positionControl;
    private PigeonIMU pigeon;
    //private PIDController control;
    private double driveOmega;
    private double turretOmega;
    private double robotYaw;
    private double startYaw;
    private double[] ypr = new double[3];
    private double[] startypr;
    private double fMultiplier;
    private double targetPosition;
    private ShuffleboardTab tab = Shuffleboard.getTab("Turret");
    private NetworkTableEntry fMult = tab.add("F Multiplier", 0).getEntry();
    private NetworkTableEntry pos = tab.add("Position", 0).getEntry();
    // private NetworkTableEntry deadbandAdd = tab.add("deadband constant", 0.01).getEntry();
    // private NetworkTableEntry p = tab.add("P", 0.09).getEntry();
    // private NetworkTableEntry i = tab.add("I", 0).getEntry();
    // private NetworkTableEntry d = tab.add("D", 0).getEntry();
    // private NetworkTableEntry mP = tab.add("mP", 0).getEntry();
    // private NetworkTableEntry mI = tab.add("mI", 0).getEntry();
    // private NetworkTableEntry mD = tab.add("mD", 0).getEntry();
    private NetworkTableEntry arbDriveMult = tab.add("drive omega mult", -0.25).getEntry();
    // private NetworkTableEntry spinButton = tab.add("rotate", false).getEntry();
    private NetworkTableEntry angleOffset = tab.add("angle offset", -2.9).getEntry();
    private NetworkTableEntry rotSpeed = tab.add("rotationSpeed", 0).getEntry();
    private GoalPhoton goalPhoton;
    private double rpmOut;
    private int scanDirection = -1;
    private double targetAngle;

    public Turret() {
        init();
    }

    @Override
    public void init() {
        joy = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
        if (RobotToggles.ENABLE_IMU) {
            pigeon = new PigeonIMU(RobotMap.PIGEON);
        }
        if (RobotToggles.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
            goalPhoton.init();
        }
        motor = new CANSparkMax(RobotMap.TURRET_YAW, MotorType.kBrushless);
        encoder = motor.getEncoder();
        panel = new ButtonPanel(RobotNumbers.BUTTON_PANEL_SLOT);
        fMultiplier = 0;
        //control = new PIDController(0, 0, 0);
        //control.setPID(RobotNumbers.turretP, RobotNumbers.turretI, RobotNumbers.turretD);
        //                                                        v   ANGERY   v
        //double motorEncoderCounts = 1; NOTE TO FUTURE PEOPLE: ENCODER SPITS OUT ROTATIONS(not counts)BY DEFAULT

        //set the motor encoder to return the position of the turret in degrees using the power of MATH
        //encoder.setPositionConversionFactor(((turretSprocketSize/motorSprocketSize)*versaRatio*degreesPerRotation));
        encoder.setPositionConversionFactor(360 / 77.7);
        encoder.setPositionConversionFactor(360 / (RobotNumbers.TURRET_SPROCKET_SIZE * RobotNumbers.TURRET_GEAR_RATIO));
        controller = motor.getPIDController();
        positionControl = new PIDController(0, 0, 0);
        motor.setInverted(false);
        encoder.setPosition(270);
        //controller.setReference(0, ControlType.kPosition);
        setMotorPID(0.5, 0, 0);
        setPosPID(0.02, 0, 0);
        motor.setIdleMode(IdleMode.kBrake);
        setBrake(true);
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    // public void updateSimple(){
    //     if(panel.getButton(5)){
    //         motor.set(rotSpeed.getDouble(0));
    //     }
    //     else if(panel.getButton(7)){
    //         motor.set(-rotSpeed.getDouble(0));
    //     }
    //     else{
    //         motor.set(0);
    //     }
    // }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {
        if (RobotToggles.DEBUG) {
            System.out.println("Turret degrees:" + turretDegrees());
        }
        //SmartDashboard.putNumber("slider",joy.getSlider());
        fMultiplier = fMult.getDouble(0);
        targetPosition = pos.getDouble(0);
        //setPosPID(p.getDouble(0.09), i.getDouble(0), d.getDouble(0));
        //setPosPID(RobotNumbers.TURRET_P,RobotNumbers.TURRET_I,RobotNumbers.TURRET_D);
        //setPosPID(0.006, 0.00001, 0.001);
        //setMotorPID(mP.getDouble(0), mI.getDouble(0), mD.getDouble(0));
        //turretOmega = -driveOmega*RobotNumbers.turretRotationSpeedMultiplier;
        //double motorOmega = turretOmega*sprocketRatio;

        //!!!!! THE TURRET ZERO IS THE PHYSICAL STOP CLOSEST TO THE GOAL

        /*things to do:
        check if there is a valid target, if not, face north based on gyro
        if there is a valid target, point at it
        if 270>position>0 then offset WHATEVER speed it is turning at by -driveOmega to counterrotate
        */
        double omegaSetpoint = 0;
        if (RobotToggles.ENABLE_VISION) {
            if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN) { //Check if the Target button is held down
                //System.out.println("I'm looking. Target is valid? " + goalPhoton.validTarget());
                if (goalPhoton.validTarget()) { //If the vision system detects a ball
                    //omegaSetpoint = positionControl.calculate(turretDegrees(), targetAngle);
                    //omegaSetpoint = positionControl.calculate(turretDegrees(), turretDegrees() - goalPhoton.getGoalAngle());
                    omegaSetpoint = -goalPhoton.getGoalAngle() / 30;
                    //System.out.println(omegaSetpoint);
                    //omegaSetpoint=0;
                    //double skew = goalPhoton.getGoalAngle();
                    //omegaSetpoint = skew > 0 ? 1 : -1;
                    //System.out.println("Skew: " + skew + " Omega: " + omegaSetpoint);
                } else {
                    //System.out.println("Target not found.");
                    omegaSetpoint = scan();
                }
            }
        }

        /*
        if (271 > turretDegrees() && turretDegrees() > -1) {
            omegaSetpoint = 0;
            //omegaSetpoint += -driveOmega * arbDriveMult.getDouble(-0.28);
            if (panel.get(ControllerEnums.ButtonPanelButtons.TARGET) == ControllerEnums.ButtonStatus.DOWN) {
                omegaSetpoint += joy.get(ControllerEnums.JoystickAxis.X_AXIS);
            }
        } else {
            omegaSetpoint = 0;
        }

        if (!goalPhoton.validTarget()) {//no target
            //face north
            SmartDashboard.putString("mode", "Target Lost");
            // if(turretDegrees()>250){
            //     scanDirection = 1;
            // }
            // else if(turretDegrees()<20){
            //     scanDirection = -1;
            // }
            // omegaSetpoint = scanDirection;
            if (chasingTarget) {
                //System.out.println(chasingTarget);
                omegaSetpoint = positionControl.calculate(turretDegrees(), targetAngle);
                //omegaSetpoint += joy.getXAxis();
            }
            //scan();
            //omegaSetpoint += positionControl.calculate(turretDegrees(), limitAngle(235+yawWrap()-360));
        } else {//target good
            SmartDashboard.putString("mode", "Facing Target");
            omegaSetpoint += positionControl.calculate(-goalPhoton.getGoalAngle(), angleOffset.getDouble(-2.9));
        }
        */


        /**
         *   If holding down the manual rotation button, then rotate the turret based on the Z rotation of the joystick.
         */
        if (joy.get(ControllerEnums.JoystickButtons.TWO) == ControllerEnums.ButtonStatus.DOWN) {
            if (RobotToggles.DEBUG) {
                System.out.println("Joystick is at " + joy.get(ControllerEnums.JoystickAxis.Z_ROTATE));
            }
            omegaSetpoint = joy.get(ControllerEnums.JoystickAxis.Z_ROTATE) * -2;
        }

        /*
        if (Math.abs(omegaSetpoint) < 0.01) {
            atTarget = true;
        } else {
            atTarget = false;
        }
           */
        if (isSafe()) {
            //if (/*spinButton.getBoolean(false)&&*/true) {
            // if(panel.getButton(12)){
            //     omegaSetpoint = 2*joy.getXAxis();
            // }
            rotateTurret(omegaSetpoint);
            if (RobotToggles.DEBUG) {
                //System.out.println("Attempting to rotate the POS at" + omegaSetpoint);
            }
            //} else {
            //rotateTurret(0);
            //}
        } else {
            //TODO remove nested ternary
            double safeTurretRotation = (turretDegrees() > 270) ? (0.25) : ((turretDegrees() < 100) ? (-0.25) : (0));
            rotateTurret(safeTurretRotation);
        }
        /*
        if (!track) {
            //rotateTurret(0);
        }
        setF(1);
        */
        if (RobotToggles.DEBUG) {
            SmartDashboard.putNumber("Turret DB Omega offset", -driveOmega * arbDriveMult.getDouble(-0.28));
            SmartDashboard.putNumber("Turret Omega", omegaSetpoint);
            SmartDashboard.putNumber("Turret Position", turretDegrees());
            SmartDashboard.putNumber("Turret Speed", encoder.getVelocity());
            SmartDashboard.putNumber("Turret FF", controller.getFF());
            SmartDashboard.putBoolean("Turret Safe", isSafe());
            if (RobotToggles.ENABLE_IMU) {
                SmartDashboard.putNumber("YawWrap", yawWrap() - 360);
                SmartDashboard.putNumber("Turret Heading from North", fieldHeading());
                SmartDashboard.putNumber("Turret North", limitAngle(235 + yawWrap() - 360));
            }
            SmartDashboard.putBoolean("Turret At Target", atTarget);
            //chasingTarget = false;
            SmartDashboard.putBoolean("Turret Track", track);
            SmartDashboard.putBoolean("Turret at Target", atTarget);
        }
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

    public void setBrake(boolean brake) {
        if (brake) {
            motor.setIdleMode(IdleMode.kBrake);
        } else {
            motor.setIdleMode(IdleMode.kCoast);
        }
    }

    public void teleopInit() {
        encoder = motor.getEncoder();
        encoder.setPosition(0);
    }

    public void updateSimple() {
        if (panel.get(ButtonPanelButtons.BUDDY_CLIMB) == ButtonStatus.DOWN) {
            motor.set(rotSpeed.getDouble(0));
        } else if (panel.get(ControllerEnums.ButtonPanelButtons.AUX_BOTTOM) == ControllerEnums.ButtonStatus.DOWN) {
            motor.set(-rotSpeed.getDouble(210));
        } else {
            motor.set(0);
        }
    }

    public void disabledInit() {
        motor.setIdleMode(IdleMode.kCoast);
    }

    private boolean isSafe() {
        double turretDeg = turretDegrees();
        return turretDeg <= 270 && turretDeg >= 100;
    }

    public boolean setTargetAngle(double target) {
        targetAngle = target;
        chasingTarget = true;
        return atTarget;
    }

    /**
     * @return the turret's heading in relation to the field
     */
    public double fieldHeading() {
        return yawWrap() - turretDegrees();
    }

    public void resetEncoderAndGyro() {
        encoder.setPosition(0);
        //resetPigeon();
    }

    /**
     * don't use
     *
     * @param degrees
     */
    private void setTurretTarget(double degrees) {
        rotateTurret(-positionControl.calculate(turretDegrees(), limitAngle(degrees)));
    }

    /**
     * Rotate the turret at a certain rad/sec
     *
     * @param speed - rad/sec to rotate the turret at ()
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

    private double turretDegrees() {
        return 270 - encoder.getPosition();//return encoder.getPosition();
    }

    private double limitAngle(double angle) {
        if (angle > RobotNumbers.TURRET_MAX_POS) {
            angle = RobotNumbers.TURRET_MAX_POS;
        }
        if (angle < RobotNumbers.TURRET_MIN_POS) {
            angle = RobotNumbers.TURRET_MIN_POS;
        }
        return angle;
    }

    // private void setF(double F){
    //     controller.setFF(F*fMultiplier);
    // }

    public void setDriveOmega(double omega) {
        driveOmega = omega;
    }

    private void pointNorth() {
        //set position of turret to whatever angle is "north"(generally towards goal)
    }

    /**
     * Scan the turret back and forth to find a target.
     */
    private double scan() {
        //TODO remove nested ternary
        //scanDirection = (turretDegrees() > 270) ? 1 : ((turretDegrees() < 100) ? -1 : scanDirection);

        if (turretDegrees() >= 260) {
            scanDirection = 1;
        } else if (turretDegrees() <= 100) {
            scanDirection = -1;
        } /*else if (scanDirection == 0){
            scanDirection = RobotNumbers.TURRET_MAX_POS < 2 * turretDegrees() ? 1 : -1;
        }*/

        return scanDirection;
        //rotateTurret(scanDirection);
    }

    public void resetPigeon() {
        updatePigeon();
        startypr = ypr;
        startYaw = yawAbs() - 90;
    }

    //pigeon ------------------------------------------------------------------------------------------------------------------------
    public void updatePigeon() {
        pigeon.getYawPitchRoll(ypr);
    }

    //absolute ypr -----------------------------------------------------------------------------------------------------------------
    public double yawAbs() { //return absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }

    //relative ypr ----------------------------------------------------------------------------------------------------------------
    public double yawRel() { //return relative(to start) yaw of pigeon
        updatePigeon();
        return (ypr[0] - startYaw);
    }

    public double yawWrap() {
        return yawRel() - Math.floor(yawRel() / 360D) * 360D;
    }
}