package frc.drive.auton.followtrajectory;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.IAutonEnumPath;
import frc.telemetry.RobotTelemetry;

/**
 * Check back later for some fun and fresh auton routines!
 */
public class AutonManager extends AbstractAutonManager {
    private final RobotTelemetry telem;
    private final RamseteController controller = new RamseteController();
    private ChassisSpeeds chassisSpeeds;
    private Trajectory Trajectory;
    private final IAutonEnumPath autonPath;

    public AutonManager(IAutonEnumPath autonEnumPath, DriveManager driveObject) { //Routine should be in the form of "YourPath" (paths/YourPath.wpilib.json)
        super(driveObject);
        telem = DRIVING_CHILD.guidance;
        autonPath = autonEnumPath;
        init();
    }

    @Override
    public void init() {
        Trajectory = paths.get(autonPath);
        Transform2d transform = telem.robotPose.minus(Trajectory.getInitialPose());
        Trajectory = Trajectory.transformBy(transform);
        timer.stop();
        timer.reset();
        timer.start();
    }

    @Override
    public void updateTest() {
    }

    @Override
    public void updateTeleop() {
    }

    @Override
    public void updateAuton() {
        Trajectory.State goal = Trajectory.sample(timer.get());
        System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
        chassisSpeeds = controller.calculate(telem.robotPose, goal);
        DRIVING_CHILD.drivePure(chassisSpeeds.vxMetersPerSecond, chassisSpeeds.omegaRadiansPerSecond);
    }

    @Override
    public void updateGeneric() {
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
}