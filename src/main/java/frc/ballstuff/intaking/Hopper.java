package frc.ballstuff.intaking;

import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.VictorMotorController;
import frc.robot.RobotSettings;

/**
 * The Hopper subsystem effectively takes a ball from the front (where the {@link frc.ballstuff.intaking.Intake intake}
 * is ) to the {@link frc.ballstuff.shooting.Shooter}
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
        if (RobotSettings.ENABLE_INDEXER_AUTO_INDEX) {
            indexSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighAccuracy);
            indexSensor.setEnabled(true);
            indexSensor.setAutomaticMode(true);
        }
        if (RobotSettings.ENABLE_AGITATOR)
            agitator = new VictorMotorController(RobotSettings.AGITATOR_MOTOR_ID);
        if (RobotSettings.ENABLE_INDEXER)
            indexer = new VictorMotorController(RobotSettings.INDEXER_MOTOR_ID);
    }

    @Override
    public void updateTest() {
        //updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    /**
     * Uses the distance sensor to determine if there is a ball in the indxer. Enable and disable the indexer using
     * {@link RobotSettings#ENABLE_INDEXER_AUTO_INDEX}
     *
     * @return distance as read by {@link #indexSensor} assuming it is {@link RobotSettings#ENABLE_INDEXER_AUTO_INDEX enabled}
     */
    public double indexerSensorRange() {
        if (RobotSettings.ENABLE_INDEXER_AUTO_INDEX) {
            return indexSensor.getRange();
        }
        return -2;
    }

    @Override
    public void updateAuton() {
    }

    /**
     * Runs every tick. Runs the indexer and agitator motors.
     */
    @Override
    public void updateGeneric() {
        if (RobotSettings.DEBUG) {
            UserInterface.putBoolean("indexer enable", indexerActive);
            UserInterface.putBoolean("agitator enable", agitatorActive);
            UserInterface.putNumber("indexer sensor", indexerSensorRange());
        }
        if (!indexerActive && !agitatorActive) {
            if (RobotSettings.ENABLE_INDEXER) {
                if (RobotSettings.ENABLE_INDEXER_AUTO_INDEX) {
                    indexer.moveAtPercent(indexerSensorRange() > 9 ? 0.4 : 0);
                } else {
                    indexer.moveAtPercent(0);
                }
            }
            if (RobotSettings.ENABLE_AGITATOR) {
                if (RobotSettings.ENABLE_INDEXER_AUTO_INDEX) {
                    agitator.moveAtPercent(indexerSensorRange() > 9 ? 0.3 : 0);
                } else {
                    agitator.moveAtPercent(0);
                }
            }
            indexed = (RobotSettings.ENABLE_INDEXER_AUTO_INDEX && indexerSensorRange() > 9);
        } else {
            if (RobotSettings.ENABLE_INDEXER) {
                indexer.moveAtPercent(indexerActive ? 0.8 : 0);
            }
            if (RobotSettings.ENABLE_AGITATOR) {
                agitator.moveAtPercent(agitatorActive ? 0.6 : 0);
            }
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
        if (RobotSettings.DEBUG) {
            System.out.println("Agitator set to " + set);
        }
    }

    /**
     * applies settings/toggles Indexer on/off
     *
     * @param set a boolean to determine wether or not Indexer is turned on/off
     */
    public void setIndexer(boolean set) {
        if (RobotSettings.DEBUG) {
            System.out.println("Indexer set to " + set);
        }
        indexerActive = set;
    }
}