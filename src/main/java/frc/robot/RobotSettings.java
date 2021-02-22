package frc.robot;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.motors.SupportedMotors;
import frc.robot.robotconfigs.DefaultConfig;

import static frc.robot.Robot.getNumbersFrom;

/**
 * Here's where the fun happens. Change config files by changing {@link Robot#getNumbersFrom}. Make configs using {@link
 * DefaultConfig}. If you dont know whats happening here, please see the docs or use intellisense to find what you are
 * looking for
 *
 * @see DefaultConfig
 * @see Robot#getNumbersFrom
 */
public class RobotSettings {
    /**
     * Toggles debug print statements
     */
    public static final boolean DEBUG = true;
    //Subsystems

    /**
     * Enables the {@link frc.drive.DriveManager drivetrain}
     */
    public static final boolean ENABLE_DRIVE = getNumbersFrom.ENABLE_DRIVE;
    /**
     * Enables the {@link frc.ballstuff.intaking.Intake intake}
     */
    public static final boolean ENABLE_INTAKE = getNumbersFrom.ENABLE_INTAKE;
    /**
     * Enables the {@link frc.ballstuff.shooting.Shooter shooter}
     */
    public static final boolean ENABLE_SHOOTER = getNumbersFrom.ENABLE_SHOOTER;
    /**
     * Enables the {@link frc.ballstuff.intaking.Hopper hopper} ({@link frc.ballstuff.intaking.Hopper#agitator} + {@link
     * frc.ballstuff.intaking.Hopper#indexer} ({@link #ENABLE_INDEXER_AUTO_INDEX}))
     */
    public static final boolean ENABLE_HOPPER = getNumbersFrom.ENABLE_HOPPER;

    /**
     * Enables the distance sensor in the {@link #ENABLE_HOPPER indexer}
     */
    public static final boolean ENABLE_INDEXER_AUTO_INDEX = getNumbersFrom.ENABLE_INDEXER_AUTO_INDEX;

    /**
     * Enables {@link Robot#chirp}
     *
     * @see frc.motors.TalonMotorController
     */
    public static final boolean ENABLE_MUSIC = getNumbersFrom.ENABLE_MUSIC;

    /**
     * Whether the {@link #ENABLE_DRIVE drivetrain} {@link frc.drive.DriveManager#followerL follower motors} will have 1
     * or 2 followers apeice
     */
    public static final boolean DRIVE_USE_6_MOTORS = getNumbersFrom.DRIVE_USE_6_MOTORS;
    /**
     * Invert left side of {@link #ENABLE_DRIVE drivetrain}
     */
    public static final boolean DRIVE_INVERT_LEFT = getNumbersFrom.DRIVE_INVERT_LEFT;
    /**
     * Invert right side of {@link #ENABLE_DRIVE drivetrain}
     */
    public static final boolean DRIVE_INVERT_RIGHT = getNumbersFrom.DRIVE_INVERT_RIGHT;

    //Misc
    public static final boolean ENABLE_VISION = getNumbersFrom.ENABLE_VISION;
    public static final boolean USE_PHOTONVISION = getNumbersFrom.USE_PHOTONVISION;
    public static final boolean ENABLE_IMU = getNumbersFrom.ENABLE_IMU;
    public static final boolean USE_PIGEON = getNumbersFrom.USE_PIGEON;
    //TODO make this a supported imu's enum
    public static final boolean USE_NAVX2 = getNumbersFrom.USE_NAVX2;

    //SHOOTER
    /**
     * Whether the {@link #ENABLE_SHOOTER shooter} has two or one motors
     */
    public static final boolean SHOOTER_USE_TWO_MOTORS = getNumbersFrom.SHOOTER_USE_TWO_MOTORS;
    /**
     * Whether to invert the {@link #ENABLE_SHOOTER shooter} direction
     */
    public static final boolean SHOOTER_INVERTED = getNumbersFrom.SHOOTER_INVERTED;

    //UI Style
    /**
     * The {@link #ENABLE_DRIVE drivetrain} control style to use. Should be used firstly for changing controllers and
     * secondly changing controller behavior
     *
     * @see frc.controllers.BaseController
     */
    public static final DriveTypes DRIVE_STYLE = getNumbersFrom.DRIVE_STYLE;

