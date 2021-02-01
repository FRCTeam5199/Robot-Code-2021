package frc.robot;

import frc.misc.InitializationFailureException;
import frc.robot.robotconfigs.*;

public class RobotNumbers {
    public static final AbstractConfig getNumbersFrom = RobotToggles.getNumbersFrom;

    public static final double DRIVEBASE_P;
    public static final double DRIVEBASE_I;
    public static final double DRIVEBASE_D;
    public static final double DRIVEBASE_F;
    public static final int DRIVE_TIMEOUT_MS;
    public static final int DRIVEBASE_SENSOR_UNITS_PER_ROTATION;
    public static final double MAX_SPEED;
    public static final double MAX_ROTATION;
    public static final double WHEEL_DIAMETER;
    public static final double MAX_MOTOR_SPEED;
    public static final double TURN_SCALE;
    public static final double DRIVE_SCALE;

    public static final double SHOOTER_P;
    public static final double SHOOTER_I;
    public static final double SHOOTER_D;
    public static final double SHOOTER_F;
    public static final double SHOOTER_RECOVERY_P;
    public static final double SHOOTER_RECOVERY_I;
    public static final double SHOOTER_RECOVERY_D;
    public static final double SHOOTER_RECOVERY_F;
    public static final double SHOOTER_SENSOR_UNITS_PER_ROTATION;
    public static final double motorPulleySize;
    public static final double driverPulleySize;
    public static final double CAMERA_HEIGHT;
    public static final double CAMERA_PITCH;
    public static final double TARGET_HEIGHT;

    public static final double XBOX_CONTROLLER_DEADZONE;
    public static final double MOTOR_SPROCKET_SIZE;
    public static final double TURRET_SPROCKET_SIZE;
    public static final double TURRET_GEAR_RATIO;
    public static final double TURRET_MAX_POS;
    public static final double TURRET_MIN_POS;
    public static final double TURRET_P;
    public static final double TURRET_I;
    public static final double TURRET_D;
    //public static final double TURRET_F = 0.001;
    public static final int SHOOTER_TIMEOUT_MS;
    public static final double AUTON_TOLERANCE;
    public static final double HEADING_P;
    public static final double HEADING_I;
    public static final double HEADING_D;
    public static final double AUTO_SPEED;
    public static final double AUTO_ROTATION_SPEED;
    public static double triggerSensitivity;
    public static int XBOX_CONTROLLER_SLOT;
    public static int FLIGHT_STICK_SLOT;
    public static int BUTTON_PANEL_SLOT;

