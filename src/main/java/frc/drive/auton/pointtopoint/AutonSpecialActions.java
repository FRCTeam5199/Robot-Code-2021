package frc.drive.auton.pointtopoint;

/**
 * Used by {@link AutonManager Point to Point} to do cool things with the robot. Processed in {@link
 * AutonManager#updateAuton()}.
 *
 * @author Smaltin
 */
public enum AutonSpecialActions {
    /**
     * You're all good, do nothing
     */
    NONE,
    /**
     * Fire a ball using the shooter
     */
    SHOOT_ONE,
    /**
     * Fire two balls using the shooter
     */
    SHOOT_TWO,
    /**
     * Fire three balls using the shooter
     */
    SHOOT_THREE,
    /**
     * Fire four balls using the shooter
     */
    SHOOT_FOUR,
    /**
     * Fire all (5) balls using the shooter
     */
    SHOOT_ALL,
    /**
     * Pull the intake up using the piston
     */
    INTAKE_UP,
    /**
     * Drop the intake using the piston
     */
    INTAKE_DOWN,
    /**
     * Aim at the target and articulate the hood.
     */
    AIM_AT_TARGET,
    /**
     * Spin up the intake to go in
     */
    INTAKE_IN,
    /**
     * Turns off the intake
     */
    INTAKE_OFF,
    /**
     * Resets the hood to 0 and the turret angle to (relatively) 0
     */
    RESET_SHOOTER
}