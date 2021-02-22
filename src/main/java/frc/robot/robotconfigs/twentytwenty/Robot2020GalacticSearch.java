package frc.robot.robotconfigs.twentytwenty;

import frc.ballstuff.intaking.IntakeControlStyles;
import frc.ballstuff.shooting.ShootingControlStyles;
import frc.drive.DriveTypes;
import frc.drive.auton.AutonType;
import frc.motors.SupportedMotors;
import frc.robot.robotconfigs.DefaultConfig;

public class Robot2020GalacticSearch extends DefaultConfig {
    public Robot2020GalacticSearch() {
        //Subsystems
        ENABLE_DRIVE = true;
        ENABLE_INTAKE = true;
        ENABLE_SHOOTER = false;
        ENABLE_HOPPER = false;

        DRIVE_USE_6_MOTORS = true;
        DRIVE_INVERT_LEFT = true;
        DRIVE_INVERT_RIGHT = false;

        //Misc
        ENABLE_VISION = true;
        USE_PHOTONVISION = true;
        ENABLE_IMU = true;
        USE_PIGEON = true;
        USE_NAVX2 = false;

        //SHOOTER
        SHOOTER_USE_SPARKS = false;
        SHOOTER_USE_TWO_MOTORS = true;
        SHOOTER_INVERTED = true;

        //INTAKE
        INDEXER_AUTO_INDEX = true;

        //UI Styles
        DRIVE_STYLE = DriveTypes.STANDARD;
        SHOOTER_CONTROL_STYLE = ShootingControlStyles.STANDARD;
        INTAKE_CONTROL_STYLE = IntakeControlStyles.STANDARD;
        DRIVE_MOTOR_TYPE = SupportedMotors.CAN_SPARK_MAX;

        AUTON_TYPE = AutonType.GALACTIC_SEARCH;

        DRIVEBASE_P = 0.0075;
        DRIVEBASE_I = 0;
        DRIVEBASE_D = 0.002;
        DRIVEBASE_F = 0;
        DRIVE_TIMEOUT_MS = 30;
        DRIVEBASE_SENSOR_UNITS_PER_ROTATION = 2048;//4096 if MagEncoder, built in 2048
        MAX_SPEED = 10; //max speed in fps - REAL IS 10(for 4in wheels)
        MAX_ROTATION = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
        WHEEL_DIAMETER = 6; //update: now it's used once
        MAX_MOTOR_SPEED = 5000; //theoretical max motor speed in rpm
        TURN_SCALE = 0.7;
        DRIVE_SCALE = 1;
        DRIVE_GEARING = 1 / 9.0;

        SHOOTER_P = 0.001;
        SHOOTER_I = 0.00003;
        SHOOTER_D = 0.0001;
        SHOOTER_F = 0.001;
        SHOOTER_RECOVERY_P = SHOOTER_P;//= 0.00037;
        SHOOTER_RECOVERY_I = SHOOTER_I;//= 0;
        SHOOTER_RECOVERY_D = SHOOTER_D;//= 0;
        SHOOTER_RECOVERY_F = SHOOTER_F;//= 0.00019;
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
        TURRET_P = 0.006;
        TURRET_I = 0.00001;
        TURRET_D = 0.001;

        //TURRET_F = 0.001;
        SHOOTER_TIMEOUT_MS = 20;
        AUTON_TOLERANCE = 0.1;
        HEADING_P = 0.08;
        HEADING_I = 0.000005;
        HEADING_D = 0.0003;
        AUTO_SPEED = 3;
        AUTO_ROTATION_SPEED = 1;
        GOAL_CAM_NAME = "GoalCamera";
        BALL_CAM_NAME = "BallCamera";

        //Drive Motors
        DRIVE_LEADER_L = 1; //talon
        DRIVE_FOLLOWERS_L = new int[]{2, 3}; //talon
        DRIVE_LEADER_R = 4; //talon
        DRIVE_FOLLOWERS_R = new int[]{5, 6}; //talon

        //Shooter Motors
        SHOOTER_LEADER = 7; //talon
        SHOOTER_FOLLOWER = 8; //talon

        //turret
        TURRET_YAW = 33; //550

        //hopper
        AGITATOR_MOTOR = 10; //victor
        INDEXER_MOTOR = 11; //victor

        //intake
        INTAKE_MOTOR = 12; //victor
        IMU = 22; //pigeon
        
        XBOX_CONTROLLER_SLOT = 0;
        FLIGHT_STICK_SLOT = 1;
        BUTTON_PANEL_SLOT = 2;
    }
}
