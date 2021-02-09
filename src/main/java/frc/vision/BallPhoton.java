package frc.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.LinearFilter;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPipelineResult;
import org.photonvision.PhotonTrackedTarget;

import java.util.List;

public class BallPhoton implements ISubsystem {

    PhotonCamera ballCamera;
    List<PhotonTrackedTarget> targets;
    PhotonPipelineResult cameraResult;
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
        ballCamera = new PhotonCamera("BallCamera");
        cameraResult = ballCamera.getLatestResult();
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
    public void updateAuton() {
    }

    /**
     * updates generic things for BallPhoton
     */
    @Override
    public void updateGeneric() {
        if (validTarget()) {
            targets = cameraResult.getTargets();
        }
    }

    /**
     * Get angle between crosshair and Ball left/right with filter calculation.
     *
     * @return angle between crosshair and Ball, left negative, 29.8 degrees in both directions.
     */
    public double getBallAngleSmoothed(int targetId) {
        if (validTarget() && targetId <= targets.size()) {
            return filter.calculate(targets.get(targetId).getYaw());
        }
        return -10;
    }

    /**
     * Check for a valid target in the camera's view.
     *
     * @return whether or not there is a valid target in view.
     */
    public boolean validTarget() {
        return cameraResult.hasTargets();
    }

    /**
     * Get angle between crosshair and Ball left/right.
     *
     * @return angle between crosshair and Ball, left negative, 29.8 degrees in both directions.
     */
    public double getBallAngle(int targetId) {
        if (validTarget() && targetId <= targets.size()) {
            return targets.get(targetId).getYaw();
        }
        return -10;
    }

    /**
     * Get angle between crosshair and Ball up/down.
     *
     * @return angle between crosshair and Ball, down negative, 22 degrees in both directions.
     */
    public double getBallPitch(int targetId) {
        if (validTarget() && targetId <= targets.size()) {
            return targets.get(targetId).getPitch();
        }
        return -10;
    }

    /**
     * Get the size of the Ball onscreen.
     *
     * @return size of the Ball in % of the screen, 0-100.
     */
    public double getBallSize(int targetId) {
        if (validTarget() && targetId <= targets.size()) {
            return targets.get(targetId).getArea();
        }
        return -10;
    }
}