package frc.drive.auton.galacticsearch;

import frc.drive.auton.IAutonEnumPath;
import frc.drive.auton.Point;

public enum GalacticSearchPaths implements IAutonEnumPath {
    PATH_A_RED(
            new Point[]{
                    new Point(2.7, 0.94),
                    new Point(12.26, 0.17),
                    new Point(-17.37, 0.11)
            }
            , "PathARed"
    ),
    PATH_A_BLUE(
            new Point[]{
                    new Point(15, 0.06),
                    new Point(-6.4, 0.03),
                    new Point(-0.25, 0.01)
            }
            , "PathABlue"
    ),
    PATH_B_RED(
            new Point[]{
                    new Point(-21.2, 1.09),
                    new Point(12.46, .14),
                    new Point(-6.88, 0.04)
            }
            , "PathBRed"
    ),
    PATH_B_BLUE(
            new Point[]{
                    new Point(6.3, 0.07),
                    new Point(-6.9, 0.02),
                    new Point(3.5, 0.01)
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
