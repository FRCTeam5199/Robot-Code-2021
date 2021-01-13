package frc.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotMap;

import edu.wpi.first.wpilibj.LinearFilter;

public class GoalChameleon{
    public NetworkTableEntry yaw;
    public NetworkTableEntry size;
    public NetworkTableEntry isValid;
    public NetworkTableEntry pitch;
    public NetworkTableEntry pose;
    NetworkTableInstance table;
    NetworkTable cameraTable;
    LinearFilter filter;

    public void init(){
        filter = LinearFilter.movingAverage(5);
        table = NetworkTableInstance.getDefault();
        cameraTable = table.getTable("chameleon-vision").getSubTable(RobotMap.goalCamName);
        yaw = cameraTable.getEntry("targetYaw");
        size = cameraTable.getEntry("targetFittedWidth");
        isValid = cameraTable.getEntry("isValid");
        pitch = cameraTable.getEntry("targetPitch");
        pose = cameraTable.getEntry("targetPose");
    }

    public void update(){
        getGoalAngleSmoothed();
    }

    /**
     * Check for a valid target in the camera's view.
     * @return whether or not there is a valid target in view.
     */
    public boolean validTarget(){
        return isValid.getBoolean(false);
    }

    /**
     * Get angle between crosshair and goal left/right with filter calculation.
     * @return angle between crosshair and goal, left negative, 29.8 degrees in both directions.
     */
    public double getGoalAngleSmoothed(){ 
        double angle = yaw.getDouble(0);
        if(isValid.getBoolean(false)){
            return filter.calculate(angle);
        }
        return 0;
    }

    /**
     * Get angle between crosshair and goal left/right.
     * @return angle between crosshair and goal, left negative, 29.8 degrees in both directions.
     */
    public double getGoalAngle(){ 
        double angle = yaw.getDouble(0);
        if(isValid.getBoolean(false)){
            return angle;
        }
        return 0;
    }

    /**
     * Get angle between crosshair and goal up/down.
     * @return angle between crosshair and goal, down negative, 22 degrees in both directions.
     */
    public double getGoalPitch(){ 
        double angle = pitch.getDouble(0);
        if(isValid.getBoolean(false)){
            return angle;
        }
        return 0;
    }

    /**
     * Get the size of the goal onscreen.
     * @return size of the goal in % of the screen, 0-100.
     */
    public double getGoalSize(){
        double goalSize = size.getDouble(0);
        if(isValid.getBoolean(false)){
            return goalSize;
        }
        return 0;
    }

    /**
     * Get the distance between the robot and the goal using SolvePNP in chameleon
     * @return distance to goal in meters
     */
    public double getGoalDistance(){
        double[] defaultPos = {0,0,0};
        double[] goalPos = pose.getDoubleArray(new double[3]);
        double dist = Math.sqrt(Math.pow(goalPos[0], 2)+Math.pow(goalPos[1], 2));
        return dist; //return distance
    }
}