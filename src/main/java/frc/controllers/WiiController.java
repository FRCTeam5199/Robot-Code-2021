package frc.controllers;

/**
 * For mario kart drive. Google it if u dunno what it is Wii remote driver for windows:
 * https://www.julianloehr.de/educational-work/hid-wiimote/
 *
 * @see BaseController
 * @see ControllerEnums.WiiAxis
 * @see ControllerEnums.WiiButton
 * @see ControllerEnums.ButtonStatus
 */
public class WiiController extends BaseController {

    public static BaseController createOrGet(int channel) {
        if (channel < 0 || channel >= 6)
            throw new ArrayIndexOutOfBoundsException("You cant have a controller with id of " + channel);
        if (BaseController.allControllers[channel] == null)
            return BaseController.allControllers[channel] = new WiiController(channel);
        if (BaseController.allControllers[channel] instanceof WiiController)
            return BaseController.allControllers[channel];
        throw new ArrayStoreException("A different controller has already been made for channel " + channel);
    }

    private WiiController(int n) {
        super(n);
    }

    /**
     * Gets the Raw axis value starting at 0
     *
     * @return the state of passed axis on a scale of [-1,1]
     * @see #get(ControllerEnums.WiiButton)
     */
    public double get(ControllerEnums.WiiAxis axis) {
        return stick.getRawAxis(axis.AXIS_VALUE);
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     *
     * @return the status of the passed button
     * @see #get(ControllerEnums.WiiAxis)
     */
    public ControllerEnums.ButtonStatus get(ControllerEnums.WiiButton button) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
