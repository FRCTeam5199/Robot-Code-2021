package frc.controllers;

public class ControllerInterfaces {
    /**
     * This is the basic interface inherited by controller mapping enums. It is not needed and instead can
     * be inlined into {@link IDiscreteInput} and {@link IContinuousInput}
     */
    public interface IControllerMapping{
        int getChannel();
    }

    /**
     * Discrete is a button status return like...a button
     */
    public interface IDiscreteInput extends IControllerMapping{
    }

    /**
     * Continuous is a double return like a joystick or tilt of a remote.
     */
    public interface IContinuousInput extends IControllerMapping{
    }
}
