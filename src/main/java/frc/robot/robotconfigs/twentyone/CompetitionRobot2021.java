package frc.robot.robotconfigs.twentyone;

import edu.wpi.first.wpilibj.I2C;
import frc.ballstuff.intaking.Intake;
import frc.ballstuff.shooting.Shooter;
import frc.drive.AbstractDriveManager;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.motors.AbstractMotorController;
import frc.robot.robotconfigs.DefaultConfig;
import frc.telemetry.imu.AbstractIMU;
import frc.vision.camera.IVision;

public class CompetitionRobot2021 extends DefaultConfig {
    public CompetitionRobot2021() {
        ENABLE_DRIVE = true;
        ENABLE_INTAKE = false;
        ENABLE_TURRET = true;
        ENABLE_SHOOTER = true;
        ENABLE_HOPPER = true;
        ENABLE_AGITATOR = true;
        ENABLE_INDEXER = true;
        ENABLE_MUSIC = false;
        ENABLE_HOOD_ARTICULATION = true;
        ENABLE_MEMES = false;
        ENABLE_OVERHEAT_DETECTION = false;
        ENABLE_INTAKE_SERVOS = true;

        DRIVE_INVERT_LEFT = true;
        DRIVE_INVERT_RIGHT = false;

        TURRET_INVERT = true;

        //Misc
        ENABLE_VISION = true;
        USE_PHOTONVISION = false;
        ENABLE_IMU = true;
        IMU_NAVX_PORT = I2C.Port.kMXP;

        //SHOOTER
        SHOOTER_MOTOR_TYPE = AbstractMotorController.SupportedMotors.CAN_SPARK_MAX;
        SHOOTER_USE_TWO_MOTORS = true;
        SHOOTER_INVERTED = false;
        GOAL_CAMERA_TYPE = IVision.SupportedVision.LIMELIGHT;
        INDEXER_DETECTION_CUTOFF_DISTANCE = 5;
        CALIBRATED_HOOD_POSITION_ARRAY = new double[][]{
                {2.415, 0.05},
                {1.466, 0.77},
                {0.925, 1.05},
                {0.481, 1.135},
        };

        //INTAKE
        ENABLE_INDEXER_AUTO_INDEX = true;

        //UI Styles
        DRIVE_STYLE = AbstractDriveManager.DriveControlStyles.STANDARD;
        SHOOTER_CONTROL_STYLE = Shooter.ShootingControlStyles.STANDARD_OFFSEASON_2021;//ShootingControlStyles.ACCURACY_2021;
        INTAKE_CONTROL_STYLE = Intake.IntakeControlStyles.ROBOT_2021;
        DRIVE_MOTOR_TYPE = AbstractMotorController.SupportedMotors.TALON_FX;
        IMU_TYPE = AbstractIMU.SupportedIMU.PIGEON;
        AUTON_TYPE = AutonType.POINT_TO_POINT;

        DRIVEBASE_PID = new PID(0.00075, 0, 0.002);
        SHOOTER_PID = new PID(0.001, 0.0000005, 0.03, 0);//Accuracy. SPEED = new PID(0.0004, 0.0000007, 0.03, 0);
        SHOOTER_CONST_SPEED_PID = new PID(0.0001, 0.0000007, 0.05, 0);
        SHOOTER_RECOVERY_PID = SHOOTER_PID;
        TURRET_PID = new PID(0.006, 0.00001, 0.001);
        HEADING_PID = new PID(0.08, 0.000005, 0.0003);
        DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
        DRIVEBASE_DISTANCE_BETWEEN_WHEELS = 0.435991;
        MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
        RUMBLE_TOLERANCE_FPS = 8;
        MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
        WHEEL_DIAMETER = 5; //update: now it's used once
        TURN_SCALE = 0.7;
        DRIVE_SCALE = 1;
        DRIVE_GEARING = 12.0 / 60.0;

        SHOOTER_SENSOR_UNITS_PER_ROTATION = 2048;
        motorPulleySize = 0;//?;
        driverPulleySize = 0;//?;
        CAMERA_HEIGHT = 0; //Inches
        CAMERA_PITCH = 0; //Radians
        TARGET_HEIGHT = 0;//2.44; //Meters

        XBOX_CONTROLLER_DEADZONE = 0.07;
        MOTOR_SPROCKET_SIZE = 1.25;
        TURRET_SPROCKET_SIZE = 11.1;
        TURRET_GEAR_RATIO = 7;
        TURRET_MAX_POS = 385;
        TURRET_MIN_POS = -2;
        TURRET_MOTOR_TYPE = AbstractMotorController.SupportedMotors.CAN_SPARK_MAX;
        AUTON_TOLERANCE = 0.05;
        AUTO_SPEED = 1.00;//3;
        AUTO_ROTATION_SPEED = 1;
        XBOX_CONTROLLER_USB_SLOT = 0;
        FLIGHT_STICK_USB_SLOT = 1;
        BUTTON_PANEL_USB_SLOT = 2;

        GOAL_CAM_NAME = "GoalCamera";
        BALL_CAM_NAME = "BallCamera";

        //PDP
        PDP_ID = 0;

        //Drive Motors
        DRIVE_LEADER_L_ID = 1; //talon
        DRIVE_FOLLOWERS_L_IDS = new int[]{2}; //talon

        DRIVE_LEADER_R_ID = 3; //talon
        DRIVE_FOLLOWERS_R_IDS = new int[]{4}; //talon

        //Shooter Motors
        SHOOTER_LEADER_ID = 7; //neo
        SHOOTER_FOLLOWER_ID = 8; //neo
        SHOOTER_HOOD_ID = 32;
        SHOOTER_HOOD_MAX_POS = 47;//11 on 9x;
        SHOOTER_HOOD_MIN_POS = -0.1;
        SHOOTER_HOOD_INVERT_MOTOR = false;
        SHOOTER_HOOD_CONTROL_SPEED = 0.5;
        SHOOTER_HOOD_OUT_OF_BOUNDS_SPEED = 0.3;
        TRENCH_FRONT_HOOD_POSITION = SHOOTER_HOOD_MAX_POS * (27.0/57);
        INITIATION_LINE_HOOD_POSITION = SHOOTER_HOOD_MAX_POS * (13.0/57);

        //turret
        TURRET_YAW_ID = 33; //neo 550
        //hopper
        AGITATOR_MOTOR_ID = 10; //victor
        INDEXER_MOTOR_ID = 11; //victor
        //intake
        INTAKE_MOTOR_ID = 12; //victor
        INTAKE_SERVO_R_ID = 2;
        INTAKE_SERVO_L_ID = 3;
    }
}