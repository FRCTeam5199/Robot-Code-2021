package frc.ballstuff.shooting;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.misc.ISubsystem;
import frc.misc.PID;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.robot.Robot;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.vision.camera.IVision;

import java.util.Objects;

import static frc.robot.Robot.*;

/**
 * Shooter pertains to spinning the flywheel that actually makes the balls go really fast (or slow if you don't like
 * fast things)
 */
public class Shooter implements ISubsystem {
    public static final boolean DEBUG = false;
    private final NetworkTableEntry P = UserInterface.SHOOTER_P.getEntry(),
            I = UserInterface.SHOOTER_I.getEntry(),
            D = UserInterface.SHOOTER_D.getEntry(),
            F = UserInterface.SHOOTER_F.getEntry(),
            constSpeed = UserInterface.SHOOTER_CONST_SPEED.getEntry(),
            calibratePID = UserInterface.SHOOTER_CALIBRATE_PID.getEntry(),
            rpmGraph = UserInterface.SHOOTER_RPM_GRAPH.getEntry();
    public double speed = 4200;
    public int goalTicks = 300;
    public int ballsShot = 0, ticksPassed = 0, emptyIndexerTicks = 0, hopperCooldownTicks = 0, ballsToShoot = 0;
    public int timerTicks = 0;
    public IVision goalCamera;
    public AbstractMotorController leader, follower;
    public boolean isConstSpeed, isConstSpeedLast = false, shooting = false, isSpinningUp = false, isSpinningUpHeld, singleShot = false, multiShot = false, loadingIndexer = false;
    public boolean checkForDips = false;
    public boolean tryFiringBalls = false;
    BaseController panel, joystickController, xbox;
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
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case EXPERIMENTAL_OFFSEASON_2021:
            case STANDARD_OFFSEASON_2021:
            case STANDARD:
                joystickController = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
                panel = BaseController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT, BaseController.Controllers.BUTTON_PANEL_CONTROLLER);
                xbox = BaseController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT, BaseController.Controllers.XBOX_CONTROLLER);
                break;
            case BOP_IT:
                joystickController = BaseController.createOrGet(3, BaseController.Controllers.BOP_IT_CONTROLLER);
                break;
            case XBOX_CONTROLLER:
                joystickController = BaseController.createOrGet(0, BaseController.Controllers.XBOX_CONTROLLER);
                break;
            case FLIGHT_STICK:
                joystickController = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        createAndInitMotors();
        if (robotSettings.ENABLE_VISION) {
            goalCamera = IVision.manufactureGoalCamera(robotSettings.GOAL_CAMERA_TYPE);
        }
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return !leader.isFailed() && !follower.isFailed() ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {

    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    /**
     * Creates and initializes all the controllers you might use in the shooter
     */
    public void createControllers() {
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case ACCURACY_2021:
            case SPEED_2021:
            case EXPERIMENTAL_OFFSEASON_2021:
            case STANDARD_OFFSEASON_2021:
            case STANDARD:
                panel = BaseController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT, BaseController.Controllers.BUTTON_PANEL_CONTROLLER);
                xbox = BaseController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT, BaseController.Controllers.XBOX_CONTROLLER);
                joystickController = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
            case FLIGHT_STICK:
                joystickController = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
                break;
            case BOP_IT:
                joystickController = BaseController.createOrGet(3, BaseController.Controllers.BOP_IT_CONTROLLER);
                break;
            case XBOX_CONTROLLER:
                joystickController = BaseController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT, BaseController.Controllers.XBOX_CONTROLLER);
                break;
            case DRUM_TIME:
                joystickController = BaseController.createOrGet(5, BaseController.Controllers.DRUM_CONTROLLER);
                break;
            case WII:
                joystickController = BaseController.createOrGet(4, BaseController.Controllers.WII_CONTROLLER);
                break;
            case GUITAR:
                joystickController = BaseController.createOrGet(6, BaseController.Controllers.SIX_BUTTON_GUITAR_CONTROLLER);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.SHOOTER_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
    }

    /**
     * Throws information about the shooter onto the shuffleboard, such as PID configuration, a speed graph, and a few
     * other important things.
     *
     * @author Smaltin
     */
    private void updateShuffleboard() {
        if (calibratePID.getBoolean(false)) {
            PID readPid = new PID(P.getDouble(robotSettings.SHOOTER_PID.getP()), I.getDouble(robotSettings.SHOOTER_PID.getI()), D.getDouble(robotSettings.SHOOTER_PID.getD()), F.getDouble(robotSettings.SHOOTER_PID.getF()));
            if (!lastPID.equals(readPid)) {
                lastPID = readPid;
                leader.setPid(lastPID);
                if (robotSettings.DEBUG && DEBUG) {
                    System.out.println("Set shooter pid to " + lastPID);
                }
            }
        } else {
            if (!isConstSpeed && isConstSpeedLast) {
                leader.setPid(robotSettings.SHOOTER_PID);
                isConstSpeedLast = false;
                if (DEBUG && robotSettings.DEBUG) {
                    System.out.println("Normal shooter PID.");
                }
            } else {
                if (DEBUG && robotSettings.DEBUG) {
                    System.out.println("Running constant speed PID.");
                }
            }
        }
        UserInterface.smartDashboardPutNumber("RPM", leader.getSpeed());
        rpmGraph.setNumber(leader.getSpeed());
        UserInterface.smartDashboardPutNumber("Target RPM", speed);
        UserInterface.smartDashboardPutBoolean("atSpeed", isAtSpeed());
        UserInterface.smartDashboardPutBoolean("IS SHOOTING?", shooting);
    }

    /**
     * Runs the default update which unsets hopper {@link frc.ballstuff.intaking.Hopper#setAll(boolean) active flags}
     * and sets speed to constant speed (or 0)
     */
    private void shooterDefault() {
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(false);
        }
        double speedYouWant = constSpeed.getDouble(0);
        if (speedYouWant != 0) {
            isConstSpeed = true;
            if (!isConstSpeedLast) {
                isConstSpeedLast = true;
                leader.setPid(robotSettings.SHOOTER_CONST_SPEED_PID);
            }
            leader.moveAtVelocity(speedYouWant);
        } else {
            leader.moveAtPercent(0);
        }
        shooting = false;
        ballsShot = 0;
    }

    /**
     * if the shooter is actually at the requested speed
     *
     * @return if the shooter is actually at the requested speed
     */
    public boolean isAtSpeed() {
        return Math.abs(leader.getSpeed() - speed) < 200;
    }

    @Override
    public void updateAuton() {
        updateShuffleboard();
    }

    /**
     * Input is parsed and shooter object maintained appropriately.
     *
     * @throws IllegalStateException if control is not implemented for {@link frc.robot.robotconfigs.DefaultConfig#SHOOTER_CONTROL_STYLE
     *                               current control style}
     * @see frc.robot.robotconfigs.DefaultConfig#SHOOTER_CONTROL_STYLE
     * @see ShootingControlStyles
     */
    @Override
    public void updateGeneric() throws IllegalStateException {
        if (leader.getSpeed() > 1000) {
            if (robotSettings.ENABLE_DRIVE) {
                //xbox.rumble(0.1 * (leader.getSpeed() / 4200));
            }
        }
        if (ShootingControlStyles.getSendableChooser().getSelected() != null && robotSettings.SHOOTER_CONTROL_STYLE != ShootingControlStyles.getSendableChooser().getSelected()) {
            robotSettings.SHOOTER_CONTROL_STYLE = ShootingControlStyles.getSendableChooser().getSelected();
            if (Robot.turret != null)
                Robot.turret.updateControl();
            createControllers();
        }
        MotorDisconnectedIssue.handleIssue(this, leader, follower);
        updateShuffleboard();
        switch (robotSettings.SHOOTER_CONTROL_STYLE) {
            case STANDARD: {
                if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_FLIGHTSTICK.shoot(this);
                    isConstSpeed = false;
                } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                    isConstSpeed = false;
                } else {
                    if (robotSettings.ENABLE_HOPPER) {
                        hopper.setAll(false);
                    }
                    leader.moveAtPercent(0);
                    shooting = false;
                    ballsShot = 0;
                }
                if (robotSettings.ENABLE_SHOOTER_COOLING) {
                    if (panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) { //OFF
                        pneumatics.shooterCooling.set(false);
                    } else if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN) { //ON
                        pneumatics.shooterCooling.set(true);
                    }
                }
                break;
            }
            case STANDARD_OFFSEASON_2021: {
                if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_FLIGHTSTICK.shoot(this);
                    isConstSpeed = false;
                } else if ((panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN || panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN)) {
                    shooter.setSpeed(3700 + (500 * shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER)));
                    if (joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                        ShootingEnums.FIRE_HIGH_SPEED_SPINUP.shoot(this);
                    } else {
                        shooter.setShooting(false);
                        shooter.tryFiringBalls = false;
                        hopper.setAll(false);
                    }
                } else if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    tryFiringBalls = true;
                    if (articulatedHood.isAtWantedPosition) {
                        ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                        isConstSpeed = false;
                    }
                } else {
                    tryFiringBalls = false;
                    if (robotSettings.ENABLE_HOPPER) {
                        hopper.setAll(false);
                    }
                    leader.moveAtPercent(0);
                    ballsShot = 0;
                    shooterDefault();
                }
                if (robotSettings.ENABLE_SHOOTER_COOLING) {
                    if (panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) { //OFF
                        pneumatics.shooterCooling.set(false);
                    } else if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN) { //ON
                        pneumatics.shooterCooling.set(true);
                    }
                }
                break;
            }
            case EXPERIMENTAL_OFFSEASON_2021: {
                if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    if (!isSpinningUpHeld) {
                        isSpinningUp = !isSpinningUp;
                        isSpinningUpHeld = true;
                    }
                } else if (panel.get(ButtonPanelButtons.SOLID_SPEED) == ButtonStatus.UP) {
                    isSpinningUpHeld = false;
                }
                if (isSpinningUp) {
                    ShootingEnums.FIRE_SOLID_SPEED_OFFSEASON21.shoot(this);
                } else {
                    shooterDefault();
                }
                break;
            }
            case ACCURACY_2021: {
                if (Robot.articulatedHood.unTargeted) {
                    shooterDefault();
                } else if (panel.get(ControllerEnums.ButtonPanelTapedButtons.SOLID_SPEED) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_FLIGHTSTICK.shoot(this);
                    isConstSpeed = false;
                } else if (singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                    isConstSpeed = false;
                } else if (panel.get(ControllerEnums.ButtonPanelTapedButtons.SINGLE_SHOT) == ButtonStatus.DOWN) {
                    singleShot = true;
                } else {
                    shooterDefault();
                }
                if (robotSettings.ENABLE_SHOOTER_COOLING) {
                    if (panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) { //OFF
                        pneumatics.shooterCooling.set(false);
                    } else if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN) { //ON
                        pneumatics.shooterCooling.set(true);
                    }
                }
                break;
            }
            case SPEED_2021: {
                if (panel.get(ButtonPanelButtons.TARGET) == ButtonStatus.DOWN && joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_WITH_HOPPER_CONTROLLED.shoot(this);
                    //ShootingEnums.FIRE_TIMED.shoot(this);
                    isConstSpeed = false;
                } else if (panel.get(ButtonPanelButtons.HOPPER_IN) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_FLIGHTSTICK.shoot(this);
                    isConstSpeed = false;
                } else {
                    //shooterDefault();
                }
                if (robotSettings.ENABLE_SHOOTER_COOLING) {
                    if (panel.get(ButtonPanelButtons.AUX_BOTTOM) == ButtonStatus.DOWN) { //OFF
                        pneumatics.shooterCooling.set(false);
                    } else if (panel.get(ButtonPanelButtons.AUX_TOP) == ButtonStatus.DOWN) { //ON
                        pneumatics.shooterCooling.set(true);
                    }
                }
                break;
            }
            case FLIGHT_STICK: {
                if (joystickController.get(JoystickButtons.TWO) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_FLIGHTSTICK.shoot(this);
                } else if (joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_HIGH_SPEED.shoot(this);
                    isConstSpeed = false;
                } else {
                    if (robotSettings.ENABLE_HOPPER) {
                        hopper.setAll(false);
                    }
                    leader.moveAtPercent(0);
                    shooting = false;
                    ballsShot = 0;
                }
                break;
            }
            case BOP_IT: {
                if (joystickController.get(ControllerEnums.BopItButtons.PULLIT) == ButtonStatus.DOWN || singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                    isConstSpeed = false;
                } else {
                    shooting = false;
                    ballsShot = 0;
                    shooterDefault();
                }
                break;
            }
            case GUITAR: {
                if (joystickController.get(ControllerEnums.SixKeyGuitarButtons.HERO_POWER) == ButtonStatus.DOWN || singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                    isConstSpeed = false;
                } else {
                    shooting = false;
                    ballsShot = 0;
                    shooterDefault();
                }
                break;
            }

            case WII: {
                if (joystickController.get(ControllerEnums.WiiButton.TWO) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_WII.shoot(this);
                    isConstSpeed = false;
                } else if (joystickController.get(ControllerEnums.WiiButton.ONE) == ButtonStatus.DOWN || singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                    isConstSpeed = false;
                } else {
                    shooting = false;
                    ballsShot = 0;
                    shooterDefault();
                }
            }

            case DRUM_TIME: {
                if (joystickController.get(ControllerEnums.DrumButton.A) == ButtonStatus.DOWN) {
                    ShootingEnums.FIRE_SOLID_SPEED_DRUMS.shoot(this);
                    isConstSpeed = false;
                } else if (joystickController.get(ControllerEnums.DrumButton.ONE) == ButtonStatus.DOWN || singleShot) {
                    ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
                    isConstSpeed = false;
                } else {
                    shooting = false;
                    ballsShot = 0;
                    shooterDefault();
                }
            }
            case XBOX_CONTROLLER: {
                if (joystickController.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1) {
                    ShootingEnums.FIRE_SOLID_SPEED_XBOX_CONTROLLER.shoot(this);
                } else {
                    leader.moveAtPercent(0);
                    shooting = false;
                    ballsShot = 0;
                }
                break;
            }
            default:
                throw new IllegalStateException("This UI not implemented for this controller");
        }
    }

    @Override
    public void initTest() {
        initGeneric();
    }

    @Override
    public void initTeleop() {
        initGeneric();
    }

    @Override
    public void initAuton() {
        initGeneric();
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {
        isConstSpeedLast = false;
        isConstSpeed = false;
        singleShot = false;
        if (robotSettings.SHOOTER_CONTROL_STYLE == ShootingControlStyles.SPEED_2021) {
            leader.setPid(new PID(0.0025, 0.0000007, 0.03, 0));
        } else {
            leader.setPid(robotSettings.SHOOTER_PID);
        }
    }

    @Override
    public String getSubsystemName() {
        return "Shooter";
    }

    /**
     * Initialize the motors. Checks for SHOOTER_USE_SPARKS and SHOOTER_USE_TWO_MOTORS to allow modularity.
     *
     * @throws IllegalStateException If the motor configuration is not implemented
     */
    private void createAndInitMotors() throws IllegalStateException {
        switch (robotSettings.SHOOTER_MOTOR_TYPE) {
            case CAN_SPARK_MAX:
                leader = new SparkMotorController(robotSettings.SHOOTER_LEADER_ID);
                if (robotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new SparkMotorController(robotSettings.SHOOTER_FOLLOWER_ID);
                    follower.setSensorToRealDistanceFactor(1);
                }
                leader.setSensorToRealDistanceFactor(1);
                break;
            case TALON_FX:
                leader = new TalonMotorController(robotSettings.SHOOTER_LEADER_ID);
                if (robotSettings.SHOOTER_USE_TWO_MOTORS) {
                    follower = new TalonMotorController(robotSettings.SHOOTER_FOLLOWER_ID);
                    follower.setSensorToRealDistanceFactor(600 / robotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                }
                leader.setSensorToRealDistanceFactor(600 / robotSettings.SHOOTER_SENSOR_UNITS_PER_ROTATION);
                break;
            default:
                throw new IllegalStateException("No such supported shooter motor config for " + robotSettings.SHOOTER_MOTOR_TYPE.name());
        }

        leader.setInverted(robotSettings.SHOOTER_INVERTED);
        if (robotSettings.SHOOTER_USE_TWO_MOTORS) {
            follower.follow(leader, !robotSettings.SHOOTER_INVERTED).setCurrentLimit(80).setBrake(false);
        }
        leader.setCurrentLimit(80).setBrake(false).setOpenLoopRampRate(40).resetEncoder();
    }

    /**
     * If the shooter is at the requested speed
     *
     * @param rpm how fast it should be going
     * @return if the shooter is at the requested speed
     */
    public boolean isAtSpeed(int rpm) {
        return Math.abs(leader.getSpeed() - rpm) < 200;
    }

    /**
     * if the goal photon is in use and has a valid target in its sights
     *
     * @return if the goal photon is in use and has a valid target in its sights
     */
    public boolean isValidTarget() {
        return robotSettings.ENABLE_VISION && goalCamera.hasValidTarget();
    }

    /**
     * Gets the current speed of the leader motor
     *
     * @return the current speed of the leader motor based on the output units of {@link #leader}
     */
    public double getSpeed() {
        return leader.getSpeed();
    }

    /**
     * Set drive wheel RPM
     *
     * @param rpm speed to set
     */
    public void setSpeed(double rpm) {
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("set shooter speed to " + rpm);
        }
        speed = rpm;
        leader.moveAtVelocity(rpm);
    }

    /**
     * Getter for {@link #shooting}
     *
     * @return whether the shooter is shooting or not
     */
    public boolean isShooting() {
        return shooting;
    }

    void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    /**
     * Old method of firing a single shot out of the shooter. This is replaced by {@link Shooter#fireAmount(int)}
     *
     * @return true if the ball has been shot or false if the ball has not been shot yet
     */
    @Deprecated
    public boolean fireSingleShot() {
        singleShot = true;
        shooting = true;
        ShootingEnums.FIRE_SINGLE_SHOT.shoot(this);
        isConstSpeed = !singleShot;
        if (!singleShot) {
            shooting = false;
            setPercentSpeed(0);
            hopper.setAll(false);
        }
        updateShuffleboard();
        return !singleShot;
    }

    /**
     * Moves the shooter at a -1 to 1 percent speed
     *
     * @param percentSpeed percent from -1 to 1 to move the shooter at
     */
    public void setPercentSpeed(double percentSpeed) {
        leader.moveAtPercent(percentSpeed);
    }

    /**
     * Fires multiple balls without caring if it sees the target. Good for autonomous and the discord/slaque bot
     *
     * @param shots how many balls to shoot
     * @return if the balls have finished shooting
     * @author Smaltin
     */
    public boolean fireAmount(int shots) {
        ballsToShoot = shots;
        multiShot = true;
        ShootingEnums.FIRE_MULTIPLE_SHOTS.shoot(this);
        isConstSpeed = !multiShot;
        updateShuffleboard();
        if (!multiShot) {
            shooting = false;
            setPercentSpeed(0);
            hopper.setAll(false);
        }
        return !multiShot;
    }

    public boolean fireTimed(int seconds) {
        goalTicks = seconds*50; //tick = 20ms. 50 ticks in a second.
        if (!shooting) {
            ticksPassed = 0;
            shooting = true;
            multiShot = true;
        }
        ShootingEnums.FIRE_TIMED.shoot(this);
        updateShuffleboard();
        if (!multiShot) {
            shooting = false;
            setPercentSpeed(0);
            hopper.setAll(false);
        }
        return !multiShot;
    }

    /**
     * Used to change how the input is handled by the {@link Shooter} and what kind of controller to use
     */
    public enum ShootingControlStyles {
        STANDARD, BOP_IT, XBOX_CONTROLLER, ACCURACY_2021, SPEED_2021, STANDARD_2020, EXPERIMENTAL_OFFSEASON_2021, STANDARD_OFFSEASON_2021, WII, DRUM_TIME, GUITAR, FLIGHT_STICK;

        private static SendableChooser<ShootingControlStyles> myChooser;

        public static SendableChooser<ShootingControlStyles> getSendableChooser() {
            return Objects.requireNonNullElseGet(myChooser, () -> {
                myChooser = new SendableChooser<>();
                for (ShootingControlStyles style : ShootingControlStyles.values())
                    myChooser.addOption(style.name(), style);
                return myChooser;
            });
        }
    }
}