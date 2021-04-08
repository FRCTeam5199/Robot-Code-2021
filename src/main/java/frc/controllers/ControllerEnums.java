package frc.controllers;

import static frc.robot.Robot.robotSettings;

/**
 * These enums are used for each controller. If you make a new controller, create a new enum for its mappings here for
 * safekeeping and then implement a get function in {@link BaseController} and then create a new class extending
 * BaseController that overrides that get (the gets in BaseController should all throw exceptions so if an {@link
 * XBoxController xbox controller} is queried for a {@link WiiAxis wii axis} it should throw a fit)
 *
 * @see BaseController
 */
public class ControllerEnums {

    /**
     * @see WiiController
     */
    public enum WiiAxis {
        LEFT_RIGHT_NUMBERPAD(0), UP_DOWN_NUMBERPAD(1), ROTATIONAL_TILT(3), FORWARD_TILT(4);

        public final int AXIS_VALUE;

        WiiAxis(int val) {
            AXIS_VALUE = val;
        }
    }

    /**
     * @see WiiController
     */
    public enum WiiButton {
        ONE(1), TWO(2), A(3), B(4), PLUS(5), MINUS(6), HOME(7);

        public final int AXIS_VALUE;

        WiiButton(int val) {
            AXIS_VALUE = val;
        }
    }

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
     * @see XBoxController
     */
    public enum XboxAxes {
        LEFT_JOY_X(0, robotSettings.XBOX_CONTROLLER_DEADZONE), LEFT_JOY_Y(1, robotSettings.XBOX_CONTROLLER_DEADZONE), LEFT_TRIGGER(2, 0), RIGHT_TRIGGER(3, 0), RIGHT_JOY_X(4, robotSettings.XBOX_CONTROLLER_DEADZONE), RIGHT_JOY_Y(5, robotSettings.XBOX_CONTROLLER_DEADZONE);

        public final int AXIS_VALUE;
        public final double DEADZONE;

        /**
         * @param id - the xbox axis id requested
         * @return the {@link XboxAxes} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped xbox axis with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the xbox axis from a provided int
         */
        @Deprecated
        public static XboxAxes get(int id) throws NoSuchFieldException {
            for (XboxAxes axis : XboxAxes.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Xbox axis with an ID of " + id);
        }

        XboxAxes(int axis, double deadzone) {
            this.AXIS_VALUE = axis;
            this.DEADZONE = deadzone;
        }
    }

    /**
     * @see XBoxController
     */
    public enum XBoxButtons {
        A_CROSS(1), B_CIRCLE(2), X_SQUARE(3), Y_TRIANGLE(4), LEFT_BUMPER(5), RIGHT_BUMPER(6), GUIDE(7), MENU(8);

        public final int AXIS_VALUE;

        /**
         * @param id - the xbox button id requested
         * @return the {@link XBoxButtons} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped xbox button with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the xbox button from a provided int
         */
        @Deprecated
        public static XBoxButtons get(int id) throws NoSuchFieldException {
            for (XBoxButtons axis : XBoxButtons.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Xbox button with an ID of " + id);
        }

        XBoxButtons(int axis) {
            this.AXIS_VALUE = axis;
        }
    }

    /**
     * @see JoystickController
     */
    public enum JoystickAxis {
        X_AXIS(0), Y_AXIS(1), Z_ROTATE(2), SLIDER(3);

        public final int AXIS_VALUE;

        /**
         * @param id - the flight stick axis id requested
         * @return the {@link JoystickAxis} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped flight stick axis with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the flight stick axis from a provided int
         */
        @Deprecated
        public static JoystickAxis get(int id) throws NoSuchFieldException {
            for (JoystickAxis axis : JoystickAxis.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Controller axis with an ID of " + id);
        }

        JoystickAxis(int value) {
            this.AXIS_VALUE = value;
        }
    }

    /**
     * @see JoystickController
     */
    public enum JoystickHatDirection {
        UP(315, 0, 45), DOWN(135, 180, 225), LEFT(270), RIGHT(90);

        public final int[] ACCEPTED_VALUES;

        JoystickHatDirection(int... values) {
            this.ACCEPTED_VALUES = values;
        }
    }

    /**
     * @see JoystickController
     */
    public enum JoystickButtons {
        ONE(1), TWO(2), THREE(3), FIVE(5), EIGHT(8), ELEVEN(11);

        public final int AXIS_VALUE;

        /**
         * @param id - the flight stick button id requested
         * @return the {@link JoystickButtons} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped flight stick button with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the flight stick button from a provided int
         */
        @Deprecated
        public static JoystickButtons get(int id) throws NoSuchFieldException {
            for (JoystickButtons axis : JoystickButtons.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Joystick Button with an ID of " + id);
        }

        JoystickButtons(int value) {
            this.AXIS_VALUE = value;
        }
    }

