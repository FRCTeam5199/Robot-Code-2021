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

    public enum XBoxButtons {
        A_CROSS(1),
        B_CIRCLE(2),
        X_SQUARE(3),
        Y_TRIANGLE(4),
        LEFT_BUMPER(5),
        RIGHT_BUMPER(6),
        GUIDE(7),
        MENU(8);

        public final int AXIS_VALUE;

        XBoxButtons(int axis) {
            this.AXIS_VALUE = axis;
        }
    }

    public enum JoystickAxis {
        X_AXIS(0),
        Y_AXIS(1),
        Z_ROTATE(2),
        SLIDER(3);

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

        JoystickHatDirection(int... values) {
            this.ACCEPTED_VALUES = values;
        }
    }

    //Uh. theyre all numbered so idk what to do here
    public enum JoystickButtons {
        ONE(1),
        TWO(2);

        public final int AXIS_VALUE;

        JoystickButtons(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum ButtonPanelButtons {
        RAISE_CLIMBER(1),
        LOWER_CLIMBER(2),
        CLIMBER_LOCK(3),
        CLIMBER_UNLOCK(4),
        BUDDY_CLIMB(5),
        AUX_TOP(6),
        AUX_BOTTOM(7),
        INTAKE_UP(8),
        INTAKE_DOWN(9),
        HOPPER_IN(10),
        HOPPER_OUT(11),
        TARGET(12),
        SOLID_SPEED(13);

        public final int AXIS_VALUE;

        ButtonPanelButtons(int value) {
            this.AXIS_VALUE = value;
        }
    }
}