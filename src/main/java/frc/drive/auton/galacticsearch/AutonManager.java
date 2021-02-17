package frc.drive.auton.galacticsearch;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.Point;
import frc.robot.Robot;
import frc.telemetry.RobotTelemetry;

import java.io.IOException;
import java.nio.file.Path;

public class AutonManager extends AbstractAutonManager {
    private final Timer timer = new Timer();
    private Trajectory trajectory = new Trajectory();
    private final RobotTelemetry telem;
    private final RamseteController controller = new RamseteController();

    public AutonManager(DriveManager driveManager){
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
        telem.updateAuton();
        //RamseteCommand ramseteCommand = new RamseteCommand(Trajectory, () -> telem.robotPose, controller, DRIVING_CHILD.kinematics, DRIVING_CHILD::driveFPS);
        Trajectory.State goal = trajectory.sample(timer.get());
        System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
        ChassisSpeeds chassisSpeeds = controller.calculate(telem.robotPose, goal);
        DRIVING_CHILD.drivePure(chassisSpeeds.vxMetersPerSecond, chassisSpeeds.omegaRadiansPerSecond);

    }

    @Override
    public void updateGeneric() {

    }

    public AutonManager initAuton() {
        Point[] cringePoints = new Point[]{
                new Point(Robot.ballPhoton.getBallAngle(0), Robot.ballPhoton.getBallSize(0)),
                new Point(Robot.ballPhoton.getBallAngle(1), Robot.ballPhoton.getBallSize(1)),
                new Point(Robot.ballPhoton.getBallAngle(2), Robot.ballPhoton.getBallSize(2))
        };
        for (Point point : cringePoints)
            System.out.println("Heres what they told me: " + point);
        GalacticSearchPaths path = getPath(cringePoints);
        System.out.println("I chose" + path.name());
        Path routinePath = Filesystem.getDeployDirectory().toPath().resolve("paths/" + (path.PATH_FILE_LOCATION).trim() + ".wpilib.json");
        try {
            trajectory = TrajectoryUtil.fromPathweaverJson(routinePath);
        } catch(IOException e) {
            DriverStation.reportError("Unable to open trajectory: " + routinePath, e.getStackTrace());
        }
        timer.start();
        return this;
    }

    private static GalacticSearchPaths getPath(Point[] pointData) {
        GalacticSearchPaths bestPath = null;
        double bestOption = Double.MAX_VALUE;
        System.out.print("Data in: ");
        for (int i = 0; i < 3; i ++)
            System.out.print(pointData[i]);
        System.out.println();
        for (GalacticSearchPaths path : GalacticSearchPaths.values()){
            System.out.print(path.name() + " ");
            double SOSQ = sumOfSquares(path.POINTS, pointData);
            if (SOSQ < bestOption){
                bestOption = SOSQ;
                bestPath = path;
            }
        }
        return bestPath;
        /*for (double tolerance = 0.1; tolerance < 1000; tolerance += 0.1) {
            int matches = 0;
            for (GalacticSearchPaths path : GalacticSearchPaths.values())
                if (isMatch(pointData, path.POINTS, BASE_YAW_TOLERANCE * tolerance, BASE_AREA_TOLERANCE * tolerance))
                    matches++;
            if (matches == 1) {
                for (GalacticSearchPaths path : GalacticSearchPaths.values())
                    if (isMatch(pointData, path.POINTS, BASE_YAW_TOLERANCE * tolerance, BASE_AREA_TOLERANCE * tolerance))
                        return path;
                    throw new IllegalThreadStateException("If this happens, your fucked");
            }
        }
        throw new IllegalStateException("Could not find a matching path");*/
    }

    /*private static int countMatches(Point[] guesses, Point[][] testPoints, double toleranceX, double toleranceY) {
        int count = 0;
        for (Point[] testPoint : testPoints)
            if (isMatch(guesses, testPoint, toleranceX, toleranceY)) {
                count++;
                lastPoints = testPoint;
            }
        return count;
    }*/

    private static double sumOfSquares(Point[] guesses, Point[] testPoints) {
        double out = 0;
        for (int i = 0; i < 3; i++) {
            System.out.print(guesses[i]);
            out += Math.pow(guesses[i].X - testPoints[i].X, 2);
            out += Math.pow(100 * (guesses[i].Y - testPoints[i].Y), 2);
        }
        System.out.println(" had " + out);
        return out;
    }
}