package frc.telemetry;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.misc.UtilFunctions;

import static frc.robot.Robot.robotSettings;

public class RobotTelemetryStandard extends AbstractRobotTelemetry implements ISubsystem {
    private final boolean DEBUG = true;
    private final NetworkTableEntry robotLocation = UserInterface.ROBOT_LOCATION.getEntry();
    public DifferentialDriveOdometry odometer;

    public RobotTelemetryStandard(AbstractDriveManager driver) {
        super(driver);
        if (!(driver instanceof DriveManagerStandard))
            throw new IllegalArgumentException("Wrong drive manager for this telem");
    }

    /**
     * creates pigeon, heading pid, and odometer
     */
    @Override
    public void init() {
        super.init();
        if (imu != null) {
            odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(imu.absoluteYaw())); //getRotations should be in distance traveled since start (inches)
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), Units.inchesToMeters(((DriveManagerStandard) driver).leaderL.getRotations()), Units.inchesToMeters(((DriveManagerStandard) driver).leaderR.getRotations()));
        }
    }

    /**
     * updates the robot orientation based on the IMU and distance traveled
     */
    @Override
    public void updateGeneric() {
        if (robotSettings.ENABLE_IMU) {
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(imu.absoluteYaw())), Units.inchesToMeters(((DriveManagerStandard) driver).leaderL.getRotations()), Units.inchesToMeters(((DriveManagerStandard) driver).leaderR.getRotations()));
            super.updateGeneric();
        }
        if (DEBUG) {
            robotLocation.setString("(" + odometer.getPoseMeters().getX() + ", " + odometer.getPoseMeters().getY() + ")");
        }
    }

    /**
     * Resets all orienting to zeroes.
     */
    public void resetOdometry() {
        if (robotSettings.ENABLE_IMU) {
            odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(imu.absoluteYaw()));
            imu.resetOdometry();
        }
        driver.resetDriveEncoders();
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {
        updateGeneric();
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

    @Override
    public void initTest() {
        resetOdometry();
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
    public double headingError(double wayX, double wayY) {
        return angleFromHere(wayX, wayY) - imu.yawWraparoundAhead();
    }

    /**
     * Calculates the angle in coordinate space between here and a given coordinates
     *
     * @param wayX x coord of query point
     * @param wayY y coord of query point
     * @return the angle between the heading and the point passed in
     */
    public double angleFromHere(double wayX, double wayY) {
        return Math.toDegrees(Math.atan2(wayY - fieldY(), wayX - fieldX()));
    }
}