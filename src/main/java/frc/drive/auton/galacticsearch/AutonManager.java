package frc.drive.auton.galacticsearch;

import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.Point;
import frc.robot.Robot;

public class AutonManager extends AbstractAutonManager {
    private GalacticSearchPaths path;

    public AutonManager(DriveManager driveManager){
        super(driveManager);
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

    }

    @Override
    public void updateGeneric() {

    }

    public AutonManager initAuton() {
        path = getPath(new Point[]{
                new Point(Robot.ballPhoton.getBallAngle(0), Robot.ballPhoton.getBallSize(0)),
                new Point(Robot.ballPhoton.getBallAngle(1), Robot.ballPhoton.getBallSize(1)),
                new Point(Robot.ballPhoton.getBallAngle(2), Robot.ballPhoton.getBallSize(2))
        });
        System.out.println("I chose" + path.name());
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