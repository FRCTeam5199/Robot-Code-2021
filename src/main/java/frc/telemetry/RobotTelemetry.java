package frc.telemetry;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.drive.DriveManagerSwerve;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.misc.UtilFunctions;
import frc.telemetry.imu.AbstractIMU;
import frc.telemetry.imu.WrappedNavX2IMU;
import frc.telemetry.imu.WrappedPigeonIMU;

import static frc.robot.Robot.robotSettings;

public class RobotTelemetry implements ISubsystem {
    private final AbstractDriveManager driver;
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;
    public DifferentialDriveOdometry odometer;
    public AbstractIMU imu;

    public RobotTelemetry(AbstractDriveManager driver) {
        addToMetaList();
        this.driver = driver;
        init();
    }

    /**
     * creates pigeon, heading pid, and odometer
     */
    @Override
    public void init() {
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
        if (driver instanceof DriveManagerStandard) {
            odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(imu.absoluteYaw()));
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), Units.inchesToMeters(((DriveManagerStandard) driver).leaderL.getRotations()), Units.inchesToMeters(((DriveManagerStandard) driver).leaderR.getRotations()));
        } else if (driver instanceof DriveManagerSwerve) {
            //TODO implement this
        }
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {
        //updateGeneric();
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    /**
     * does {@link #updateGeneric()}
     */
    @Override
    public void updateAuton() {
        updateGeneric();
    }

    /**
     * updates the robot orientation based on the IMU and distance traveled
     */
    @Override
    public void updateGeneric() {
        if (robotSettings.ENABLE_IMU) {
            if (driver instanceof DriveManagerStandard)
                robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), Units.inchesToMeters(((DriveManagerStandard) driver).leaderL.getRotations()), Units.inchesToMeters(((DriveManagerStandard) driver).leaderR.getRotations()));
            else if (driver instanceof DriveManagerSwerve) {
                //TODO implement this
                SwerveModuleState frontLeft = ((DriveManagerSwerve) driver).moduleStates[0], frontRight = ((DriveManagerSwerve) driver).moduleStates[1], backLeft = ((DriveManagerSwerve) driver).moduleStates[2], backRight = ((DriveManagerSwerve) driver).moduleStates[3];
                //robotPose = odometer.update(Rotation2d.fromDegrees(imu.absoluteYaw()), frontLeft, frontRight, backLeft, backRight);
            }
            robotTranslation = robotPose.getTranslation();
            robotRotation = robotPose.getRotation();
            UserInterface.smartDashboardPutNumber("Yaw", imu.absoluteYaw());
        }
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
    public String getSubsystemName() {
        return "Guidance";
    }

    /**
     * Wraps the angle between prograde (straight forward) and the location of the given point on a range of -180 to
     * 180
     *
     * @param x x coord of other point
     * @param y y coord of other point
     * @return apparent angle between heading and passed coords
     * @see #headingError(double, double)
     */
    public double realHeadingError(double x, double y) {
        return UtilFunctions.mathematicalMod(headingError(x, y) + 180, 360) - 180;
    }

    /**
     * Gives the angle between the way the bot is facing and another point (bounds unknown, see {@link
     * #realHeadingError(double, double)})
     *
     * @param wayX x coord of query point
     * @param wayY y coord of query point
     * @return angle between heading and given point
     */
    private double headingError(double wayX, double wayY) {
        return angleFromHere(wayX, wayY) - imu.yawWraparoundAhead();
    }

    /**
     * Calculates the angle in coordinate space between here and a given coordinates
     *
     * @param wayX x coord of query point
     * @param wayY y coord of query point
     * @return the angle between the heading and the point passed in
     */
    private double angleFromHere(double wayX, double wayY) {
        return Math.toDegrees(Math.atan2(wayY - fieldY(), wayX - fieldX()));
    }

    /**
     * @return the robot's Y position in relation to its starting position(away positive) typically facing away from
     * opposing alliance station
     */
    public double fieldY() {
        return robotTranslation.getY();
    }

    /**
     * @return the robot's X position in relation to its starting position(right positive) typically facing away from
     * opposing alliance station
     */
    public double fieldX() {
        return robotTranslation.getX();
    }

    /**
     * Resets all orienting to zeroes.
     */
    public void resetOdometry() {
        if (robotSettings.ENABLE_IMU)
            imu.resetOdometry();
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(imu.absoluteYaw()));
        driver.resetDriveEncoders();
    }
}