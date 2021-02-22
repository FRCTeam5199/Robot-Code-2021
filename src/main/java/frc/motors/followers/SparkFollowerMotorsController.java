package frc.motors.followers;

import frc.motors.SparkMotorController;

/**
 * This works to wrap Neo's and maybe some other motors
 */
public class SparkFollowerMotorsController extends AbstractFollowerMotorController {
    public SparkFollowerMotorsController(int... ids) {
        for (int i = 0; i < ids.length; i++)
            motors[i] = new SparkMotorController(ids[i]);
    }

    @Override
    public void invert(boolean invert) {
        //do nothing because we are too cool
    }
}
