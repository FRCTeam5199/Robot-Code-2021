package frc.motors.followers;

import frc.motors.AbstractMotor;

public abstract class AbstractFollowerMotor {
    protected AbstractMotor[] motors;

    public void follow(AbstractMotor leader){
        for (AbstractMotor follower : motors)
            follower.follow(leader);
    }

    public abstract void invert(boolean invert);

    public void resetEncoders(){
        for (AbstractMotor motor : motors)
            motor.resetEncoder();
    }

    public void setBrake(boolean brake){
        for (AbstractMotor motor : motors)
            motor.setBrake(brake);
    }

    public void setCurrentLimit(int limit){
        for (AbstractMotor motor : motors)
            motor.setCurrentLimit(limit);
    }
}
