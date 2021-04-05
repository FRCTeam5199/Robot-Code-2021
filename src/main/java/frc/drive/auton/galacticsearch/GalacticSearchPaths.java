package frc.drive.auton.galacticsearch;

import frc.drive.auton.IAutonEnumPath;
import frc.drive.auton.Point;

public enum GalacticSearchPaths implements IAutonEnumPath {
    PATH_A_RED(
            new Point[]{
                    new Point(-.4, .48),
                    new Point(9.5, .14),
                    new Point(-14.2, 0.11)
            }
            , "PathARed"
    ),
    PATH_A_BLUE(
            new Point[]{
                    new Point(15.7, .1),
                    new Point(-6.9, 0.05),
                    new Point(.38, 0.03)
            }
            , "PathABlue"
    ),
    PATH_B_RED(
            new Point[]{
                    new Point(-18.2, .62),
                    new Point(10.2, .14),
                    new Point(-5.3, 0.06)
            }
            , "PathBRed"
    ),
    PATH_B_BLUE(
            new Point[]{
                    new Point(7.8, 0.08),
                    new Point(-4.7, 0.04),
                    new Point(5, 0.02)
            }
            , "PathBBlue"
    );

    public static Point[][] ALL_POINTS;
    private static int registerIndex = 0;
    public final String PATH_FILE_LOCATION;
    public final Point[] POINTS;

    /**
     * Determines the path based on the given points
     *
     * @param points a given set of points
     * @return the path determined
     */
    public static GalacticSearchPaths getFromPoints(Point[] points) {
        for (GalacticSearchPaths path : GalacticSearchPaths.values())
            if (path.POINTS == points)
                return path;
        throw new IllegalStateException("Could not find a path with those points");
    }

    GalacticSearchPaths(Point[] points, String pathloc) {
        POINTS = points;
        PATH_FILE_LOCATION = pathloc;
        addMyPoint();
    }

    void addMyPoint() {
        if (ALL_POINTS == null)
            ALL_POINTS = new Point[4][3];
        System.arraycopy(POINTS, 0, ALL_POINTS[registerIndex], 0, 3);
        registerIndex++;
    }

    @Override
    public String getDeployLocation() {
        return PATH_FILE_LOCATION;
    }
}
