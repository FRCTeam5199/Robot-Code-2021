package frc.robot.robotconfigs.twentytwenty;

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

public class Robot2020 extends DefaultConfig {
    public Robot2020() {
        ENABLE_DRIVE = true;
        ENABLE_INTAKE = true;
        ENABLE_TURRET = true;
        ENABLE_SHOOTER = true;
        ENABLE_HOPPER = true;
        ENABLE_AGITATOR = true;
        ENABLE_INDEXER = true;
        ENABLE_MUSIC = false;
        ENABLE_HOOD_ARTICULATION = false;

        DRIVE_INVERT_LEFT = true;
        DRIVE_INVERT_RIGHT = false;

        //Misc
        ENABLE_VISION = true;
        USE_PHOTONVISION = true;
        ENABLE_IMU = true;
        IMU_NAVX_PORT = I2C.Port.kMXP;

        //SHOOTER
        SHOOTER_MOTOR_TYPE = AbstractMotorController.SupportedMotors.TALON_FX;
        SHOOTER_USE_TWO_MOTORS = true;
        SHOOTER_INVERTED = false;
        GOAL_CAMERA_TYPE = IVision.SupportedVision.PHOTON;
        INDEXER_DETECTION_CUTOFF_DISTANCE = 9;

        //INTAKE
        ENABLE_INDEXER_AUTO_INDEX = true;

        //UI Style
        DRIVE_STYLE = AbstractDriveManager.DriveControlStyles.STANDARD;
        SHOOTER_CONTROL_STYLE = Shooter.ShootingControlStyles.STANDARD;
        INTAKE_CONTROL_STYLE = Intake.IntakeControlStyles.STANDARD;
        DRIVE_MOTOR_TYPE = AbstractMotorController.SupportedMotors.CAN_SPARK_MAX;
        IMU_TYPE = AbstractIMU.SupportedIMU.PIGEON;
        AUTON_TYPE = AutonType.FOLLOW_PATH;

        DRIVEBASE_PID = new PID(0, 0, 0.000005, 0.00002);
        SHOOTER_PID = new PID(0.001, 0.000009, 0.0001, 0.023);
        SHOOTER_CONST_SPEED_PID = SHOOTER_PID;
        SHOOTER_RECOVERY_PID = SHOOTER_PID;
        TURRET_PID = new PID(0.006, 0.00001, 0.001);
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
        TURRET_MOTOR_TYPE = AbstractMotorController.SupportedMotors.CAN_SPARK_MAX;
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
