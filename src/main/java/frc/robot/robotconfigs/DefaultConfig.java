package frc.robot.robotconfigs;

import edu.wpi.first.wpilibj.I2C;
import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.motors.SupportedMotors;

/**
 * Literally dont mind me I am simply vibing I am here because it means you only have to change one value to completely
 * change robot settings (Otherwise, you would have to make 5 changes instead of 1)
 *
 * @author jojo2357
 */
public abstract class DefaultConfig {
    //Subsystems
    public boolean ENABLE_DRIVE = false;
    public boolean ENABLE_INTAKE = false;
    public boolean ENABLE_SHOOTER = false;
    public boolean ENABLE_HOPPER = false;
    public boolean ENABLE_AGITATOR = false;
    public boolean ENABLE_INDEXER = false;
    public boolean ENABLE_MUSIC = false;
    public boolean ENABLE_PDP = false;
    public boolean ENABLE_LEDS = false;
    public boolean ENABLE_TURRET = false;

    public boolean DRIVE_USE_6_MOTORS = false;
    public boolean DRIVE_INVERT_LEFT = true;
    public boolean DRIVE_INVERT_RIGHT = false;

    //Misc
    public boolean ENABLE_VISION = false;
    public boolean USE_PHOTONVISION = true;
    public boolean ENABLE_IMU = false;
    public boolean USE_PIGEON = false;
    public boolean USE_NAVX2 = false;

    //SHOOTER
    public boolean SHOOTER_USE_TWO_MOTORS = true;
    public boolean SHOOTER_INVERTED = true;

    //INTAKE
    public boolean ENABLE_INDEXER_AUTO_INDEX = true;
    public int INDEXER_DETECTION_CUTOFF_DISTANCE = -2;

    //UI Styles
    public DriveTypes DRIVE_STYLE = DriveTypes.STANDARD;
    public ShootingControlStyles SHOOTER_CONTROL_STYLE = ShootingControlStyles.STANDARD;
    public IntakeControlStyles INTAKE_CONTROL_STYLE = IntakeControlStyles.STANDARD;

    public SupportedMotors SHOOTER_MOTOR_TYPE = SupportedMotors.TALON_FX;
    public SupportedMotors DRIVE_MOTOR_TYPE = SupportedMotors.TALON_FX;
    public SupportedMotors TURRET_MOTOR_TYPE = SupportedMotors.CAN_SPARK_MAX;

    public AutonType AUTON_TYPE = AutonType.FOLLOW_PATH;

    public int DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
    public double DRIVEBASE_DISTANCE_BETWEEN_WHEELS = -2; //Distance in meters between wheels
    public double MAX_SPEED = 0; //max speed in fps - REAL IS 10(for 4in wheels)
    public double RUMBLE_TOLERANCE_FPS = 0; //The minimum value in which the controller will begin rumbling
    public double MAX_ROTATION = 0; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
    public double WHEEL_DIAMETER = 0;
    public double TURN_SCALE = 1;
    public double DRIVE_SCALE = 1;
    public double DRIVE_GEARING = 10 / 70.0;

    public PID DRIVEBASE_PID = PID.EMPTY_PID;
    public PID SHOOTER_PID = PID.EMPTY_PID;
    public PID SHOOTER_RECOVERY_PID = SHOOTER_PID;
    public PID TURRET_PID = PID.EMPTY_PID;
    public PID HEADING_PID = PID.EMPTY_PID;
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
    public double AUTON_TOLERANCE = 0.1;
    public double AUTO_SPEED = 3;
    public double AUTO_ROTATION_SPEED = 1;
    public String GOAL_CAM_NAME = "GoalCamera";
    public String BALL_CAM_NAME = "BallCamera";


    //Drive Motors
    public int DRIVE_LEADER_L_ID; //talon
    public int[] DRIVE_FOLLOWERS_L_IDS; //talon
    public int DRIVE_LEADER_R_ID; //talon
    public int[] DRIVE_FOLLOWERS_R_IDS; //talon
    //Shooter Motors
    public int SHOOTER_LEADER_ID = 7; //talon
    public int SHOOTER_FOLLOWER_ID = 8; //talon
    //turret
    public int TURRET_YAW_ID = 33; //550
    //hopper
    public int AGITATOR_MOTOR_ID = 10; //victor
    public int INDEXER_MOTOR_ID = 11; //victor
    //intake
    public int INTAKE_MOTOR_ID = 12; //victor
    public int IMU_ID = 22; //pigeon
    //leds
    public int LED_STRAND_LENGTH = 60;
    public int LED_STRAND_PORT_ID = 9;
    //pdp
    public int PDP_ID = 0;

    public int XBOX_CONTROLLER_USB_SLOT = 0;
    public int FLIGHT_STICK_USB_SLOT = 1;
    public int BUTTON_PANEL_USB_SLOT = 2;

    public I2C.Port IMU_NAVX_PORT = I2C.Port.kMXP;
}