    /**
     * The {@link #ENABLE_SHOOTER shooter} style to use. Should be used firstly for changing controllers and secondly
     * changing controller behavior
     *
     * @see frc.controllers.BaseController
     */
    public static final ShootingControlStyles SHOOTER_CONTROL_STYLE = getNumbersFrom.SHOOTER_CONTROL_STYLE;

    /**
     * The {@link #ENABLE_INTAKE intake} style to use. Should be used firstly for changing controllers and secondly
     * changing controller behavior
     *
     * @see frc.controllers.BaseController
     */
    public static final IntakeControlStyles INTAKE_CONTROL_STYLE = getNumbersFrom.INTAKE_CONTROL_STYLE;

    public static final AutonType AUTON_MODE = getNumbersFrom.AUTON_TYPE;
    public static final SupportedMotors DRIVE_MOTOR_TYPE = getNumbersFrom.DRIVE_MOTOR_TYPE;
    public static final SupportedMotors SHOOTER_MOTOR_TYPE = getNumbersFrom.SHOOTER_MOTOR_TYPE;

    public static boolean autonComplete = false;

    public static void printToggles() {
        System.out.println("-------------------<RobotSettings>-----------------");
        System.out.println("          Driving " + ENABLE_DRIVE);
        System.out.println("         Intaking " + ENABLE_INTAKE);
        System.out.println("         Shooting " + ENABLE_SHOOTER);
        System.out.println("          Hopping " + ENABLE_HOPPER);
        System.out.println("           Vision " + ENABLE_VISION);
        System.out.println("              IMU " + ENABLE_IMU);
        System.out.println("      Pigeon/NavX " + USE_PIGEON + "/" + USE_NAVX2);
        System.out.println("     Drive motors " + DRIVE_MOTOR_TYPE.name());
        System.out.println("   Drive 6 motors " + DRIVE_USE_6_MOTORS);
        System.out.println("   Shooter motors " + SHOOTER_MOTOR_TYPE.name());
        System.out.println("     Shoot with 2 " + SHOOTER_USE_TWO_MOTORS);
        System.out.println("      Drive style " + DRIVE_STYLE.name());
        System.out.println("      Shoot style " + SHOOTER_CONTROL_STYLE.name());
        System.out.println("     Intake style " + INTAKE_CONTROL_STYLE.name());
        System.out.println("  Auton Completed " + autonComplete);
        System.out.println("-------------------</RobotSettings>-----------------");
    }

    public static final PID DRIVEBASE_PID = getNumbersFrom.DRIVEBASE_PID;
    public static final int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = getNumbersFrom.DRIVEBASE_SENSOR_UNITS_PER_ROTATION;
    public static final double MAX_SPEED = getNumbersFrom.MAX_SPEED;
    public static final double MAX_ROTATION = getNumbersFrom.MAX_ROTATION;
    public static final double WHEEL_DIAMETER = getNumbersFrom.WHEEL_DIAMETER;
    public static final double MAX_MOTOR_SPEED = getNumbersFrom.MAX_MOTOR_SPEED;
    public static final double TURN_SCALE = getNumbersFrom.TURN_SCALE;
    public static final double DRIVE_SCALE = getNumbersFrom.DRIVE_SCALE;
    public static final double DRIVE_GEARING = getNumbersFrom.DRIVE_GEARING;

    public static final PID SHOOTER_PID = getNumbersFrom.SHOOTER_PID;
    public static final PID SHOOTER_RECOVERY_PID = getNumbersFrom.SHOOTER_RECOVERY_PID;
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
    public static final PID TURRET_PID = getNumbersFrom.TURRET_PID;
    public static final double AUTON_TOLERANCE = getNumbersFrom.AUTON_TOLERANCE;
    public static final PID HEADING_PID = getNumbersFrom.HEADING_PID;
    public static final double AUTO_SPEED = getNumbersFrom.AUTO_SPEED;
    public static final double AUTO_ROTATION_SPEED = getNumbersFrom.AUTO_ROTATION_SPEED;
    public static int XBOX_CONTROLLER_USB_SLOT = getNumbersFrom.XBOX_CONTROLLER_USB_SLOT;
    public static int FLIGHT_STICK_USB_SLOT = getNumbersFrom.FLIGHT_STICK_USB_SLOT;
    public static int BUTTON_PANEL_USB_SLOT = getNumbersFrom.BUTTON_PANEL_USB_SLOT;

