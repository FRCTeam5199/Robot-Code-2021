package frc.selfdiagnostics;

import frc.misc.ISubsystem;
import frc.robot.Robot;

/**
 * Pretty self explanatory. This issue is regarding an non operational Motor
 */
public class MotorDisconnectedIssue implements ISimpleIssue {
    private static final String[] fixes = {"Ensure there is a motor with an id of %d", "Ensure motor with id %d is plugged in", "Ensure motor %d can spin freely", "Verify motor %d is not boiling"};
    private final int faultedMotor;
    private final boolean knownFix;
    private final String fix;

    public static void reportIssue(ISubsystem owner, int id) {
        reportIssue(owner, id, "");
    }

    public static void reportIssue(ISubsystem owner, int id, String fix) {
        System.out.println("Issue reported!");
        IssueHandler.issues.put(owner, new MotorDisconnectedIssue(id, fix));
    }

    public static void resolveIssue(ISubsystem owner, int fixedMotorID) {
        if (IssueHandler.issues.get(owner) instanceof MotorDisconnectedIssue)
            if (((MotorDisconnectedIssue) IssueHandler.issues.get(owner)).faultedMotor == fixedMotorID)
                IssueHandler.issues.remove(owner);
    }

    private MotorDisconnectedIssue(int motorID, String theFix) {
        faultedMotor = motorID;
        knownFix = !theFix.equals("");
        fix = theFix;
    }

    @Override
    public String getRandomFix() {
        return knownFix ? fix : "Not sure, " + String.format(fixes[Robot.RANDOM.nextInt(fixes.length)], faultedMotor);
    }
}
