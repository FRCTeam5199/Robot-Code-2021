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

    abstract double getAngle();

    abstract double getPitch();

    abstract double getAngleSmoothed();

    abstract double getSize();

    abstract boolean hasValidTarget();

}
