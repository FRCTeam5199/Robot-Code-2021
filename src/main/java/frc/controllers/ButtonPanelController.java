package frc.controllers;

import frc.controllers.ControllerEnums.ButtonPanelButtons;
import frc.robot.Robot;

/**
 * Our custom built button panel that has a bunch of levers and switches (lol jk it has buttons silly) that is pretty
 * basic
 *
 * @see BaseController
 * @see ButtonPanelButtons
 * @see ControllerEnums.ButtonStatus
 */
public class ButtonPanelController extends BaseController {
    ButtonPanelController(Integer n) {
        super(n);
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     */
    @Override
    public ControllerEnums.ButtonStatus get(ControllerInterfaces.IDiscreteInput button) {
        if (button instanceof ButtonPanelButtons || button instanceof ControllerEnums.ButtonPanelTapedButtons || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.getChannel()));
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.ButtonPanelButtons.class.toString() + " but got " + button.getClass().toString() + " instead");
    }
}