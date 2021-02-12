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
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
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
import frc.vision.GoalPhoton;

import static frc.misc.UtilFunctions.weightedAverage;
import static frc.robot.Robot.hopper;
import static frc.robot.Robot.shooter;

/**
 * Shooter pertains to spinning the flywheel that actually makes the balls go really fast
 */
public class Shooter implements ISubsystem {

    public final String[] data = {"match time", "init time", "speed", "target speed", "motor temperature", "motor current", "powered", "P", "I", "D", "rP", "rI", "rD", "distance"};
    public final String[] units = {"seconds", "seconds", "rpm", "rpm", "C", "A", "T/F", "num", "num", "num", "num", "num", "num", "meters"};
    private final Timer timer = new Timer();
    private final int ballsShot = 0;
    private final boolean poweredState = false;
    /**
     * Array of goal size calculated from vision to RPM speeds required for shooting. {goalsize, RPM}
     */
    private final double[][] sizeSpeedsArray = {{0, 0}, {45, 4100}, {55, 4150}, {65, 4170}, {75, 4150}, {85, 4500},};
    private final double speedMult = 1;
    /**
     * Array of voltages and _. {Voltage, _}
     */
    private final double[][] voltageFFArray = {{0, 0}, {11, 190}, {13, 185}};
    private final ShuffleboardTab tab = Shuffleboard.getTab("Shooter");
    private final NetworkTableEntry P = tab.add("P", RobotNumbers.SHOOTER_P).getEntry();
    private final NetworkTableEntry I = tab.add("I", RobotNumbers.SHOOTER_I).getEntry();
    private final NetworkTableEntry D = tab.add("D", RobotNumbers.SHOOTER_D).getEntry();
    private final NetworkTableEntry F = tab.add("F", RobotNumbers.SHOOTER_F).getEntry();
    private final NetworkTableEntry constSpeed = tab.add("Constant Speed", 0).getEntry();
    public BaseController panel, joystickController;
    public double speed;
    public boolean atSpeed = false;
    public double actualRPM;
    public boolean interpolationEnabled = false;
    public boolean shooting;
    boolean trackingTarget = false;
    private CANSparkMax leader, follower;
    private TalonFX falconLeader, falconFollower;
    private CANPIDController speedo;
    private CANEncoder encoder;
    private GoalPhoton goalPhoton;
    private boolean enabled = true;
    private boolean spunUp = false;
    private boolean recoveryPID = false;
    private Timer shootTimer, indexTimer, shooterTimer;
    private boolean timerStarted = false;
    private boolean timerFlag = false;
    private double lastP = 0;
    private double lastI = 0;
    private double lastD = 0;
    private double lastF = 0;

    public Shooter() {
        init();
    }

