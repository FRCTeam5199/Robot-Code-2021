package frc.drive.auton.pointtopoint;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.drive.auton.Point;
import frc.drive.auton.RobotTelemetry;
import frc.misc.ISubsystem;
import frc.robot.RobotNumbers;

/**
 * Check back later for some fun and fresh auton routines!
 *
 * @author jojo2357
 */
public class AutonManager implements ISubsystem {
    private final AutonRoutines routine;
    private final DriveManager DRIVING_CHILD;
    private final RobotTelemetry telem;
    private PIDController headingPID;
    DifferentialDriveOdometry odometer;

    public AutonManager(AutonRoutines routine, DriveManager driveObject){
        this.routine = routine;
        this.routine.currentWaypoint = 0;
        DRIVING_CHILD = driveObject;
        telem = DRIVING_CHILD.guidance;
        init();
    }

    @Override
    public void init() {
        headingPID = new PIDController(RobotNumbers.HEADING_P, RobotNumbers.HEADING_I, RobotNumbers.HEADING_D);
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(telem.yawAbs()), new Pose2d(0, 0, new Rotation2d()));
        telem.resetPigeon();
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
        if (attackPoint(routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION), 1)){
            System.out.println("IN TOLERANCE");
            if (++routine.currentWaypoint < routine.WAYPOINTS.size())
                //throw new IllegalStateException("Holy crap theres no way it worked. This is illegal");
            attackPoint(routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION), 1);
        }
    }

    @Override
    public void updateGeneric() {
        telem.robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(telem.yawAbs())), telem.getMetersLeft(), telem.getMetersRight());
        telem.updateGeneric();
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
        // put("x", fieldX());
        // put("y", fieldY());
        // put("head", fieldHeading());
        // put("angleTo", angleToPos(x.getDouble(0), y.getDouble(0)));
        // // put("error", headingError(x.getDouble(0), y.getDouble(0)));
        // put("wrap", headingErrorWraparound(x.getDouble(0), y.getDouble(0)));
        // put("rotationOffset", rotationOffset);
        // SmartDashboard.putNumber("xDiff", xDiff);
        // SmartDashboard.putNumber("yDiff", yDiff);
        // SmartDashboard.putNumber("xpos", robotTranslation.getY());
        // SmartDashboard.putNumber("ypos", -robotTranslation.getX());
        // SmartDashboard.putNumber("angleTarget", angleTarget);
        // SmartDashboard.putNumber("heading", yawWraparound());
        // SmartDashboard.putNumber("abs", yawAbs());
        // SmartDashboard.putNumber("rotationOffset", -rotationOffset*RobotNumbers.autoRotationMultiplier); //number being fed into drive()
        // SmartDashboard.putNumber("rotationDifference", -(angleTarget-yawWraparound()));
        // SmartDashboard.putBoolean("inTolerance", inTolerance);
        // SmartDashboard.putNumber("left", getMetersLeft());
        // SmartDashboard.putNumber("right", getMetersRight());
        return inTolerance;
    }
}
