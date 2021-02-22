package frc.telemetry;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * This is a class to interface the Navx2 Inertial Measurement Unit (IMU) but allowing versatility in swapping between
 * different IMU's
 */
public class WrappedNavX2IMU extends AbstractIMU {
    private AHRS navX2IMU;

    public WrappedNavX2IMU() {
        addToMetaList();
        init();
    }

    /**
     * Creates a new IMU
     */
    //TODO make this a setting
    public void init() {
        navX2IMU = new AHRS(SerialPort.Port.kUSB);
    }

    /**
     * Update the values for the IMU
     */
    @Override
    public void updateGeneric() {
        ypr[0] = navX2IMU.getYaw();
        ypr[1] = navX2IMU.getPitch();
        ypr[2] = navX2IMU.getRoll();
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
     * Returns the yaw in relativity to the last zeroing
     *
     * @return relative yaw
     */
    @Override
    public double relativeYaw() {
        updateGeneric();
        return (ypr[0] - startYaw);
    }

    /**
     * Returns the yaw
     *
     * @return yaw of robot in degrees
     */
    @Override
    public double absoluteYaw() {
        updateGeneric();
        return ypr[0];
    }

    /**
     * Resets the odometry (IMU)
     */
    @Override
    public void resetOdometry() {
        updateGeneric();
        navX2IMU.reset();
        startypr = ypr;
        startYaw = absoluteYaw();
    }
}