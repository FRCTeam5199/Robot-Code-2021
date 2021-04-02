package frc.controllers;

import frc.controllers.ControllerEnums.ButtonPanelButtons;

/**
 * Our custom built button panel that has a bunch of levers and switches (lol jk it has buttons silly) that is pretty
 * basic
 *
 * @see BaseController
 * @see ButtonPanelButtons
 * @see ControllerEnums.ButtonStatus
 */
public class ButtonPanelController extends BaseController {
    public static BaseController createOrGet(int channel) {
        if (channel < 0 || channel >= 6)
            throw new ArrayIndexOutOfBoundsException("You cant have a controller with id of " + channel);
        if (BaseController.allControllers[channel] == null)
            return BaseController.allControllers[channel] = new ButtonPanelController(channel);
        if (BaseController.allControllers[channel] instanceof ButtonPanelController)
            return BaseController.allControllers[channel];
        throw new ArrayStoreException("A different controller has already been made for channel " + channel);
    }

    private ButtonPanelController(int n) {
        super(n);
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     */
    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.ButtonPanelButtons n) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(n.AXIS_VALUE));
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     */
    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.ButtonPanelTapedButtons n) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(n.AXIS_VALUE));
    }
}