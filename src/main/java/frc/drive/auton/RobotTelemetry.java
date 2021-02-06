package frc.drive.auton;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.misc.ISubsystem;
import frc.misc.UtilFunctions;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

public class RobotTelemetry implements ISubsystem {
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;
    private final DriveManager driver;
    private PigeonIMU pigeon;
    public PIDController headingPID;
    public DifferentialDriveOdometry odometer;

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];
    private double startYaw;

    public RobotTelemetry(DriveManager driver){
        this.driver = driver;
        init();
    }

    /**
     * Wraps the angle between prograde (straight forward) and the location of the given point on a range of -180 to 180
     *
     * @see #headingError(double, double)
     * @param x x coord of other point
     * @param y y coord of other point
     * @return apparent angle between heading and passed coords
     */
    public double realHeadingError(double x, double y) {
        return UtilFunctions.mathematicalMod(headingError(x, y) + 180, 360) - 180;
    }

    /**
     * Gives the angle between the way the bot is facing and another point (bounds unknown, see {@link #realHeadingError(double, double)})
     *
     * @param wayX x coord of query point
     * @param wayY y coord of query point
     * @return angle between heading and given point
     */
    private double headingError(double wayX, double wayY) {
        return angleFromHere(wayX, wayY) - fieldHeading();
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

    public double fieldHeading() {
        return yawWraparoundAhead();
    }

    /**
     * @return the robot's X position in relation to its starting position(right positive)
     * typically facing away from opposing alliance station
     */
    public double fieldX() {
        return robotTranslation.getX();
    }

    /**
     * @return the robot's Y position in relation to its starting position(away positive)
     * typically facing away from opposing alliance station
     */
    public double fieldY() {
        return robotTranslation.getY();
    }

    /**
     * Gets the yaw of the bot and wraps it on the bound -180 to 180
     *
     * @return wrapped yaw val
     */
    public double yawWraparoundAhead() {
        return UtilFunctions.mathematicalMod(yawRel() + 180, 360) - 180;
    }


    /**
     * Yaw since last restart
     * 
     * @return yaw since last restart
     */
    public double yawRel() { //return relative(to start) yaw of pigeon
        updatePigeon();
        return (ypr[0] - startYaw);
    }

    /**
     * Updates the Pigeon IMU data
     */
    public void updatePigeon() {
        pigeon.getYawPitchRoll(ypr);
    }

    /**
     * Resets the Pigeon IMU
     */
    public void resetPigeon() {
        updatePigeon();
        startypr = ypr;
        startYaw = yawAbs();
    }

    /**
    * Zeroes the encoders at their current position
    */
    public void resetEncoders() {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            driver.leaderL.getEncoder().setPosition(0);
            driver.leaderR.getEncoder().setPosition(0);
        } else {
            driver.leaderLTalon.setSelectedSensorPosition(0);
            driver.leaderRTalon.setSelectedSensorPosition(0);
        }
    }

    /**
     * Resets all orienting to zeroes.
     *
     * @param pose ignored
     * @param rotation ignored
     */
    public void resetOdometry(Pose2d pose, Rotation2d rotation){
        //odometer.resetPosition(pose, rotation);
        resetPigeon();
        resetEncoders();
    }

    /**
     * gets the absolute yaw of the pigeon since last zeroing event (startup and {@link RobotTelemetry#resetPigeon() reset})
     *
     * @return absolute yaw of pigeon
     */
    public double yawAbs() {  //get absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }

    //TODO implement for falcos
    public double getRPMLeft() {
        return (driver.leaderL.getEncoder().getVelocity()) / 9;
    }

    public double getRPMRight() {
        return (driver.leaderR.getEncoder().getVelocity()) / 9;
    }

    //TODO implement for falcos
    public double getRotationsLeft() {
        return (driver.leaderL.getEncoder().getPosition()) / 9;
    }

    public double getRotationsRight() {
        return (driver.leaderR.getEncoder().getPosition()) / 9;
    }

    /**
     * Gets the meters traveled by the left encoder
     *
     * @return wheel meters traveled
     */
    public double getMetersLeft() {
        return Units.feetToMeters(getRotationsLeft() * UtilFunctions.wheelCircumference() / 12);
    }

    /**
     * Gets the meters traveled by the right encoder
     *
     * @return wheel meters traveled
     */
    public double getMetersRight() {
        return Units.feetToMeters(getRotationsRight() * UtilFunctions.wheelCircumference() / 12);
    }

    /**
     * creates pigeon, heading pid, and odometer
     */
    @Override
    public void init() {
        resetEncoders();
        pigeon = new PigeonIMU(RobotMap.PIGEON);
        headingPID = new PIDController(RobotNumbers.HEADING_P, RobotNumbers.HEADING_I, RobotNumbers.HEADING_D);
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(yawAbs()), new Pose2d(0, 0, new Rotation2d()));
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
        //updateGeneric();
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
        if (RobotToggles.ENABLE_IMU) {
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(yawAbs())), getMetersLeft(), getMetersRight());
            robotTranslation = robotPose.getTranslation();
            robotRotation = robotPose.getRotation();
        }
    }
}