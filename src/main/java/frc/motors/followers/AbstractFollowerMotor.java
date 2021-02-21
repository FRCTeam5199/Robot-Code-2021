package frc.motors.followers;

import frc.motors.AbstractMotor;

/**
 * This class should be used to hold all the follower motors that will follow the same motor
 *
 * @see AbstractMotor
 * @see SparkFollowerMotors
 * @see TalonFollowerMotor
 */
public abstract class AbstractFollowerMotor {
    /**
     * The list of all motors this object is responsible for maintaining
     */
    protected AbstractMotor[] motors;

    /**
     * Sets the motor inversion for all of the followers based on the param
     *
     * @param invert whether to invert motor rotation
     * @see AbstractMotor#setInverted(boolean)
     */
    public abstract void invert(boolean invert);

    /**
     * Makes each motor follow the passed motor
     * The passed motor type must match the follower types
     *
     * @param leader Parent motor for these child motors to follow
     * @see AbstractMotor#follow(AbstractMotor)
     */
    public void follow(AbstractMotor leader) {
        for (AbstractMotor follower : motors)
            follower.follow(leader);
    }

    /**
     * Sets the idle mode for all children to either be
     * (brake = false) minimally resistive or
     * (brake = true) to resist all motion/use ERF to slow motor (actual implemetation varies between motors)
     *
     * @param brake whether to apply idle resistance
     * @see AbstractMotor#setBrake(boolean)
     */
    public void setBrake(boolean brake) {
        for (AbstractMotor motor : motors)
            motor.setBrake(brake);
    }

    /**
     * Sets the maximum allowable current that will flow through each follower motor
     *
     * @param limit max current in amps
     * @see AbstractMotor#setCurrentLimit(int)
     */
    public void setCurrentLimit(int limit) {
        for (AbstractMotor motor : motors)
            motor.setCurrentLimit(limit);
    }
}
