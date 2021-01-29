package frc.vision;

import edu.wpi.first.wpilibj.LinearFilter;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.util.Units;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPipelineResult;
import org.photonvision.PhotonUtils;
import edu.wpi.first.networktables.*;

public class GoalPhoton implements ISubsystem {

    
    public NetworkTableEntry yaw;
    //public NetworkTableEntry skew;
    public NetworkTableEntry size;
    public NetworkTableEntry hasTarget;
    public NetworkTableEntry pitch;
    public NetworkTableEntry pose;
    NetworkTableInstance table;
    NetworkTable cameraTable;
    
    LinearFilter filter;

    /*
    PhotonCamera camera;
    PhotonPipelineResult latest;
    private double skew;
    private double size;
    private double pitch;
    private boolean hasTarget;
    private Transform2d pose;
    */

    public GoalPhoton() {
        init();
    }

    public void init() {
        //camera = new PhotonCamera(RobotMap.GOAL_CAM_NAME);
        filter = LinearFilter.movingAverage(5);
        
        table = NetworkTableInstance.getDefault();
        cameraTable = table.getTable("photonvision").getSubTable(RobotMap.GOAL_CAM_NAME);
        yaw = cameraTable.getEntry("targetYaw");
        //skew = cameraTable.getEntry("targetSkew");
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
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {
        //getGoalAngleSmoothed();
        /*
        latest = getLatestResult();
        hasTarget = latest.hasTargets();
        if (latest.hasTargets()){
            skew = latest.getBestTarget().getSkew();
            size = latest.getBestTarget().getArea();
            pitch = latest.getBestTarget().getPitch();
            pose = latest.getBestTarget().getCameraToTarget();
        }
        */
    }

    /**
     * Fetches the latest pipeline result from the pi
     *
     * @return Latest vision pipeline result
     */
    /*
    public PhotonPipelineResult getLatestResult() {
        return camera.getLatestResult();
    }
    */

    /**
     * Check for a valid target in the camera's view.
     *
     * @return whether or not there is a valid target in view.
     */
    public boolean validTarget() {
        return hasTarget.getBoolean(false);
    }

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

    /**
     * Get the distance between the robot and the goal, assuming camera and goal height are static
     *
     * @return distance to goal in meters
     */
    /*
    public double getGoalDistance() {
        
        double[] defaultPos = {0, 0, 0};
        double[] goalPos = pose.getDoubleArray(new double[3]);
        double dist = Math.sqrt(Math.pow(goalPos[0], 2) + Math.pow(goalPos[1], 2));
        
        //return PhotonUtils.calculateDistanceToTargetMeters(Units.inchesToMeters(RobotNumbers.CAMERA_HEIGHT), RobotNumbers.TARGET_HEIGHT, RobotNumbers.CAMERA_PITCH, Math.toRadians(getGoalPitch()));
    }
    */
}