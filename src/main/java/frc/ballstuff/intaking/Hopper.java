package frc.ballstuff.intaking;

import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.misc.ISubsystem;
import frc.motors.AbstractMotorController;
import frc.motors.VictorMotorController;
import frc.robot.RobotMap;
import frc.robot.RobotToggles;

/**
 * The Hopper subsystem effectively takes a ball from the front (where the {@link frc.ballstuff.intaking.Intake intake} is )
 * to the {@link frc.ballstuff.shooting.Shooter}
 */
public class Hopper implements ISubsystem {
    public AbstractMotorController agitator, indexer;
    public Rev2mDistanceSensor indexSensor;
    public boolean indexed = false;
    private boolean agitatorActive = false;
    private boolean indexerActive = false;

    public Hopper() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        if (RobotToggles.INDEXER_AUTO_INDEX) {
            indexSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighAccuracy);
            indexSensor.setEnabled(true);
            indexSensor.setAutomaticMode(true);
        }
        agitator = new VictorMotorController(RobotMap.AGITATOR_MOTOR);
        indexer = new VictorMotorController(RobotMap.INDEXER_MOTOR);
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    public double indexerSensorRange() {
        if (RobotToggles.INDEXER_AUTO_INDEX) {
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
            indexer.moveAtPercent(indexerSensorRange() > 9 ? 0.4 : 0);
            agitator.moveAtPercent(indexerSensorRange() > 9 ? 0.3 : 0);
            indexed = indexerSensorRange() > 9;
        } else {
            indexer.moveAtPercent(indexerActive ? 0.8 : 0);
            agitator.moveAtPercent(agitatorActive ? 0.6 : 0);
            indexed = true;//indexerSensorRange() > 9;
        }
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
     * applies settings/toggles Agitator and Indexer on/off
     *
     * @param set a boolean to determine wether or not Agitator and Indexer is turned on/off
     */
    public void setAll(boolean set) {
        setAgitator(set);
        setIndexer(set);
    }

    /**
     * applies settings/toggles Agitator on/off
     *
     * @param set a boolean to determine wether or not Agitator is turned on/off
     */
    public void setAgitator(boolean set) {
        agitatorActive = set;
        if (RobotToggles.DEBUG) {
            System.out.println("Agitator set to " + set);
        }
    }

    /**
     * applies settings/toggles Indexer on/off
     *
     * @param set a boolean to determine wether or not Indexer is turned on/off
     */
    public void setIndexer(boolean set) {
        if (RobotToggles.DEBUG) {
            System.out.println("Indexer set to " + set);
        }
        indexerActive = set;
    }
}