package frc.motors;

/**
 * This is the base class for any motor. It is not an interface because it has to have a
 * {@link #sensorToRevolutionFactor nonstatic field} which is not doable in an interface.
 * If you are to use a motor that we did not implement, make a new class and use an Abstract motor
 *
 * @author jojo2357
 * @see frc.motors.followers.AbstractFollowerMotor
 * @see SparkMotor
 * @see TalonMotor
 */
public abstract class AbstractMotor {
    /**
     * Value to convert from sensor position to real units (this will vary between motors so know your units!)
     * Destination units are RPM that include the gearing on the motor
     * <p>
     * This is a Double (object, not value). If required but not set, will throw a NPE
     * If it is giving you hell and you dont want to actually fix the issue, just change to double
     */
    protected Double sensorToRevolutionFactor;

    /**
     * Uses PID to attempt to reach the requested speed
     * Different from {@link #moveAtVelocity(double)} because this does not account for gearing or real distances
     *
     * @param rpm speed requested in rpm (revolutions per minutiae)
     */
    public abstract void moveAtRotations(double rpm);

    /**
     * Inverts the motor rotation from default for that motor (varies motor to motor of course)
     *
     * @param invert Whether to inver the motor or not
     */
    public abstract void setInverted(boolean invert);

    /**
     * Have this motor follow another motor (must be the same motor ie talon to talon).
     * This motor will be the child and the passed motor will be the leader
     *
     * @param leader motor to follow
     * @see frc.motors.followers.AbstractFollowerMotor
     */
    public abstract void follow(AbstractMotor leader);

    /**
     * Sets current encoder position to be the zero position
     */
    public abstract void resetEncoder();

    /**
     * You know how it is. Timeout is default per motor, channel defaults to 0 or motor default
     *
     * @param p proportional (the muscle)
     * @param i integral (the impatient)
     * @param d derivative (the careful)
     * @param f feed-forward (the wildcard)
     */
    public abstract void setPid(double p, double i, double d, double f);

    /**
     * Moves the motor at the requested velocity per time (default fps but units vary based on {@link #sensorToRevolutionFactor})
     * Different from {@link #moveAtRotations(double)} since this accounts for real distances not motor speed.
     *
     * @param amount requested drive velocity
     */
    public abstract void moveAtVelocity(double amount);

    /**
     * Sets the idle mode to either be
     * (brake = false) minimally resistive or
     * (brake = true) to resist all motion/use ERF to slow motor (actual implemetation varies between motors)
     *
     * @param brake whether to apply idle resistance
     */
    public abstract void setBrake(boolean brake);

    /**
     * Gets the default encoder's position.
     * Accounts for gearings but not wheelings unless ur crazy and accounted for wheel size in {@link #sensorToRevolutionFactor}
     *
     * @return Distance output shaft has moved in rotations
     */
    public abstract double getRotations();

    /**
     * Sets the maximum allowable current that will flow through this motor
     *
     * @param limit max current in amps
     */
    public abstract void setCurrentLimit(int limit);

    /**
     * Sets the motor output based on voltage (please dont do this)
     *
     * @param voltage 0 to 12-ish volts requested on this motor
     * @deprecated
     */
    @Deprecated
    public abstract void moveAtVoltage(double voltage);

    /**
     * Sets the motor output on a percent output basis
     *
     * @param percent 0 to 1 output requested
     */
    public abstract void moveAtPercent(double percent);

    /**
     * Sets the ramp rate for open loop control modes.
     * This is the maximum rate at which the motor controller's output is allowed to change in said mode.
     *
     * @param timeToMax time in seconds to go from 0 to full power
     */
    public abstract void setOpenLoopRampRate(double timeToMax);

    /**
     * see docs for {@link #sensorToRevolutionFactor} for full explanation
     *
     * @param s2rf Conversion from encoder units to RPM including the gearing
     */
    public void setSensorToRevolutionFactor(double s2rf) {
        sensorToRevolutionFactor = s2rf;
    }
}
