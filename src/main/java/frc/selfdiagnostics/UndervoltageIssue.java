package frc.selfdiagnostics;

import frc.gpws.Sound;
import frc.gpws.SoundManager;
import frc.misc.ISubsystem;
import frc.robot.Main;

public class UndervoltageIssue implements ISimpleIssue {
    public static void handleIssue(ISubsystem owner, boolean report) {
        if (report)
            resolveIssue(owner);
        else
            reportIssue(owner);
    }

    private static void resolveIssue(ISubsystem owner) {
        if (IssueHandler.issues.get(owner) instanceof UndervoltageIssue) {
            IssueHandler.issues.remove(owner);
        }
    }

    private static void reportIssue(ISubsystem owner) {
        if (!IssueHandler.issues.containsKey(owner) || !(IssueHandler.issues.get(owner) instanceof UndervoltageIssue)) {
            System.err.println(">>>>>>>POSSIBLE BROWNOUT DETECTED<<<<<<<");
            Main.pipeline.sendSound(new Sound(SoundManager.SoundPacks.Jojo, SoundManager.Sounds.Battery, SoundManager.Sounds.Low));
            IssueHandler.issues.put(owner, new UndervoltageIssue());
        }
    }

    private UndervoltageIssue() {

    }

    @Override
    public String getRandomFix() {
        return "Replace the battery or turn off unused subsystems";
    }
}
