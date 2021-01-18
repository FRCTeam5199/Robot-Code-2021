package frc.ballstuff.shooting;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.ButtonPanel;
import frc.controllers.JoystickController;
import frc.controllers.XBoxController;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

public class Shooter {

    public final String[] data = {"match time", "init time", "speed", "target speed", "motor temperature", "motor current", "powered", "P", "I", "D", "rP", "rI", "rD", "distance"};
    public final String[] units = {"seconds", "seconds", "rpm", "rpm", "C", "A", "T/F", "num", "num", "num", "num", "num", "num", "meters"};
    private final double pulleyRatio = RobotNumbers.motorPulleySize / RobotNumbers.driverPulleySize;
    public double speed;
    public boolean atSpeed = false;
    public double actualRPM;
    public boolean interpolationEnabled = false;
    private CANSparkMax leader, follower;
    private TalonFX falconLeader, falconFollower;
    private CANPIDController speedo;
    private CANEncoder encoder;
    // private ShuffleboardTab tab = Shuffleboard.getTab("Shooter");
    // private NetworkTableEntry shooterSpeed = tab.add("Shooter Speed", 0).getEntry();
    // private NetworkTableEntry shooterToggle = tab.add("Shooter Toggle", false).getEntry();
    // private NetworkTableEntry manualSpeedOverride = tab.add("SPEED OVERRIDE", false).getEntry();
    // private NetworkTableEntry rampRate = tab.add("Ramp Rate", 40).getEntry();
    private XBoxController xBoxController;
    private boolean enabled = true;
    private JoystickController joystickController;
    private ButtonPanel panel;
    private Timer timer = new Timer();
    private double targetRPM;
    private int ballsShot = 0;
    private boolean poweredState;
    private boolean spunUp = false;
    private boolean recoveryPID = false;
    private double lastSpeed;
    private double[][] sizeSpeedsArray = {{0, 0}, {45, 4100}, {55, 4150}, {65, 4170}, {75, 4150}, {85, 4500},};
    private double speedMult = 1;
    private double[][] voltageFFArray = {{0, 0}, {11, 190}, {13, 185}};

    public Shooter() {
        init();
        poweredState = false;
    }

    /**
     * Initialize the Shooter object.
     */
    public void init() {
        createAndInitMotors();

        SmartDashboard.putString("ZONE", "none");
        joystickController = new JoystickController(1);
        panel = new ButtonPanel(2);
    }

    private void createAndInitMotors() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            leader = new CANSparkMax(RobotMap.SHOOTER_LEADER, MotorType.kBrushless);
            follower = new CANSparkMax(RobotMap.SHOOTER_FOLLOWER, MotorType.kBrushless);

            leader.setInverted(true);
            follower.follow(leader, true);

            leader.setSmartCurrentLimit(80);
            follower.setSmartCurrentLimit(80);
            leader.setIdleMode(IdleMode.kCoast);
            follower.setIdleMode(IdleMode.kCoast);

