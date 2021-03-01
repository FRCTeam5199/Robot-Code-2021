package frc.vision;

import frc.misc.ISubsystem;

/**
 * I'm just simply vibing here, calm down bro. Anyone that can SEE would use me
 *
 * @author Smaltin
 */
public interface IVision extends ISubsystem {

    /**
     * Returns the angle between the camera and the object
     *
     * @return the angle in degrees between the camera and the object
     */
    default double getAngle() {
        return getAngle(0);
    }

    /**
     * Returns the pitch between the camera and the object
     *
     * @return the altitude in degrees between the camera and the object
     */
    default double getPitch() {
        return getPitch(0);
    }

    /**
     * Returns the pitch between the camera and the object, but filtered
     *
     * @return the altitude in degrees after smoothing
     */
    default double getAngleSmoothed() {
        return getAngleSmoothed(0);
    }

    /**
     * Returns the size of the object as visible from the camera
     *
     * @return the size in degrees between the camera and the object
     */
    default double getSize() {
        return getSize(0);
    }

    double getAngle(int targetId);

    double getPitch(int targetId);

    double getAngleSmoothed(int targetId);

    double getSize(int targetId);

    boolean hasValidTarget();

}
