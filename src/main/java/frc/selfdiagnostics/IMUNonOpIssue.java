package frc.selfdiagnostics;

import frc.misc.ISubsystem;
import frc.robot.Robot;

public class IMUNonOpIssue implements SimpleIssue {
    private static final String[] fixes = {"Ensure the %1$s is plugged in", "Ensure the %1$s is listening for the right port", "Watch the print stream for warnings and ensure the resets go through"};
    private final String imuName;

    public static void reportIssue(ISubsystem owner, String imuName) {
        IssueHandler.issues.put(owner, new IMUNonOpIssue(imuName));
    }

    private IMUNonOpIssue(String name) {
        imuName = name;
    }

    @Override
    public String getRandomFix() {
        return String.format(fixes[Robot.RANDOM.nextInt(fixes.length)], imuName);
    }
}
