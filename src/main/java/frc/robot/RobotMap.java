package frc.robot;

import frc.robot.robotconfigs.DefaultConfig;

public class RobotMap {
    public static final DefaultConfig getNumbersFrom = RobotToggles.getNumbersFrom;

    public static final String GOAL_CAM_NAME = getNumbersFrom.GOAL_CAM_NAME;
    public static final String BALL_CAM_NAME = getNumbersFrom.BALL_CAM_NAME;

    //Drive Motors
    public static final int DRIVE_LEADER_L = getNumbersFrom.DRIVE_LEADER_L;
    public static final int[] DRIVE_FOLLOWERS_L = getNumbersFrom.DRIVE_FOLLOWERS_L;

    public static final int DRIVE_LEADER_R = getNumbersFrom.DRIVE_LEADER_R;
    public static final int[] DRIVE_FOLLOWERS_R = getNumbersFrom.DRIVE_FOLLOWERS_R;

    //Shooter Motors
    public static final int SHOOTER_LEADER = getNumbersFrom.SHOOTER_LEADER;
    public static final int SHOOTER_FOLLOWER = getNumbersFrom.SHOOTER_FOLLOWER;

    //turret
    public static final int TURRET_YAW = getNumbersFrom.TURRET_YAW;

    //hopper
    public static final int AGITATOR_MOTOR = getNumbersFrom.AGITATOR_MOTOR;
    public static final int INDEXER_MOTOR = getNumbersFrom.INDEXER_MOTOR;

    //intake
    public static final int INTAKE_MOTOR = getNumbersFrom.INTAKE_MOTOR;

    public static final int IMU = getNumbersFrom.IMU;

    public static void printMappings() {
        System.out.println("-------------------<RobotMappings>-----------------");
        System.out.println("                    Goal cam name: " + GOAL_CAM_NAME);
        System.out.println("                    Ball cam name: " + BALL_CAM_NAME);
        System.out.println(" Drive leader left id (followers): " + DRIVE_LEADER_L + " (" + DRIVE_FOLLOWERS_L[0] + (DRIVE_FOLLOWERS_L.length > 1 ? ", " + DRIVE_FOLLOWERS_L[1] : "") + ")");
        System.out.println("Drive leader right id (followers): " + DRIVE_LEADER_R + " (" + DRIVE_FOLLOWERS_R[0] + (DRIVE_FOLLOWERS_R.length > 1 ? ", " + DRIVE_FOLLOWERS_R[1] : "") + ")");
        System.out.println("        Shooter leader (follower): " + SHOOTER_LEADER + " (" + SHOOTER_FOLLOWER + ")");
        System.out.println("                       Turret yaw: " + TURRET_YAW);
        System.out.println("                      Agitator id: " + AGITATOR_MOTOR);
        System.out.println("                       Indexer id: " + INDEXER_MOTOR);
        System.out.println("                        Intake id: " + INTAKE_MOTOR);
        System.out.println("                           IMU id: " + IMU);
        System.out.println("-------------------</RobotMappings>-----------------");
    }
}