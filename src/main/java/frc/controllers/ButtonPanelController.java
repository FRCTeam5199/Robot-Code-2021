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
    public ButtonPanelController(int n) {
        super(n);
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     */
    @Override
    public ControllerEnums.ButtonStatus get(ButtonPanelButtons n) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(n.AXIS_VALUE));
    }
}