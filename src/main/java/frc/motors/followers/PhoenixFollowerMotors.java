package frc.motors.followers;

import frc.motors.PhoenixMotor;

public class PhoenixFollowerMotors extends AbstractFollowerMotor {
    public PhoenixFollowerMotors(int... ids) {
        for (int i = 0; i < ids.length; i++)
            motors[i] = new PhoenixMotor(ids[i]);
    }

    @Override
    public void invert(boolean invert) {
        //do nothing because we are too cool
    }
}
