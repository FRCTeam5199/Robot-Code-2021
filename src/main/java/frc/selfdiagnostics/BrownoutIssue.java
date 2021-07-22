package frc.selfdiagnostics;

import frc.gpws.Alarms;
import frc.misc.ClientSide;
import frc.misc.ISubsystem;
import frc.robot.Main;

import static frc.robot.Robot.robotSettings;

/**
 *
 */
@ClientSide
public class BrownoutIssue implements ISimpleIssue {
    /**
     * Unresolved issues from your childhood? no matter! just use a brownout issue!
     *
     * @param owner  should be {@link frc.pdp.PDP}
     * @param report true to report
     */
    public static void handleIssue(ISubsystem owner, boolean report) {
        if (report) {
            reportIssue(owner);
        }
    }

    private static void reportIssue(ISubsystem owner) {
        if (robotSettings.ENABLE_MEMES)
        Main.pipeline.sendAlarm(Alarms.Brownout, true);
        if (!IssueHandler.issues.containsKey(owner) || !(IssueHandler.issues.get(owner) instanceof BrownoutIssue)) {
            System.err.println(">>>>>>>BROWNOUT DETECTED<<<<<<<");
            IssueHandler.issues.put(owner, new BrownoutIssue());
        }
    }

    private static void resolveIssue(ISubsystem owner) {
        if (IssueHandler.issues.get(owner) instanceof BrownoutIssue) {
            IssueHandler.issues.remove(owner);
        }
    }

    private BrownoutIssue() {

    }

    @Override
    public String getRandomFix() {
        return "Replace battery";
    }
}
