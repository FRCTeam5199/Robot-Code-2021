package frc.robot;

public class RobotNumbers {
    public static double triggerSensitivity = 0.25;
    public static int XBOX_CONTROLLER_SLOT = 0;
    public static int FLIGHT_STICK_SLOT = 1;
    public static int BUTTON_PANEL_SLOT = 2;

    public static final double DRIVEBASE_P = 0.08;
    public static final double DRIVEBASE_I = 0;
    public static final double DRIVEBASE_D = 0;
    public static final double DRIVEBASE_F = 0;
    public static final int DRIVE_TIMEOUT_MS = 0;

    public static final int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
    public static final double MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
    public static final double MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
    public static final double WHEEL_DIAMETER = 6; //update: now it's used once
    public static final double MAX_MOTOR_SPEED = 5000; //theoretical max motor speed in rpm

    public static final double XBOX_CONTROLLER_DEADZONE = 0.07;

    public static final double TURN_SCALE = 0.7;
    public static final double DRIVE_SCALE = 0.1;

    public static final double SHOOTER_P = 0.00035;
    public static final double SHOOTER_I = 0;
    public static final double SHOOTER_D = 0;
    public static final double SHOOTER_F = 0.00019;
    //TODO figure out what these are and how they are used
    public static final double SHOOTER_RECOVERY_P = 0.00037;
    public static final double SHOOTER_RECOVERY_I = 0;
    public static final double SHOOTER_RECOVERY_D = 0;
    public static final double SHOOTER_RECOVERY_F = 0.00019;

    public static final double motorPulleySize = 0;//?;
    public static final double driverPulleySize = 0;//?;
}
