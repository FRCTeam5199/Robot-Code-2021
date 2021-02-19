package frc.vision;

import frc.misc.ISubsystem;

/**
 * I'm just simply vibing here, calm down bro.
 *
 * @author Smaltin
 */
public abstract class AbstractVision implements ISubsystem {
    @Override
    public void init() {

    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {

    }

    public abstract double getAngle();

    public abstract double getPitch();

    public abstract double getAngleSmoothed();

    public abstract double getSize();

    public abstract boolean hasValidTarget();

}
