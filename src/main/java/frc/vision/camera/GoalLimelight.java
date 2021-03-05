package frc.vision.camera;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.LinearFilter;

/**
 * This is for the limelight looking at the goal that the shooter is shooting at
 */
public class GoalLimelight implements IVision {
    private NetworkTableEntry yaw;
    private NetworkTableEntry size;
    private NetworkTableEntry hasTarget;
    private NetworkTableEntry pitch;
    private NetworkTableEntry pose;
    private LinearFilter filter;

    public GoalLimelight() {
        addToMetaList();
        init();
    }

    /**
     * creates all of the network table stuff
     */
    @Override
    public void init() {
        NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
        filter = LinearFilter.movingAverage(5);
        yaw = limelight.getEntry("tx");
        size = limelight.getEntry("ta");
        hasTarget = limelight.getEntry("tv");
        pitch = limelight.getEntry("ty");
        pose = limelight.getEntry("camtran");
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
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "Goal Camera";
    }

    @Override
    public double getAngle(int channelIgnored) {
        if (hasValidTarget()) {
            return yaw.getDouble(0);
        } else {
            return 0;
        }
    }

    @Override
    public double getPitch(int channelIgnored) {
        if (hasValidTarget()) {
            return pitch.getDouble(0);
        } else {
            return 0;
        }
    }

    @Override
    public double getAngleSmoothed(int channelIgnored) {
        if (hasValidTarget()) {
            return filter.calculate(yaw.getDouble(0));
        } else {
            return 0;
        }
    }

    @Override
    public double getSize(int channelIgnored) {
        if (hasValidTarget()) {
            return size.getDouble(0);
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasValidTarget() {
        return (hasTarget.getDouble(0) == 1); //0 = not visible, 1 = visible
    }
}
