package frc.drive.auton;

import java.util.ArrayList;
import java.util.Arrays;

public enum AutonRoutines {
    GO_FORWARD_GO_BACK(
        new AutonWaypoint(new Point(0, 0), 1),
        new AutonWaypoint(new Point(5, 0), 1),
        new AutonWaypoint(new Point(0, 0), 1)
    ),
    BARREL_RACING_PATH_ONE( //Doesn't work yet, but the numbers are here. -Sterling
        new AutonWaypoint(new Point(4.084849326482433,-8.00097208721011)), //Starting Position
        new AutonWaypoint(new Point(10.625607554506319, -7.042771837244827)), //Approach Loop 1
        new AutonWaypoint(new Point(14.125121510901264, -9.917372587140676)), //Start Loop 1
        new AutonWaypoint(new Point(11.04221635883905,-11.292181641438688)), //Mid Loop 1
        new AutonWaypoint(new Point(12.125399250104152,-8.500902652409389)), //End Loop 1
        new AutonWaypoint(new Point(17.957922510762394,-7.501041522010833)),
        new AutonWaypoint(new Point(21.45743646715734,-6.042910706846271)), //Start Loop 2
        new AutonWaypoint(new Point(21.290792945424247,-3.501597000416609)), //Mid Loop 2
        new AutonWaypoint(new Point(18.332870434661853,-5.75128454381336)), //End Loop 2
        new AutonWaypoint(new Point(22.37397583668935,-10.750590195806138)), //Start Loop 3
        new AutonWaypoint(new Point(26.49840299958339,-11.000555478405778)), //LowerMid Loop 3
        new AutonWaypoint(new Point(26.62338564088321,-8.209276489376476)), //UpperMid Loop 3
        new AutonWaypoint(new Point(22.54061935842244,-7.667685043743925)), //End Loop 3
        new AutonWaypoint(new Point(17.416331065129842,-6.959450076378282)),
        new AutonWaypoint(new Point(11.54214692403833,-6.001249826412999)),
        new AutonWaypoint(new Point(4.084849326482433,-6.0845715872795445)) //Ending Position
    );

    public int currentWaypoint = 0;
    public final ArrayList<AutonWaypoint> WAYPOINTS = new ArrayList<>();

    AutonRoutines(AutonWaypoint... waypoints) {
        WAYPOINTS.addAll(Arrays.asList(waypoints));
    }
}
