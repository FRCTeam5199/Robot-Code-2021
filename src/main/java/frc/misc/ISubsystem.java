package frc.misc;

import frc.robot.Robot;
import frc.selfdiagnostics.ISimpleIssue;

/**
 * The generic layout for any subsystem. Ensures that any subsystem on the robot has the appropriate fields
 *
 * @author jojo2357
 */
public interface ISubsystem {
    /**
     * Everyone needs to start somewhere! Use this function to set up all of the stuff in the subsystem (This is
     * basically {@link Robot#robotInit()})
     *
     * @see Robot#robotInit()
     */
    void init();

    /**
     * When testing, put the code to execute every tick in here
     *
     * @see Robot#testPeriodic()
     */
    void updateTest();

    /**
     * Put all of the code for tele operation to run every tick in here
     *
     * @see Robot#teleopPeriodic()
     */
    void updateTeleop();

    /**
     * Put subsystem apprpriate code to run every tick during auton code
     *
     * @see Robot#autonomousPeriodic()
     */
    void updateAuton();

    /**
     * Put subsystem apprpriate code to run every tick in every mode here (There is not corrollary in {@link Robot})
     */
    void updateGeneric();

    /**
     * Runs every time the robot is enabled into test mode
     *
     * @see Robot#testInit()
     */
    void initTest();

    /**
     * Runs when the robot is enabled into teleop
     *
     * @see Robot#teleopInit()
     */
    void initTeleop();

    /**
     * Runs when the robot is enabled into auton
     *
     * @see Robot#autonomousInit()
     */
    void initAuton();

    /**
     * Runs when the robot is disabled from any mode
     *
     * @see Robot#disabledInit()
     */
    void initDisabled();

    /**
     * Put repeated init code here from {@link #initAuton()} {@link #initTest()} {@link #initTest()} {@link
     * #initDisabled()}
     */
    void initGeneric();

    /**
     * Used in {@link ISimpleIssue} to provide a user friendly explanation as to why their jank isnt working. This could
     * be static but that doesnt make sense since you should be querying an object anyway.
     *
     * @return the hardcoded name of the subsystem
     */
    String getSubsystemName();

    /**
     * In devlopment, adds this object to {@link Robot#subsystems a master registry} for later use
     */
    default void addToMetaList() {
        Robot.subsystems.add(this);
    }
}
