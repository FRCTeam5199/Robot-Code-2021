package frc.motors.followers;

import frc.motors.SparkMotor;

public class SparkFollowerMotors extends AbstractFollowerMotor {
    public SparkFollowerMotors(int... ids) {
        for (int i = 0; i < ids.length; i++)
            motors[i] = new SparkMotor(ids[i]);
    }

    @Override
    public void invert(boolean invert) {
        //do nothing because we are too cool
    }
}
