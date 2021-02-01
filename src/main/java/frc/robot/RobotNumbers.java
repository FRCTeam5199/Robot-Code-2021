package frc.robot;

import frc.misc.InitializationFailureException;

public class RobotNumbers {
    /*public static final RobotNumbers getNumbersFrom = new RobotNumbers();
    public static final double gotNumbers;

    static {
        try {
            gotNumbers = getNumbersFrom.getClass().getField("DRIVEBASE_P").getDouble(getNumbersFrom);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InitializationFailureException(e.toString(), "");
        }
    }*/

    public static final double DRIVEBASE_P = 0;//0.0075;
    public static final double DRIVEBASE_I = 0;//0
    public static final double DRIVEBASE_D = 0.000005;//0.002;
    public static final double DRIVEBASE_F = 0.00002;//0;
    public static final int DRIVE_TIMEOUT_MS = 30;
    public static final int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
    public static final double MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
    public static final double MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
    public static final double WHEEL_DIAMETER = 6; //update: now it's used once
    public static final double MAX_MOTOR_SPEED = 5000; //theoretical max motor speed in rpm
    public static final double TURN_SCALE = 0.7;
    public static final double DRIVE_SCALE = 1;

    public static final double SHOOTER_P = 0.001;
    public static final double SHOOTER_I = 0.00003;
    public static final double SHOOTER_D = 0.0001;
    public static final double SHOOTER_F = 0.001;
    public static final double SHOOTER_RECOVERY_P = SHOOTER_P;//= 0.00037;
    public static final double SHOOTER_RECOVERY_I = SHOOTER_I;//= 0;
    public static final double SHOOTER_RECOVERY_D = SHOOTER_D;//= 0;
    public static final double SHOOTER_RECOVERY_F = SHOOTER_F;//= 0.00019;
    public static final double SHOOTER_SENSOR_UNITS_PER_ROTATION = 2048;
    public static final double motorPulleySize = 0;//?;
    public static final double driverPulleySize = 0;//?;
    public static final double CAMERA_HEIGHT = 0; //Inches
    public static final double CAMERA_PITCH = 0; //Radians
    public static final double TARGET_HEIGHT = 0;//2.44; //Meters

    public static final double XBOX_CONTROLLER_DEADZONE = 0.07;
    public static final double MOTOR_SPROCKET_SIZE = 1;
    public static final double TURRET_SPROCKET_SIZE = 11.1;
    public static final double TURRET_GEAR_RATIO = 7;
    public static final double TURRET_MAX_POS = 270;
    public static final double TURRET_MIN_POS = 0;
    public static final double TURRET_P = 0.006;
    public static final double TURRET_I = 0.00001;
    public static final double TURRET_D = 0.001;
    //public static final double TURRET_F = 0.001;
    public static final int SHOOTER_TIMEOUT_MS = 20;
    public static final double AUTON_TOLERANCE = 0.1;
    public static final double HEADING_P = 0.08;
    public static final double HEADING_I = 0.000005;
    public static final double HEADING_D = 0.0003;
    public static final double AUTO_SPEED = 3;
    public static final double AUTO_ROTATION_SPEED = 1;
    public static double triggerSensitivity = 0.25;
    public static int XBOX_CONTROLLER_SLOT = 0;
    public static int FLIGHT_STICK_SLOT = 1;
    public static int BUTTON_PANEL_SLOT = 2;
}