package frc.robot;

import frc.drive.DriveTypes;
import frc.misc.InitializationFailureException;
import frc.robot.robotconfigs.AbstractConfig;
import frc.robot.robotconfigs.Robot2020;

public class RobotToggles {
    /**
     * If you change this ONE SINGULAR VARIBLE the ENTIRE CONFIG WILL CHANGE.
     * Use this to select which robot you are using from the list under robotconfigs
     */
    public static final AbstractConfig getNumbersFrom = new Robot2020();

    public static final boolean DEBUG;
    //Subsystems

    /**
     * Enables the drive
     */
    public static final boolean ENABLE_DRIVE;
    public static final boolean ENABLE_INTAKE;
    public static final boolean ENABLE_CLIMBER;
    public static final boolean ENABLE_SHOOTER;
    public static final boolean ENABLE_HOPPER;

    //Drivetrain
    public static final boolean DRIVE_USE_SPARKS;
    public static final boolean DRIVE_USE_6_MOTORS;
    public static final boolean DRIVE_INVERT_LEFT;
    public static final boolean DRIVE_INVERT_RIGHT;
    public static final DriveTypes EXPERIMENTAL_DRIVE;
    public static final boolean CALIBRATE_DRIVE_PID;

    //Misc
    public static final boolean ENABLE_VISION;
    public static final boolean USE_PHOTONVISION;
    public static final boolean ENABLE_IMU;
    public static final boolean USE_PIGEON;
    public static final boolean USE_NAVX2;

    //SHOOTER
    public static final boolean SHOOTER_USE_SPARKS;
    public static final boolean SHOOTER_USE_TWO_MOTORS;
    public static final boolean SHOOTER_INVERTED;

    //@author jojo2357
    static {
        try {
            DEBUG = getNumbersFrom.getClass().getField("DEBUG").getBoolean(getNumbersFrom);

            ENABLE_DRIVE = getNumbersFrom.getClass().getField("ENABLE_DRIVE").getBoolean(getNumbersFrom);
            ENABLE_INTAKE = getNumbersFrom.getClass().getField("ENABLE_INTAKE").getBoolean(getNumbersFrom);
            ENABLE_CLIMBER = getNumbersFrom.getClass().getField("ENABLE_CLIMBER").getBoolean(getNumbersFrom);
            ENABLE_SHOOTER = getNumbersFrom.getClass().getField("ENABLE_SHOOTER").getBoolean(getNumbersFrom);
            ENABLE_HOPPER = getNumbersFrom.getClass().getField("ENABLE_HOPPER").getBoolean(getNumbersFrom);

            DRIVE_USE_SPARKS = getNumbersFrom.getClass().getField("DRIVE_USE_SPARKS").getBoolean(getNumbersFrom);
            DRIVE_USE_6_MOTORS = getNumbersFrom.getClass().getField("DRIVE_USE_6_MOTORS").getBoolean(getNumbersFrom);
            DRIVE_INVERT_LEFT = getNumbersFrom.getClass().getField("DRIVE_INVERT_LEFT").getBoolean(getNumbersFrom);
            DRIVE_INVERT_RIGHT = getNumbersFrom.getClass().getField("DRIVE_INVERT_RIGHT").getBoolean(getNumbersFrom);
            EXPERIMENTAL_DRIVE = (DriveTypes) getNumbersFrom.getClass().getField("DRIVE_INVERT_RIGHT").get(getNumbersFrom);
            CALIBRATE_DRIVE_PID = getNumbersFrom.getClass().getField("CALIBRATE_DRIVE_PID").getBoolean(getNumbersFrom);

            ENABLE_VISION = getNumbersFrom.getClass().getField("ENABLE_VISION").getBoolean(getNumbersFrom);
            USE_PHOTONVISION = getNumbersFrom.getClass().getField("USE_PHOTONVISION").getBoolean(getNumbersFrom);
            ENABLE_IMU = getNumbersFrom.getClass().getField("ENABLE_IMU").getBoolean(getNumbersFrom);
            USE_PIGEON = getNumbersFrom.getClass().getField("USE_PIGEON").getBoolean(getNumbersFrom);
            USE_NAVX2 = getNumbersFrom.getClass().getField("USE_NAVX2").getBoolean(getNumbersFrom);

            SHOOTER_USE_SPARKS = getNumbersFrom.getClass().getField("SHOOTER_USE_SPARKS").getBoolean(getNumbersFrom);
            SHOOTER_USE_TWO_MOTORS = getNumbersFrom.getClass().getField("SHOOTER_USE_TWO_MOTORS").getBoolean(getNumbersFrom);
            SHOOTER_INVERTED = getNumbersFrom.getClass().getField("SHOOTER_INVERTED").getBoolean(getNumbersFrom);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InitializationFailureException(e.toString(), "");
        }
    }
}
