package frc.telemetry;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SerialPort;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.SPI;

public class WrappedNavX2IMU extends AbstractIMU {
    private final AHRS navX2IMU;

    public WrappedNavX2IMU() {
        navX2IMU = createIMU();
        //TODO make this better (stop being cringe) & make joey stop sobbing over his failures of hardcoding something
    }

    private AHRS createIMU() {
        AHRS out = null;
        try {
            out = new AHRS(SerialPort.Port.kUSB);
        } catch (RuntimeException ex) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        return out;
    }

    @Override
    public void updateGeneric() {
        ypr[0] = navX2IMU.getYaw();
        ypr[1] = navX2IMU.getPitch();
        ypr[2] = navX2IMU.getRoll();
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