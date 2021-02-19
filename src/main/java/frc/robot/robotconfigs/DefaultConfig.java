package frc.robot.robotconfigs;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;

/**
 * Literally dont mind me I am simply vibing
 * I am here because it means you only have to change one value to completely change robot settings
 * (Otherwise, you would have to make 4 changes instead of 1)
 *
 * @author jojo2357
 */
public abstract class DefaultConfig {
    //Subsystems
    public boolean ENABLE_DRIVE = false;
    public boolean ENABLE_INTAKE = false;
    public boolean ENABLE_CLIMBER = false;
    public boolean ENABLE_SHOOTER = false;
    public boolean ENABLE_HOPPER = false;
    public boolean ENABLE_MUSIC = true;

    //Drivetrain
    public boolean DRIVE_USE_SPARKS = false;
    public boolean DRIVE_USE_6_MOTORS = false;
    public boolean DRIVE_INVERT_LEFT = true;
    public boolean DRIVE_INVERT_RIGHT = false;

    public boolean CALIBRATE_DRIVE_PID = false;
    public boolean CALIBRATE_SHOOTER_PID = false;

    //Misc
    public boolean ENABLE_VISION = false;
    public boolean USE_PHOTONVISION = true;
    public boolean ENABLE_IMU = false;
    public boolean USE_PIGEON = false;
    public boolean USE_NAVX2 = false;

    //SHOOTER
    public boolean SHOOTER_USE_SPARKS = false;
    public boolean SHOOTER_USE_TWO_MOTORS = true;
    public boolean SHOOTER_INVERTED = true;

    //INTAKE
    public boolean INDEXER_AUTO_INDEX = true;

    //UI Styles
    public DriveTypes EXPERIMENTAL_DRIVE = DriveTypes.STANDARD;
    public ShootingControlStyles SHOOTER_CONTROL_STYLE = ShootingControlStyles.STANDARD;
    public IntakeControlStyles INTAKE_CONTROL_STYLE = IntakeControlStyles.STANDARD;

    public boolean GALACTIC_SEARCH = false;

    public double DRIVEBASE_P = 0;
    public double DRIVEBASE_I = 0;
    public double DRIVEBASE_D = 0;
    public double DRIVEBASE_F = 0;
    public int DRIVE_TIMEOUT_MS = 0;
    public int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
    public double MAX_SPEED = 0; //max speed in fps - REAL IS 10(for 4in wheels)
    public double MAX_ROTATION = 0; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
    public double WHEEL_DIAMETER = 0; //update: now it's used once
    public double MAX_MOTOR_SPEED = 0; //theoretical max motor speed in rpm
    public double TURN_SCALE = 1;
    public double DRIVE_SCALE = 1;

    public double SHOOTER_P = 0;
    public double SHOOTER_I = 0;
    public double SHOOTER_D = 0;
    public double SHOOTER_F = 0;
    public double SHOOTER_RECOVERY_P = SHOOTER_P;//= 0.00037;
    public double SHOOTER_RECOVERY_I = SHOOTER_I;//= 0;
    public double SHOOTER_RECOVERY_D = SHOOTER_D;//= 0;
    public double SHOOTER_RECOVERY_F = SHOOTER_F;//= 0.00019;
    public double SHOOTER_SENSOR_UNITS_PER_ROTATION = 2048;
    public double motorPulleySize = 0;//?;
    public double driverPulleySize = 0;//?;
    public double CAMERA_HEIGHT = 0; //Inches
    public double CAMERA_PITCH = 0; //Radians
    public double TARGET_HEIGHT = 0;//2.44; //Meters

    public double XBOX_CONTROLLER_DEADZONE = 0.07;
    public double MOTOR_SPROCKET_SIZE = 0;
    public double TURRET_SPROCKET_SIZE = 0;
    public double TURRET_GEAR_RATIO = 0;
    public double TURRET_MAX_POS = 270;
    public double TURRET_MIN_POS = 0;
    public double TURRET_P = 0;
    public double TURRET_I = 0;
    public double TURRET_D = 0;
    //public double TURRET_F = 0.001;
    public int SHOOTER_TIMEOUT_MS = 0;
    public double AUTON_TOLERANCE = 0.1;
    public double HEADING_P = 0.08;
    public double HEADING_I = 0.000005;
    public double HEADING_D = 0.0003;
    public double AUTO_SPEED = 3;
    public double AUTO_ROTATION_SPEED = 1;
    public String GOAL_CAM_NAME = "GoalCamera";
    public String BALL_CAM_NAME = "BallCamera";
    //Drive Motors
    public int DRIVE_LEADER_L; //talon
    public int[] DRIVE_FOLLOWERS_L; //talon
    public int DRIVE_LEADER_R; //talon
    public int[] DRIVE_FOLLOWERS_R; //talon
    //Shooter Motors
    public int SHOOTER_LEADER = 7; //talon
    public int SHOOTER_FOLLOWER = 8; //talon
    //turret
    public int TURRET_YAW = 33; //550
    //climber
    public int CLIMBER_A = 8; //victor
    public int CLIMBER_B = 9; //victor
    //hopper
    public int AGITATOR_MOTOR = 10; //victor
    public int INDEXER_MOTOR = 11; //victor
    //intake
    public int INTAKE_MOTOR = 12; //victor
    public int IMU = 22; //pigeon
    public int PCM = 23; //pcm
    //pneumatics
    public int INTAKE_OUT = 4;
    public int INTAKE_IN = 5;
    public int BUDDY_UNLOCK = 0;
    public int SHIFTERS = 6;
    public int CLIMBER_LOCK_IN = 2;
    public int CLIMBER_LOCK_OUT = 3;
    public double triggerSensitivity = 0.25;
    public int XBOX_CONTROLLER_SLOT = 0;
    public int FLIGHT_STICK_SLOT = 1;
    public int BUTTON_PANEL_SLOT = 2;
}
