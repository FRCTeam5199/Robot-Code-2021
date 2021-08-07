package frc.drive.auton.galacticsearch;

import frc.drive.AbstractDriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.Point;
import frc.misc.UserInterface;
import frc.vision.camera.BallPhoton;
import frc.vision.camera.IVision;

import static frc.robot.Robot.robotSettings;

/**
 * Used for the galactic search challenge which includes automatically determining a path to take at enable-time.
 * <p>
 * Requiremed: {@link frc.robot.robotconfigs.DefaultConfig#ENABLE_VISION } {@link frc.robot.robotconfigs.DefaultConfig#ENABLE_IMU
 * }
 */
public class AutonManager extends AbstractAutonManager {
    private IVision ballPhoton;

    public AutonManager(AbstractDriveManager driveManager) {
        super(driveManager);
    }

    @Override
    public void init() {
        if (robotSettings.ENABLE_VISION) {
            ballPhoton = BallPhoton.BALL_PHOTON;
        }
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateGeneric() {

    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    /**
     * gathers data points (yaw, apparent size) from {} and plots them against expected values. However, given the fact
     * that the robot wont always be exactly in the right place and that the auton paths will have a bit of tolerance to
     * them, we need a flexible solution. Introducing {@link #sumOfSquares(Point[], Point[]) Least Sum of Squares
     * regression}! It takes the expected layout with the smallest error and runs that path
     */
    @Override
    public void initAuton() {
        Point[] cringePoints = new Point[]{
                new Point(ballPhoton.getAngle(0), ballPhoton.getSize(0)),
                new Point(ballPhoton.getAngle(1), ballPhoton.getSize(1)),
                new Point(ballPhoton.getAngle(2), ballPhoton.getSize(2))
        };
        for (Point point : cringePoints)
            System.out.println("Heres what they told me: " + point);
        GalacticSearchPaths path = getPath(cringePoints);
        System.out.println("I chose" + path.name());
        UserInterface.smartDashboardPutString("Auton Path", path.name());
        autonPath = path;
        super.initAuton();
    }

    /**
     * This is the method that organizes the path comparisons using {@link #sumOfSquares(Point[], Point[])} to calculate
     * the error.
     *
     * @param pointData the (perceived yaw, apparent size) array size 3 points of observations
     * @return the path with the smallest error from the passed points
     * @throws IllegalStateException if the least error is {@literal >} 50, dont run for safety reasons
     */
    private static GalacticSearchPaths getPath(Point[] pointData) throws IllegalStateException {
        GalacticSearchPaths bestPath = null;
        double bestOption = Double.MAX_VALUE;
        System.out.print("Data in: ");
        for (int i = 0; i < 3; i++)
            System.out.print(pointData[i]);
        System.out.println();
        for (GalacticSearchPaths path : GalacticSearchPaths.values()) {
            System.out.print(path.name() + " ");
            double SOSQ = sumOfSquares(path.POINTS, pointData);
            if (SOSQ < bestOption) {
                bestOption = SOSQ;
                bestPath = path;
            }
        }
        if (bestOption > 500)
            throw new IllegalStateException("I dont see a path. For safety, I will not run " + bestPath);
        return bestPath;
    }

    /**
     * takes the distance between each point and squares it and returns the sum of the square errors
     *
     * @param guesses    the perceived points
     * @param testPoints the points to plot against
     * @return the sum of squares error
     */
    private static double sumOfSquares(Point[] guesses, Point[] testPoints) {
        double out = 0;
        for (int i = 0; i < Math.min(guesses.length, testPoints.length); i++) {
            System.out.print(guesses[i]);
            out += Math.pow(guesses[i].X - testPoints[i].X, 2);
            out += Math.pow(100 * (guesses[i].Y - testPoints[i].Y), 2);
        }
        System.out.println(" had " + out);
        return out;
    }
}