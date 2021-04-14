package frc.telemetry;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerSwerve;

import static frc.robot.Robot.robotSettings;

public class RobotTelemetrySwivel extends AbstractRobotTelemetry {
    private SwerveDriveOdometry odometer;

    protected RobotTelemetrySwivel(AbstractDriveManager driver) {
        super(driver);
        if (!(driver instanceof DriveManagerSwerve))
            throw new IllegalArgumentException("Nope");
    }

    @Override
    public void init() {
        super.init();
        if (imu != null) {
            odometer = new SwerveDriveOdometry(((DriveManagerSwerve) driver).kinematics, Rotation2d.fromDegrees(imu.absoluteYaw()));
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), ((DriveManagerSwerve) driver).getStates());
        }
    }

    @Override
    public void updateGeneric() {
        if (robotSettings.ENABLE_IMU) {
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), ((DriveManagerSwerve) driver).getStates());
            super.updateGeneric();
        }
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

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
}
