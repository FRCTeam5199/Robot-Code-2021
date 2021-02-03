package frc.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.LinearFilter;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;

public class GoalPhoton implements ISubsystem {
    public NetworkTableEntry yaw;
    public NetworkTableEntry size;
    public NetworkTableEntry hasTarget;
    public NetworkTableEntry pitch;
    public NetworkTableEntry pose;
    NetworkTableInstance table;
    NetworkTable cameraTable;
    LinearFilter filter;

    public GoalPhoton() {
        init();
    }

    public void init() {
        filter = LinearFilter.movingAverage(5);
        table = NetworkTableInstance.getDefault();
        cameraTable = table.getTable("photonvision").getSubTable(RobotMap.GOAL_CAM_NAME);
        yaw = cameraTable.getEntry("targetYaw");
        size = cameraTable.getEntry("targetArea");
        hasTarget = cameraTable.getEntry("hasTarget");
        pitch = cameraTable.getEntry("targetPitch");
        pose = cameraTable.getEntry("targetPose");
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() { }

    @Override
    public void updateGeneric() { }

    /**
     * Get angle between crosshair and goal left/right with filter calculation.
     *
     * @return angle between crosshair and goal, left negative, 29.8 degrees in both directions.
     */
    public double getGoalAngleSmoothed() {
        double angle = yaw.getDouble(0);
        if (validTarget()) {
            return filter.calculate(angle);
        }
        return 0;
    }

    /**
     * Check for a valid target in the camera's view.
     *
     * @return whether or not there is a valid target in view.
     */
    public boolean validTarget() {
        return hasTarget.getBoolean(false);
    }

    /**
     * Get angle between crosshair and goal left/right.
     *
     * @return angle between crosshair and goal, left negative, 29.8 degrees in both directions.
     */
    public double getGoalAngle() {
        double angle = yaw.getDouble(0);
        if (validTarget()) {
            return angle;
        }
        return 0;
    }

    /**
     * Get angle between crosshair and goal up/down.
     *
     * @return angle between crosshair and goal, down negative, 22 degrees in both directions.
     */
    public double getGoalPitch() {
        double angle = pitch.getDouble(0);
        if (validTarget()) {
            return angle;
        }
        return 0;
    }

    /**
     * Get the size of the goal onscreen.
     *
     * @return size of the goal in % of the screen, 0-100.
     */
    public double getGoalSize() {
        double goalSize = size.getDouble(0);
        if (validTarget()) {
            return goalSize;
        }
        return 0;
    }
}