package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.controllers.ControllerEnums.XBoxButtons;
import frc.controllers.ControllerEnums.XboxAxes;
import frc.robot.RobotNumbers;

public class XBoxController {
    private final Joystick stick;
    private boolean triggerFlag = false;

    /**
     * Creates a new Xbox Controller object on a specified usb port
     *
     * @param n the usb port that the controller is on
     */
    public XBoxController(int n) {
        stick = new Joystick(n);
    }

    public void setTriggerSensitivity(double sens) { //sets sensitivity
        RobotNumbers.triggerSensitivity = sens;
    }

    /**
    * Gets the momentary status of a trigger
    *
    * @param trigger the trigger to query
    * @return true if the trigger has changed its state being pressed down
    */
    public boolean isTriggerPressedMomentary(XboxAxes trigger) throws IllegalArgumentException {
        if (trigger != XboxAxes.LEFT_TRIGGER && trigger != XboxAxes.RIGHT_TRIGGER) {
            throw new IllegalArgumentException("trigger must be an xbox trigger");
        }
        boolean out = isTriggerPressed(trigger) != triggerFlag && isTriggerPressed(trigger);
        triggerFlag = isTriggerPressed(trigger);
        return out;
    }

    /**
     * get joystick axis value regardless of deadzone
     *
     * @param trigger trigger position to query
     * @return returns true when trigger is past its "deadzone"
     */
    public boolean isTriggerPressed(XboxAxes trigger) {
        return get(trigger) > RobotNumbers.triggerSensitivity;
    }

    /**
     * get the state of an xbox axis
     *
     * @see #get(XBoxButtons)
     * @param axis xbox controller axis to query
     * @return the state of inputted axis on a scale of [-1,1]
     */
    public double get(XboxAxes axis) {
        if (Math.abs(stick.getRawAxis(axis.AXIS_VALUE)) > axis.DEADZONE) //makes sure axis is outside of the deadzone
            return stick.getRawAxis(axis.AXIS_VALUE);
        return 0;
    }

    /**
     * Gets the status of a button on the xbox controller
     *
     * @see #get(XboxAxes)
     * @param button the button to query
     * @return the status of queried button
     */
    public ButtonStatus get(XBoxButtons button) {
        return ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    /**
     * get joystick axis value regardless of deadzone
     *
     * @param axis xbox controller axis to query
     * @return returns stick axis value regardless of deadzone
     */
    public double getIgnoreSensitivity(XboxAxes axis) {
        return stick.getRawAxis(axis.AXIS_VALUE);
    }
}
