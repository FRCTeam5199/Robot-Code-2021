package frc.telemetry;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.drive.DriveManagerSwerve;
import frc.drive.auton.Point;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.telemetry.imu.AbstractIMU;

import static frc.robot.Robot.robotSettings;

public abstract class AbstractRobotTelemetry implements ISubsystem {
    protected final AbstractDriveManager driver;
    public AbstractIMU imu;
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;

    public static AbstractRobotTelemetry createTelem(AbstractDriveManager driver) {
        if (driver instanceof DriveManagerSwerve)
            return new RobotTelemetrySwivel(driver);
        if (driver instanceof DriveManagerStandard)
            return new RobotTelemetryStandard(driver);
        throw new IllegalArgumentException("Cannot create telem for that");
    }

    protected AbstractRobotTelemetry(AbstractDriveManager driver) {
        if (this instanceof RobotTelemetrySwivel ^ (this.driver = driver) instanceof DriveManagerSwerve)
            throw new IllegalArgumentException("Incompatible telem and drive combo");
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        driver.resetDriveEncoders();
        if (!robotSettings.ENABLE_IMU)
            return;
        imu = AbstractIMU.createIMU(robotSettings.IMU_TYPE);
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return (imu != null && imu.getSubsystemStatus() == SubsystemStatus.NOMINAL) && driver.getSubsystemStatus() == SubsystemStatus.NOMINAL ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    @Override
    public void updateGeneric() {
        robotTranslation = robotPose.getTranslation();
        robotRotation = robotPose.getRotation();
        UserInterface.smartDashboardPutNumber("Yaw", imu.absoluteYaw());
    }

    @Override
    public String getSubsystemName() {
        return "Guidance";
    }

    public void resetOdometry() {
        imu.resetOdometry();
        driver.resetDriveEncoders();
    }

    public Point getLocation() {
        return new Point(fieldX(), fieldY());
    }

    /**
     * @return the robot's X position in relation to its starting position(right positive) typically facing away from
     * opposing alliance station
     */
    public double fieldX() {
        return robotPose.getTranslation().getX();
    }

    /**
     * @return the robot's Y position in relation to its starting position(away positive) typically facing away from
     * opposing alliance station
     */
    public double fieldY() {
        return robotPose.getTranslation().getY();
    }
}
