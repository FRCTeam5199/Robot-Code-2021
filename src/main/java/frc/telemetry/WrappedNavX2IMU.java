package frc.telemetry;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SerialPort;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.SPI;

public class WrappedNavX2IMU extends AbstractIMU {
    private AHRS navX2IMU;

    public WrappedNavX2IMU() {
        addToMetaList();
        init();
    }

    public void init() {
        navX2IMU = new AHRS(SerialPort.Port.kUSB);
    }

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

    @Override
    public double relativeYaw() {
        updateGeneric();
        return (ypr[0] - startYaw);
    }

    @Override
    public double absoluteYaw() {
        updateGeneric();
        return ypr[0];
    }

    @Override
    public void resetOdometry() {
        updateGeneric();
        navX2IMU.reset();
        startypr = ypr;
        startYaw = absoluteYaw();
    }
}