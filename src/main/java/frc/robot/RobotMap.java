package frc.robot;

public class RobotMap{
    //public static final int [NAME] = [ID];
    //public static final int wristMotor = 5; <-- EXAMPLE
    public static final String GOAL_CAM_NAME = "GoalCam";
    public static final String BALL_CAM_NAME = "BallCam";
    
    //Drive Motors
    public static final int DRIVE_LEADER_L = 1; //neo
    public static final int DRIVE_FOLLOWER_L1 = 2; //neo
    public static final int DRIVE_FOLLOWER_L2 = 3; //neo

    public static final int DRIVE_LEADER_R = 4; //neo
    public static final int DRIVE_FOLLOWER_R1 = 5; //neo
    public static final int DRIVE_FOLLOWER_R2 = 6; //neo

    //Shooter Motors
    public static final int SHOOTER_LEADER = 7; //neo
    public static final int SHOOTER_FOLLOWER = 8; //neo

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
    public static final int turretYaw = 33; //550
    //climber
    public static final int climberA = 8; //victor
    public static final int climberB = 9; //victor
    //hopper
    public static final int agitatorMotor = 10; //victor 
    public static final int indexerMotor = 11; //victor
    //intake
    public static final int intakeMotor = 12; //victor

    public static final int pigeon = 22; //pigeon
    public static final int pcm = 23; //pcm

    //pneumatics
    public static final int intakeOut = 4;
    public static final int intakeIn = 5;
    public static final int buddyUnlock = 0;
    public static final int shifters = 6;
    public static final int climberLockIn = 2;
    public static final int climberLockOut = 3;
   
}