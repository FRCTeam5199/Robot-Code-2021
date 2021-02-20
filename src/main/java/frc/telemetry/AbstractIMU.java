package frc.telemetry;

import frc.misc.ISubsystem;
import frc.misc.UtilFunctions;

/**
 * Bro chill out im literally just vibing here
 *
 * @author jojo2357
 */
public abstract class AbstractIMU implements ISubsystem {
    protected double startYaw;

    protected double[] ypr = new double[3];
    protected double[] startypr = new double[3];

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

    public abstract double relativeYaw();

    public abstract double absoluteYaw();

    /**
     * Gets the yaw of the bot and wraps it on the bound -180 to 180
     *
     * @return wrapped yaw val
     */
    public double yawWraparoundAhead() {
        return UtilFunctions.mathematicalMod(relativeYaw() + 180, 360) - 180;
    }

    public abstract void resetOdometry();
}
