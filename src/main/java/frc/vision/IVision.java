package frc.vision;

import frc.misc.ISubsystem;

/**
 * I'm just simply vibing here, calm down bro.
 *
 * @author Smaltin
 */
public interface IVision extends ISubsystem {
    default double getAngle(){
        return getAngle(0);
    }

    default double getPitch(){
        return getPitch(0);
    }

    default double getAngleSmoothed(){
        return getAngleSmoothed(0);
    }

    default double getSize(){
        return getSize(0);
    }

    double getAngle(int targetId);

    double getPitch(int targetId);

    double getAngleSmoothed(int targetId);

    double getSize(int targetId);

    boolean hasValidTarget();

}
