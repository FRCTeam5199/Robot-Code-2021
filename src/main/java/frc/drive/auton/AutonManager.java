package frc.drive.auton;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
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

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];
    private double startYaw;
    private double feetDriven = 0;

    private PIDController headingPID;
    DifferentialDriveOdometry odometer;

    public AutonManager(AutonRoutines routine, DriveManager driveObject){
        this.routine = routine;
        DRIVING_CHILD = driveObject;
    }

    @Override
    public void init() {
        headingPID = new PIDController(RobotNumbers.HEADING_P, RobotNumbers.HEADING_I, RobotNumbers.HEADING_D);
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(DRIVING_CHILD.yawAbs()), new Pose2d(0, 0, new Rotation2d()));
        DRIVING_CHILD.resetPigeon();
        //leaderL.getEncoder().setPosition(0);
        //leaderR.getEncoder().setPosition(0);
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {
        updateGeneric();
        if (attackPoint(routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION), 1)){
            if (++routine.currentWaypoint >= routine.WAYPOINTS.size())
                throw new RuntimeException("Holy crap theres no way it worked.");
            attackPoint(routine.WAYPOINTS.get(routine.currentWaypoint).LOCATION.subtract(routine.WAYPOINTS.get(0).LOCATION), 1);
        }
    }

    @Override
    public void updateGeneric() {
        DRIVING_CHILD.robotPose = DRIVING_CHILD.odometer.update(new Rotation2d(Units.degreesToRadians(DRIVING_CHILD.yawAbs())), DRIVING_CHILD.getMetersLeft(), DRIVING_CHILD.getMetersRight());
        DRIVING_CHILD.robotTranslation = DRIVING_CHILD.robotPose.getTranslation();
        DRIVING_CHILD.robotRotation = DRIVING_CHILD.robotPose.getRotation();
        double[] dataElements = {DRIVING_CHILD.robotTranslation.getX(), DRIVING_CHILD.robotTranslation.getY()};
        feetDriven = (DRIVING_CHILD.getFeetLeft()+DRIVING_CHILD.getFeetRight())/2;
        //setPID(driveP.getDouble(RobotNumbers.drivebaseP), driveI.getDouble(RobotNumbers.drivebaseI), driveD.getDouble(RobotNumbers.drivebaseD), driveF.getDouble(RobotNumbers.drivebaseF));
        //setPID(0,0,0.000005,0.000001);
        //SmartDashboard.putNumber("Left Speed", DRIVING_CHILD.leaderL.getEncoder().getVelocity());
    }

    /**
     * "Attack"(drive towards) a point on the field. Units are in meters and its scary.
     *
     * @return Boolean representing whether the robot is within tolerance of the waypoint or not.
     */
    public boolean attackPoint(Point point, double speed) {
        //logic: use PID to drive in such a way that the robot's heading is adjusted towards the target as it moves forward
        //wait is this just pure pursuit made by an idiot?
        double rotationOffset = DRIVING_CHILD.headingPID.calculate(DRIVING_CHILD.headingErrorWraparound(point.X, point.Y), 0);
        Point here = new Point(DRIVING_CHILD.fieldX(), DRIVING_CHILD.fieldY());
        boolean inTolerance = here.isWithin(RobotNumbers.AUTON_TOLERANCE, point);
        if (!inTolerance) {
            DRIVING_CHILD.drive(RobotNumbers.AUTO_SPEED * speed, rotationOffset * RobotNumbers.AUTO_ROTATION_SPEED);
            // leaderL.set(0);
            // leaderR.set(0);
        } else {
            DRIVING_CHILD.drive(0, 0);
            // leaderL.set(0);
            // leaderR.set(0);
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