            leader.getEncoder().setPosition(0);
            leader.setOpenLoopRampRate(40);
            encoder = leader.getEncoder();
            //setPID(4e-5, 0, 0);
            speedo = leader.getPIDController();
            speedo.setOutputRange(-1, 1);
        } else {
            falconLeader = new TalonFX(RobotMap.SHOOTER_LEADER);
            falconLeader.setInverted(TalonFXInvertType.Clockwise);

            falconFollower = new TalonFX(RobotMap.SHOOTER_FOLLOWER);
            falconFollower.setInverted(TalonFXInvertType.CounterClockwise);
            falconFollower.follow(falconLeader);

            falconLeader.setNeutralMode(NeutralMode.Coast);
            falconFollower.setNeutralMode(NeutralMode.Coast);
        }
    }

    /**
     * Update the Shooter object.
     */
    public void update() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            actualRPM = leader.getEncoder().getVelocity();
        } else {
            actualRPM = falconLeader.getSelectedSensorVelocity(); //do math: 4096 units/rotation, units/100ms
        }
        checkState();
        //put code here to set speed based on distance to goal
        boolean disabled = false;
        double closeDist = 3;

        if (!panel.getButton(13)) {
            speed = 4200 * ((joystickController.getSlider() * 0.25) + 1); //4200
            disabled = false;
        } else {
            speed = 0;
            disabled = true;
        }

        if (!interpolationEnabled) {
            speed = 4200;
        }

        //setPID(P,I,D);

        if (!disabled) {
            setSpeed(speed);
        } else {
            if (RobotToggles.SHOOTER_USE_SPARKS) {
                leader.set(0);
            } else {
                falconLeader.set(ControlMode.PercentOutput, 0);
            }
        }

        SmartDashboard.putNumber("RPM", actualRPM);
        SmartDashboard.putNumber("Target RPM", speed);

        SmartDashboard.putBoolean("atSpeed", atSpeed);
        SmartDashboard.putNumber("ballsShot", ballsShot);
        SmartDashboard.putBoolean("shooter enable", enabled);
    }

    private void checkState() {
        if (actualRPM >= speed - 50) {
            atSpeed = true;
            spunUp = true;
        }
        if (actualRPM < speed - 30) {
            atSpeed = false;
        }

        if (spunUp && actualRPM < speed - 55) {
            recoveryPID = true;
        }
        if (actualRPM < speed - 1200) {
            recoveryPID = false;
            spunUp = false;
        }
        if (recoveryPID) {
            setPID(RobotNumbers.SHOOTER_RECOVERY_P, RobotNumbers.SHOOTER_RECOVERY_I, RobotNumbers.SHOOTER_RECOVERY_D, RobotNumbers.SHOOTER_F);
            //setPID(P,I,D, F);
        } else {
            setPID(RobotNumbers.SHOOTER_P, RobotNumbers.SHOOTER_I, RobotNumbers.SHOOTER_D, RobotNumbers.SHOOTER_F);
        }
    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm
     */
    public void setSpeed(double rpm) {
        //System.out.println("setSpeed1");
        speedo.setReference(rpm, ControlType.kVelocity);
        //System.out.println("setSpeed2");
    }

    /**
     * Set the P, I, and D values for the shooter.
     *
     * @param P - P value
     * @param I - I value
     * @param D - D value
     */
    private void setPID(double P, double I, double D, double F) {
        speedo.setP(P);
        speedo.setI(I);
        speedo.setD(D);
        speedo.setFF(F);
    }

    public void spinUp() {
        setSpeed(speed);
    }

    /**
     * Get motor speed based
     *
     * @param distance
     * @return
     */
    private double getSpeedBasedOnDistance(double distance) {
        return 0;
    }

    public boolean atSpeed() {
        return leader.getEncoder().getVelocity() > speed - 80;
    }

    public boolean spunUp() {
        return spunUp;
    }

    public boolean recovering() {
        return recoveryPID;
    }

    /**
     * Enable or disable the shooter being spun up.
     *
     * @param toggle - spun up true or false
     */
    public void toggle(boolean toggle) {
        enabled = toggle;
    }

    private double interpolateFF() {
        double voltage = RobotController.getBatteryVoltage();
        int index = 0;
        for (int i = 0; i < voltageFFArray.length; i++) {
            if (voltage > voltageFFArray[i][0]) {
                index = i;
            }
        }
        //now index is the index of the low end, index+1 = high end
        if (index + 1 >= voltageFFArray.length) {
            return voltageFFArray[sizeSpeedsArray.length - 1][1];
        }
        double sizeGap = voltageFFArray[index][0] - voltageFFArray[index + 1][0];
        double gapFromLowEnd = voltage - voltageFFArray[index][0];
        double portionOfGap = gapFromLowEnd / sizeGap;

        double speedGap = voltageFFArray[index][1] - voltageFFArray[index + 1][1];
        double outSpeed = voltageFFArray[index][1] + speedGap * portionOfGap; //low end + gap * portion
        return 0;
    }
}
