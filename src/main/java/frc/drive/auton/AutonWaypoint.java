package frc.drive.auton;



public class AutonWaypoint {
    public final Point LOCATION; /** where its next point is */
    public final double SPEED; /** speed of the robot */
    public final AutonSpecialActions SPECIAL_ACTION; /** special action we want it to do if any */

    public AutonWaypoint(double x, double y, double speed, AutonSpecialActions action) /**taking it to a point with special action*/ {
        this(new Point(x, y), speed, action);
    }

    public AutonWaypoint(Point pos, double speed, AutonSpecialActions specialAction) {
        LOCATION = pos;
        SPEED = speed;
        SPECIAL_ACTION = specialAction;
    }

    public AutonWaypoint(double x, double y, double speed)/**taking it to a point no special action*/ {
        this(new Point(x, y), speed);
    }

    //----------------------------------------------------------------------------------------
//Anything below here has a default of no action.
//----------------------------------------------------------------------------------------
    public AutonWaypoint(Point pos, double speed) {
        this(pos, speed, AutonSpecialActions.NONE);
    }

    public AutonWaypoint(Point pos) {
        this(pos, 1);
    }
}
