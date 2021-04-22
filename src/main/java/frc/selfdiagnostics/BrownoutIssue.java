package frc.selfdiagnostics;

import frc.gpws.Alarms;
import frc.gpws.Sound;
import frc.gpws.SoundManager;
import frc.misc.ISubsystem;
import frc.robot.Main;

public class BrownoutIssue implements ISimpleIssue {
    public static void handleIssue(ISubsystem owner, boolean report) {
        if (report) {
            reportIssue(owner);
        }
    }

    private static void reportIssue(ISubsystem owner) {
        if (!IssueHandler.issues.containsKey(owner) || !(IssueHandler.issues.get(owner) instanceof BrownoutIssue)) {
            Main.pipeline.sendAlarm(Alarms.Brownout);
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
