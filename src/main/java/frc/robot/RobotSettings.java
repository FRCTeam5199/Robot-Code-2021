package frc.robot;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.motors.SupportedMotors;
import frc.robot.robotconfigs.DefaultConfig;
import frc.telemetry.imu.SupportedIMU;
import frc.vision.camera.SupportedVision;

import static frc.robot.Robot.settingsFile;

/**
 * Here's where the fun happens. Change config files by changing {@link Robot#settingsFile}. Make configs using {@link
 * DefaultConfig}. If you dont know whats happening here, please see the docs or use intellisense to find what you are
 * looking for
 *
 * @see DefaultConfig
 * @see Robot#settingsFile
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
    public static final boolean ENABLE_DRIVE = settingsFile.ENABLE_DRIVE;
    /**
     * Enables the {@link frc.ballstuff.intaking.Intake intake}
     */
    public static final boolean ENABLE_INTAKE = settingsFile.ENABLE_INTAKE;
    /**
     * Enables the {@link frc.ballstuff.shooting.Shooter shooter}
     */
    public static final boolean ENABLE_SHOOTER = settingsFile.ENABLE_SHOOTER;
    /**
     * Enables the {@link frc.ballstuff.intaking.Hopper hopper} ({@link frc.ballstuff.intaking.Hopper#agitator} + {@link
     * frc.ballstuff.intaking.Hopper#indexer} ({@link #ENABLE_INDEXER_AUTO_INDEX}))
     */
    public static final boolean ENABLE_HOPPER = settingsFile.ENABLE_HOPPER;

    /**
     * Enables the distance sensor in the {@link #ENABLE_HOPPER indexer}
     */
    public static final boolean ENABLE_INDEXER_AUTO_INDEX = settingsFile.ENABLE_INDEXER_AUTO_INDEX;
    public static final int INDEXER_DETECTION_CUTOFF_DISTANCE = settingsFile.INDEXER_DETECTION_CUTOFF_DISTANCE;

    public static final boolean ENABLE_AGITATOR = settingsFile.ENABLE_AGITATOR;
    public static final boolean ENABLE_INDEXER = settingsFile.ENABLE_INDEXER;
    public static final boolean ENABLE_TURRET = settingsFile.ENABLE_TURRET;

    /**
     * Enables {@link Robot#chirp}
     *
     * @see frc.motors.TalonMotorController
     */
    public static final boolean ENABLE_MUSIC = settingsFile.ENABLE_MUSIC;

    /**
     * Enables {@link frc.misc.LEDs}
     */
    public static final boolean ENABLE_LEDS = settingsFile.ENABLE_LEDS;

    /**
     * Enables {@link frc.pdp.PDP}
     */
    public static final boolean ENABLE_PDP = settingsFile.ENABLE_PDP;

    public static final int LED_STRAND_LENGTH = settingsFile.LED_STRAND_LENGTH;

    public static final int LED_STRAND_PORT_ID = settingsFile.LED_STRAND_PORT_ID;

    /**
     * Whether the {@link #ENABLE_DRIVE drivetrain} {@link frc.drive.DriveManager#followerL follower motors} will have 1
     * or 2 followers apeice
     */
    public static final boolean DRIVE_USE_6_MOTORS = settingsFile.DRIVE_USE_6_MOTORS;
    /**
     * Invert left side of {@link #ENABLE_DRIVE drivetrain}
     */
    public static final boolean DRIVE_INVERT_LEFT = settingsFile.DRIVE_INVERT_LEFT;
    /**
     * Invert right side of {@link #ENABLE_DRIVE drivetrain}
     */
    public static final boolean DRIVE_INVERT_RIGHT = settingsFile.DRIVE_INVERT_RIGHT;

    //Misc
    public static final boolean ENABLE_VISION = settingsFile.ENABLE_VISION;
    public static final boolean USE_PHOTONVISION = settingsFile.USE_PHOTONVISION;
    public static final boolean ENABLE_IMU = settingsFile.ENABLE_IMU;
    public static final Object IMU_NAVX_PORT = settingsFile.IMU_NAVX_PORT;

    //SHOOTER
    /**
     * Whether the {@link #ENABLE_SHOOTER shooter} has two or one motors
     */
    public static final boolean SHOOTER_USE_TWO_MOTORS = settingsFile.SHOOTER_USE_TWO_MOTORS;
    /**
     * Whether to invert the {@link #ENABLE_SHOOTER shooter} direction
     */
    public static final boolean SHOOTER_INVERTED = settingsFile.SHOOTER_INVERTED;

    /**
     * Should the hood be able to be articulated?
     */
    public static final boolean ENABLE_HOOD_ARTICULATION = settingsFile.ENABLE_HOOD_ARTICULATION;

    //UI Style
    /**
     * The {@link #ENABLE_DRIVE drivetrain} control style to use. Should be used firstly for changing controllers and
     * secondly changing controller behavior
     *
     * @see frc.controllers.BaseController
     */
    public static final DriveTypes DRIVE_STYLE = settingsFile.DRIVE_STYLE;

    /**
     * The {@link #ENABLE_SHOOTER shooter} style to use. Should be used firstly for changing controllers and secondly
     * changing controller behavior
     *
     * @see frc.controllers.BaseController
     */
    public static final ShootingControlStyles SHOOTER_CONTROL_STYLE = settingsFile.SHOOTER_CONTROL_STYLE;

    /**
     * The {@link #ENABLE_INTAKE intake} style to use. Should be used firstly for changing controllers and secondly
     * changing controller behavior
     *
     * @see frc.controllers.BaseController
     */
    public static final IntakeControlStyles INTAKE_CONTROL_STYLE = settingsFile.INTAKE_CONTROL_STYLE;

    public static final AutonType AUTON_MODE = settingsFile.AUTON_TYPE;
    public static final SupportedMotors DRIVE_MOTOR_TYPE = settingsFile.DRIVE_MOTOR_TYPE;
    public static final SupportedMotors SHOOTER_MOTOR_TYPE = settingsFile.SHOOTER_MOTOR_TYPE;
    public static final SupportedMotors TURRET_MOTOR_TYPE = settingsFile.TURRET_MOTOR_TYPE;
    public static final SupportedMotors HOOD_MOTOR_TYPE = settingsFile.HOOD_MOTOR_TYPE;
    public static final SupportedIMU IMU_TYPE = settingsFile.IMU_TYPE;
    public static final String AUTON_COMPLETE_NOISE = "LevelComplete_4_6000";
    public static final int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = settingsFile.DRIVEBASE_SENSOR_UNITS_PER_ROTATION;
    public static final double MAX_SPEED = (settingsFile.DRIVE_MOTOR_TYPE.MAX_SPEED_RPM * settingsFile.DRIVE_GEARING / (settingsFile.WHEEL_DIAMETER * Math.PI / 12)) / 60;
    public static final double RUMBLE_TOLERANCE_FPS = settingsFile.RUMBLE_TOLERANCE_FPS;
    public static final double MAX_ROTATION = settingsFile.MAX_ROTATION;
    public static final double WHEEL_DIAMETER = settingsFile.WHEEL_DIAMETER;
    public static final double TURN_SCALE = settingsFile.TURN_SCALE;
    public static final double DRIVE_SCALE = settingsFile.DRIVE_SCALE;
    public static final double DRIVE_GEARING = settingsFile.DRIVE_GEARING;
    public static final PID SHOOTER_PID = settingsFile.SHOOTER_PID;
    public static final PID SHOOTER_RECOVERY_PID = settingsFile.SHOOTER_RECOVERY_PID;
    public static final PID HEADING_PID = settingsFile.HEADING_PID;
    public static final PID DRIVEBASE_PID = settingsFile.DRIVEBASE_PID;
    public static final PID TURRET_PID = settingsFile.TURRET_PID;
    public static final double DRIVEBASE_DISTANCE_BETWEEN_WHEELS = settingsFile.DRIVEBASE_DISTANCE_BETWEEN_WHEELS; //in Meters
    public static final double SHOOTER_SENSOR_UNITS_PER_ROTATION = settingsFile.SHOOTER_SENSOR_UNITS_PER_ROTATION;
    public static final double motorPulleySize = settingsFile.motorPulleySize;
    public static final double driverPulleySize = settingsFile.driverPulleySize;
    public static final double CAMERA_HEIGHT = settingsFile.CAMERA_HEIGHT;
    public static final double CAMERA_PITCH = settingsFile.CAMERA_PITCH;
    public static final SupportedVision GOAL_CAMERA_TYPE = settingsFile.GOAL_CAMERA_TYPE;
    public static final double TARGET_HEIGHT = settingsFile.TARGET_HEIGHT;
    public static final double XBOX_CONTROLLER_DEADZONE = settingsFile.XBOX_CONTROLLER_DEADZONE;
    public static final double MOTOR_SPROCKET_SIZE = settingsFile.MOTOR_SPROCKET_SIZE;
    public static final double TURRET_SPROCKET_SIZE = settingsFile.TURRET_SPROCKET_SIZE;
    public static final double TURRET_GEAR_RATIO = settingsFile.TURRET_GEAR_RATIO;
    public static final double TURRET_MAX_POS = settingsFile.TURRET_MAX_POS;
    public static final double TURRET_MIN_POS = settingsFile.TURRET_MIN_POS;
    public static final double AUTON_TOLERANCE = settingsFile.AUTON_TOLERANCE;
    public static final double AUTO_SPEED = settingsFile.AUTO_SPEED;
    public static final double AUTO_ROTATION_SPEED = settingsFile.AUTO_ROTATION_SPEED;
    public static final String GOAL_CAM_NAME = settingsFile.GOAL_CAM_NAME;
    public static final String BALL_CAM_NAME = settingsFile.BALL_CAM_NAME;
    //Drive Motors
    public static final int DRIVE_LEADER_L_ID = settingsFile.DRIVE_LEADER_L_ID;
    public static final int[] DRIVE_FOLLOWERS_L_IDS = settingsFile.DRIVE_FOLLOWERS_L_IDS;
    public static final int DRIVE_LEADER_R_ID = settingsFile.DRIVE_LEADER_R_ID;
    public static final int[] DRIVE_FOLLOWERS_R_IDS = settingsFile.DRIVE_FOLLOWERS_R_IDS;
    //Shooter Motors
    public static final int SHOOTER_LEADER_ID = settingsFile.SHOOTER_LEADER_ID;
    public static final int SHOOTER_FOLLOWER_ID = settingsFile.SHOOTER_FOLLOWER_ID;
    //hood
    public static final int SHOOTER_HOOD_ID = settingsFile.SHOOTER_HOOD_ID;
    //turret
    public static final int TURRET_YAW_ID = settingsFile.TURRET_YAW_ID;
    //hopper
    public static final int AGITATOR_MOTOR_ID = settingsFile.AGITATOR_MOTOR_ID;
    public static final int INDEXER_MOTOR_ID = settingsFile.INDEXER_MOTOR_ID;
    public static final int HOPPER_BALL_COUNT = settingsFile.HOPPER_BALL_COUNT;
    /**
     * The id for the {@link #ENABLE_INTAKE intake motor}
     */
    public static final int INTAKE_MOTOR_ID = settingsFile.INTAKE_MOTOR_ID;
    public static final int IMU_ID = settingsFile.IMU_ID;
    /**
     * The id for the {@link #ENABLE_PDP PDP}
     */
    public static final int PDP_ID = settingsFile.PDP_ID;
    public static final String DISCORD_BOT_TOKEN = Robot.preferences.getString("botkey", "none");
    public static boolean autonComplete = false;
    public static int XBOX_CONTROLLER_USB_SLOT = settingsFile.XBOX_CONTROLLER_USB_SLOT;
    public static int FLIGHT_STICK_USB_SLOT = settingsFile.FLIGHT_STICK_USB_SLOT;
    public static int BUTTON_PANEL_USB_SLOT = settingsFile.BUTTON_PANEL_USB_SLOT;

    /**
     * Prints the enabled toggles for the loaded settings
     */
    public static void printToggles() {
        System.out.println("-------------------<RobotSettings>-----------------");
        System.out.println("          Driving " + ENABLE_DRIVE);
        System.out.println("         Intaking " + ENABLE_INTAKE);
        System.out.println("         Shooting " + ENABLE_SHOOTER);
        System.out.println("          Hopping " + ENABLE_HOPPER);
        System.out.println("           Vision " + ENABLE_VISION);
        System.out.println("              IMU " + ENABLE_IMU);
        System.out.println("              IMU " + IMU_TYPE.name());
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
        System.out.println("                       Turret yaw: " + TURRET_YAW_ID);
        System.out.println("                      Agitator id: " + AGITATOR_MOTOR_ID);
        System.out.println("                       Indexer id: " + INDEXER_MOTOR_ID);
        System.out.println("                        Intake id: " + INTAKE_MOTOR_ID);
        System.out.println("                           IMU id: " + IMU_ID);
        System.out.println("-------------------</RobotSettingspings>-----------------");
    }
}
