package frc.drive.auton;

public class AutonWaypoint {
    public final Point LOCATION;
    public final double SPEED;
    public final AutonSpecialActions SPECIAL_ACTION;

    public AutonWaypoint(Point pos, double speed, AutonSpecialActions specialAction){
        LOCATION = pos;
        SPEED = speed;
        SPECIAL_ACTION = specialAction;
    }

    public AutonWaypoint(double x, double y, double speed, AutonSpecialActions action){
        this(new Point(x,y), speed, action);
    }
//----------------------------------------------------------------------------------------
//Anything below here has a default of no action.
//----------------------------------------------------------------------------------------
    public AutonWaypoint(Point pos, double speed){
        this(pos, speed, AutonSpecialActions.NONE);
    }

    public AutonWaypoint(double x, double y, double speed){
        this(new Point(x,y), speed);
    }
}
