package frc.ballstuff.shooting;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ButtonPanelController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickAxis;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.controllers.JoystickController;
import frc.misc.ISubsystem;
import frc.misc.PID;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.RobotSettings;
import frc.vision.GoalPhoton;
import frc.vision.IVision;

import static frc.robot.Robot.hopper;
import static frc.robot.Robot.shooter;

/**
 * Shooter pertains to spinning the flywheel that actually makes the balls go really fast
 */
public class Shooter implements ISubsystem {
    private final ShuffleboardTab tab = Shuffleboard.getTab("Shooter");
    private final NetworkTableEntry P = tab.add("P", RobotSettings.SHOOTER_PID.getP()).getEntry(),
            I = tab.add("I", RobotSettings.SHOOTER_PID.getI()).getEntry(),
            D = tab.add("D", RobotSettings.SHOOTER_PID.getD()).getEntry(),
            F = tab.add("F", RobotSettings.SHOOTER_PID.getF()).getEntry(),
            constSpeed = tab.add("Constant Speed", 0).getEntry(),
            calibratePID = tab.add("Recalibrate PID", false).getEntry();
    public BaseController panel, joystickController;
    public double speed, actualRPM;
    public boolean shooting;
    public boolean atSpeed = false;
    public boolean interpolationEnabled = false;
    boolean trackingTarget = false;
    private AbstractMotorController leader, follower;
    private IVision goalPhoton;
    private boolean enabled = true;
    private boolean spunUp = false;
    private boolean recoveryPID = false;
    private Timer shootTimer;
    private boolean timerStarted = false;
    private PID lastPID = PID.EMPTY_PID;

    public Shooter() {
        addToMetaList();
        init();
    }

    /**
     * Initialize the Shooter object including the controller and the cameras and timers
     */
    @Override
    public void init() throws IllegalStateException {
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD:
                joystickController = new JoystickController(RobotSettings.FLIGHT_STICK_USB_SLOT);
                panel = new ButtonPanelController(RobotSettings.BUTTON_PANEL_USB_SLOT);
                break;
            case BOP_IT:
                joystickController = new BopItBasicController(1);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        createAndInitMotors();

        //SmartDashboard.putString("ZONE", "none");
        if (RobotSettings.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
        }

        createTimers();
    }

    /**
     * Initialize the motors. Checks for SHOOTER_USE_SPARKS and SHOOTER_USE_TWO_MOTORS to allow modularity.
     */
    private void createAndInitMotors() {
        switch (RobotSettings.SHOOTER_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                leader = new SparkMotorController(RobotSettings.SHOOTER_LEADER_ID);
                if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new SparkMotorController(RobotSettings.SHOOTER_FOLLOWER_ID);
                }
                leader.setSensorToRevolutionFactor(1);
                break;
            case TALON_FX:
                leader = new TalonMotorController(RobotSettings.SHOOTER_LEADER_ID);
                if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new TalonMotorController(RobotSettings.SHOOTER_FOLLOWER_ID);
                }
                leader.setSensorToRevolutionFactor(600 / RobotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                break;
            default:
                throw new IllegalStateException("No such supported shooter motor config for " + RobotSettings.SHOOTER_MOTOR_TYPE.name());
        }

        leader.setInverted(RobotSettings.SHOOTER_INVERTED);
        if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
            follower.follow(leader);
            follower.setInverted(RobotSettings.SHOOTER_INVERTED);
        }
        leader.setCurrentLimit(80);
        if (RobotSettings.SHOOTER_USE_TWO_MOTORS) {
            follower.setCurrentLimit(80);
            follower.setBrake(false);
        }
        leader.setBrake(false);
        leader.resetEncoder();
        leader.setOpenLoopRampRate(40);
    }

    /**
     * makes timers for the shooting and indexing of balls
     */
    private void createTimers() {
        shootTimer = new Timer();
        shootTimer.stop();
        shootTimer.reset();
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
    }

    public void setPercentSpeed(double percent) {
        if (percent <= 1) {
            leader.moveAtPercent(percent);
        }
    }

    /**
     * Set the P, I, and D values for the shooter.
     *
     * @param pid the {@link PID} object that contains pertinent pidf data
     */
    private void setPID(PID pid) {
        leader.setPid(pid);
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
        actualRPM = leader.getRotations();
        checkState();
        boolean lockOntoTarget = false;
        switch (RobotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD: {
                boolean solidSpeed = panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN;
                double adjustmentFactor = joystickController.getPositive(JoystickAxis.SLIDER);
                if (RobotSettings.ENABLE_VISION) {
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
            case BOP_IT: {
                if (joystickController.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED.shoot(this);
                }

            }
            default:
                throw new IllegalStateException("This UI not implemented for this controller");
        }
        if (calibratePID.getBoolean(false)) {
            PID readPid = new PID(P.getDouble(RobotSettings.DRIVEBASE_PID.getP()), I.getDouble(RobotSettings.DRIVEBASE_PID.getI()), D.getDouble(RobotSettings.DRIVEBASE_PID.getD()), F.getDouble(RobotSettings.DRIVEBASE_PID.getF()));
            if (!lastPID.equals(readPid)) {
                lastPID = readPid;
                setPID(lastPID);
                if (RobotSettings.DEBUG) {
                    System.out.println("Set shooter pid to " + lastPID);
                }
            }
        }
        if (RobotSettings.DEBUG) {
            SmartDashboard.putNumber("RPM", actualRPM);
            SmartDashboard.putNumber("Target RPM", speed);

            SmartDashboard.putBoolean("atSpeed", atSpeed);
            SmartDashboard.putBoolean("shooter enable", enabled);
        }
        updateControls();
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        if (RobotSettings.DEBUG) {
            System.out.println("setSpeed1");
        }
        leader.moveAtRotations(rpm);
        if (RobotSettings.DEBUG) {
            System.out.println("setSpeed2");
        }
    }

    /**
     * if the shooter is actually at the requested speed
     *
     * @return if the shooter is actually at the requested speed
     */
    public boolean atSpeed() {
        return leader.getRotations() > speed - 80;
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
     * if the goal photon is in use and has a valid target in its sights
     *
     * @return if the goal photon is in use and has a valid target in its sights
     */
    public boolean validTarget() {
        return RobotSettings.ENABLE_VISION && goalPhoton.hasValidTarget();
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