    //@author jojo2357
    static {
        try {
            DRIVEBASE_P = getNumbersFrom.getClass().getField("DRIVEBASE_P").getDouble(getNumbersFrom);
            DRIVEBASE_I = getNumbersFrom.getClass().getField("DRIVEBASE_I").getDouble(getNumbersFrom);
            DRIVEBASE_D = getNumbersFrom.getClass().getField("DRIVEBASE_D").getDouble(getNumbersFrom);
            DRIVEBASE_F = getNumbersFrom.getClass().getField("DRIVEBASE_F").getDouble(getNumbersFrom);
            DRIVE_TIMEOUT_MS = getNumbersFrom.getClass().getField("DRIVE_TIMEOUT_MS").getInt(getNumbersFrom);
            DRIVEBASE_SENSOR_UNITS_PER_ROTATION = getNumbersFrom.getClass().getField("DRIVEBASE_SENSOR_UNITS_PER_ROTATION").getInt(getNumbersFrom);
            MAX_SPEED = getNumbersFrom.getClass().getField("MAX_SPEED").getDouble(getNumbersFrom);
            MAX_ROTATION = getNumbersFrom.getClass().getField("MAX_ROTATION").getDouble(getNumbersFrom);
            WHEEL_DIAMETER = getNumbersFrom.getClass().getField("WHEEL_DIAMETER").getDouble(getNumbersFrom);
            MAX_MOTOR_SPEED = getNumbersFrom.getClass().getField("MAX_MOTOR_SPEED").getDouble(getNumbersFrom);
            TURN_SCALE = getNumbersFrom.getClass().getField("TURN_SCALE").getDouble(getNumbersFrom);
            DRIVE_SCALE = getNumbersFrom.getClass().getField("DRIVE_SCALE").getDouble(getNumbersFrom);

            SHOOTER_P = getNumbersFrom.getClass().getField("SHOOTER_P").getDouble(getNumbersFrom);
            SHOOTER_I = getNumbersFrom.getClass().getField("SHOOTER_I").getDouble(getNumbersFrom);
            SHOOTER_D = getNumbersFrom.getClass().getField("SHOOTER_D").getDouble(getNumbersFrom);
            SHOOTER_F = getNumbersFrom.getClass().getField("SHOOTER_F").getDouble(getNumbersFrom);
            SHOOTER_RECOVERY_P = SHOOTER_P;//= 0.00037;
            SHOOTER_RECOVERY_I = SHOOTER_I;//= 0;
            SHOOTER_RECOVERY_D = SHOOTER_D;//= 0;
            SHOOTER_RECOVERY_F = SHOOTER_F;//= 0.00019;
            SHOOTER_SENSOR_UNITS_PER_ROTATION = getNumbersFrom.getClass().getField("SHOOTER_SENSOR_UNITS_PER_ROTATION").getDouble(getNumbersFrom);
            motorPulleySize = getNumbersFrom.getClass().getField("motorPulleySize").getDouble(getNumbersFrom);
            driverPulleySize = getNumbersFrom.getClass().getField("driverPulleySize").getDouble(getNumbersFrom);
            CAMERA_HEIGHT = getNumbersFrom.getClass().getField("CAMERA_HEIGHT").getDouble(getNumbersFrom);
            CAMERA_PITCH = getNumbersFrom.getClass().getField("CAMERA_PITCH").getDouble(getNumbersFrom);
            TARGET_HEIGHT = getNumbersFrom.getClass().getField("TARGET_HEIGHT").getDouble(getNumbersFrom);

            XBOX_CONTROLLER_DEADZONE = getNumbersFrom.getClass().getField("XBOX_CONTROLLER_DEADZONE").getDouble(getNumbersFrom);
            MOTOR_SPROCKET_SIZE = getNumbersFrom.getClass().getField("MOTOR_SPROCKET_SIZE").getDouble(getNumbersFrom);
            TURRET_SPROCKET_SIZE = getNumbersFrom.getClass().getField("TURRET_SPROCKET_SIZE").getDouble(getNumbersFrom);
            TURRET_GEAR_RATIO = getNumbersFrom.getClass().getField("TURRET_GEAR_RATIO").getDouble(getNumbersFrom);
            TURRET_MAX_POS = getNumbersFrom.getClass().getField("TURRET_MAX_POS").getDouble(getNumbersFrom);
            TURRET_MIN_POS = getNumbersFrom.getClass().getField("TURRET_MIN_POS").getDouble(getNumbersFrom);
            TURRET_P = getNumbersFrom.getClass().getField("TURRET_P").getDouble(getNumbersFrom);
            TURRET_I = getNumbersFrom.getClass().getField("TURRET_I").getDouble(getNumbersFrom);
            TURRET_D = getNumbersFrom.getClass().getField("TURRET_D").getDouble(getNumbersFrom);
            // TURRET_F = 0.001;
            SHOOTER_TIMEOUT_MS = getNumbersFrom.getClass().getField("SHOOTER_TIMEOUT_MS").getInt(getNumbersFrom);
            AUTON_TOLERANCE = getNumbersFrom.getClass().getField("AUTON_TOLERANCE").getDouble(getNumbersFrom);
            HEADING_P = getNumbersFrom.getClass().getField("HEADING_P").getDouble(getNumbersFrom);
            HEADING_I = getNumbersFrom.getClass().getField("HEADING_I").getDouble(getNumbersFrom);
            HEADING_D = getNumbersFrom.getClass().getField("HEADING_D").getDouble(getNumbersFrom);
            AUTO_SPEED = getNumbersFrom.getClass().getField("AUTO_SPEED").getDouble(getNumbersFrom);
            AUTO_ROTATION_SPEED = getNumbersFrom.getClass().getField("AUTO_ROTATION_SPEED").getDouble(getNumbersFrom);
            triggerSensitivity = getNumbersFrom.getClass().getField("triggerSensitivity").getDouble(getNumbersFrom);
            XBOX_CONTROLLER_SLOT = getNumbersFrom.getClass().getField("XBOX_CONTROLLER_SLOT").getInt(getNumbersFrom);
            FLIGHT_STICK_SLOT = getNumbersFrom.getClass().getField("FLIGHT_STICK_SLOT").getInt(getNumbersFrom);
            BUTTON_PANEL_SLOT = getNumbersFrom.getClass().getField("BUTTON_PANEL_SLOT").getInt(getNumbersFrom);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InitializationFailureException(e.toString(), "");
        }
    }
}