    /**
     * Prints out "Numbers" which pertain to constants regarding the robot such as gearings, wheel sizes, etc. Not to be
     * confused with {@link #printMappings()} which prints numbers associated witd ID's and software. this is hardware
     */
    public static void printNumbers() {
        System.out.println("-------------------<RobotSettings>-----------------");
        System.out.println("Drive PIDF " + DRIVEBASE_PID);
        System.out.println("Max drive speed/rotation " + MAX_SPEED + "/" + MAX_ROTATION);
        System.out.println("Turn + drive scale " + TURN_SCALE + "/" + DRIVE_SCALE);
        //System.out.println("");
        System.out.println("-------------------</RobotSettings>----------------");
    }

    public static final String GOAL_CAM_NAME = getNumbersFrom.GOAL_CAM_NAME;
    public static final String BALL_CAM_NAME = getNumbersFrom.BALL_CAM_NAME;

    //Drive Motors
    public static final int DRIVE_LEADER_L_ID = getNumbersFrom.DRIVE_LEADER_L_ID;
    public static final int[] DRIVE_FOLLOWERS_L_IDS = getNumbersFrom.DRIVE_FOLLOWERS_L_IDS;

    public static final int DRIVE_LEADER_R_ID = getNumbersFrom.DRIVE_LEADER_R_ID;
    public static final int[] DRIVE_FOLLOWERS_R_IDS = getNumbersFrom.DRIVE_FOLLOWERS_R_IDS;

    //Shooter Motors
    public static final int SHOOTER_LEADER_ID = getNumbersFrom.SHOOTER_LEADER_ID;
    public static final int SHOOTER_FOLLOWER_ID = getNumbersFrom.SHOOTER_FOLLOWER_ID;

    //turret
    public static final int TURRET_YAW = getNumbersFrom.TURRET_YAW;

    //hopper
    public static final int AGITATOR_MOTOR_ID = getNumbersFrom.AGITATOR_MOTOR_ID;
    public static final int INDEXER_MOTOR_ID = getNumbersFrom.INDEXER_MOTOR_ID;

    /**
     * The id for the {@link #ENABLE_INTAKE intake motor}
     */
    public static final int INTAKE_MOTOR_ID = getNumbersFrom.INTAKE_MOTOR_ID;

    /**
     * The id of the {@link #ENABLE_IMU IMU} which should be independent of the {@link #USE_PIGEON IMU type}
     */
    public static final int IMU_ID = getNumbersFrom.IMU_ID;

    /**
     * Prints out all of the id's for anything that needs an id
     */
    public static void printMappings() {
        System.out.println("-------------------<RobotSettingspings>-----------------");
        System.out.println("                    Goal cam name: " + GOAL_CAM_NAME);
        System.out.println("                    Ball cam name: " + BALL_CAM_NAME);
        System.out.println(" Drive leader left id (followers): " + DRIVE_LEADER_L_ID + " (" + DRIVE_FOLLOWERS_L_IDS[0] + (DRIVE_FOLLOWERS_L_IDS.length > 1 ? ", " + DRIVE_FOLLOWERS_L_IDS[1] : "") + ")");
        System.out.println("Drive leader right id (followers): " + DRIVE_LEADER_R_ID + " (" + DRIVE_FOLLOWERS_R_IDS[0] + (DRIVE_FOLLOWERS_R_IDS.length > 1 ? ", " + DRIVE_FOLLOWERS_R_IDS[1] : "") + ")");
        System.out.println("        Shooter leader (follower): " + SHOOTER_LEADER_ID + " (" + SHOOTER_FOLLOWER_ID + ")");
        System.out.println("                       Turret yaw: " + TURRET_YAW);
        System.out.println("                      Agitator id: " + AGITATOR_MOTOR_ID);
        System.out.println("                       Indexer id: " + INDEXER_MOTOR_ID);
        System.out.println("                        Intake id: " + INTAKE_MOTOR_ID);
        System.out.println("                           IMU id: " + IMU_ID);
        System.out.println("-------------------</RobotSettingspings>-----------------");
    }
}
