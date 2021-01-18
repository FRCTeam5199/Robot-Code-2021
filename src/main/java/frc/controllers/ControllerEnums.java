package frc.controllers;

import frc.robot.RobotNumbers;

public class ControllerEnums {
    public enum ButtonStatus {
        UP,
        DOWN;

        public static ButtonStatus get(boolean pressed) {
            if (pressed) {
                return DOWN;
            }
            return UP;
        }
    }

    public enum XboxAxes {
        LEFT_JOY_X(0, RobotNumbers.XBOX_CONTROLLER_DEADZONE),
        LEFT_JOY_Y(1, RobotNumbers.XBOX_CONTROLLER_DEADZONE),
        LEFT_TRIGGER(2, 0),
        RIGHT_TRIGGER(3, 0),
        RIGHT_JOY_X(4, RobotNumbers.XBOX_CONTROLLER_DEADZONE),
        RIGHT_JOY_Y(5, RobotNumbers.XBOX_CONTROLLER_DEADZONE);

        public final int AXIS_VALUE;
        public final double DEADZONE;

        XboxAxes(int axis, double deadzone) {
            this.AXIS_VALUE = axis;
            this.DEADZONE = deadzone;
        }
    }

    public enum JoystickAxis {
        X(0),
        Y(1),
        Z(2);

        public final int AXIS_VALUE;

        JoystickAxis(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum JoystickHatDirection {
        UP(315, 0, 45),
        DOWN(135, 180, 225),
        LEFT(270),
        RIGHT(90);

        public final int[] ACCEPTED_VALUES;

        JoystickHatDirection(int...values){
            this.ACCEPTED_VALUES = values;
        }
    }
}