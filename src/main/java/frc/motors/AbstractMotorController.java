package frc.motors;

import frc.misc.PID;
import frc.misc.UserInterface;
import frc.motors.followers.AbstractFollowerMotorController;

import java.util.ArrayList;

/**
 * This is the base class for any motor. It is not an interface because it has to have a {@link
 * #sensorToRealDistanceFactor nonstatic field} which is not doable in an interface. If you are to use a motor that we
 * did not implement, make a new class and use an Abstract motor
 *
 * @author jojo2357
 * @see AbstractFollowerMotorController
 * @see SparkMotorController
 * @see TalonMotorController
 * @see VictorMotorController
 */
public abstract class AbstractMotorController {
    public static final ArrayList<AbstractMotorController> motorList = new ArrayList<>();
    public boolean failureFlag = false;
    /**
     * Value to convert from sensor position to real units (this will vary between motors so know your units!)
     * Destination units are RPM that include the gearing on the motor
     * <p>
     * This is a Double (object, not value). If required but not set, will throw a NPE If it is giving you hell and you
     * dont want to actually fix the issue, just change to double
     */
    public Double sensorToRealDistanceFactor;
    protected String potentialFix;
    protected boolean isOverheated;

    /**
     * Inverts the motor rotation from default for that motor (varies motor to motor of course)
     *
     * @param invert Whether to inver the motor or not
     * @return this object (factory style)
     */
    public abstract AbstractMotorController setInverted(boolean invert);

    public abstract String getName();

    /**
     * Have this motor follow another motor (must be the same motor ie talon to talon). This motor will be the child and
     * the passed motor will be the leader
     *
     * @param leader motor to follow
     * @return this object for factory style construction
     * @see AbstractFollowerMotorController
     */
    @Deprecated
    public abstract AbstractMotorController follow(AbstractMotorController leader);

    /**
     * Have this motor follow another motor (must be the same motor ie talon to talon). This motor will be the child and
     * the passed motor will be the leader
     *
     * @param leader motor to follow
     * @param invert whether to invert this follower
     * @return this object for factory style construction
     * @see AbstractFollowerMotorController
     */
    public abstract AbstractMotorController follow(AbstractMotorController leader, boolean invert);


    /**
     * Sets current encoder position to be the zero position. If you are absolutely crazy and want to set the encoder to
     * an artifical position, create an abstract method in {@link AbstractMotorController} that takes an position Then,
     * implement this method to call your overloaded method and pass a default of 0. But really dont do that please
     */
    public abstract void resetEncoder();

    /**
     * You know how it is. Timeout is default per motor, channel defaults to 0 or motor default
     *
     * @param pid the {@link PID} object that contains pertinent pidf data
     * @return this object for factory style construction
     */
    public abstract AbstractMotorController setPid(PID pid);

    /**
     * @param amount requested drive velocity
     */
    public abstract void moveAtVelocity(double amount);

    /**
     * @param pos requested position
     */
    public abstract void moveAtPosition(double pos);

    /**
     * Sets the idle mode to either be (brake = false) minimally resistive or (brake = true) to resist all motion/use
     * ERF to slow motor (actual implemetation varies between motors)
     *
     * @param brake whether to apply idle resistance
     * @return this object for factory style construction
     */
    public abstract AbstractMotorController setBrake(boolean brake);

    /**
     * Gets the default encoder's position. Accounts for gearings but not wheelings unless ur crazy and accounted for
     * wheel size in {@link #sensorToRealDistanceFactor}
     *
     * @return Distance output shaft has moved in rotations
     */
    public abstract double getRotations();

    public abstract double getSpeed();

    /**
     * Sets the maximum allowable current that will flow through this motor
     *
     * @param limit max current in amps
     * @return this object for factory style construction
     */
    public abstract AbstractMotorController setCurrentLimit(int limit);

    /**
     * Sets the motor output on a percent output basis
     *
     * @param percent -1 to 1 output requested
     */
    public abstract void moveAtPercent(double percent);

    /**
     * Sets the ramp rate for open loop control modes. This is the maximum rate at which the motor controller's output
     * is allowed to change in said mode.
     *
     * @param timeToMax time in seconds to go from 0 to full power
     * @return this object for factory style construction
     */
    public abstract AbstractMotorController setOpenLoopRampRate(double timeToMax);

    public abstract String getSuggestedFix();

    protected AbstractMotorController() {
        motorList.add(this);
    }

    /**
     * see docs for {@link #sensorToRealDistanceFactor} for full explanation
     *
     * @param s2rf Conversion from encoder units to RPM including the gearing
     */
    public void setSensorToRealDistanceFactor(double s2rf) {
        sensorToRealDistanceFactor = s2rf;
    }

    protected boolean isTemperatureAcceptable(int myID) {
        if (getMotorTemperature() > 100) {
            if (!isOverheated) {
                UserInterface.smartDashboardPutBoolean("OVERHEAT " + myID, false);
                isOverheated = true;
            }
        } else if (isOverheated) {
            isOverheated = false;
            UserInterface.smartDashboardPutBoolean("OVERHEAT " + myID, true);
        }
        return !isOverheated;
    }

    /**
     * Gets the temperature of the motor
     *
     * @return the temperature in celcius
     */
    public abstract double getMotorTemperature();
}
