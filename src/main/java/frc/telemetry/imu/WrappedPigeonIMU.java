package frc.telemetry.imu;

import com.ctre.phoenix.sensors.PigeonIMU;

import static frc.robot.Robot.robotSettings;

/**
 * This is a class to interface the Pigeon Inertial Measurement Unit (IMU) but allowing versatility in swapping between
 * different IMU's
 */
public class WrappedPigeonIMU extends AbstractIMU {
    private PigeonIMU pigeon;

    protected WrappedPigeonIMU() {
        super();
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

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

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
     * Updates the Pigeon IMU data
     */
    @Override
    public void updateGeneric() {
        pigeon.getYawPitchRoll(ypr);
        super.updateGeneric();
    }

    @Override
    public String getSubsystemName() {
        return "Pigeon IMU";
    }
}
