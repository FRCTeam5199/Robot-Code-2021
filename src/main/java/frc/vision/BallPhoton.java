package frc.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.LinearFilter;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;

public class BallPhoton implements ISubsystem {
    public NetworkTableEntry yaw;
    public NetworkTableEntry size;
    public NetworkTableEntry hasTarget;
    public NetworkTableEntry pitch;
    public NetworkTableEntry pose;
    NetworkTableInstance table;
    NetworkTable cameraTable;
    LinearFilter filter;

    /**
     * inits BallPhoton
     */
    public BallPhoton() {
        init();
    }

    /**
     * stores values in simpler variable names
     */
    public void init() {
        filter = LinearFilter.movingAverage(5);
        table = NetworkTableInstance.getDefault();
        cameraTable = table.getTable("photonvision").getSubTable(RobotMap.BALL_CAM_NAME);
        yaw = cameraTable.getEntry("targetYaw");
        size = cameraTable.getEntry("targetArea");
        hasTarget = cameraTable.getEntry("hasTarget");
        pitch = cameraTable.getEntry("targetPitch");
        pose = cameraTable.getEntry("targetPose");
    }

    /**
     * calls updateGeneric
     * see BallPhoton.updateGeneric
     */
    @Override
    public void updateTest() {
        updateGeneric();
    }

    /**
     * calls updateGeneric
     * see BallPhoton.updateGeneric
     */
    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    /**
     *
     */
    @Override
    public void updateAuton() { }

    /**
     * updates generic things for BallPhoton
     */
    @Override
    public void updateGeneric() { }

    /**
     * Get angle between crosshair and Ball left/right with filter calculation.
     *
     * @return angle between crosshair and Ball, left negative, 29.8 degrees in both directions.
     */
    public double getBallAngleSmoothed() {
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
     * Get angle between crosshair and Ball left/right.
     *
     * @return angle between crosshair and Ball, left negative, 29.8 degrees in both directions.
     */
    public double getBallAngle(int num) {
        double angle = yaw.getDouble(num);
        if (validTarget()) {
            return angle;
        }
        return 0;
    }

    /**
     * Get angle between crosshair and Ball up/down.
     *
     * @return angle between crosshair and Ball, down negative, 22 degrees in both directions.
     */
    public double getBallPitch(int num) {
        double angle = pitch.getDouble(num);
        if (validTarget()) {
            return angle;
        }
        return 0;
    }

    /**
     * Get the size of the Ball onscreen.
     *
     * @return size of the Ball in % of the screen, 0-100.
     */
    public double getBallSize(int num) {
        double BallSize = size.getDouble(num);
        if (validTarget()) {
            return BallSize;
        }
        return 0;
    }
}