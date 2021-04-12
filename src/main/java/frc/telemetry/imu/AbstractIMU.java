package frc.telemetry.imu;

import frc.misc.ISubsystem;
import frc.misc.UtilFunctions;
import frc.selfdiagnostics.IMUNonOpIssue;

/**
 * Bro chill out im literally just vibing here
 *
 * @author jojo2357
 */
public abstract class AbstractIMU implements ISubsystem {
    protected double startYaw;

    protected double[] ypr = new double[3];
    protected double[] startypr = new double[3];

    public abstract double absoluteYaw();

    public abstract void resetOdometry();

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
        updateGeneric();
    }

    @Override
    public void updateGeneric() {
        if (ypr[0] == 0)
            IMUNonOpIssue.reportIssue(this, getSubsystemName());
        else
            IMUNonOpIssue.resolveIssue(this);
    }

    @Override
    public String getSubsystemName() {
        return "IMU";
    }

    /**
     * Gets the yaw of the bot and wraps it on the bound -180 to 180
     *
     * @return wrapped yaw val
     */
    public double yawWraparoundAhead() {
        return UtilFunctions.mathematicalMod(relativeYaw() + 180, 360) - 180;
    }

    public abstract double relativeYaw();
}
