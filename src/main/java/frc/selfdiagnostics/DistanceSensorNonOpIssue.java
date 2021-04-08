package frc.selfdiagnostics;

import frc.misc.ISubsystem;
import frc.robot.Robot;

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

    public static void reportIssue(ISubsystem owner, String imuName) {
        IssueHandler.issues.put(owner, new DistanceSensorNonOpIssue(imuName));
    }

    public static void resolveIssue(ISubsystem owner) {
        IssueHandler.issues.remove(owner);
    }

    private DistanceSensorNonOpIssue(String name) {
        distanceSensorName = name;
    }

    @Override
    public String getRandomFix() {
        return String.format(fixes[Robot.RANDOM.nextInt(fixes.length)], distanceSensorName);
    }

}
