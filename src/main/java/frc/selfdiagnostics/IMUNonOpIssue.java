package frc.selfdiagnostics;

import frc.gpws.Sound;
import frc.gpws.SoundManager;
import frc.misc.ISubsystem;
import frc.robot.Main;

import static frc.robot.Robot.robotSettings;

/**
 * Pretty self explanatory. This issue is regarding an non operational IMU
 */
public class IMUNonOpIssue implements ISimpleIssue {
    private static final String[] fixes = {"Ensure the %1$s is plugged in", "Ensure the %1$s is listening for the right port", "Watch the print stream for warnings and ensure the resets go through"};
    private final String imuName;

    public static void handleIssue(ISubsystem owner, String imuName, boolean report) {
        if (report)
            resolveIssue(owner);
        else
            reportIssue(owner, imuName);
    }

    private static void resolveIssue(ISubsystem owner) {
        if (IssueHandler.issues.get(owner) instanceof IMUNonOpIssue) {
            System.out.println("Cringe ahhahahahahahaa op");
            if (robotSettings.ENABLE_MEMES)
            Main.pipeline.sendSound(new Sound(SoundManager.SoundPacks.Jojo, SoundManager.Sounds.IMU, SoundManager.Sounds.Reconnected));
            IssueHandler.issues.remove(owner);
        }
    }

    private static void reportIssue(ISubsystem owner, String imuName) {
        if (!IssueHandler.issues.containsKey(owner) || !(IssueHandler.issues.get(owner) instanceof IMUNonOpIssue)) {
            System.out.println("Cringe ahhahahahahahaa nonop");
            IssueHandler.issues.put(owner, new IMUNonOpIssue(imuName));
            Main.pipeline.sendSound(new Sound(SoundManager.SoundPacks.Jojo, SoundManager.Sounds.IMU, SoundManager.Sounds.NonOperational));
        }
    }

    private IMUNonOpIssue(String name) {
        imuName = name;
    }

    @Override
    public String getRandomFix() {
        return String.format(fixes[Main.RANDOM.nextInt(fixes.length)], imuName);
    }
}
