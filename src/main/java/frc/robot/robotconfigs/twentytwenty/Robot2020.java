package frc.robot.robotconfigs.twentytwenty;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.motors.SupportedMotors;
import frc.robot.robotconfigs.DefaultConfig;
import frc.telemetry.imu.SupportedIMU;
import frc.vision.camera.SupportedVision;

public class Robot2020 extends DefaultConfig {
    public Robot2020() {
        //Subsystems
        ENABLE_DRIVE = false;
        ENABLE_INTAKE = false;
        ENABLE_SHOOTER = true;
        ENABLE_HOPPER = true;
        ENABLE_AGITATOR = true;
        ENABLE_INDEXER = true;

        DRIVE_USE_6_MOTORS = true;
        DRIVE_INVERT_LEFT = true;
        DRIVE_INVERT_RIGHT = false;

        //Misc
        ENABLE_VISION = false;
        USE_PHOTONVISION = true;
        ENABLE_IMU = false;

        //SHOOTER
        SHOOTER_MOTOR_TYPE = SupportedMotors.TALON_FX;
        SHOOTER_USE_TWO_MOTORS = true;
        SHOOTER_INVERTED = false;
        GOAL_CAMERA_TYPE = SupportedVision.PHOTON;
        ENABLE_HOOD_ARTICULATION = false;

        //INTAKE
        ENABLE_INDEXER_AUTO_INDEX = true;
        INDEXER_DETECTION_CUTOFF_DISTANCE = 9;

        //UI Style
        DRIVE_STYLE = DriveTypes.STANDARD;
        SHOOTER_CONTROL_STYLE = ShootingControlStyles.STANDARD;
        INTAKE_CONTROL_STYLE = IntakeControlStyles.STANDARD;
        DRIVE_MOTOR_TYPE = SupportedMotors.CAN_SPARK_MAX;
        IMU_TYPE = SupportedIMU.PIGEON;
        AUTON_TYPE = AutonType.FOLLOW_PATH;

        DRIVEBASE_PID = new PID(0, 0, 0.000005, 0.00002);
        SHOOTER_PID = new PID(0.001, 0.000009, 0.0001, 0.023);
        SHOOTER_RECOVERY_PID = SHOOTER_PID;
        HEADING_PID = new PID(0.08, 0.000005, 0.0003);
        DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
        DRIVEBASE_DISTANCE_BETWEEN_WHEELS = 0.5588;
        MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
        RUMBLE_TOLERANCE_FPS = 8;
        MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
        WHEEL_DIAMETER = 6; //update: now it's used once
        TURN_SCALE = 0.7;
        DRIVE_SCALE = 1;
        DRIVE_GEARING = 1 / 9.0;

        SHOOTER_SENSOR_UNITS_PER_ROTATION = 2048;
        motorPulleySize = 0;//?;
        driverPulleySize = 0;//?;
        CAMERA_HEIGHT = 0; //Inches
        CAMERA_PITCH = 0; //Radians
        TARGET_HEIGHT = 0;//2.44; //Meters

        XBOX_CONTROLLER_DEADZONE = 0.07;
        MOTOR_SPROCKET_SIZE = 1;
        TURRET_SPROCKET_SIZE = 11.1;
        TURRET_GEAR_RATIO = 7;
        TURRET_MAX_POS = 270;
        TURRET_MIN_POS = 0;
        AUTON_TOLERANCE = 0.1;
        AUTO_SPEED = 3;
        AUTO_ROTATION_SPEED = 1;
        XBOX_CONTROLLER_USB_SLOT = 0;
        FLIGHT_STICK_USB_SLOT = 1;
        BUTTON_PANEL_USB_SLOT = 2;

        GOAL_CAM_NAME = "GoalCamera";
        BALL_CAM_NAME = "BallCamera";

        //Drive Motors
        DRIVE_LEADER_L_ID = 1; //talon
        DRIVE_FOLLOWERS_L_IDS = new int[]{2, 3}; //talon

        DRIVE_LEADER_R_ID = 4; //talon
        DRIVE_FOLLOWERS_R_IDS = new int[]{5, 6}; //talon

        //Shooter Motors
        SHOOTER_LEADER_ID = 7; //talon
        SHOOTER_FOLLOWER_ID = 8; //talon

        //turret
        TURRET_YAW_ID = 33; //550

        //hopper
        AGITATOR_MOTOR_ID = 10; //victor
        INDEXER_MOTOR_ID = 11; //victor

        //intake
        INTAKE_MOTOR_ID = 12; //victor

        IMU_ID = 22; //pigeon
    }
}
