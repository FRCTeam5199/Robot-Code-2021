package frc.controllers;

import edu.wpi.first.wpilibj.GenericHID;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XBoxPOVButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.robot.Robot;

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
     * @see #get(frc.controllers.ControllerInterfaces.IDiscreteInput)
     */
    @Override
    public double get(ControllerInterfaces.IContinuousInput axis) {
        if (axis instanceof ControllerEnums.XboxAxes)
            if (Math.abs(controller.getRawAxis(axis.getChannel())) > ((XboxAxes)axis).DEADZONE) //makes sure axis is outside of the deadzone
                return controller.getRawAxis(axis.getChannel());
            else
                return 0;
        else if (Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return controller.getRawAxis(axis.getChannel());
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.XboxAxes.class.toString() + " but got " + axis.getClass().toString() + " instead");
    }

    /**
     * Gets the status of a button on the xbox controller
     *
     * @param button the button to query
     * @return the status of queried button
     * @see #get(frc.controllers.ControllerInterfaces.IContinuousInput)
     */
    @Override
    public ButtonStatus get(ControllerInterfaces.IDiscreteInput button) {
        if (button instanceof ControllerEnums.XBoxButtons || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.getChannel()));
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.XBoxButtons.class.toString() + " but got " + button.getClass().toString() + " instead");
    }

    @Override
    public boolean hatIsExactly(ControllerEnums.RawCompassInput direction) {
        return direction.POV_ANGLE == controller.getPOV();
    }

    @Override
    public boolean hatIs(ControllerEnums.ResolvedCompassInput direction) {
        return direction.containsAngle(controller.getPOV());
    }

    @Override
    public void rumble(double percent) {
        controller.setRumble(GenericHID.RumbleType.kLeftRumble, percent);
        controller.setRumble(GenericHID.RumbleType.kRightRumble, percent);
    }
}
