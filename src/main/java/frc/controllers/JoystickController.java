package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickAxis;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.controllers.ControllerEnums.JoystickHatDirection;

public class JoystickController extends BaseController{

    /**
     * joystick controller
     * 
     * @param n - an integer
     */
    public JoystickController(int n) {
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
     * @param axis
     * @return axis.AXIS_VALUE
     */
    public double get(JoystickAxis axis) {
        return stick.getRawAxis(axis.AXIS_VALUE);//return ((1 - joy.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    /**
     * returns positive joystick axis value
     * 
     * @param axis
     * @return positive axis value
     */
    public double getPositive(JoystickAxis axis) {
        return ((1 - stick.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    /**
     * gets joystick button status
     * 
     * @param button
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
