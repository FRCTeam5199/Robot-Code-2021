package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickAxis;
import frc.controllers.ControllerEnums.JoystickButtons;
import frc.controllers.ControllerEnums.JoystickHatDirection;

public class JoystickController {

    private final Joystick joy;

    public JoystickController(int n) {
        joy = new Joystick(n);
    }

    /**
     * @return The status of the hat on the joystick
     * @throws IllegalStateException
     */
    public JoystickHatDirection getHat() throws IllegalStateException {
        for (JoystickHatDirection dir : JoystickHatDirection.values())
            for (int acceptedValue : dir.ACCEPTED_VALUES)
                if (acceptedValue == joy.getPOV())
                    return dir;
        throw new IllegalStateException("I could not wrap " + joy.getPOV() + " inside the JoystickHatDirection enumeration.");
    }

    public double get(JoystickAxis axis) {
        return joy.getRawAxis(axis.AXIS_VALUE);//return ((1 - joy.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    public double getPositive(JoystickAxis axis) {
        return ((1 - joy.getRawAxis(axis.AXIS_VALUE)) / 2);
    }

    public ButtonStatus get(JoystickButtons button) {
        return ButtonStatus.get(joy.getRawButton(button.AXIS_VALUE));
    }

    /**
     * Return true if joystick direction is an accepted value, false if not
     *
     * @param direction - the direction of the joystick hat
     * @return true if and only if the direction of the hat is included in the enum passed in
     */
    public boolean hatIs(JoystickHatDirection direction) {
        int output = joy.getPOV();
        for (int angle : direction.ACCEPTED_VALUES)
            if (angle == output) {
                return true;
            }
        return false;
    }
}
