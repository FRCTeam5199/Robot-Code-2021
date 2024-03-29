package frc.controllers;

import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickAxis;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.controllers.ControllerEnums.JoystickHatDirection;

/**
 * This is the flight stick that we use. It has a bunch of useless buttons so its special in my heart
 *
 * @see BaseController
 * @see ControllerEnums.JoystickAxis
 * @see ControllerEnums.JoystickHatDirection
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
     * ensures that the joystick hat direction is an accepted value
     *
     * @return The direction of the hat on the joystick
     * @throws IllegalStateException if the current direction doesnt have a matching enum
     */
    public JoystickHatDirection getHat() throws IllegalStateException {
        for (JoystickHatDirection dir : JoystickHatDirection.values())
            for (int acceptedValue : dir.ACCEPTED_VALUES)
                if (acceptedValue == controller.getPOV())
                    return dir;
        throw new IllegalStateException("I could not wrap " + controller.getPOV() + " inside the JoystickHatDirection enumeration.");
    }

    /**
     * returns joystick's axis value
     *
     * @param axis the joystick axis to query
     * @return axis.AXIS_VALUE
     */
    public double get(JoystickAxis axis) {
        return controller.getRawAxis(axis.AXIS_VALUE);//return ((1 - joy.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    /**
     * returns positive joystick axis value
     *
     * @param axis the joystick axis to query
     * @return positive axis value
     * @see #get(JoystickAxis)
     */
    public double getPositive(JoystickAxis axis) {
        return ((1 - controller.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    /**
     * gets joystick button status
     *
     * @param button the joystick button to query
     * @return button status
     */
    public ButtonStatus get(JoystickButtons button) {
        return ButtonStatus.get(controller.getRawButton(button.AXIS_VALUE));
    }

    /**
     * Return true if joystick direction is an accepted value, false if not
     *
     * @param direction - the direction of the joystick hat
     * @return true if and only if the direction of the hat is included in the enum passed in
     */
    public boolean hatIs(JoystickHatDirection direction) {
        int output = controller.getPOV();
        for (int angle : direction.ACCEPTED_VALUES)
            if (angle == output) {
                return true;
            }
        return false;
    }
}
