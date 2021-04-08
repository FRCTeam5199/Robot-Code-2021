package frc.vision.camera;

import edu.wpi.first.wpilibj.LinearFilter;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPipelineResult;
import org.photonvision.PhotonTrackedTarget;

import java.util.List;

import static frc.robot.Robot.robotSettings;

public class BallPhoton implements IVision {
    public static final BallPhoton BALL_PHOTON = new BallPhoton();
    private static final boolean DEBUG = false;
    private PhotonCamera ballCamera;
    private List<PhotonTrackedTarget> targets;
    private PhotonPipelineResult cameraResult;
    private LinearFilter filter;

    /**
     * inits BallPhoton
     */
    private BallPhoton() {
        addToMetaList();
        init();
    }

    /**
     * stores values in simpler variable names
     */
    public void init() {
        filter = LinearFilter.movingAverage(5);
        ballCamera = new PhotonCamera(robotSettings.BALL_CAM_NAME);
        cameraResult = ballCamera.getLatestResult();
        System.out.println("Found " + cameraResult.targets.size() + " targets");
    }

    /**
     * calls updateGeneric see BallPhoton.updateGeneric
     */
    @Override
    public void updateTest() {
        updateGeneric();
    }

    /**
     * calls updateGeneric see BallPhoton.updateGeneric
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
        updateGeneric();
    }

    /**
     * updates generic things for BallPhoton
     */
    @Override
    public void updateGeneric() {
        cameraResult = ballCamera.getLatestResult();
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Found " + cameraResult.targets.size() + " targets");
        }
        targets = cameraResult.getTargets();
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {
        updateAuton();
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "Ball Photon";
    }

    /**
     * Get angle between crosshair and Ball left/right.
     *
     * @param targetId the id of the object to query
     * @return angle between crosshair and Ball, left negative, 29.8 degrees in both directions.
     */
    @Override
    public double getAngle(int targetId) {
        if (targets==null)
            updateGeneric();
        if (hasValidTarget()/* && targetId < targets.size()*/) {
            return targets.get(targetId)
                    .getYaw();
        }
        return -10000;
    }

    /**
     * Get angle between crosshair and Ball up/down.
     *
     * @param targetId the id of the object to query
     * @return angle between crosshair and Ball, down negative, 22 degrees in both directions.
     */
    public double getPitch(int targetId) {
        if (hasValidTarget()/* && targetId <= targets.size()*/) {
            return targets.get(targetId).getPitch();
        }
        return -10;
    }

    /**
     * Get angle between crosshair and Ball left/right with filter calculation.
     *
     * @param targetId the id of the object to query
     * @return angle between crosshair and Ball, left negative, 29.8 degrees in both directions.
     */
    @Override
    public double getAngleSmoothed(int targetId) {
        if (hasValidTarget()/* && targetId <= targets.size()*/) {
            return filter.calculate(targets.get(targetId).getYaw());
        }
        return -10000;
    }

    /**
     * Get the size of the Ball onscreen.
     *
     * @param targetId the id of the object to query
     * @return size of the Ball in % of the screen, 0-100.
     */
    public double getSize(int targetId) {
        if (hasValidTarget()/* && targetId < targets.size()*/) {
            return targets.get(targetId).getArea();
        }
        return -10;
    }

    /**
     * Check for a valid target in the camera's view.
     *
     * @return whether or not there is a valid target in view.
     */
    @Override
    public boolean hasValidTarget() {
        return cameraResult.hasTargets();
    }

    @Override
    public void setLedMode(VisionLEDMode ledMode) {
        throw new UnsupportedOperationException("Cannot set LED mode " + ledMode.name() + " on " + getSubsystemName());
    }
}