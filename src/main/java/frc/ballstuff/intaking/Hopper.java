package frc.ballstuff.intaking;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
import frc.controllers.ButtonPanel;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

/**
 * The Hopper subsystem effectively takes a ball from the front (where the {@link frc.ballstuff.intaking.Intake intake} is )
 * to the {@link frc.ballstuff.shooting.Shooter}
 */
public class Hopper implements ISubsystem {
    public VictorSPX agitator, indexer;
    public Rev2mDistanceSensor indexSensor;
    public boolean autoIndex = true;
    public boolean indexed = false;
    private double fireOffset = 0;
    // private ShuffleboardTab tab = Shuffleboard.getTab("balls");
    // private NetworkTableEntry aSpeed = tab.add("Agitator Speed", 0.6).getEntry();
    // private NetworkTableEntry iSpeed = tab.add("Indexer Speed", 0.7).getEntry();
    // public NetworkTableEntry visionOverride = tab.add("VISION OVERRIDE", false).getEntry();
    // public NetworkTableEntry spinupOverride = tab.add("SPINUP OVERRIDE", false).getEntry();
    // public NetworkTableEntry disableOverride = tab.add("LOADING DISABLE", false).getEntry();
    private boolean isReversed = false;
    private boolean isForced = false;
    private boolean agitatorActive = false;
    private boolean indexerActive = false;

    public Hopper() {
        init();
    }

    @Override
    public void init() {
        if (autoIndex) {
            indexSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighAccuracy);
            indexSensor.setEnabled(true);
            indexSensor.setAutomaticMode(true);
        }
        agitator = new VictorSPX(RobotMap.AGITATOR_MOTOR);
        indexer = new VictorSPX(RobotMap.INDEXER_MOTOR);
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    public double indexerSensorRange() {
        if (autoIndex) {
            return indexSensor.getRange();
        }
        return -2;
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }

    /**
     * Runs every tick. Runs the indexer and agitator motors.
     */
    @Override
    public void updateGeneric() {
        if (RobotToggles.DEBUG) {
            SmartDashboard.putBoolean("indexer enable", indexerActive);
            SmartDashboard.putBoolean("agitator enable", agitatorActive);
            SmartDashboard.putNumber("indexer sensor", indexerSensorRange());
        }
        if (!indexerActive && !agitatorActive) {
            indexer.set(ControlMode.PercentOutput, indexerSensorRange() > 9 ? 0.3 : 0);
            agitator.set(ControlMode.PercentOutput, indexerSensorRange() > 9 ? 0.3 : 0);
            indexed = indexerSensorRange() > 9;
        } else {
            indexer.set(ControlMode.PercentOutput, indexerActive ? 0.8 : 0);
            agitator.set(ControlMode.PercentOutput, agitatorActive ? 0.6 : 0);
            indexed = indexerSensorRange() > 9;
        }
    }

    public void setAll(boolean set) {
        setAgitator(set);
        setIndexer(set);
    }

    public void setAgitator(boolean set) {
        agitatorActive = set;
        if(RobotToggles.DEBUG){
            System.out.println("Agitator set to " + set);
        }
    }

    public void setIndexer(boolean set) {
        if (RobotToggles.DEBUG){
            System.out.println("Indexer set to " + set);
        }
        indexerActive = set;
    }

    public void setReverse(boolean reverse) {
        isReversed = reverse;
    }

    public void setForced(boolean forced) {
        isForced = forced;
    }
}