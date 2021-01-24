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
import frc.controllers.XBoxController;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.vision.GoalChameleon;

import static frc.robot.Robot.hopper;

public class Shooter implements ISubsystem {

    public final String[] data = {
            "match time", "init time", "speed", "target speed", "motor temperature", "motor current", "powered", "P",
            "I", "D", "rP", "rI", "rD", "distance"
    };
    public final String[] units = {
            "seconds", "seconds", "rpm", "rpm", "C", "A", "T/F", "num", "num", "num", "num", "num", "num", "meters"
    };
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
    private GoalChameleon chameleon;
    // private ShuffleboardTab tab = Shuffleboard.getTab("Shooter");
    // private NetworkTableEntry shooterSpeed = tab.add("Shooter Speed", 0).getEntry();
    // private NetworkTableEntry shooterToggle = tab.add("Shooter Toggle", false).getEntry();
    // private NetworkTableEntry manualSpeedOverride = tab.add("SPEED OVERRIDE", false).getEntry();
    // private NetworkTableEntry rampRate = tab.add("Ramp Rate", 40).getEntry();
    private XBoxController xBoxController;
    private boolean enabled = true;
    private JoystickController joystickController;
    private ButtonPanel panel;
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
        joystickController = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
        panel = new ButtonPanel(RobotNumbers.BUTTON_PANEL_SLOT);

        shootTimer = new Timer();
        indexTimer = new Timer();
        indexTimer.stop();
        indexTimer.reset();
        shootTimer.stop();
        shootTimer.reset();
        chameleon = new GoalChameleon();

    }

    private void createAndInitMotors() {
        if (RobotToggles.SHOOTER_USE_SPARKS) {
            leader = new CANSparkMax(RobotMap.SHOOTER_LEADER, MotorType.kBrushless);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                follower = new CANSparkMax(RobotMap.SHOOTER_FOLLOWER, MotorType.kBrushless);
            }

            leader.setInverted(true);
            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                follower.follow(leader, true);
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
            falconLeader.setInverted(TalonFXInvertType.CounterClockwise);

            if (RobotToggles.SHOOTER_USE_TWO_MOTORS) {
                falconFollower = new TalonFX(RobotMap.SHOOTER_FOLLOWER);
                falconFollower.setInverted(TalonFXInvertType.Clockwise);
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
            actualRPM = falconLeader.getSelectedSensorVelocity(); //do math: 4096 units/rotation, units/100ms
            actualRPM *= 600 / RobotNumbers.SHOOTER_SENSOR_UNITS_PER_ROTATION;
        }
        checkState();
        //put code here to set speed based on distance to goal
        boolean disabled = panel.get(ButtonPanelButtons.SOLID_SPEED) != ButtonStatus.DOWN;

        if (!disabled) {
            
            speed = 4200 * ((joystickController.get(JoystickAxis.SLIDER) * 0.25) + 1); //4200
        } else {
            speed = 0;
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
        if (RobotToggles.SHOOTER_USE_SPARKS){
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
        }else {
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
        return chameleon.validTarget();
    }

    public void fireHighSpeed() {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        // boolean runDisable = hopper.disableOverride.getBoolean(false);
        toggle(true);
        hopper.setAgitator((spunUp() || recovering() || false) && (validTarget() || false) && !false);
        hopper.setAgitator((spunUp() || recovering() || false) && (validTarget() || false) && !false);
        hopper.setIndexer((spunUp() || recovering() || false) && (validTarget() || false) && !false);
    }

    public void fireHighAccuracy() {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        boolean runDisable = false;//hopper.disableOverride.getBoolean(false);
        toggle(true);
        hopper.setAgitator((atSpeed() || false));//&&(validTarget()||visOverride)&&!runDisable);
        hopper.setIndexer((atSpeed() || false));//&&(validTarget()||visOverride)&&!runDisable);
    }

    private void fireMixed() {
        shooting = joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN;
        if (shooting) {
            if (atSpeed() && hopper.indexed) {
                if (!timerStarted) {
                    shootTimer.start();
                    timerStarted = true;
                }
                if (shootTimer.hasPeriodPassed(0.1)) {
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                hopper.setIndexer(false);
                hopper.setAgitator(false);
                shootTimer.stop();
                shootTimer.reset();
                timerStarted = false;
            }
        } else {
            hopper.setIndexer(false);
            hopper.setAgitator(false);
            shootTimer.stop();
            shootTimer.reset();
            timerStarted = false;
        }
    }

    private void fireIndexerDependent() {
        if (joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
            hopper.setIndexer(atSpeed && hopper.indexed);
        }
    }

    public void fireTimed() {
        if (joystickController.get(JoystickButtons.ONE) == ButtonStatus.DOWN) {
            shooting = true;
            if (atSpeed()) {
                if (!timerStarted) {
                    shootTimer.start();
                    timerStarted = true;
                }
                if (shootTimer.hasPeriodPassed(0.5)) {
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                hopper.setIndexer(false);
                hopper.setAgitator(false);
                shootTimer.stop();
                shootTimer.reset();
                timerStarted = false;
            }
        } else {
            shooting = false;
            hopper.setIndexer(false);
            hopper.setAgitator(false);
            shootTimer.stop();
            shootTimer.reset();
            timerStarted = false;
        }
    }

    public void feedIn() {

    }

    public void stopFiring() {
        toggle(false);
        hopper.setAgitator(false);
        hopper.setIndexer(false);
        shooting = false;
    }

    public void setupShooterTimer() {
        shooterTimer = new Timer();
        timerFlag = false;
        shooterTimer.stop();
        shooterTimer.reset();
        stopFiring();
    }

    public void fireThreeBalls() {
        fireHighAccuracy();
        shooting = true;
        allBallsFired = false;
        //return true if speed has been at target speed for a certain amount of time
        // if(atSpeed&&shooterTimer.get()>2){
        //     shooterTimer.stop();   //stop the timerasw
        //     //shooterTimer.reset();  //set the timer to zero
        //     stopFiring();          //stop firing
        //     allBallsFired = true;
        // }

        //if the shooter is at speed, reset and start the timer

        timerFlag = atSpeed();
        if (atSpeed()) {
            if (!timerFlag) {
                shooterTimer.reset();
                shooterTimer.start();
                timerFlag = true;
                System.out.println("Starting Timer");
            }
        } else {
            timerFlag = false;
            shooterTimer.stop();
            System.out.println("Stopping Timer");
            //shooterTimer.reset();
        }
        if ((atSpeed()) && shooterTimer.get() > 0.4) {
            stopFiring();
            shooterTimer.stop();
            shooterTimer.reset();
            allBallsFired = true;
            System.out.println("STOPPING THINGS!!!!!!");
        }

        System.out.println(shooterTimer.get() + " " + (actualRPM > speed - 50));
    }
}
