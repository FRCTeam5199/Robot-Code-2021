package frc.drive.auton.pointtopoint;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.Point;
import frc.robot.RobotNumbers;
import frc.telemetry.RobotTelemetry;

/**
 * Check back later for some fun and fresh auton routines!
 *
 * @author jojo2357
 */
public class AutonManager extends AbstractAutonManager {
    private final AutonRoutines routine;
    private final DriveManager DRIVING_CHILD;
    private final RobotTelemetry telem;
    private DifferentialDriveOdometry odometer;
    private PIDController headingPID;

    public AutonManager(AutonRoutines routine, DriveManager driveObject) {
        super(driveObject);
        this.routine = routine;
        this.routine.currentWaypoint = 0;
        DRIVING_CHILD = driveObject;
        telem = DRIVING_CHILD.guidance;
        init();
    }

    @Override
    public void init() {
        headingPID = new PIDController(RobotNumbers.HEADING_P, RobotNumbers.HEADING_I, RobotNumbers.HEADING_D);
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(telem.imu.absoluteYaw()), new Pose2d(0, 0, new Rotation2d()));
        telem.imu.resetOdometry();
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {
        if (routine.currentWaypoint >= routine.WAYPOINTS.size())
            return;
        updateGeneric();
        System.out.println("Home is: " + routine.WAYPOINTS.get(0).LOCATION + " and im going to " + routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION));
        if (attackPoint(routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION), 1)) {
            System.out.println("IN TOLERANCE");
            if (++routine.currentWaypoint < routine.WAYPOINTS.size())
                //throw new IllegalStateException("Holy crap theres no way it worked. This is illegal");
                attackPoint(routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION), 1);
        }
    }

    @Override
    public void updateGeneric() {
        telem.robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(telem.imu.absoluteYaw())), telem.getMetersLeft(), telem.getMetersRight());
        telem.updateGeneric();
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    /**
     * "Attack"(drive towards) a point on the field. Units are in meters and its scary.
     *
     * @param point the {@link Point point on the field} to attack
     * @param speed the speed at which to do it
     * @return true if point has been attacked
     */
    public boolean attackPoint(Point point, double speed) {
        double rotationOffset = telem.headingPID.calculate(telem.realHeadingError(point.X, point.Y));
        Point here = new Point(telem.fieldX(), telem.fieldY());
        System.out.println("I am at " + here + " and trying to turn " + rotationOffset);
        boolean inTolerance = here.isWithin(RobotNumbers.AUTON_TOLERANCE, point);
        if (!inTolerance) {
            DRIVING_CHILD.drivePure(RobotNumbers.AUTO_SPEED * speed, -rotationOffset * RobotNumbers.AUTO_ROTATION_SPEED);
        } else {
            DRIVING_CHILD.drive(0, 0);
        }
        return inTolerance;
    }
}
