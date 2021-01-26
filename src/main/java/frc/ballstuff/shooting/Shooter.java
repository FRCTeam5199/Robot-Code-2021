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
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickAxis;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.controllers.JoystickController;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

import static frc.ballstuff.shooting.ShootingStyles.fireIndexerDependent;
import static frc.robot.Robot.hopper;
import static frc.robot.Robot.shooter;

public class Shooter implements ISubsystem {

    public final String[] data = {
            "match time", "init time", "speed", "target speed", "motor temperature", "motor current", "powered", "P",
            "I", "D", "rP", "rI", "rD", "distance"
    };
    public final String[] units = {
            "seconds", "seconds", "rpm", "rpm", "C", "A", "T/F", "num", "num", "num", "num", "num", "num", "meters"
    };
    public final JoystickController joystickController = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
    private final double pulleyRatio = RobotNumbers.motorPulleySize / RobotNumbers.driverPulleySize;
    private final Timer timer = new Timer();
    private final int ballsShot = 0;
    private final boolean poweredState = false;
    private final double[][] sizeSpeedsArray = {{0, 0}, {45, 4100}, {55, 4150}, {65, 4170}, {75, 4150}, {85, 4500},};
    private final double speedMult = 1;
    private final double[][] voltageFFArray = {{0, 0}, {11, 190}, {13, 185}};
    public double speed;
    public boolean atSpeed = false;
    public double actualRPM;
    public boolean interpolationEnabled = false;
    public boolean shooting;
    public boolean allBallsFired = false;
    private CANSparkMax leader, follower;
    private TalonFX falconLeader, falconFollower;
    private CANPIDController speedo;
    private CANEncoder encoder;
    //private GoalChameleon chameleon;
    // private ShuffleboardTab tab = Shuffleboard.getTab("Shooter");
    // private NetworkTableEntry shooterSpeed = tab.add("Shooter Speed", 0).getEntry();
    // private NetworkTableEntry shooterToggle = tab.add("Shooter Toggle", false).getEntry();
    // private NetworkTableEntry manualSpeedOverride = tab.add("SPEED OVERRIDE", false).getEntry();
    // private NetworkTableEntry rampRate = tab.add("Ramp Rate", 40).getEntry();
    //public final XBoxController xBoxController;
    private boolean enabled = true;
    private ButtonPanel panel = new ButtonPanel(RobotNumbers.BUTTON_PANEL_SLOT);
    private double targetRPM;
    private boolean spunUp = false;
    private boolean recoveryPID = false;
    private double lastSpeed;
    private Timer shootTimer;
    private Timer indexTimer;
    private boolean timerStarted = false;
    private Timer shooterTimer;
    private boolean timerFlag = false;

    public Shooter() {
        init();
    }

    /**
     * Initialize the Shooter object.
     */
    @Override
    public void init() {
        createAndInitMotors();

        SmartDashboard.putString("ZONE", "none");
        //chameleon = new GoalChameleon();
        createTimers();
    }

    private void createTimers() {
        shootTimer = new Timer();
        indexTimer = new Timer();
        indexTimer.stop();
        indexTimer.reset();
        shootTimer.stop();
        shootTimer.reset();
    }

