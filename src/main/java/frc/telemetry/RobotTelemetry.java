package frc.telemetry;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.misc.ISubsystem;
import frc.misc.UtilFunctions;
import frc.robot.RobotSettings;

public class RobotTelemetry implements ISubsystem {
    private final DriveManager driver;
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;
    public PIDController headingPID;
    public DifferentialDriveOdometry odometer;
    public AbstractIMU imu;

    public RobotTelemetry(DriveManager driver) {
        addToMetaList();
        this.driver = driver;
        init();
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
     * @return the robot's X position in relation to its starting position(right positive) typically facing away from
     * opposing alliance station
     */
    public double fieldX() {
        return robotTranslation.getX();
    }

    /**
     * @return the robot's Y position in relation to its starting position(away positive) typically facing away from
     * opposing alliance station
     */
    public double fieldY() {
        return robotTranslation.getY();
    }

    /**
     * Zeroes the encoders at their current position
     */
    public void resetEncoders() {
        driver.leaderL.resetEncoder();
        driver.leaderR.resetEncoder();
    }

    /**
     * Resets all orienting to zeroes.
     *
     * @param pose     ignored
     * @param rotation ignored
     */
    public void resetOdometry(Pose2d pose, Rotation2d rotation) {
        //odometer.resetPosition(pose, rotation);
        if (RobotSettings.ENABLE_IMU)
            imu.resetOdometry();
        resetEncoders();
    }

    /**
     * Gets the meters traveled by the left encoder
     *
     * @return wheel meters traveled
     */
    public double getMetersLeft() {
        return Units.feetToMeters(driver.leaderL.getRotations() * UtilFunctions.wheelCircumference() / 12);
    }

    /**
     * Gets the meters traveled by the right encoder
     *
     * @return wheel meters traveled
     */
    public double getMetersRight() {
        return Units.feetToMeters(driver.leaderR.getRotations() * UtilFunctions.wheelCircumference() / 12);
    }

    /**
     * creates pigeon, heading pid, and odometer
     */
    @Override
    public void init() {
        resetEncoders();
        if (!RobotSettings.ENABLE_IMU)
            return;
        if (RobotSettings.USE_PIGEON) {
            imu = new WrappedPigeonIMU();
        } else {
            imu = new WrappedNavX2IMU();
        }
        headingPID = new PIDController(RobotSettings.HEADING_PID.getP(), RobotSettings.HEADING_PID.getI(), RobotSettings.HEADING_PID.getD());
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(imu.absoluteYaw()), new Pose2d(0, 0, new Rotation2d()));
        robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), getMetersLeft(), getMetersRight());
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
        if (RobotSettings.ENABLE_IMU) {
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), getMetersLeft(), getMetersRight());
            robotTranslation = robotPose.getTranslation();
            robotRotation = robotPose.getRotation();
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
}