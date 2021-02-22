package frc.robot;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.motors.SupportedMotors;
import frc.robot.robotconfigs.DefaultConfig;
import frc.robot.robotconfigs.twentyone.CompetitionRobot2021;

public class RobotSettings {
    /**
     * If you change this ONE SINGULAR VARIBLE the ENTIRE CONFIG WILL CHANGE. Use this to select which robot you are
     * using from the list under robotconfigs
     */
    public static final DefaultConfig getNumbersFrom = new CompetitionRobot2021();

    public static final boolean DEBUG = true;
    //Subsystems

    /**
     * Enables the drive
     */
    public static final boolean ENABLE_DRIVE = getNumbersFrom.ENABLE_DRIVE;
    public static final boolean ENABLE_INTAKE = getNumbersFrom.ENABLE_INTAKE;
    public static final boolean ENABLE_SHOOTER = getNumbersFrom.ENABLE_SHOOTER;
    public static final boolean ENABLE_HOPPER = getNumbersFrom.ENABLE_HOPPER;
    public static final boolean ENABLE_MUSIC = getNumbersFrom.ENABLE_MUSIC;

    public static final boolean DRIVE_USE_6_MOTORS = getNumbersFrom.DRIVE_USE_6_MOTORS;
    public static final boolean DRIVE_INVERT_LEFT = getNumbersFrom.DRIVE_INVERT_LEFT;
    public static final boolean DRIVE_INVERT_RIGHT = getNumbersFrom.DRIVE_INVERT_RIGHT;

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
    public static final DriveTypes DRIVE_STYLE = getNumbersFrom.DRIVE_STYLE;
    public static final ShootingControlStyles SHOOTER_CONTROL_STYLE = getNumbersFrom.SHOOTER_CONTROL_STYLE;
    public static final IntakeControlStyles INTAKE_CONTROL_STYLE = getNumbersFrom.INTAKE_CONTROL_STYLE;

    public static final AutonType AUTON_MODE = getNumbersFrom.AUTON_TYPE;
    public static final SupportedMotors DRIVE_MOTOR_TYPE = getNumbersFrom.DRIVE_MOTOR_TYPE;

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
        System.out.println("Shoot with sparks " + SHOOTER_USE_SPARKS);
        System.out.println("     Shoot with 2 " + SHOOTER_USE_TWO_MOTORS);
        System.out.println("      Drive style " + DRIVE_STYLE.name());
        System.out.println("      Shoot style " + SHOOTER_CONTROL_STYLE.name());
        System.out.println("     Intake style " + INTAKE_CONTROL_STYLE.name());
        System.out.println("  Auton Completed " + autonComplete);
        System.out.println("-------------------</RobotSettings>-----------------");
    }

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
    public static final double DRIVE_GEARING = getNumbersFrom.DRIVE_GEARING;

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
    public static int XBOX_CONTROLLER_SLOT = getNumbersFrom.XBOX_CONTROLLER_SLOT;
    public static int FLIGHT_STICK_SLOT = getNumbersFrom.FLIGHT_STICK_SLOT;
    public static int BUTTON_PANEL_SLOT = getNumbersFrom.BUTTON_PANEL_SLOT;

    public static void printNumbers() {
        System.out.println("-------------------<RobotSettings>-----------------");
        System.out.println("Drive PIDF (timeout) " + DRIVEBASE_P + ", " + DRIVEBASE_I + ", " + DRIVEBASE_D + ", " + DRIVEBASE_F + " (" + DRIVE_TIMEOUT_MS + ")");
        System.out.println("Max drive speed/rotation " + MAX_SPEED + "/" + MAX_ROTATION);
        System.out.println("Turn + drive scale " + TURN_SCALE + "/" + DRIVE_SCALE);
        //System.out.println("");
        System.out.println("-------------------</RobotSettings>----------------");
    }

    public static final String GOAL_CAM_NAME = getNumbersFrom.GOAL_CAM_NAME;
    public static final String BALL_CAM_NAME = getNumbersFrom.BALL_CAM_NAME;

    //Drive Motors
    public static final int DRIVE_LEADER_L = getNumbersFrom.DRIVE_LEADER_L;
    public static final int[] DRIVE_FOLLOWERS_L = getNumbersFrom.DRIVE_FOLLOWERS_L;

    public static final int DRIVE_LEADER_R = getNumbersFrom.DRIVE_LEADER_R;
    public static final int[] DRIVE_FOLLOWERS_R = getNumbersFrom.DRIVE_FOLLOWERS_R;

    //Shooter Motors
    public static final int SHOOTER_LEADER = getNumbersFrom.SHOOTER_LEADER;
    public static final int SHOOTER_FOLLOWER = getNumbersFrom.SHOOTER_FOLLOWER;

    //turret
    public static final int TURRET_YAW = getNumbersFrom.TURRET_YAW;

    //hopper
    public static final int AGITATOR_MOTOR = getNumbersFrom.AGITATOR_MOTOR;
    public static final int INDEXER_MOTOR = getNumbersFrom.INDEXER_MOTOR;

    //intake
    public static final int INTAKE_MOTOR = getNumbersFrom.INTAKE_MOTOR;

    public static final int IMU = getNumbersFrom.IMU;

    public static void printMappings() {
        System.out.println("-------------------<RobotSettingspings>-----------------");
        System.out.println("                    Goal cam name: " + GOAL_CAM_NAME);
        System.out.println("                    Ball cam name: " + BALL_CAM_NAME);
        System.out.println(" Drive leader left id (followers): " + DRIVE_LEADER_L + " (" + DRIVE_FOLLOWERS_L[0] + (DRIVE_FOLLOWERS_L.length > 1 ? ", " + DRIVE_FOLLOWERS_L[1] : "") + ")");
        System.out.println("Drive leader right id (followers): " + DRIVE_LEADER_R + " (" + DRIVE_FOLLOWERS_R[0] + (DRIVE_FOLLOWERS_R.length > 1 ? ", " + DRIVE_FOLLOWERS_R[1] : "") + ")");
        System.out.println("        Shooter leader (follower): " + SHOOTER_LEADER + " (" + SHOOTER_FOLLOWER + ")");
        System.out.println("                       Turret yaw: " + TURRET_YAW);
        System.out.println("                      Agitator id: " + AGITATOR_MOTOR);
        System.out.println("                       Indexer id: " + INDEXER_MOTOR);
        System.out.println("                        Intake id: " + INTAKE_MOTOR);
        System.out.println("                           IMU id: " + IMU);
        System.out.println("-------------------</RobotSettingspings>-----------------");
    }
}
