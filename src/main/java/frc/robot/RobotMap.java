package frc.robot;

import frc.misc.InitializationFailureException;
import frc.robot.robotconfigs.AbstractConfig;

public class RobotMap {
    public static final AbstractConfig getNumbersFrom = RobotToggles.getNumbersFrom;

    public static final String GOAL_CAM_NAME;
    public static final String BALL_CAM_NAME;

    //Drive Motors
    public static final int DRIVE_LEADER_L;
    public static final int[] DRIVE_FOLLOWERS_L;
    
    public static final int DRIVE_LEADER_R;
    public static final int[] DRIVE_FOLLOWERS_R;

    //Shooter Motors
    public static final int SHOOTER_LEADER;
    public static final int SHOOTER_FOLLOWER;
    
    //turret
    public static final int TURRET_YAW;
    //climber
    public static final int CLIMBER_A;
    public static final int CLIMBER_B;
    //hopper
    public static final int AGITATOR_MOTOR;
    public static final int INDEXER_MOTOR;
    //intake
    public static final int INTAKE_MOTOR;

    public static final int PIGEON;
    public static final int PCM;

    //pneumatics
    public static final int INTAKE_OUT;
    public static final int INTAKE_IN;
    public static final int BUDDY_UNLOCK;
    public static final int SHIFTERS;
    public static final int CLIMBER_LOCK_IN;
    public static final int CLIMBER_LOCK_OUT;

    //@author jojo2357
    static {
        try {
            GOAL_CAM_NAME = (String) getNumbersFrom.getClass().getField("GOAL_CAM_NAME").get(getNumbersFrom);
            BALL_CAM_NAME = (String) getNumbersFrom.getClass().getField("BALL_CAM_NAME").get(getNumbersFrom);

            //Drive Motors
            DRIVE_LEADER_L = getNumbersFrom.getClass().getField("DRIVE_LEADER_L").getInt(getNumbersFrom);
            DRIVE_FOLLOWERS_L = (int[]) getNumbersFrom.getClass().getField("DRIVE_FOLLOWERS_L").get(getNumbersFrom);

            DRIVE_LEADER_R = getNumbersFrom.getClass().getField("DRIVE_LEADER_L").getInt(getNumbersFrom);
            DRIVE_FOLLOWERS_R = (int[]) getNumbersFrom.getClass().getField("DRIVE_FOLLOWERS_R").get(getNumbersFrom);

            //Shooter Motors
            SHOOTER_LEADER = getNumbersFrom.getClass().getField("SHOOTER_LEADER").getInt(getNumbersFrom);
            SHOOTER_FOLLOWER = getNumbersFrom.getClass().getField("SHOOTER_FOLLOWER").getInt(getNumbersFrom);

            TURRET_YAW = getNumbersFrom.getClass().getField("TURRET_YAW").getInt(getNumbersFrom);
            CLIMBER_A = getNumbersFrom.getClass().getField("CLIMBER_A").getInt(getNumbersFrom);
            CLIMBER_B = getNumbersFrom.getClass().getField("CLIMBER_B").getInt(getNumbersFrom);
            AGITATOR_MOTOR = getNumbersFrom.getClass().getField("AGITATOR_MOTOR").getInt(getNumbersFrom);
            INDEXER_MOTOR = getNumbersFrom.getClass().getField("INDEXER_MOTOR").getInt(getNumbersFrom);
            INTAKE_MOTOR = getNumbersFrom.getClass().getField("INTAKE_MOTOR").getInt(getNumbersFrom);

            PIGEON = getNumbersFrom.getClass().getField("PIGEON").getInt(getNumbersFrom);
            PCM = getNumbersFrom.getClass().getField("PCM").getInt(getNumbersFrom);

            INTAKE_OUT = getNumbersFrom.getClass().getField("INTAKE_OUT").getInt(getNumbersFrom);
            INTAKE_IN = getNumbersFrom.getClass().getField("INTAKE_IN").getInt(getNumbersFrom);
            BUDDY_UNLOCK = getNumbersFrom.getClass().getField("BUDDY_UNLOCK").getInt(getNumbersFrom);
            SHIFTERS = getNumbersFrom.getClass().getField("SHIFTERS").getInt(getNumbersFrom);
            CLIMBER_LOCK_IN = getNumbersFrom.getClass().getField("CLIMBER_LOCK_IN").getInt(getNumbersFrom);
            CLIMBER_LOCK_OUT = getNumbersFrom.getClass().getField("CLIMBER_LOCK_OUT").getInt(getNumbersFrom);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InitializationFailureException(e.toString(), "");
        }
    }

}