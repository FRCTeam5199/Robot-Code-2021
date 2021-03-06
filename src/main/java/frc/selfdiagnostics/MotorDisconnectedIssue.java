package frc.selfdiagnostics;

import frc.misc.ISubsystem;
import frc.robot.Robot;

public class MotorDisconnectedIssue implements ISimpleIssue {
    private static final String[] fixes = {"Ensure there is a motor with an id of %d", "Ensure motor with id %d is plugged in", "Ensure motor %d can spin freely", "Verify motor %d is not boiling"};
    private final int faultedMotor;

    public static void reportIssue(ISubsystem owner, int id) {
        System.out.println("Issue reported!");
        IssueHandler.issues.put(owner, new MotorDisconnectedIssue(id));
    }

    private MotorDisconnectedIssue(int motorID) {
        faultedMotor = motorID;
    }

    @Override
    public String getRandomFix() {
        return String.format(fixes[Robot.RANDOM.nextInt(fixes.length)], faultedMotor);
    }
}
