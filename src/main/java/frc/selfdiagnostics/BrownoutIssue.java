package frc.selfdiagnostics;

import frc.gpws.Sound;
import frc.gpws.SoundManager;
import frc.misc.ISubsystem;
import frc.robot.Main;

public class BrownoutIssue implements ISimpleIssue{
    public static void handleIssue(ISubsystem owner, boolean report) {
        if (!report)
            resolveIssue(owner);
        else
            reportIssue(owner);
    }

    private static void resolveIssue(ISubsystem owner) {
        if (IssueHandler.issues.get(owner) instanceof BrownoutIssue) {
            IssueHandler.issues.remove(owner);
        }
    }

    private static void reportIssue(ISubsystem owner) {
        Main.pipeline.sendSound(new Sound(SoundManager.SoundPacks.Jojo, SoundManager.Sounds.Brownout, SoundManager.Sounds.Brownout, SoundManager.Sounds.Brownout, SoundManager.Sounds.Replace, SoundManager.Sounds.Battery, SoundManager.Sounds.Replace, SoundManager.Sounds.Battery));
        if (!IssueHandler.issues.containsKey(owner) || !(IssueHandler.issues.get(owner) instanceof BrownoutIssue)) {
            System.err.println(">>>>>>>BROWNOUT DETECTED<<<<<<<");
            IssueHandler.issues.put(owner, new BrownoutIssue());
        }
    }

    private BrownoutIssue() {

    }
    @Override
    public String getRandomFix() {
        return null;
    }
}
