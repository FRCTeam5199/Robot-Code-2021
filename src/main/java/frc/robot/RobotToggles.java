package frc.robot;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.robot.robotconfigs.CompetitionRobot2021;
import frc.robot.robotconfigs.DefaultConfig;
import frc.robot.robotconfigs.PracticeRobot2021;

public class RobotToggles {
    /**
     * If you change this ONE SINGULAR VARIBLE the ENTIRE CONFIG WILL CHANGE.
     * Use this to select which robot you are using from the list under robotconfigs
     */
    public static final DefaultConfig getNumbersFrom = new CompetitionRobot2021();

    public static final boolean DEBUG = true;
    //Subsystems

    /**
     * Enables the drive
     */
    public static final boolean ENABLE_DRIVE = getNumbersFrom.ENABLE_DRIVE;
    public static final boolean ENABLE_INTAKE = getNumbersFrom.ENABLE_INTAKE;
    public static final boolean ENABLE_CLIMBER = getNumbersFrom.ENABLE_CLIMBER;
    public static final boolean ENABLE_SHOOTER = getNumbersFrom.ENABLE_SHOOTER;
    public static final boolean ENABLE_HOPPER = getNumbersFrom.ENABLE_HOPPER;
    public static final boolean ENABLE_MUSIC = getNumbersFrom.ENABLE_MUSIC;

    //Drivetrain
    public static final boolean DRIVE_USE_SPARKS = getNumbersFrom.DRIVE_USE_SPARKS;
    public static final boolean DRIVE_USE_6_MOTORS = getNumbersFrom.DRIVE_USE_6_MOTORS;
    public static final boolean DRIVE_INVERT_LEFT = getNumbersFrom.DRIVE_INVERT_LEFT;
    public static final boolean DRIVE_INVERT_RIGHT = getNumbersFrom.DRIVE_INVERT_RIGHT;

    public static final boolean CALIBRATE_DRIVE_PID = getNumbersFrom.CALIBRATE_DRIVE_PID;
    public static final boolean CALIBRATE_SHOOTER_PID = getNumbersFrom.CALIBRATE_SHOOTER_PID;

    //Misc
    public static final boolean ENABLE_VISION = getNumbersFrom.ENABLE_VISION;
    public static final boolean USE_PHOTONVISION = getNumbersFrom.USE_PHOTONVISION;
    public static final boolean ENABLE_IMU = getNumbersFrom.ENABLE_IMU;
    public static final boolean USE_PIGEON = getNumbersFrom.USE_PIGEON;
    public static final boolean USE_NAVX2 = getNumbersFrom.USE_NAVX2;

    //SHOOTER
    public static final boolean SHOOTER_USE_SPARKS = getNumbersFrom.SHOOTER_USE_SPARKS;
    public static final boolean SHOOTER_USE_TWO_MOTORS = getNumbersFrom.SHOOTER_USE_TWO_MOTORS;
    public static final boolean SHOOTER_INVERTED = getNumbersFrom.SHOOTER_INVERTED;

    //INTAKE
    public static final boolean INDEXER_AUTO_INDEX = getNumbersFrom.INDEXER_AUTO_INDEX;

    //UI Style
    public static final DriveTypes EXPERIMENTAL_DRIVE = getNumbersFrom.EXPERIMENTAL_DRIVE;
    public static final ShootingControlStyles SHOOTER_CONTROL_STYLE = getNumbersFrom.SHOOTER_CONTROL_STYLE;
    public static final IntakeControlStyles INTAKE_CONTROL_STYLE = getNumbersFrom.INTAKE_CONTROL_STYLE;

    public static final boolean GALACTIC_SEARCH = getNumbersFrom.GALACTIC_SEARCH;

    public static void printToggles() {
        System.out.println("-------------------<RobotToggles>-----------------");
        System.out.println("          Driving " + ENABLE_DRIVE);
        System.out.println("         Intaking " + ENABLE_INTAKE);
        System.out.println("         Shooting " + ENABLE_SHOOTER);
        System.out.println("          Hopping " + ENABLE_HOPPER);
        System.out.println("           Vision " + ENABLE_VISION);
        System.out.println("              IMU " + ENABLE_IMU);
        System.out.println("      Pigeon/NavX " + USE_PIGEON + "/" + USE_NAVX2);
        System.out.println("     Drive sparks " + DRIVE_USE_SPARKS);
        System.out.println("   Drive 6 motors " + DRIVE_USE_6_MOTORS);
        System.out.println("  Calibrate drive " + CALIBRATE_DRIVE_PID);
        System.out.println("Calibrate shooter " + CALIBRATE_SHOOTER_PID);
        System.out.println("Shoot with sparks " + SHOOTER_USE_SPARKS);
        System.out.println("     Shoot with 2 " + SHOOTER_USE_TWO_MOTORS);
        System.out.println("      Drive style " + EXPERIMENTAL_DRIVE.name());
        System.out.println("      Shoot style " + SHOOTER_CONTROL_STYLE.name());
        System.out.println("     Intake style " + INTAKE_CONTROL_STYLE.name());
        System.out.println("-------------------</RobotToggles>-----------------");
    }
}