    /**
     * @see ButtonPanelController
     */
    public enum ButtonPanelButtons {
        RAISE_CLIMBER(1), LOWER_CLIMBER(2), CLIMBER_LOCK(3), CLIMBER_UNLOCK(4), BUDDY_CLIMB(5), AUX_TOP(6), AUX_BOTTOM(7), INTAKE_UP(8), INTAKE_DOWN(9), HOPPER_IN(10), HOPPER_OUT(11), TARGET(12), SOLID_SPEED(13);

        public final int AXIS_VALUE;

        /**
         * @param id - the button panel button id requested
         * @return the {@link ButtonPanelButtons} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped button panel button with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the button panel button from a provided int
         */
        @Deprecated
        public static ButtonPanelButtons get(int id) throws NoSuchFieldException {
            for (ButtonPanelButtons axis : ButtonPanelButtons.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Button Panel Button with an ID of " + id);
        }

        ButtonPanelButtons(int value) {
            this.AXIS_VALUE = value;
        }
    }

    /**
     * @see ButtonPanelController
     */
    public enum ButtonPanelTapedButtons {
        RAISE_CLIMBER(1), LOWER_CLIMBER(2), CLIMBER_LOCK(3), CLIMBER_UNLOCK(4), BUDDY_CLIMB(5), AUX_TOP(6), HOOD_POS_1(7), SINGLE_SHOT(8), HOOD_POS_2(9), SOLID_SPEED(10), HOOD_POS_3(11), TARGET(12), HOOD_POS_4(13);

        public final int AXIS_VALUE;

        /**
         * @param id - the button panel button id requested
         * @return the {@link ButtonPanelTapedButtons} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped button panel button with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the button panel button from a provided int
         */
        @Deprecated
        public static ButtonPanelTapedButtons get(int id) throws NoSuchFieldException {
            for (ButtonPanelTapedButtons axis : ButtonPanelTapedButtons.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Button Panel taped Button with an ID of " + id);
        }

        ButtonPanelTapedButtons(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum SixKeyGuitarButtons {
        ONE(2), TWO(3), THREE(4), FOUR(1), FIVE(5), SIX(6), HERO_POWER(9), PAUSE(10), MENU(11), REFRESH(13);

        public final int AXIS_VALUE;

        /**
         * @param id - the guitar button id requested
         * @return the {@link SixKeyGuitarButtons} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped guitar button with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the guitar button from a provided int
         */
        @Deprecated
        public static SixKeyGuitarButtons get(int id) throws NoSuchFieldException {
            for (SixKeyGuitarButtons axis : SixKeyGuitarButtons.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Button Panel Button with an ID of " + id);
        }

        SixKeyGuitarButtons(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum SixKeyGuitarAxis {
        STRUM(1), PITCH(2), WHAMMY(3);

        public final int AXIS_VALUE;

        /**
         * @param id - the guitar axis id requested
         * @return the {@link SixKeyGuitarAxis} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped guitar axis with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the guitar axis from a provided int
         */
        @Deprecated
        public static SixKeyGuitarAxis get(int id) throws NoSuchFieldException {
            for (SixKeyGuitarAxis axis : SixKeyGuitarAxis.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Button Panel Button with an ID of " + id);
        }

        SixKeyGuitarAxis(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum Drums {
        RED(3),
        YELLOW(4),
        BLUE(1),
        GREEN(2);

        public final int AXIS_VALUE;

        /**
         * @param id - the drum drum id requested
         * @return the {@link Drums} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped drum drum with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the drum drum from a provided int
         */
        @Deprecated
        public static Drums get(int id) throws NoSuchFieldException {
            for (Drums axis : Drums.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Button Panel Button with an ID of " + id);
        }

        Drums(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum DrumButton {
        ONE(1),
        TWO(4),
        A(2),
        B(3),
        PEDAL(5),
        SELECT(9),
        START(10),
        HOME(13);

        public final int AXIS_VALUE;

        /**
         * @param id - the Drum button id requested
         * @return the {@link DrumButton} enum with id mathing input
         * @throws NoSuchFieldException if there is no mapped drum button with that id
         * @deprecated Feature that should <b>never</b> be used. Gets the drum button from a provided int
         */
        @Deprecated
        public static DrumButton get(int id) throws NoSuchFieldException {
            for (DrumButton axis : DrumButton.values()) {
                if (axis.AXIS_VALUE == id) return axis;
            }
            throw new NoSuchFieldException("There is no Button Panel Button with an ID of " + id);
        }

        DrumButton(int value) {
            this.AXIS_VALUE = value;
        }
    }

    public enum BopItButtons {
        BOPIT(3),
        PULLIT(4),
        TWISTIT(1);

        public final int AXIS_VALUE;

        BopItButtons(int value) {
            AXIS_VALUE = value;
        }
    }
}