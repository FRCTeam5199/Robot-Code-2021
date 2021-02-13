package frc.robot.robotconfigs;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;

public class Robot2020 extends DefaultConfig {
    //Subsystems
    public static final boolean ENABLE_DRIVE = true;
    public static final boolean ENABLE_INTAKE = true;
    public static final boolean ENABLE_CLIMBER = false;
    public static final boolean ENABLE_SHOOTER = true;
    public static final boolean ENABLE_HOPPER = true;

    //Drivetrain
    public static final boolean DRIVE_USE_SPARKS = true;
    public static final boolean DRIVE_USE_6_MOTORS = true;
    public static final boolean DRIVE_INVERT_LEFT = true;
    public static final boolean DRIVE_INVERT_RIGHT = false;

    public static final boolean CALIBRATE_DRIVE_PID = false;
    public static final boolean CALIBRATE_SHOOTER_PID = false;

    //Misc
    public static final boolean ENABLE_VISION = true;
    public static final boolean USE_PHOTONVISION = true;
    public static final boolean ENABLE_IMU = true;
    public static final boolean USE_PIGEON = true;
    public static final boolean USE_NAVX2 = false;

    //SHOOTER
    public static final boolean SHOOTER_USE_SPARKS = false;
    public static final boolean SHOOTER_USE_TWO_MOTORS = true;
    public static final boolean SHOOTER_INVERTED = true;

    //INTAKE
    public static final boolean INDEXER_AUTO_INDEX = true;
    
    //UI Style
    public static final DriveTypes EXPERIMENTAL_DRIVE = DriveTypes.STANDARD;
    public static final ShootingControlStyles SHOOTER_CONTROL_STYLE = ShootingControlStyles.STANDARD;
    public static final IntakeControlStyles INTAKE_CONTROL_STYLE = IntakeControlStyles.STANDARD;
    public static final boolean GALACTIC_SEARCH = false;

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

    public static final String GOAL_CAM_NAME = "GoalCamera";
    public static final String BALL_CAM_NAME = "BallCamera";

    //Drive Motors
    public static final int DRIVE_LEADER_L = 1; //talon
    public static final int[] DRIVE_FOLLOWERS_L = {2, 3}; //talon

    public static final int DRIVE_LEADER_R = 4; //talon
    public static final int[] DRIVE_FOLLOWERS_R = {5, 6}; //talon

    //Shooter Motors
    public static final int SHOOTER_LEADER = 7; //talon
    public static final int SHOOTER_FOLLOWER = 8; //talon

    //turret
    public static final int TURRET_YAW = 33; //550
    //climber
    public static final int CLIMBER_A = 8; //victor
    public static final int CLIMBER_B = 9; //victor
    //hopper
    public static final int AGITATOR_MOTOR = 10; //victor
    public static final int INDEXER_MOTOR = 11; //victor
    //intake
    public static final int INTAKE_MOTOR = 12; //victor

    public static final int IMU = 22; //pigeon
    public static final int PCM = 23; //pcm

    //pneumatics
    public static final int INTAKE_OUT = 4;
    public static final int INTAKE_IN = 5;
    public static final int BUDDY_UNLOCK = 0;
    public static final int SHIFTERS = 6;
    public static final int CLIMBER_LOCK_IN = 2;
    public static final int CLIMBER_LOCK_OUT = 3;
}
