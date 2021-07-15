package frc.drive.auton.galacticsearch;

import frc.drive.auton.IAutonEnumPath;
import frc.drive.auton.Point;

/**
 * The paths for {@link AutonManager galactic search auton}. These are special becuase they store predicted ball
 * positions for the sum of squares algorithm
 */
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

    public final String PATH_FILE_LOCATION;
    public final Point[] POINTS;

    /**
     * The constructor. No special processing done here
     *
     * @param points  The predicted location of the balls in the camera
     * @param pathloc The name of the file holding the path (can be changed independently of the enum name)
     */
    GalacticSearchPaths(Point[] points, String pathloc) {
        POINTS = points;
        PATH_FILE_LOCATION = pathloc;
    }

    @Override
    public String getDeployLocation() {
        return PATH_FILE_LOCATION;
    }
}
