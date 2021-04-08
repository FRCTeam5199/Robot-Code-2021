package frc.ballstuff.intaking;

import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.VictorMotorController;
import frc.selfdiagnostics.MotorDisconnectedIssue;
import frc.vision.distancesensor.IDistanceSensor;
import frc.vision.distancesensor.RevDistanceSensor;

import static frc.robot.Robot.robotSettings;

/**
 * The Hopper subsystem effectively takes a ball from the front (where the {@link frc.ballstuff.intaking.Intake intake}
 * is ) to the {@link frc.ballstuff.shooting.Shooter}
 */
public class Hopper implements ISubsystem {
    private static final boolean DEBUG = true;
    private AbstractMotorController agitator, indexer;
    private IDistanceSensor indexSensor;
    private boolean agitatorActive = false, indexerActive = false;

    public Hopper() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        if (robotSettings.ENABLE_INDEXER_AUTO_INDEX) {
            indexSensor = new RevDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighAccuracy);
            System.out.println("Enabling index sensor.");
        }
        if (robotSettings.ENABLE_AGITATOR)
            agitator = new VictorMotorController(robotSettings.AGITATOR_MOTOR_ID);
        if (robotSettings.ENABLE_INDEXER)
            indexer = new VictorMotorController(robotSettings.INDEXER_MOTOR_ID);
    }

    @Override
    public void updateTest() {
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {
        if (!robotSettings.autonComplete) {
            updateTeleop();
            agitator.moveAtPercent(0.6);
        } else {
            agitator.moveAtPercent(0);
            indexer.moveAtPercent(0);
        }
    }

    /**
     * Uses the distance sensor to determine if there is a ball in the indxer. Enable and disable the indexer using
     * {@link frc.robot.robotconfigs.DefaultConfig#ENABLE_INDEXER_AUTO_INDEX}
     *
     * @return distance as read by {@link #indexSensor} assuming it is {@link frc.robot.robotconfigs.DefaultConfig#ENABLE_INDEXER_AUTO_INDEX
     * enabled}
     */
    public double indexerSensorRange() {
        if (robotSettings.ENABLE_INDEXER_AUTO_INDEX) {
            return indexSensor.getDistance();
        }
        return -2;
    }

    public boolean isIndexed() {
        return robotSettings.ENABLE_INDEXER_AUTO_INDEX && indexerSensorRange() < robotSettings.INDEXER_DETECTION_CUTOFF_DISTANCE;
    }

    /**
     * Runs every tick. Runs the indexer and agitator motors.
     */
    @Override
    public void updateGeneric() {
        if (robotSettings.ENABLE_INDEXER) {
            if (indexer.failureFlag) {
                MotorDisconnectedIssue.reportIssue(this, robotSettings.INDEXER_MOTOR_ID, indexer.getSuggestedFix());
            } else {
                MotorDisconnectedIssue.resolveIssue(this, robotSettings.INDEXER_MOTOR_ID);
            }
        }
        if (robotSettings.ENABLE_AGITATOR) {
            if (agitator.failureFlag) {
                MotorDisconnectedIssue.reportIssue(this, robotSettings.AGITATOR_MOTOR_ID, indexer.getSuggestedFix());
            } else {
                MotorDisconnectedIssue.resolveIssue(this, robotSettings.AGITATOR_MOTOR_ID);
            }
        }
        if (!indexerActive && !agitatorActive) {
            if (robotSettings.ENABLE_INDEXER) {
                if (robotSettings.ENABLE_INDEXER_AUTO_INDEX) {
                    indexer.moveAtPercent(indexerSensorRange() > robotSettings.INDEXER_DETECTION_CUTOFF_DISTANCE ? 0.3 : 0);
                } else {
                    indexer.moveAtPercent(0);
                }
            } //2021 COMP 4 & 2020 COMP 9
            if (robotSettings.ENABLE_AGITATOR) {
                if (robotSettings.ENABLE_INDEXER_AUTO_INDEX) {
                    agitator.moveAtPercent(indexerSensorRange() > robotSettings.INDEXER_DETECTION_CUTOFF_DISTANCE ? 0.5 : 0);
                } else {
                    agitator.moveAtPercent(0);
                }
            }
        } else {
            if (robotSettings.ENABLE_INDEXER) {
                indexer.moveAtPercent(indexerActive ? 0.9 : 0);
            }
            if (robotSettings.ENABLE_AGITATOR) {
                agitator.moveAtPercent(agitatorActive ? 0.6 : 0);
            }
        }
        if (robotSettings.DEBUG && DEBUG) {
            UserInterface.smartDashboardPutBoolean("indexer enable", indexerActive);
            UserInterface.smartDashboardPutBoolean("agitator enable", agitatorActive);
            UserInterface.smartDashboardPutNumber("indexer sensor", indexerSensorRange());
            UserInterface.smartDashboardPutBoolean("hopper indexed", isIndexed());
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

    @Override
    public String getSubsystemName() {
        return "Hopper";
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
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Agitator set to " + set);
        }
    }

    /**
     * applies settings/toggles Indexer on/off
     *
     * @param set a boolean to determine wether or not Indexer is turned on/off
     */
    public void setIndexer(boolean set) {
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Indexer set to " + set);
        }
        indexerActive = set;
    }
}