    /**
     * Initialize the motors. Checks for SHOOTER_USE_SPARKS and SHOOTER_USE_TWO_MOTORS to allow modularity.
     */
    private void createAndInitMotors() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            leader = new CANSparkMax(RobotMap.SHOOTER_LEADER, MotorType.kBrushless);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                follower = new CANSparkMax(RobotMap.SHOOTER_FOLLOWER, MotorType.kBrushless);
            }

            leader.setInverted(RobotToggles.SHOOTER_INVERTED);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                follower.follow(leader, RobotToggles.SHOOTER_INVERTED);
            }

            leader.setSmartCurrentLimit(80);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                follower.setSmartCurrentLimit(80);
                follower.setIdleMode(IdleMode.kCoast);
            }
            leader.setIdleMode(IdleMode.kCoast);
            leader.getEncoder().setPosition(0);
            leader.setOpenLoopRampRate(40);
            encoder = leader.getEncoder();

            //setPID(4e-5, 0, 0);
            speedo = leader.getPIDController();
            speedo.setOutputRange(-1, 1);
        } else {
            falconLeader = new TalonFX(RobotMap.SHOOTER_LEADER);
            TalonFXInvertType leaderDirection = RobotToggles.SHOOTER_INVERTED ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise;
            falconLeader.setInverted(leaderDirection);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                falconFollower = new TalonFX(RobotMap.SHOOTER_FOLLOWER);
                TalonFXInvertType followerDirection = !RobotToggles.SHOOTER_INVERTED ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise;
                falconFollower.setInverted(followerDirection);
                falconFollower.follow(falconLeader);
                falconFollower.setNeutralMode(NeutralMode.Coast);
            }

            falconLeader.setNeutralMode(NeutralMode.Coast);
        }
    }

    /**
     * Update the Shooter object.
     */
    public void update() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            actualRPM = leader.getEncoder().getVelocity();
        } else {
            actualRPM = falconLeader.getSelectedSensorVelocity() * 600 / RobotNumbers.SHOOTER_SENSOR_UNITS_PER_ROTATION; //do math: 4096 units/rotation, units/100ms
        }
        checkState();
        //put code here to set speed based on distance to goal
        boolean disabled = panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.UP;

        if (!interpolationEnabled) {
            speed = 4200;
        } else if (!disabled) {
            speed = 4200 * ((joystickController.getPositive(JoystickAxis.SLIDER) * 0.25) + 1); //4200
        } else {
            speed = 0;
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

        if (RobotToggles.DEBUG) {
            SmartDashboard.putNumber("RPM", actualRPM);
            SmartDashboard.putNumber("Target RPM", speed);

            SmartDashboard.putBoolean("atSpeed", atSpeed);
            SmartDashboard.putNumber("ballsShot", ballsShot);
            SmartDashboard.putBoolean("shooter enable", enabled);
        }
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
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        //System.out.println("setSpeed1");
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            speedo.setReference(rpm, ControlType.kVelocity);
        } else {
            falconLeader.set(ControlMode.Velocity, rpm * RobotNumbers.SHOOTER_SENSOR_UNITS_PER_ROTATION / 600.0);
        }
        System.out.println("setSpeed2");
    }

    /**
     * Set the P, I, and D values for the shooter.
     *
     * @param P - P value
     * @param I - I value
     * @param D - D value
     * @param F - F value
     */
    private void setPID(double P, double I, double D, double F) {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            speedo.setP(P);
            speedo.setI(I);
            speedo.setD(D);
            speedo.setFF(F);
        } else {
            falconLeader.config_kF(0, RobotNumbers.SHOOTER_F, RobotNumbers.SHOOTER_TIMEOUT_MS);
            falconLeader.config_kP(0, RobotNumbers.SHOOTER_P, RobotNumbers.SHOOTER_TIMEOUT_MS);
            falconLeader.config_kI(0, RobotNumbers.SHOOTER_I, RobotNumbers.SHOOTER_TIMEOUT_MS);
            falconLeader.config_kD(0, RobotNumbers.SHOOTER_D, RobotNumbers.SHOOTER_TIMEOUT_MS);
        }

    }

    public void spinUp() {
        setSpeed(speed);
    }

    /**
     * Get motor speed based
     *
     * @param distance ignored
     * @return 0
     */
    private double getSpeedBasedOnDistance(double distance) {
        return 0;
    }

    public boolean atSpeed() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            return leader.getEncoder().getVelocity() > speed - 80;
        } else {
            return falconLeader.getSelectedSensorVelocity() > speed - 80;
        }
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

    @Override
    public void updateTest() {
        updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {
        update();
        updateControls();
        fireIndexerDependent();
        //indexing = joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN;
    }

    public void updateControls() {
        if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
            toggle(true);
        }
        if (joystickController.get(JoystickButtons.ELEVEN) == ButtonStatus.DOWN) {
            toggle(joystickController.get(JoystickButtons.EIGHT) == ButtonStatus.DOWN);
        }
    }

    public boolean validTarget() {
        return true;//return chameleon.validTarget();
    }


    public void feedIn() {

    }

    public void stopFiring() {
        toggle(false);
        hopper.setAll(false);
        shooting = false;
    }

    public void setupShooterTimer() {
        shooterTimer = new Timer();
        timerFlag = false;
        shooterTimer.stop();
        shooterTimer.reset();
        stopFiring();
    }

    public void ensureTimerStarted() {
        if (!shooter.timerStarted) {
            shooter.shootTimer.start();
            shooter.timerStarted = true;
        }
    }

    public void resetShootTimer() {
        shootTimer.stop();
        shootTimer.reset();
        timerStarted = false;
    }

    public Timer getShootTimer() {
        return shootTimer;
    }
}
