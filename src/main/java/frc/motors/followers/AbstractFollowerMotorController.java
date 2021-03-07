package frc.motors.followers;

import frc.motors.AbstractMotorController;

/**
 * This class should be used to hold all the follower motors that will follow the same motor
 *
 * @see AbstractMotorController
 * @see SparkFollowerMotorsController
 * @see TalonFollowerMotorController
 */
public abstract class AbstractFollowerMotorController {
    /**
     * The list of all motors this object is responsible for maintaining
     */
    protected AbstractMotorController[] motors;

    /**
     * Sets the motor inversion for all of the followers based on the param
     *
     * @param invert whether to invert motor rotation
     * @see AbstractMotorController#setInverted(boolean)
     */
    public abstract void invert(boolean invert);

    /**
     * Makes each motor follow the passed motor The passed motor type must match the follower types
     *
     * @param leader Parent motor for these child motors to follow
     * @see AbstractMotorController#follow(AbstractMotorController)
     */
    public void follow(AbstractMotorController leader) {
        follow(leader, false);
    }

    public void follow(AbstractMotorController leader, boolean invert) {
        for (AbstractMotorController follower : motors)
            follower.follow(leader, invert);
    }

    /**
     * Sets the idle mode for all children to either be (brake = false) minimally resistive or (brake = true) to resist
     * all motion/use ERF to slow motor (actual implemetation varies between motors)
     *
     * @param brake whether to apply idle resistance
     * @see AbstractMotorController#setBrake(boolean)
     */
    public void setBrake(boolean brake) {
        for (AbstractMotorController motor : motors)
            motor.setBrake(brake);
    }

    /**
     * Sets the maximum allowable current that will flow through each follower motor
     *
     * @param limit max current in amps
     * @see AbstractMotorController#setCurrentLimit(int)
     */
    public void setCurrentLimit(int limit) {
        for (AbstractMotorController motor : motors)
            motor.setCurrentLimit(limit);
    }

    public boolean failureFlag() {
        for (AbstractMotorController motor : motors)
            if (motor.failureFlag)
                return true;
        return false;
    }

    public String getSuggestedFix() {
        for (AbstractMotorController motor : motors)
            if (!motor.getSuggestedFix().equals(""))
                return motor.getSuggestedFix();
        return "";
    }
}
