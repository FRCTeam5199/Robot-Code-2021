package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;

import java.util.function.Function;

/**
 * for ANY CONTROLLER, put EVERY GET METHOD in here as well as in the proper class! This allows for the COMPLETE HOT
 * SWAPPING of controllers simply by changing the constructor used.
 *
 * @author jojo2357
 */
public abstract class BaseController {
    /**
     * This is the meta registrar of controllers. This is to prevent two different controller types from existing on the
     * same channel and to reduce memory impact by reducing redundant objects. To use, simply query the index
     * corresponding to the port desired and if null, create and set. Otherwise, verify controller type then use.
     *
     * @see #createOrGet(int, Controllers)
     */
    protected static final BaseController[] allControllers = new BaseController[6];
    private static final String GENERIC_ERROR_CLAUSE = "If you believe this is a mistake, please override the overloaded get in the appropriate class";
    protected final Joystick controller;
    private final int JOYSTICK_CHANNEL;

    public static BaseController createOrGet(int channel, Controllers controllerType) throws ArrayIndexOutOfBoundsException, ArrayStoreException, UnsupportedOperationException {
        if (channel < 0 || channel >= 6)
            throw new ArrayIndexOutOfBoundsException("You cant have a controller with id of " + channel);
        return allControllers[channel] = controllerType.constructor.apply(channel);
    }

    protected BaseController(Integer channel) {
        controller = new Joystick(channel);
        JOYSTICK_CHANNEL = channel;
    }

    @Deprecated
    public double get(int channel) {
        return controller.getRawAxis(channel);
    }

    public ControllerEnums.ButtonStatus get(ControllerInterfaces.IDiscreteInput n) {
        throw new UnsupportedOperationException("This controller does not support getting a button status. " + GENERIC_ERROR_CLAUSE);
    }

    public double get(ControllerInterfaces.IContinuousInput axis) {
        throw new UnsupportedOperationException("This controller does not support getting a continuous input. " + GENERIC_ERROR_CLAUSE);
    }

    public double getPositive(ControllerInterfaces.IContinuousInput axis) {
        throw new UnsupportedOperationException("This controller does not support getting a continuous input. " + GENERIC_ERROR_CLAUSE);
    }

    public boolean hatIsExactly(ControllerEnums.RawCompassInput direction) {
        throw new UnsupportedOperationException("This controller does not have a hat. " + GENERIC_ERROR_CLAUSE);
    }

    public boolean hatIs(ControllerEnums.ResolvedCompassInput direction) {
        throw new UnsupportedOperationException("This controller does not have a hat. " + GENERIC_ERROR_CLAUSE);
    }

    public void rumble(double percent) {
        throw new UnsupportedOperationException("This controller does not support rumbling. " + GENERIC_ERROR_CLAUSE);
    }

    //should NOT print BaseController (i hope)
    @Override
    public String toString() {
        return this.getClass().getName() + " on channel " + JOYSTICK_CHANNEL;
    }

    public enum Controllers {
        BOP_IT_CONTROLLER(BopItBasicController::new),
        BUTTON_PANEL_CONTROLLER(ButtonPanelController::new),
        DRUM_CONTROLLER(DrumTimeController::new),
        JOYSTICK_CONTROLLER(JoystickController::new),
        SIX_BUTTON_GUITAR_CONTROLLER(SixButtonGuitarController::new),
        WII_CONTROLLER(WiiController::new),
        XBOX_CONTROLLER(XBoxController::new);

        private final Function<Integer, BaseController> constructor;

        Controllers(Function<Integer, BaseController> structor) {
            constructor = structor;
        }
    }
}
