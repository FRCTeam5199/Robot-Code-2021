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

    public int getHat() {
        return joy.getPOV();
    }

    public double get(JoystickAxis axis) {
        return ((1 - joy.getRawAxis(axis.AXIS_VALUE)) / 2);
    }
    
    public ButtonStatus get(JoystickButtons button) {
        return ButtonStatus.get(joy.getRawButton(button.AXIS_VALUE));
    }

    public boolean hatIs(JoystickHatDirection direction) {
        int output = joy.getPOV();
        for (int angle : direction.ACCEPTED_VALUES)
            if (angle == output) {
                return true;
            }
        return false;
    }
}
