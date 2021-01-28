package frc.drive.auton;

import java.util.ArrayList;
import java.util.Arrays;

public enum AutonRoutines {
    GO_FORWARD_GO_BACK(
            new AutonWaypoint(new Point(0, 0), 1),
            new AutonWaypoint(new Point(5, 0), 1)
    );

    public final ArrayList<AutonWaypoint> WAYPOINTS = new ArrayList<>();

    AutonRoutines(AutonWaypoint...waypoints){
        WAYPOINTS.addAll(Arrays.asList(waypoints));
    }
}
