package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.JoystickAxis;
import frc.controllers.ControllerEnums.JoystickHatDirection;

public class JoystickController {

    private final Joystick joy;

    public JoystickController(int n) {
        joy = new Joystick(n);
    }

    public int getHat() {
        return joy.getPOV();
    }

    public double getAxis(JoystickAxis axis) {
        return joy.getRawAxis(axis.AXIS_VALUE);
    }

    public double getSlider() {
        return ((1 - joy.getRawAxis(3)) / 2);
    }

    //TODO change to an enum where button is named
    public ButtonStatus getButton(int button) {
        return ButtonStatus.get(joy.getRawButton(button));
    }

    public boolean hatIs(JoystickHatDirection direction) {
        int output = joy.getPOV();
        for (int angle : direction.ACCEPTED_VALUES)
            if (angle == output)
                return true;
        return false;
    }
}
