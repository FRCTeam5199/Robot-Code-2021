package frc.telemetry.imu;

import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
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

    public abstract void resetOdometry();

    public static AbstractIMU createIMU(SupportedIMU imuType) {
        switch (imuType) {
            case PIGEON:
                return new WrappedPigeonIMU();
            case NAVX2:
                return new WrappedNavX2IMU();
            default:
                throw new IllegalArgumentException("Connot make a " + imuType.name());
        }
    }

    protected AbstractIMU() {
        init();
        addToMetaList();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return absoluteYaw() != 0 ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    /**
     * gets the absolute yaw of the pigeon since last zeroing event (startup)
     *
     * @return absolute yaw of pigeon
     */
    public double absoluteYaw() {
        updateGeneric();
        return ypr[0];
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
        updateGeneric();
    }

    @Override
    public void updateGeneric() {
        IMUNonOpIssue.handleIssue(this, getSubsystemName(), ypr[0] != 0);
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

    /**
     * Yaw since last restart
     *
     * @return yaw since last restart
     */
    public double relativeYaw() {
        updateGeneric();
        return (ypr[0] - startYaw);
    }

    /**
     * What dont you get about SIMPLY VIBING?
     */
    public enum SupportedIMU {
        NAVX2, PIGEON
    }
}
