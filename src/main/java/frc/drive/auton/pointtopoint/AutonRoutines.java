package frc.drive.auton.pointtopoint;

import edu.wpi.first.wpilibj.util.Units;
import frc.drive.auton.Point;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is where we store paths for point to point auton. Other autons use pathweaver to basically do this except a lot
 * better but when it comes to debugging this is a solid auton. Points are in meters and speed is relative and unitless
 */
public enum AutonRoutines {
    GO_FORWARD_GO_BACK(
            new AutonWaypoint(new Point(0, 0), 1),
            new AutonWaypoint(new Point(3, 0), 1),
            new AutonWaypoint(new Point(3, -3), 1),
            new AutonWaypoint(new Point(0, 0), 1)
        /*new AutonWaypoint(new Point(0, 0), 1),
        new AutonWaypoint(new Point(Units.feetToMeters(20), 0), 1),
        new AutonWaypoint(new Point(0, Units.feetToMeters(9)), 1),
        new AutonWaypoint(new Point(Units.feetToMeters(20), Units.feetToMeters(9)), 1)*/
    ),
    BARREL_RACING_PATH_ONE( //Doesn't work yet, but the numbers are here. -Sterling
            new AutonWaypoint(new Point(Units.feetToMeters(4.084849326482433), Units.feetToMeters(-8.00097208721011)), 1), //Starting Position
            new AutonWaypoint(new Point(Units.feetToMeters(10.625607554506319), Units.feetToMeters(-7.042771837244827)), 1), //Approach Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(14.125121510901264), Units.feetToMeters(-9.917372587140676)), 1), //Start Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(11.04221635883905), Units.feetToMeters(-11.292181641438688)), 1), //Mid Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(12.125399250104152), Units.feetToMeters(-8.500902652409389)), 1), //End Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(17.957922510762394), Units.feetToMeters(-7.501041522010833)), 1),
            new AutonWaypoint(new Point(Units.feetToMeters(21.45743646715734), Units.feetToMeters(-6.042910706846271)), 1), //Start Loop 2
            new AutonWaypoint(new Point(Units.feetToMeters(21.290792945424247), Units.feetToMeters(-3.501597000416609)), 1), //Mid Loop 2
            new AutonWaypoint(new Point(Units.feetToMeters(18.332870434661853), Units.feetToMeters(-5.75128454381336)), 1), //End Loop 2
            new AutonWaypoint(new Point(Units.feetToMeters(22.37397583668935), Units.feetToMeters(-10.750590195806138)), 1), //Start Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(26.49840299958339), Units.feetToMeters(-11.000555478405778)), 1), //LowerMid Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(26.62338564088321), Units.feetToMeters(-8.209276489376476)), 1), //UpperMid Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(22.54061935842244), Units.feetToMeters(-7.667685043743925)), 1), //End Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(17.416331065129842), Units.feetToMeters(-6.959450076378282)), 1),
            new AutonWaypoint(new Point(Units.feetToMeters(11.54214692403833), Units.feetToMeters(-6.001249826412999)), 1),
            new AutonWaypoint(new Point(Units.feetToMeters(4.084849326482433), Units.feetToMeters(-6.0845715872795445)), 1) //Ending Position
    ),
    BARREL_RACING_PATH_LOL( //Doesn't work yet, but the numbers are here. -Sterling
            new AutonWaypoint(new Point(Units.feetToMeters(4.084849326482433), Units.feetToMeters(-8.00097208721011)), 1), //Starting Position
            new AutonWaypoint(new Point(Units.feetToMeters(10.625607554506319), Units.feetToMeters(-7.042771837244827)), 1), //Approach Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(14.125121510901264), Units.feetToMeters(-9.917372587140676)), 1), //Start Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(11.04221635883905), Units.feetToMeters(-11.292181641438688)), 1), //Mid Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(12.125399250104152), Units.feetToMeters(-8.500902652409389)), 1), //End Loop 1
            new AutonWaypoint(new Point(Units.feetToMeters(17.957922510762394), Units.feetToMeters(-7.501041522010833)), 1),
            new AutonWaypoint(new Point(Units.feetToMeters(21.45743646715734), Units.feetToMeters(-6.042910706846271)), 1), //Start Loop 2
            new AutonWaypoint(new Point(Units.feetToMeters(21.290792945424247), Units.feetToMeters(-3.501597000416609)), 1), //Mid Loop 2
            new AutonWaypoint(new Point(Units.feetToMeters(18.332870434661853), Units.feetToMeters(-5.75128454381336)), 1), //End Loop 2
            new AutonWaypoint(new Point(Units.feetToMeters(22.37397583668935), Units.feetToMeters(-10.750590195806138)), 1), //Start Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(26.49840299958339), Units.feetToMeters(-11.000555478405778)), 1), //LowerMid Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(26.62338564088321), Units.feetToMeters(-8.209276489376476)), 1), //UpperMid Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(22.54061935842244), Units.feetToMeters(-7.667685043743925)), 1), //End Loop 3
            new AutonWaypoint(new Point(Units.feetToMeters(17.416331065129842), Units.feetToMeters(-6.959450076378282)), 1),
            new AutonWaypoint(new Point(Units.feetToMeters(11.54214692403833), Units.feetToMeters(-6.001249826412999)), 1),
            new AutonWaypoint(new Point(Units.feetToMeters(4.084849326482433), Units.feetToMeters(-6.0845715872795445)), 1) //Ending Position
    ),
    TRACE_CARPET_1_31_2021(
            new AutonWaypoint(new Point(0, 0), 1),
            new AutonWaypoint(new Point(7.494228280781437, 0), 1),
            new AutonWaypoint(new Point(7.494228280781437, -3.6531160166183034), 1),
            new AutonWaypoint(new Point(0, -3.6531160166183034), 1),
            new AutonWaypoint(new Point(0, 0), 1)
    ),

    CARPET_TEST_SLALOM(
            new AutonWaypoint(new Point(0.4858349134944604, -0.8050449256126393), 3),
            new AutonWaypoint(new Point(2.2549439188290363, -0.8585070109386842), 3),
            new AutonWaypoint(new Point(2.264664297979227, -2.447789001994746), 3),
            new AutonWaypoint(new Point(3.926849132661714, -2.4429288124196513), 3),
            new AutonWaypoint(new Point(3.9025481847862387, -0.8487866317884941), 3),
            new AutonWaypoint(new Point(5.603614536069486, -0.8244856839130191), 3),
            new AutonWaypoint(new Point(5.623055294369865, -2.4332084332694612), 3),
            new AutonWaypoint(new Point(3.926849132661714, -2.438068622844556), 3),
            new AutonWaypoint(new Point(3.9171287535115233, -0.8439264422133992), 3),
            new AutonWaypoint(new Point(2.2549439188290363, -0.8536468213635892), 3),
            new AutonWaypoint(new Point(2.264664297979227, -2.447789001994746), 3),
            new AutonWaypoint(new Point(0.5392969988205053, -2.423488054119271), 3)
    );

    /**
     * Holds all of the waypoints as specified in the constructor calls
     */
    public final ArrayList<AutonWaypoint> WAYPOINTS = new ArrayList<>();
    /**
     * keeps track of what point to attack. Should step forward when {@link Point#isWithin(double, Point)} is within a
     * suitable tolerance
     */
    public int currentWaypoint = 0;

    AutonRoutines(AutonWaypoint... waypoints) {
        WAYPOINTS.addAll(Arrays.asList(waypoints));
    }
}
