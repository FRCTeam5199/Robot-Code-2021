package frc.robot.robotconfigs.twentytwenty;

import edu.wpi.first.wpilibj.I2C;
import frc.ballstuff.intaking.Intake;
import frc.ballstuff.shooting.Shooter;
import frc.climber.Climber;
import frc.drive.AbstractDriveManager;
import frc.drive.auton.AutonType;
import frc.misc.PID;
import frc.robot.robotconfigs.DefaultConfig;
import frc.telemetry.imu.AbstractIMU;
import frc.vision.camera.IVision;

import static frc.motors.AbstractMotorController.SupportedMotors.*;

public class Robot2020 extends DefaultConfig {
    public Robot2020() {
        ENABLE_DRIVE = true;
        ENABLE_BALL_SHIFTERS = true; //TODO implement this
        ENABLE_INTAKE = true;
        ENABLE_TURRET = true;
        ENABLE_SHOOTER = true;
        ENABLE_HOPPER = true;
        ENABLE_AGITATOR = true;
        ENABLE_INDEXER = true;
        ENABLE_MUSIC = false;
        ENABLE_HOOD_ARTICULATION = true;
        ENABLE_PNOOMATICS = true;
        ENABLE_CLIMBER = true;
        ENABLE_SHOOTER_COOLING = false;

        AUTON_COMPLETE_NOISE = "LevelComplete_4_6000";

        DRIVE_INVERT_LEFT = true;
        DRIVE_INVERT_RIGHT = false;

        //Misc
        ENABLE_VISION = true;
        USE_PHOTONVISION = true;
        ENABLE_IMU = true;
        IMU_NAVX_PORT = I2C.Port.kMXP;

        //SHOOTER
        SHOOTER_MOTOR_TYPE = TALON_FX;
        SHOOTER_USE_TWO_MOTORS = true;
        SHOOTER_INVERTED = false;
        GOAL_CAMERA_TYPE = IVision.SupportedVision.LIMELIGHT;
        INDEXER_DETECTION_CUTOFF_DISTANCE = 9;

        //INTAKE
        ENABLE_INDEXER_AUTO_INDEX = true;
        INTAKE_MOTOR_TYPE = VICTOR;

        //pnoomatics
        PCM_ID = 23;
        INTAKE_IN_ID = 5;
        INTAKE_OUT_ID = 4;
        CLIMBER_IN_ID = 2;
        CLIMBER_OUT_ID = 3;
        BALL_SHIFTERS_IN_ID = 6;
        BALL_SHIFTERS_OUT_ID = 7;
        SHOOTER_COOLING_ID = 0;

        //climber
        CLIMBER_MOTOR_IDS = new int[]{8, 9};

        //UI Style
        DRIVE_STYLE = AbstractDriveManager.DriveControlStyles.BALL_SHIFTING_STANDARD;
        SHOOTER_CONTROL_STYLE = Shooter.ShootingControlStyles.STANDARD_OFFSEASON_2021;//OFFSEASON_2021;
        INTAKE_CONTROL_STYLE = Intake.IntakeControlStyles.STANDARD;
        CLIMBER_CONTROL_STYLE = Climber.ClimberControlStyles.STANDARD;
        DRIVE_MOTOR_TYPE = CAN_SPARK_MAX;
        CLIMBER_MOTOR_TYPE = VICTOR;
        IMU_TYPE = AbstractIMU.SupportedIMU.PIGEON;
        AUTON_TYPE = AutonType.POINT_TO_POINT;

        DRIVEBASE_PID = new PID(.00018, 0.0000008, 0, 0);
        SHOOTER_PID = new PID(4, 0, 2.5, 0);
        SHOOTER_CONST_SPEED_PID = SHOOTER_PID;
        SHOOTER_RECOVERY_PID = SHOOTER_PID;
        TURRET_PID = new PID(0.006, 0.00001, 0.001);
        HEADING_PID = new PID(1.2, 0.3, 0.0003);
        DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
        DRIVEBASE_DISTANCE_BETWEEN_WHEELS = 0.5588;
        MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
        RUMBLE_TOLERANCE_FPS = 8;
        MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
        WHEEL_DIAMETER = 5.6111; //update: now it's used once
        TURN_SCALE = 0.7;
        DRIVE_SCALE = 1;
        DRIVE_GEARING = 1.0 / 9.0;

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
        TURRET_MOTOR_TYPE = CAN_SPARK_MAX;
        AUTON_TOLERANCE = 0.2;
        AUTO_SPEED = 4;
        AUTO_ROTATION_SPEED = .04;//.03;//1;
        XBOX_CONTROLLER_USB_SLOT = 0;
        FLIGHT_STICK_USB_SLOT = 1;
        BUTTON_PANEL_USB_SLOT = 2;

        GOAL_CAM_NAME = "GoalCamera";
        BALL_CAM_NAME = "BallCamera";

        //Drive Motors
        DRIVE_LEADER_L_ID = 1; //spark
        DRIVE_FOLLOWERS_L_IDS = new int[]{2, 3};//new int[]{2, 3}; //spark

        DRIVE_LEADER_R_ID = 4; //spark
        DRIVE_FOLLOWERS_R_IDS = new int[]{5, 6};//new int[]{5, 6}; //spark

        //Shooter Motors
        SHOOTER_LEADER_ID = 25; //talon
        SHOOTER_FOLLOWER_ID = 26; //talon
        SHOOTER_HOOD_ID = 35;
        HOOD_MOTOR_TYPE = CAN_SPARK_MAX;
        SHOOTER_HOOD_MAX_POS = 57;//11 on 9x;
        SHOOTER_HOOD_MIN_POS = -0.1;
        SHOOTER_HOOD_INVERT_MOTOR = true;
        SHOOTER_HOOD_CONTROL_SPEED = 0.5;
        SHOOTER_HOOD_OUT_OF_BOUNDS_SPEED = 0.3;
        TRENCH_FRONT_HOOD_POSITION = SHOOTER_HOOD_MAX_POS * (27.0 / 57);
        INITIATION_LINE_HOOD_POSITION = SHOOTER_HOOD_MAX_POS * (13.0 / 57);
        CALIBRATED_HOOD_POSITION_ARRAY = new double[][]{
                //tA, HH (Area, Hood Height)
                {2.89, 0}, //Literally can't shoot here so just make the best shot you can ig
                {2.9, 3}, //quick ramp up to 3
                {3.0, 3}, //You can make some nice shot here ig
                {2.6, 3},
                {2.7, 4},
                {2.4, 3.5},
                {2.3, 4},
                {2.2, 4.5},
                {2.1, 5},
                {1.6, 13},
                {1.1, 17},
                {0.7, 26.57},
                {0.74, 19.25},
                {0.66, 20.5},
                {0.61, 22.5},
                {0.56, 25},
                {0.49, 25},
                {0.27, 20}

        };

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
