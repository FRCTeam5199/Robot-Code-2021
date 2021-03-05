package frc.vision.distancesensor;

import frc.ballstuff.intaking.Hopper;
import frc.misc.ISubsystem;

/**
 * Used for optical distance sensors to detect distance from obstacles
 *
 * @see Hopper
 * @see RevDistanceSensor
 */
public interface IDistanceSensor extends ISubsystem {
    /**
     * Gets the distance detected by the sensor
     *
     * @return the distance read by the sensor
     */
    double getDistance();

    @Override
    default String getSubsystemName() {
        return "Distance Sensor";
    }
}
