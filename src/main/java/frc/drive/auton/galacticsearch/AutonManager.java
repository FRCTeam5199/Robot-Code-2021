package frc.drive.auton.galacticsearch;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.Point;
import frc.robot.Robot;
import frc.robot.RobotSettings;
import frc.telemetry.RobotTelemetry;

import java.io.IOException;
import java.nio.file.Path;

import static frc.robot.Robot.ballPhoton;

/**
 * Used for the galactic search challenge which includes automatically determining a path to take at enable-time.
 * <p>
 * Requirements: {@link frc.robot.RobotSettings#ENABLE_VISION} {@link frc.robot.RobotSettings#ENABLE_IMU}
 */
public class AutonManager extends AbstractAutonManager {
    private final RobotTelemetry telem;
    private final RamseteController controller = new RamseteController();
    private Trajectory trajectory = new Trajectory();

    public AutonManager(DriveManager driveManager) {
        super(driveManager);
        telem = DRIVING_CHILD.guidance;
        init();
    }

    @Override
    public void init() {
        DRIVING_CHILD.init();
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {
        Trajectory.State goal = trajectory.sample(timer.get());
        if (RobotSettings.ENABLE_IMU) {
            telem.updateAuton();
            System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
            ChassisSpeeds chassisSpeeds = controller.calculate(telem.robotPose, goal);
            DRIVING_CHILD.drivePure(chassisSpeeds.vxMetersPerSecond, chassisSpeeds.omegaRadiansPerSecond);
        }
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

    /**
     * gathers data points (yaw, apparent size) from {@link Robot#ballPhoton} and plots them against expected values.
     * However, given the fact that the robot wont always be exactly in the right place and that the auton paths will
     * have a bit of tolerance to them, we need a flexible solution. Introducing {@link #sumOfSquares(Point[], Point[])
     * Least Sum of Squares regression}! It takes the expected layout with the smallest error and runs that path
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
        Path routinePath = Filesystem.getDeployDirectory().toPath().resolve("paths/" + (path.PATH_FILE_LOCATION).trim() + ".wpilib.json");
        try {
            trajectory = TrajectoryUtil.fromPathweaverJson(routinePath);
        } catch (IOException e) {
            DriverStation.reportError("Unable to open trajectory: " + routinePath, e.getStackTrace());
        }
        timer.start();
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    /**
     * This is the method that organizes the path comparisons using {@link #sumOfSquares(Point[], Point[])} to calculate
     * the error.
     *
     * @param pointData the (perceived yaw, apparent size) array size 3 points of observations
     * @return the path with the smallest error from the passed points
     */
    private static GalacticSearchPaths getPath(Point[] pointData) {
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
        return bestPath;
    }

    /**
     * takes the distance between each point and squares it and returns the sum of the square errors
     *
     * @param guesses the perceived points
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