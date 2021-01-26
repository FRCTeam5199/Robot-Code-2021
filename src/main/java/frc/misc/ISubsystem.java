package frc.misc;

/**
 * The generic layout for any subsystem. 
 * Ensures that any subsystem on the robot has the appropriate fields
 * 
 * @author jojo2357
 */
public interface ISubsystem {
    /**
     * Everyone needs to start somewhere!
     */
    void init();
    void updateTest();
    void updateTeleop();
    void updateAuton();
    void updateGeneric();
}
