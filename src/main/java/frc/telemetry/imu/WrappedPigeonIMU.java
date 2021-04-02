package frc.telemetry.imu;

import com.ctre.phoenix.sensors.PigeonIMU;

import static frc.robot.Robot.robotSettings;

public class WrappedPigeonIMU extends AbstractIMU {
    private PigeonIMU pigeon;

    public WrappedPigeonIMU() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        pigeon = new PigeonIMU(robotSettings.IMU_ID);
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }    /**
     * Yaw since last restart
     *
     * @return yaw since last restart
     */
    @Override
    public double relativeYaw() { //return relative(to start) yaw of pigeon
        updateGeneric();
        return (ypr[0] - startYaw);
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }



    /**
     * Updates the Pigeon IMU data
     */
    @Override
    public void updateGeneric() {
        pigeon.getYawPitchRoll(ypr);
        super.updateGeneric();
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

    @Override
    public String getSubsystemName() {
        return "Pigeon IMU";
    }
}
