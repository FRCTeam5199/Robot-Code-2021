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

    public static BaseController createOrGet(int channel) {
        if (channel < 0 || channel >= 6)
            throw new ArrayIndexOutOfBoundsException("You cant have a controller with id of " + channel);
        if (BaseController.allControllers[channel] == null)
            return BaseController.allControllers[channel] = new JoystickController(channel);
        if (BaseController.allControllers[channel] instanceof JoystickController)
            return BaseController.allControllers[channel];
        throw new ArrayStoreException("A different controller has already been made for channel " + channel);
    }

    /**
     * joystick controller
     *
     * @param n - an integer
     */
    private JoystickController(int n) {
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
                if (acceptedValue == stick.getPOV())
                    return dir;
        throw new IllegalStateException("I could not wrap " + stick.getPOV() + " inside the JoystickHatDirection enumeration.");
    }

    /**
     * returns joystick's axis value
     *
     * @param axis the joystick axis to query
     * @return axis.AXIS_VALUE
     */
    public double get(JoystickAxis axis) {
        return stick.getRawAxis(axis.AXIS_VALUE);//return ((1 - joy.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    /**
     * returns positive joystick axis value
     *
     * @param axis the joystick axis to query
     * @return positive axis value
     * @see #get(JoystickAxis)
     */
    public double getPositive(JoystickAxis axis) {
        return ((1 - stick.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    /**
     * gets joystick button status
     *
     * @param button the joystick button to query
     * @return button status
     */
    public ButtonStatus get(JoystickButtons button) {
        return ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    /**
     * Return true if joystick direction is an accepted value, false if not
     *
     * @param direction - the direction of the joystick hat
     * @return true if and only if the direction of the hat is included in the enum passed in
     */
    public boolean hatIs(JoystickHatDirection direction) {
        int output = stick.getPOV();
        for (int angle : direction.ACCEPTED_VALUES)
            if (angle == output) {
                return true;
            }
        return false;
    }
}
