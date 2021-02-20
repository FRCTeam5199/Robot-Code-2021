package frc.robot;

import frc.robot.robotconfigs.DefaultConfig;

public class RobotNumbers {
    public static final DefaultConfig getNumbersFrom = RobotToggles.getNumbersFrom;

    public static final double DRIVEBASE_P = getNumbersFrom.DRIVEBASE_P;
    public static final double DRIVEBASE_I = getNumbersFrom.DRIVEBASE_I;
    public static final double DRIVEBASE_D = getNumbersFrom.DRIVEBASE_D;
    public static final double DRIVEBASE_F = getNumbersFrom.DRIVEBASE_F;
    public static final int DRIVE_TIMEOUT_MS = getNumbersFrom.DRIVE_TIMEOUT_MS;
    public static final int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = getNumbersFrom.DRIVEBASE_SENSOR_UNITS_PER_ROTATION;
    public static final double MAX_SPEED = getNumbersFrom.MAX_SPEED;
    public static final double MAX_ROTATION = getNumbersFrom.MAX_ROTATION;
    public static final double WHEEL_DIAMETER = getNumbersFrom.WHEEL_DIAMETER;
    public static final double MAX_MOTOR_SPEED = getNumbersFrom.MAX_MOTOR_SPEED;
    public static final double TURN_SCALE = getNumbersFrom.TURN_SCALE;
    public static final double DRIVE_SCALE = getNumbersFrom.DRIVE_SCALE;

    public static final double SHOOTER_P = getNumbersFrom.SHOOTER_P;
    public static final double SHOOTER_I = getNumbersFrom.SHOOTER_I;
    public static final double SHOOTER_D = getNumbersFrom.SHOOTER_D;
    public static final double SHOOTER_F = getNumbersFrom.SHOOTER_F;
    public static final double SHOOTER_RECOVERY_P = getNumbersFrom.SHOOTER_RECOVERY_P;
    public static final double SHOOTER_RECOVERY_I = getNumbersFrom.SHOOTER_RECOVERY_I;
    public static final double SHOOTER_RECOVERY_D = getNumbersFrom.SHOOTER_RECOVERY_D;
    public static final double SHOOTER_RECOVERY_F = getNumbersFrom.SHOOTER_RECOVERY_F;
    public static final double SHOOTER_SENSOR_UNITS_PER_ROTATION = getNumbersFrom.SHOOTER_SENSOR_UNITS_PER_ROTATION;
    public static final double motorPulleySize = getNumbersFrom.motorPulleySize;
    public static final double driverPulleySize = getNumbersFrom.driverPulleySize;
    public static final double CAMERA_HEIGHT = getNumbersFrom.CAMERA_HEIGHT;
    public static final double CAMERA_PITCH = getNumbersFrom.CAMERA_PITCH;
    public static final double TARGET_HEIGHT = getNumbersFrom.TARGET_HEIGHT;

    public static final double XBOX_CONTROLLER_DEADZONE = getNumbersFrom.XBOX_CONTROLLER_DEADZONE;
    public static final double MOTOR_SPROCKET_SIZE = getNumbersFrom.MOTOR_SPROCKET_SIZE;
    public static final double TURRET_SPROCKET_SIZE = getNumbersFrom.TURRET_SPROCKET_SIZE;
    public static final double TURRET_GEAR_RATIO = getNumbersFrom.TURRET_GEAR_RATIO;
    public static final double TURRET_MAX_POS = getNumbersFrom.TURRET_MAX_POS;
    public static final double TURRET_MIN_POS = getNumbersFrom.TURRET_MIN_POS;
    public static final double TURRET_P = getNumbersFrom.TURRET_P;
    public static final double TURRET_I = getNumbersFrom.TURRET_I;
    public static final double TURRET_D = getNumbersFrom.TURRET_D;
    //public static final double TURRET_F = 0.001 = getNumbersFrom.
    public static final int SHOOTER_TIMEOUT_MS = getNumbersFrom.SHOOTER_TIMEOUT_MS;
    public static final double AUTON_TOLERANCE = getNumbersFrom.AUTON_TOLERANCE;
    public static final double HEADING_P = getNumbersFrom.HEADING_P;
    public static final double HEADING_I = getNumbersFrom.HEADING_I;
    public static final double HEADING_D = getNumbersFrom.HEADING_D;
    public static final double AUTO_SPEED = getNumbersFrom.AUTO_SPEED;
    public static final double AUTO_ROTATION_SPEED = getNumbersFrom.AUTO_ROTATION_SPEED;
    public static double triggerSensitivity = getNumbersFrom.triggerSensitivity;
    public static int XBOX_CONTROLLER_SLOT = getNumbersFrom.XBOX_CONTROLLER_SLOT;
    public static int FLIGHT_STICK_SLOT = getNumbersFrom.FLIGHT_STICK_SLOT;
    public static int BUTTON_PANEL_SLOT = getNumbersFrom.BUTTON_PANEL_SLOT;

    public static void printNumbers() {
        System.out.println("-------------------<RobotNumbers>-----------------");
        System.out.println("Drive PIDF (timeout) " + DRIVEBASE_P + ", " + DRIVEBASE_I + ", " + DRIVEBASE_D + ", " + DRIVEBASE_F + " (" + DRIVE_TIMEOUT_MS + ")");
        System.out.println("Max drive speed/rotation " + MAX_SPEED + "/" + MAX_ROTATION);
        System.out.println("Turn + drive scale " + TURN_SCALE + "/" + DRIVE_SCALE);
        //System.out.println("");
        System.out.println("-------------------</RobotNumbers>----------------");
    }
}