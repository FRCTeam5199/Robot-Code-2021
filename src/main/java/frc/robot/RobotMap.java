package frc.robot;

public class RobotMap {
    //public static final int [NAME] = [ID];
    //public static final int wristMotor = 5; <-- EXAMPLE
    public static final String GOAL_CAM_NAME = "GoalCam";
    public static final String BALL_CAM_NAME = "BallCam";

    //Drive Motors
    public static final int DRIVE_LEADER_L = 1; //talon
    public static final int[] DRIVE_FOLLOWERS_L = {2, 3}; //talon

    public static final int DRIVE_LEADER_R = 4; //talon
    public static final int[] DRIVE_FOLLOWERS_R = {5 , 6}; //talon

    //Shooter Motors
    public static final int SHOOTER_LEADER = 7; //talon
    public static final int SHOOTER_FOLLOWER = 8; //talon

    /*
    //drive motors
    public static final int driveLeaderL = 1; //neo
    public static final int driveFollowerL1 = 2; //neo
    public static final int driveFollowerL2 = 3; //neo
    
    public static final int driveLeaderR = 4; //neo
    public static final int driveFollowerR1 = 5; //neo
    public static final int driveFollowerR2 = 6; //neo
    //shooter motors
    public static final int shooterLeader = 7; //neo
    public static final int shooterFollower = 8;  //neo
    */
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

    public static final int PIGEON = 22; //pigeon
    public static final int PCM = 23; //pcm

    //pneumatics
    public static final int INTAKE_OUT = 4;
    public static final int INTAKE_IN = 5;
    public static final int BUDDY_UNLOCK = 0;
    public static final int SHIFTERS = 6;
    public static final int CLIMBER_LOCK_IN = 2;
    public static final int CLIMBER_LOCK_OUT = 3;

}