package frc.telemetry;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.drive.AbstractDriveManager;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.telemetry.imu.AbstractIMU;
import frc.telemetry.imu.WrappedNavX2IMU;
import frc.telemetry.imu.WrappedPigeonIMU;

import static frc.robot.Robot.robotSettings;

public abstract class AbstractRobotTelemetry implements ISubsystem {
    protected final AbstractDriveManager driver;
    public AbstractIMU imu;
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;

    public void resetOdometry(){
        imu.resetOdometry();
        driver.resetDriveEncoders();
    }

    protected AbstractRobotTelemetry(AbstractDriveManager driver){
        addToMetaList();
        this.driver = driver;
        init();
    }

    @Override
    public void init(){
        driver.resetDriveEncoders();
        if (!robotSettings.ENABLE_IMU)
            return;
        switch (robotSettings.IMU_TYPE) {
            case PIGEON:
                imu = new WrappedPigeonIMU();
                break;
            case NAVX2:
                imu = new WrappedNavX2IMU();
        }
    }

    @Override
    public void updateGeneric(){
        robotTranslation = robotPose.getTranslation();
        robotRotation = robotPose.getRotation();
        UserInterface.smartDashboardPutNumber("Yaw", imu.absoluteYaw());
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return (imu != null && imu.getSubsystemStatus() == SubsystemStatus.NOMINAL) && driver.getSubsystemStatus() == SubsystemStatus.NOMINAL ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    @Override
    public String getSubsystemName() {
        return "Guidance";
    }

    /**
     * @return the robot's Y position in relation to its starting position(away positive) typically facing away from
     * opposing alliance station
     */
    public double fieldY() {
        return robotPose.getTranslation().getY();
    }

    /**
     * @return the robot's X position in relation to its starting position(right positive) typically facing away from
     * opposing alliance station
     */
    public double fieldX() {
        return robotPose.getTranslation().getX();
    }
}
