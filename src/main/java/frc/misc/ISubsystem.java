package frc.misc;

/**
 * The generic layout for any subsystem. 
 * Ensures that any subsystem on the robot has the appropriate fields
 * 
 * @author jojo2357
 */
public interface ISubsystem {
    /**
     * Everyone needs to start somewhere! Use this function to set up all of the stuff in the subsystem
     */
    void init();

    /**
     * When testing, put the code to execute every tick in here
     */
    void updateTest();

    /**
     * Put all of the code for tele operation to run every tick in here
     */
    void updateTeleop();

    /**
     * Put subsystem apprpriate code to run every tick during auton code
     */
    void updateAuton();

    /**
     * Put subsystem apprpriate code to run every tick in every mode here
     */
    void updateGeneric();
}
