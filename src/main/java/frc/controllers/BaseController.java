package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;

/**
 * for ANY CONTROLLER, put EVERY GET METHOD in here as well as in the proper class! This allows for the COMPLETE HOT
 * SWAPPING of controllers simply by changing the constructor used.
 *
 * @author jojo2357
 */
public abstract class BaseController {
    protected final Joystick stick;
    private final int JOYSTICK_CHANNEL;

    protected BaseController(int channel) {
        stick = new Joystick(channel);
        JOYSTICK_CHANNEL = channel;
    }

    @Deprecated
    public double get(int channel) {
        return stick.getRawAxis(channel);
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.ButtonPanelButtons n) {
        throw new UnsupportedOperationException("This controller does not support getting a button panel button. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.JoystickHatDirection getHat() throws IllegalStateException {
        throw new UnsupportedOperationException("This controller does not support getting the hat direction. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public double get(ControllerEnums.JoystickAxis axis) {
        throw new UnsupportedOperationException("This controller does not support getting a flight stick joystick axis. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public double getPositive(ControllerEnums.JoystickAxis axis) {
        throw new UnsupportedOperationException("This controller does not support getting a flight stick joystick axis. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.JoystickButtons button) {
        throw new UnsupportedOperationException("This controller does not support getting a flight stick button status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public boolean hatIs(ControllerEnums.JoystickHatDirection direction) {
        throw new UnsupportedOperationException("This controller does not support getting the flight stick hat status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public double get(ControllerEnums.XboxAxes axis) {
        throw new UnsupportedOperationException("This controller does not support getting an xbox joystick status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.XBoxButtons button) {
        throw new UnsupportedOperationException("This controller does not support getting an xbox button status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public double get(ControllerEnums.WiiAxis axis) {
        throw new UnsupportedOperationException("This controller does not support getting an wii remote tilt status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.WiiButton button) {
        throw new UnsupportedOperationException("This controller does not support getting an wii remote button status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.SixKeyGuitarButtons button) {
        throw new UnsupportedOperationException("This controller does not support getting an guitar button status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public double get(ControllerEnums.SixKeyGuitarAxis button) {
        throw new UnsupportedOperationException("This controller does not support getting an guitar axis status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.Drums drum) {
        throw new UnsupportedOperationException("This controller does not support getting an guitar axis status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.DrumButton drum) {
        throw new UnsupportedOperationException("This controller does not support getting an guitar axis status. If you believe this is a mistake, please override the overloaded get in the appropriate class");
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.BopItButtons button) {
        throw new UnsupportedOperationException("hehe");
    }

    //should NOT print BaseController (i hope)
    @Override
    public String toString() {
        return this.getClass().getName() + " on channel " + JOYSTICK_CHANNEL;
    }
}
