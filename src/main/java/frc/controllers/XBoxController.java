package frc.controllers;

import edu.wpi.first.wpilibj.GenericHID;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XBoxPOVButtons;
import frc.controllers.ControllerEnums.XboxAxes;

/**
 * The lame and basic controller. Does it get any more simpleton than this?
 *
 * @see BaseController
 * @see XboxAxes
 * @see XBoxButtons
 */
public class XBoxController extends BaseController {
    private final boolean triggerFlag = false;

    /**
     * Creates a new Xbox Controller object on a specified usb port
     *
     * @param n the usb port that the controller is on
     */
    XBoxController(Integer n) {
        super(n);
    }

    /**
     * get the state of an xbox axis
     *
     * @param axis xbox controller axis to query
     * @return the state of inputted axis on a scale of [-1,1]
     * @see #get(XBoxButtons)
     */
    @Override
    public double get(XboxAxes axis) {
        if (Math.abs(controller.getRawAxis(axis.AXIS_VALUE)) > axis.DEADZONE) //makes sure axis is outside of the deadzone
            return controller.getRawAxis(axis.AXIS_VALUE);
        return 0;
    }

    /**
     * Gets the status of a button on the xbox controller
     *
     * @param button the button to query
     * @return the status of queried button
     * @see #get(XboxAxes)
     */
    @Override
    public ButtonStatus get(XBoxButtons button) {
        return ButtonStatus.get(controller.getRawButton(button.AXIS_VALUE));
    }

    @Override
    public ButtonStatus get(XBoxPOVButtons button) {
        return ButtonStatus.get(controller.getPOV() == button.POV_ANGLE);
    }

    @Override
    public void rumble(double percent) {
        controller.setRumble(GenericHID.RumbleType.kLeftRumble, percent);
        controller.setRumble(GenericHID.RumbleType.kRightRumble, percent);
    }
}