    /**
     * Initialize the Shooter object including the controller and the cameras and timers
     */
    @Override
    public void init() throws IllegalStateException {
        switch (RobotToggles.SHOOTER_CONTROL_STYLE) {
            case STANDARD:
                joystickController = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
                panel = new ButtonPanel(RobotNumbers.BUTTON_PANEL_SLOT);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotToggles.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        createAndInitMotors();

        //SmartDashboard.putString("ZONE", "none");
        if (RobotToggles.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
        }

        createTimers();
        if (!makeSurePreconditionsMet()) {
            System.err.println("sizeSpeedsArray or voltageFFArray is not sorted right. please do this");
        }
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

            speedo = leader.getPIDController();
            speedo.setOutputRange(-1, 1);
        } else {
            TalonFXInvertType leaderDirection = RobotToggles.SHOOTER_INVERTED ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise;
            falconLeader = new TalonFX(RobotMap.SHOOTER_LEADER);
            falconLeader.setInverted(leaderDirection);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                TalonFXInvertType followerDirection = !RobotToggles.SHOOTER_INVERTED ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise;
                falconFollower = new TalonFX(RobotMap.SHOOTER_FOLLOWER);
                falconFollower.setInverted(followerDirection);
                falconFollower.follow(falconLeader);
                falconFollower.setNeutralMode(NeutralMode.Coast);
            }

            falconLeader.setNeutralMode(NeutralMode.Coast);
        }
    }

    /**
     * makes timers for the shooting and indexing of balls
     */
    private void createTimers() {
        shootTimer = new Timer();
        indexTimer = new Timer();
        indexTimer.stop();
        indexTimer.reset();
        shootTimer.stop();
        shootTimer.reset();
    }

    /**
     * Makes sure that the interplation arrays are sorted correctly
     *
     * @return whether {@link #sizeSpeedsArray} and {@link #voltageFFArray} are sorted correctly
     */
    private boolean makeSurePreconditionsMet() {
        for (int i = 0; i < sizeSpeedsArray.length - 1; i++) {
            if (sizeSpeedsArray[i][0] > sizeSpeedsArray[i + 1][0]) return false;
        }
        for (int i = 0; i < voltageFFArray.length - 1; i++) {
            if (voltageFFArray[i][0] > voltageFFArray[i + 1][0]) return false;
        }
        return true;
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {
        updateGeneric();
    }

    public void checkState() {
        if (actualRPM >= speed - 50) {
            atSpeed = true;
            spunUp = true;
        }
        atSpeed = !(actualRPM < speed - 30) && atSpeed;

        recoveryPID = spunUp && actualRPM < speed - 55 || recoveryPID;

        if (actualRPM < speed - 1200) {
            recoveryPID = false;
            spunUp = false;
        }
        /* You're bad.
        if (recoveryPID) {
            setPID(RobotNumbers.SHOOTER_RECOVERY_P, RobotNumbers.SHOOTER_RECOVERY_I, RobotNumbers.SHOOTER_RECOVERY_D, RobotNumbers.SHOOTER_F);
        } else {
            setPID(RobotNumbers.SHOOTER_P, RobotNumbers.SHOOTER_I, RobotNumbers.SHOOTER_D, RobotNumbers.SHOOTER_F);
        }
        */
    }

    public void setPercentSpeed(double percent) {
        if (percent <= 1) {
            if (RobotToggles.SHOOTER_USE_SPARKS) {
                leader.set(percent);
            } else {
                falconLeader.set(ControlMode.PercentOutput, percent);
            }
        }
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
            falconLeader.config_kP(0, P, RobotNumbers.SHOOTER_TIMEOUT_MS);
            falconLeader.config_kI(0, I, RobotNumbers.SHOOTER_TIMEOUT_MS);
            falconLeader.config_kD(0, D, RobotNumbers.SHOOTER_TIMEOUT_MS);
            falconLeader.config_kF(0, F, RobotNumbers.SHOOTER_TIMEOUT_MS);
        }
    }

    public void updateControls() {
        if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
            toggle(true);
        }
        if (joystickController.get(JoystickButtons.ELEVEN) == ButtonStatus.DOWN) {
            toggle(joystickController.get(JoystickButtons.EIGHT) == ButtonStatus.DOWN);
        }
    }

    /**
     * Enable or disable the shooter being spun up.
     *
     * @param toggle - spun up true or false
     */
    public void toggle(boolean toggle) {
        enabled = toggle;
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }

    /**
     * Input is parsed and shooter object maintained appropriately
     */
    @Override
    public void updateGeneric() {
        actualRPM = RobotToggles.SHOOTER_USE_SPARKS ? leader.getEncoder().getVelocity() : falconLeader.getSelectedSensorVelocity() * 600 / RobotNumbers.SHOOTER_SENSOR_UNITS_PER_ROTATION;
        checkState();
        boolean lockOntoTarget = false;
        switch (RobotToggles.SHOOTER_CONTROL_STYLE) {
            case STANDARD: {
                boolean solidSpeed = panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN;
                double adjustmentFactor = joystickController.getPositive(JoystickAxis.SLIDER);
                if (RobotToggles.ENABLE_VISION) {
                    lockOntoTarget = panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN;
                }
                trackingTarget = goalPhoton.hasValidTarget() && lockOntoTarget;
                if (interpolationEnabled) {
                    speed = (solidSpeed) ? (4200 * (adjustmentFactor * 0.25 + 1)) : 0;
                } else {
                    speed = 4200;
                }

                if (solidSpeed) {
                    //setSpeed(speed);
                    //ShootingEnums.FIRE_INDEXER_INDEPENDENT.shoot(this);
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                } else if (trackingTarget && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                } else {
                    hopper.setAll(false);
                    setPercentSpeed(constSpeed.getDouble(0));
                }
                break;
            }
            default:
                throw new IllegalStateException("This UI not implemented for this controller");
        }
        if (RobotToggles.CALIBRATE_SHOOTER_PID) {

            if (lastP != P.getDouble(0) || lastI != I.getDouble(0) || lastD != D.getDouble(0) || lastF != F.getDouble(0)) {
                System.out.println("P: " + P.getDouble(0) + " from " + lastP);
                System.out.println("I: " + I.getDouble(0) + " from " + lastI);
                System.out.println("D: " + D.getDouble(0) + " from " + lastD);
                System.out.println("F: " + F.getDouble(0) + " from " + lastF);
                lastP = P.getDouble(0);
                lastI = I.getDouble(0);
                lastD = D.getDouble(0);
                lastF = F.getDouble(0);
                setPID(lastP, lastI, lastD, lastF);
            }
        }
        if (RobotToggles.DEBUG) {
            SmartDashboard.putNumber("RPM", actualRPM);
            SmartDashboard.putNumber("Target RPM", speed);

            SmartDashboard.putBoolean("atSpeed", atSpeed);
            SmartDashboard.putNumber("ballsShot", ballsShot);
            SmartDashboard.putBoolean("shooter enable", enabled);
        }
        updateControls();
    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        if (RobotToggles.DEBUG) {
            System.out.println("setSpeed1");
        }
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            speedo.setReference(rpm, ControlType.kVelocity);
        } else {
            falconLeader.set(ControlMode.Velocity, rpm * RobotNumbers.SHOOTER_SENSOR_UNITS_PER_ROTATION / 600.0);
        }
        if (RobotToggles.DEBUG) {
            System.out.println("setSpeed2");
        }
    }

    /**
     * if the shooter is actually at the requested speed
     *
     * @return if the shooter is actually at the requested speed
     */
    public boolean atSpeed() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            return leader.getEncoder().getVelocity() > speed - 80;
        } else {
            return falconLeader.getSelectedSensorVelocity() > speed - 80;
        }
    }


    /**
     * getter for spunUp
     *
     * @return {@link #spunUp}
     */
    public boolean spunUp() {
        return spunUp;
    }

    /**
     * getter for recovering
     *
     * @return {@link #recoveryPID}
     */
    public boolean recovering() {
        return recoveryPID;
    }

    /**
     * adjusts speed based on distance based on {@link GoalPhoton#getSize() goalPhoton.getGoalSize()}
     *
     * @return the adjusted speed in RPM based on vision determined distance
     */
    public double interpolateSpeed() {
        double size = goalPhoton.getSize();
        double finalMult = (joystickController.get(JoystickAxis.SLIDER) * 0.25) + 1;
        if (size > sizeSpeedsArray[sizeSpeedsArray.length - 1][0]) {
            SmartDashboard.putNumber("Interpolating Shooter Speed", sizeSpeedsArray[sizeSpeedsArray.length - 1][1] * finalMult * speedMult);
            return sizeSpeedsArray[sizeSpeedsArray.length - 1][1] * finalMult * speedMult;
        }
        if (size < sizeSpeedsArray[0][0]) {
            SmartDashboard.putNumber("Interpolating Shooter Speed", sizeSpeedsArray[0][1] * speedMult * finalMult);
            return sizeSpeedsArray[0][1] * finalMult * speedMult;
        }
        for (int i = sizeSpeedsArray.length - 2; i >= 0; i--) {
            if (size > sizeSpeedsArray[i][0]) {
                SmartDashboard.putNumber("Interpolating Shooter Speed", weightedAverage(size, sizeSpeedsArray[i + 1], sizeSpeedsArray[i]) * finalMult * speedMult);
                return weightedAverage(size, sizeSpeedsArray[i + 1], sizeSpeedsArray[i]) * finalMult * speedMult;
            }
        }
        throw new IllegalStateException("The only way to get here is no not have sizeSpeedsArray sorted in ascending order based on the first value of each entry. Please ensure that it is sorted as such and try again.");
        //throw new IllegalStateException("(Shooter.java) This is literally impossible. If you somehow get here, mechanical broke something. The robot is literally on fire.");
    }

    /**
     * Probably was going to be used at one point, not quite sure why it does, you'll have to ask Conor
     *
     * @return returns a value that
     */
    private double interpolateFF() {
        double voltage = RobotController.getBatteryVoltage();
        if (voltage > voltageFFArray[voltageFFArray.length - 1][0]) return voltageFFArray[voltageFFArray.length - 1][1];
        if (voltage < voltageFFArray[0][0]) return voltageFFArray[0][0];
        for (int i = voltageFFArray.length - 2; i >= 0; i--) {
            if (voltage > voltageFFArray[i][0]) {
                return weightedAverage(voltage, voltageFFArray[i + 1], voltageFFArray[i]);
            }
        }
        throw new IllegalStateException("Battery voltage " + voltage + " could not be interpolated");
    }

    /**
     * if the goal photon is in use and has a valid target in its sights
     *
     * @return if the goal photon is in use and has a valid target in its sights
     */
    public boolean validTarget() {
        if (RobotToggles.ENABLE_VISION) {
            return goalPhoton.hasValidTarget();
        } else {
            return false;
        }
    }

    /**
     * Does as the name suggests
     */
    public void ensureTimerStarted() {
        if (!shooter.timerStarted) {
            shooter.shootTimer.start();
            shooter.timerStarted = true;
        }
    }

    /**
     * Does as the name suggests
     */
    public void resetShootTimer() {
        shootTimer.stop();
        shootTimer.reset();
        timerStarted = false;
    }

    /**
     * getter for the shoot timer
     *
     * @return {@link #shootTimer}
     */
    public Timer getShootTimer() {
        return shootTimer;
    }
}
