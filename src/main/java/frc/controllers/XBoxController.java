package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.robot.RobotNumbers;

public class XBoxController {
    private final Joystick stick;
    private boolean triggerFlag = false;

    public XBoxController(int n) {
        stick = new Joystick(n);
    }

    public void setTriggerSensitivity(double sens) {
        RobotNumbers.triggerSensitivity = sens;
    }

    public boolean isTriggerPressedMomentary(XboxAxes trigger) throws IllegalArgumentException {
        if (trigger != XboxAxes.LEFT_TRIGGER && trigger != XboxAxes.RIGHT_TRIGGER) {
            throw new IllegalArgumentException("trigger must be an xbox trigger");
        }
        boolean out = false;
        if (isTriggerPressed(trigger) != triggerFlag) {
            out = true;
        }
        triggerFlag = isTriggerPressed(trigger);
        return out;
    }

    public boolean isTriggerPressed(XboxAxes trigger) {
        return get(trigger) > RobotNumbers.triggerSensitivity;
    }

    public double get(XboxAxes axis) {
        if (Math.abs(stick.getRawAxis(axis.AXIS_VALUE)) > axis.DEADZONE) return stick.getRawAxis(axis.AXIS_VALUE);
        return 0;
    }

    public ButtonStatus get(XBoxButtons button) {
        return ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    public double getIgnoreSensitivity(XboxAxes axis) {
        return stick.getRawAxis(axis.AXIS_VALUE);
    }
}
