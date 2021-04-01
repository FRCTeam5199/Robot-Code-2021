package frc.drive.auton.followtrajectory;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.IAutonEnumPath;

import static frc.robot.Robot.RobotSettings;

/**
 * Check back later for some fun and fresh auton routines!
 */
public class AutonManager extends AbstractAutonManager {
    private final RamseteController controller = new RamseteController();
    private final IAutonEnumPath autonPath;
    private Trajectory trajectory;

    public AutonManager(IAutonEnumPath autonEnumPath, DriveManager driveObject) { //Routine should be in the form of "YourPath" (paths/YourPath.wpilib.json)
        super(driveObject);
        autonPath = autonEnumPath;
        init();
    }

    /**
     * Sets the path, starts the timers
     */
    @Override
    public void init() {

    }

    @Override
    public void updateTest() {
    }

    @Override
    public void updateTeleop() {
    }

    @Override
    public void updateAuton() {
        Trajectory.State goal = trajectory.sample(timer.get());
        if (RobotSettings.ENABLE_IMU) {
            System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
            ChassisSpeeds chassisSpeeds = controller.calculate(telem.robotPose, goal);
            DRIVING_CHILD.drivePure(Units.metersToFeet(chassisSpeeds.vxMetersPerSecond), chassisSpeeds.omegaRadiansPerSecond);
        }
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
        trajectory = paths.get(autonPath);
        if (RobotSettings.ENABLE_IMU) {
            telem.resetOdometry();
            Transform2d transform = telem.robotPose.minus(trajectory.getInitialPose());
            trajectory = trajectory.transformBy(transform);
        }
        timer.stop();
        timer.reset();
        timer.start();
    }

    @Override
    public void initDisabled() {
    }

    @Override
    public void initGeneric() {

    }
}