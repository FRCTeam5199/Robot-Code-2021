package frc.robot.robotconfigs.twentyone;

import edu.wpi.first.wpilibj.I2C;
import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.motors.SupportedMotors;
import frc.robot.robotconfigs.DefaultConfig;
import frc.telemetry.imu.SupportedIMU;

public class CompetitionRobot2021 extends DefaultConfig {
    //Subsystems
    public CompetitionRobot2021() {
        ENABLE_DRIVE = true;
        ENABLE_INTAKE = true;
        ENABLE_SHOOTER = true;
        ENABLE_HOPPER = true;
        ENABLE_AGITATOR = true;
        ENABLE_INDEXER = true;
        ENABLE_MUSIC = true;

        DRIVE_USE_6_MOTORS = false;
        DRIVE_INVERT_LEFT = true;
        DRIVE_INVERT_RIGHT = false;

        //Misc
        ENABLE_VISION = false;
        USE_PHOTONVISION = true;
        ENABLE_IMU = true;
        IMU_NAVX_PORT = I2C.Port.kMXP;

        //SHOOTER
        SHOOTER_MOTOR_TYPE = SupportedMotors.CAN_SPARK_MAX;//SupportedMotors.TALON_FX;
        SHOOTER_USE_TWO_MOTORS = true;
        SHOOTER_INVERTED = false;

        //INTAKE
        ENABLE_INDEXER_AUTO_INDEX = false;
        INDEXER_DETECTION_CUTOFF_DISTANCE = 4;

        //UI Styles
        DRIVE_STYLE = DriveTypes.STANDARD;
        SHOOTER_CONTROL_STYLE = ShootingControlStyles.XBOX_CONTROLLER;//ShootingControlStyles.STANDARD;
        INTAKE_CONTROL_STYLE = IntakeControlStyles.STANDARD;
        DRIVE_MOTOR_TYPE = SupportedMotors.TALON_FX;
        IMU_TYPE = SupportedIMU.NAVX2;

        AUTON_TYPE = AutonType.GALACTIC_SCAM;

        DRIVEBASE_PID = new PID(0.0075, 0, 0.002);
        SHOOTER_PID = new PID(0.001, 0.0000005, 0.03, 0);
        SHOOTER_RECOVERY_PID = SHOOTER_PID;
        TURRET_PID = new PID(0.006, 0.00001, 0.001);
        HEADING_PID = new PID(0.08, 0.000005, 0.0003);
        DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
        DRIVEBASE_DISTANCE_BETWEEN_WHEELS = 0.435991;
        MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
        RUMBLE_TOLERANCE_FPS = 8;
        MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
        WHEEL_DIAMETER = 4; //update: now it's used once
        TURN_SCALE = 0.7;
        DRIVE_SCALE = 1;
        DRIVE_GEARING = 10 / 60.0;

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

        //PDP
        PDP_ID = 0;

        //Drive Motors
        DRIVE_LEADER_L_ID = 1; //talon
        DRIVE_FOLLOWERS_L_IDS = new int[]{2}; //talon

        DRIVE_LEADER_R_ID = 3; //talon
        DRIVE_FOLLOWERS_R_IDS = new int[]{4}; //talon

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
    }
}
