package frc.selfdiagnostics;

import frc.misc.ISubsystem;
import frc.misc.UserInterface;

/**
 * The purpose of an Issue is to help the user to detect and rectify problems ASAP. When something is not working
 * nominally, it should {@link IMUNonOpIssue#reportIssue(ISubsystem, String) report it}. If the issue resolves itself,
 * remove it with {@link IMUNonOpIssue#resolveIssue(ISubsystem)} (note these are not inherited because in java static
 * methods arent inherited but use the template so that you can use {@link #robotPeriodic()} and {@link
 * #getRandomFix()}
 *
 * @author jojo2357
 */
public interface ISimpleIssue {
    /**
     * This should run every tick to detect a user's request for a random fix and then provides it upon request
     *
     * @see #getRandomFix()
     */
    static void robotPeriodic() {
        if (UserInterface.GET_RANDOM_FIX.getEntry().getBoolean(false)) {
            UserInterface.GET_RANDOM_FIX.getEntry().setBoolean(false);
            System.out.println("Issues reported: " + IssueHandler.issues.keySet().size());
            for (ISubsystem iSubsystem : IssueHandler.issues.keySet()) {
                String fix = IssueHandler.issues.get(iSubsystem).getRandomFix();
                System.out.println("Heres an idea: " + fix);
                UserInterface.smartDashboardPutString(iSubsystem.getSubsystemName(), fix);
            }
        }
    }

    /**
     * Get a potential fix for this issue. Returns should use {@link String#format(String, Object...)} in order to
     * insert names/id's etc. Called from {@link #robotPeriodic()}
     *
     * @return A random fix, formatted
     */
    String getRandomFix();
}
