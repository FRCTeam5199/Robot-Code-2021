package frc.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.LinearFilter;

public class GoalLimelight extends AbstractVision {
    public NetworkTableEntry yaw;
    public NetworkTableEntry size;
    public NetworkTableEntry hasTarget;
    public NetworkTableEntry pitch;
    public NetworkTableEntry pose;
    private LinearFilter filter;

    public GoalLimelight() {
        init();
    }

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
    public double getAngle() {
        if (hasValidTarget()) {
            return yaw.getDouble(0);
        } else {
            return 0;
        }
    }

    @Override
    public double getPitch() {
        if (hasValidTarget()) {
            return pitch.getDouble(0);
        } else {
            return 0;
        }
    }

    @Override
    public double getAngleSmoothed() {
        if (hasValidTarget()) {
            return filter.calculate(yaw.getDouble(0));
        } else {
            return 0;
        }
    }

    @Override
    public double getSize() {
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
