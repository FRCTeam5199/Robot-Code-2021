package frc.telemetry;

import com.ctre.phoenix.sensors.PigeonIMU;
import frc.robot.RobotMap;

public class WrappedPigeonIMU extends AbstractIMU {
    private final PigeonIMU pigeon;

    public WrappedPigeonIMU() {
        pigeon = new PigeonIMU(RobotMap.IMU);
    }

    /**
     * Yaw since last restart
     *
     * @return yaw since last restart
     */
    @Override
    public double relativeYaw() { //return relative(to start) yaw of pigeon
        updateGeneric();
        return (ypr[0] - startYaw);
    }

    /**
     * Updates the Pigeon IMU data
     */
    @Override
    public void updateGeneric() {
        pigeon.getYawPitchRoll(ypr);
    }

    /**
     * Resets the Pigeon IMU
     */
    @Override
    public void resetOdometry() {
        updateGeneric();
        startypr = ypr;
        startYaw = absoluteYaw();
    }

    /**
     * gets the absolute yaw of the pigeon since last zeroing event (startup)
     *
     * @return absolute yaw of pigeon
     */
    @Override
    public double absoluteYaw() {  //get absolute yaw of pigeon
        updateGeneric();
        return ypr[0];
    }
}
