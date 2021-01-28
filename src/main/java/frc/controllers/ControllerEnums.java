package frc.controllers;

import frc.robot.RobotNumbers;

public class ControllerEnums {

    /**
     * Contains the tidy enumarations for determining the status of a toggle on/off button
     */
    public enum ButtonStatus {
        /**
         * Button is not pressed
         */
        UP,
        /**
         * Button is pressed
         */
        DOWN;

        /**
         * Gets the enumeration matching the boolean
         *
         * @param pressed whether the button is pressed or not
         * @return the corresponding enumeration for the input passed in
         */
        public static ButtonStatus get(boolean pressed) {
            if (pressed) {
                return DOWN;
            }
            return UP;
        }
    }
    /**
     * Contains the enumerations for the joysticks and triggers of the Xbox controller
    */
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

        /**
         * @deprecated Feature that should <b>never</b> be used. Gets the xbox axis from a provided int
         *
         * @param id - the xbox axis id requested
         * @return the XboxAxis enum with id mathing input
         * @throws NoSuchFieldException if there is no xbox axis with that id
         */
        @Deprecated
        public static XboxAxes get(int id) throws NoSuchFieldException {
            for (XboxAxes axis : XboxAxes.values())
                if (axis.AXIS_VALUE == id)
                    return axis;
            throw new NoSuchFieldException("There is no Xbox axis with an ID of " + id);
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

        @Deprecated
        public static XBoxButtons get(int id) throws NoSuchFieldException {
            for (XBoxButtons axis : XBoxButtons.values())
                if (axis.AXIS_VALUE == id)
                    return axis;
            throw new NoSuchFieldException("There is no Xbox button with an ID of " + id);
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

        @Deprecated
        public static JoystickAxis get(int id) throws NoSuchFieldException {
            for (JoystickAxis axis : JoystickAxis.values())
                if (axis.AXIS_VALUE == id)
                    return axis;
            throw new NoSuchFieldException("There is no Controller axis with an ID of " + id);
        }
    }
    //sets the values for up, down, right, and left
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
        TWO(2),
        EIGHT(8),
        ELEVEN(11);

        public final int AXIS_VALUE;

        JoystickButtons(int value) {
            this.AXIS_VALUE = value;
        }

        @Deprecated
        public static JoystickButtons get(int id) throws NoSuchFieldException {
            for (JoystickButtons axis : JoystickButtons.values())
                if (axis.AXIS_VALUE == id)
                    return axis;
            throw new NoSuchFieldException("There is no Joystick Button with an ID of " + id);
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

        @Deprecated
        public static ButtonPanelButtons get(int id) throws NoSuchFieldException {
            for (ButtonPanelButtons axis : ButtonPanelButtons.values())
                if (axis.AXIS_VALUE == id)
                    return axis;
            throw new NoSuchFieldException("There is no Button Panel Button with an ID of " + id);
        }
    }
}