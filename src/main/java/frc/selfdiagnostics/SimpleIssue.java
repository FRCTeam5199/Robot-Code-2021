package frc.selfdiagnostics;

import frc.misc.ISubsystem;
import frc.misc.UserInterface;

public interface SimpleIssue {
    static void robotPeriodic() {
        if (UserInterface.GET_RANDOM_FIX.getEntry().getBoolean(false)) {
            UserInterface.GET_RANDOM_FIX.getEntry().setBoolean(false);
            for (ISubsystem iSubsystem : IssueHandler.issues.keySet()) {
                UserInterface.smartDashboardPutString(iSubsystem.getSubsystemName(), IssueHandler.issues.get(iSubsystem).getRandomFix());
            }
        }
    }

    String getRandomFix();
}
