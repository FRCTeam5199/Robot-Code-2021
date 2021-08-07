package frc.selfdiagnostics;

import frc.gpws.Sound;
import frc.gpws.SoundManager;
import frc.misc.ISubsystem;
import frc.robot.Main;

import static frc.robot.Robot.robotSettings;

/**
 * Use this when the {@link frc.vision.distancesensor.IDistanceSensor Distance sensor} appears to be
 * unresponsive/disconnected
 *
 * @author jojo2357
 */
public class DistanceSensorNonOpIssue implements ISimpleIssue {
    /**
     * The possible fixes for this class. No, it cannot go in {@link ISimpleIssue} since it is static to this class
     */
    private static final String[] fixes = {"Ensure the %1$s is plugged in", "Ensure the %1$s is listening for the right port"};
    private final String distanceSensorName;

    private DistanceSensorNonOpIssue(String name) {
        distanceSensorName = name;
    }

    public static void handleIssue(ISubsystem owner, String imuName, boolean report) {
        if (report)
            resolveIssue(owner);
        else
            reportIssue(owner, imuName);
    }

    private static void resolveIssue(ISubsystem owner) {
        if (IssueHandler.issues.get(owner) instanceof DistanceSensorNonOpIssue) {
            if (robotSettings.ENABLE_MEMES)
                Main.pipeline.sendSound(new Sound(SoundManager.SoundPacks.Jojo, SoundManager.Sounds.Distance, SoundManager.Sounds.Sensor, SoundManager.Sounds.Reconnected));
            IssueHandler.issues.remove(owner);
        }
    }

    private static void reportIssue(ISubsystem owner, String imuName) {
        if (!IssueHandler.issues.containsKey(owner) || !(IssueHandler.issues.get(owner) instanceof DistanceSensorNonOpIssue)) {
            if (robotSettings.ENABLE_MEMES)
                Main.pipeline.sendSound(new Sound(SoundManager.SoundPacks.Jojo, SoundManager.Sounds.Distance, SoundManager.Sounds.Sensor, SoundManager.Sounds.Disconnected));
            IssueHandler.issues.put(owner, new DistanceSensorNonOpIssue(imuName));
        }
    }

    @Override
    public String getRandomFix() {
        return String.format(fixes[Main.RANDOM.nextInt(fixes.length)], distanceSensorName);
    }
}
