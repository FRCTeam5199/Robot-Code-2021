package frc.robot;

import frc.drive.DriveTypes;

public class RobotToggles {
    public static final boolean DEBUG = false;
    //Subsystems

    /**
     * Enables the drive
     */
    public static final boolean ENABLE_DRIVE = true;
    public static final boolean ENABLE_INTAKE = false;
    public static final boolean ENABLE_CLIMBER = false;
    public static final boolean ENABLE_SHOOTER = false;
    public static final boolean ENABLE_HOPPER = false;


    //Drivetrain
    public static final boolean DRIVE_USE_SPARKS = false;
    public static final boolean DRIVE_USE_6_MOTORS = false;
    public static final boolean DRIVE_INVERT_LEFT = true;
    public static final boolean DRIVE_INVERT_RIGHT = false;
    public static final DriveTypes EXPERIMENTAL_DRIVE = DriveTypes.STANDARD;
    public static final boolean CALIBRATE_DRIVE_PID = false;

    //Misc
    public static final boolean ENABLE_VISION = false;
    public static final boolean USE_PHOTONVISION = true;
    public static final boolean ENABLE_IMU = false;
    public static final boolean USE_PIGEON = false;
    public static final boolean USE_NAVX2 = false;


    //SHOOTER
    public static final boolean SHOOTER_USE_SPARKS = false;
    public static final boolean SHOOTER_USE_TWO_MOTORS = true;
    public static final boolean SHOOTER_INVERTED = true;
}
