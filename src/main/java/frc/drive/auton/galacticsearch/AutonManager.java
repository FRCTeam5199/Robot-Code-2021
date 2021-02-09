package frc.drive.auton.galacticsearch;

import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.Point;
import frc.vision.BallPhoton;

public class AutonManager extends AbstractAutonManager {
    private static final double BASE_YAW_TOLERANCE = 2;
    private static final double BASE_AREA_TOLERANCE = 0.15;
    private static Point[] lastPoints;
    DriveManager driveManager;
    private BallPhoton ballPhoton;
    private GalacticSearchPaths path;

    public AutonManager(DriveManager driveManager){
        super(driveManager);
    }

    @Override
    public void init() {
        driveManager.init();

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
                new Point(ballPhoton.getBallAngle(0), ballPhoton.getBallSize(0)),
                new Point(ballPhoton.getBallAngle(1), ballPhoton.getBallSize(1)),
                new Point(ballPhoton.getBallAngle(2), ballPhoton.getBallSize(2))
        });
        System.out.println("I chose" + path.name());
        return this;
    }

    private static GalacticSearchPaths getPath(Point[] pointData) {
        for (double tolerance = 0.1; tolerance < 10; tolerance += 0.1) {
            int matches = (countMatches(pointData, GalacticSearchPaths.ALL_POINTS, BASE_YAW_TOLERANCE * tolerance, BASE_AREA_TOLERANCE * tolerance));
            if (matches > 1)
                throw new IllegalStateException("Too many possibilities");
            if (matches == 1) {
                return GalacticSearchPaths.getFromPoints(lastPoints);
            }
        }
        throw new IllegalStateException("Could not find a matching path");
    }

    private static int countMatches(Point[] guesses, Point[][] testPoints, double toleranceX, double toleranceY) {
        int count = 0;
        for (Point[] testPoint : testPoints)
            if (isMatch(guesses, testPoint, toleranceX, toleranceY)) {
                count++;
                lastPoints = testPoint;
            }
        return count;
    }

    private static boolean isMatch(Point[] guesses, Point[] testPoints, double toleranceX, double toleranceY) {
        boolean out = true;
        double correctionX = guesses[0].X - testPoints[0].X;
        double correctionY = guesses[0].Y - testPoints[0].Y;
        for (int i = 1; i < 3; i++) {
            out &= guesses[i].X - testPoints[i].X >= correctionX - toleranceX && guesses[i].X - testPoints[i].X <= correctionX + toleranceX;
            out &= guesses[i].Y - testPoints[i].Y >= correctionY - toleranceY && guesses[i].Y - testPoints[i].Y <= correctionY + toleranceY;
        }
        return out;
    }
}