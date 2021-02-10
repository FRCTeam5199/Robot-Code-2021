package frc.drive.auton.galacticsearch;

import frc.drive.auton.Point;

public enum GalacticSearchPaths {
    PATH_A_RED(
            new Point[]{
                    new Point(-0.3, 0.75),
                    new Point(-14.4, 0.22),
                    new Point(17.4, 0.12)
            }
            , ""
    ),
    PATH_A_BLUE(
            new Point[]{
                    new Point(-21.8, 0.18),
                    new Point(6, 0.1),
                    new Point(-2.1, 0.07)
            }
            , ""
    ),
    PATH_B_RED(
            new Point[]{
                    new Point(23.5, .8),
                    new Point(-14, .25),
                    new Point(7.15, 0.08)
            }
            , ""
    ),
    PATH_B_BLUE(
            new Point[]{
                    new Point(-12.4, 0.15),
                    new Point(5.7, 0.06),
                    new Point(-8.2, 0.04)
            }
            , ""
    );

    public static Point[][] ALL_POINTS;
    private static int registerIndex = 0;
    public final String PATH_FILE_LOCATION;
    public final Point[] POINTS;

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
}
