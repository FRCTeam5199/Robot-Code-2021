package frc.controllers;

import frc.controllers.ControllerEnums.JoystickAxis;
import frc.robot.Robot;

/**
 * This is the flight stick that we use. It has a bunch of useless buttons so its special in my heart
 *
 * @see BaseController
 * @see ControllerEnums.JoystickAxis
 * @see ControllerEnums.JoystickButtons
 */
public class JoystickController extends BaseController {
    /**
     * joystick controller
     *
     * @param n - an integer
     */
    JoystickController(Integer n) {
        super(n);
    }

    /**
     * returns joystick's axis value
     *
     * @param axis the joystick axis to query
     * @return axis.AXIS_VALUE
     */
    public double get(ControllerInterfaces.IContinuousInput axis) {
        if (axis instanceof JoystickAxis || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return controller.getRawAxis(axis.getChannel());
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.JoystickAxis.class.toString() + " but got " + axis.getClass().toString() + " instead");
    }

    /**
     * returns positive joystick axis value
     *
     * @param axis the joystick axis to query
     * @return positive axis value
     * @see #get(frc.controllers.ControllerInterfaces.IContinuousInput)
     */
    public double getPositive(ControllerInterfaces.IContinuousInput axis) {
        if (axis instanceof JoystickAxis || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return ((1 - controller.getRawAxis(axis.getChannel())) / 2);
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.JoystickAxis.class.toString() + " but got " + axis.getClass().toString() + " instead");
    }

    /**
     * gets joystick button status
     *
     * @param button the joystick button to query
     * @return button status
     */
    @Override
    public ControllerEnums.ButtonStatus get(ControllerInterfaces.IDiscreteInput button) {
        if (button instanceof ControllerEnums.JoystickButtons || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.getChannel()));
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.JoystickButtons.class.toString() + " but got " + button.getClass().toString() + " instead");
    }

    /**
     * Return true if joystick direction is an accepted value, false if not
     *
     * @param direction - the direction of the joystick hat
     * @return true if and only if the direction of the hat is included in the enum passed in
     */
    public boolean hatIs(ControllerEnums.ResolvedCompassInput direction) {
        int output = controller.getPOV();
        for (int angle : direction.ACCEPTED_VALUES)
            if (angle == output) {
                return true;
            }
        return false;
    }
}