package frc.drive.auton.galacticsearchscam;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.drive.DriveManagerSwerve;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.galacticsearch.GalacticSearchPaths;

import static frc.robot.Robot.robotSettings;

/**
 * This is for running a preselected galactic search path
 */
public class AutonManager extends AbstractAutonManager {
    private final RamseteController controller = new RamseteController();
    private Trajectory trajectory = new Trajectory();

    public AutonManager(AbstractDriveManager driveManager) {
        super(driveManager);
        init();
    }

    @Override
    public void init() {
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    /**
     * Runs the auton
     */
    @Override
    public void updateAuton() {
        if (!robotSettings.autonComplete) {
            //TrajectoryUtil.serializeTrajectory();
            Trajectory.State goal = trajectory.sample(timer.get());
            if (robotSettings.ENABLE_IMU) {
                System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
                ChassisSpeeds chassisSpeeds = controller.calculate(telem.robotPose, goal);
                if (DRIVING_CHILD instanceof DriveManagerStandard)
                    ((DriveManagerStandard) DRIVING_CHILD).drivePure(Units.metersToFeet(chassisSpeeds.vxMetersPerSecond), chassisSpeeds.omegaRadiansPerSecond * 2);
                else if (DRIVING_CHILD instanceof DriveManagerSwerve) {
                    //TODO implement this
                }
            }
            if (timer.get() > trajectory.getTotalTimeSeconds()) {
                onFinish();
            }
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
        robotSettings.autonComplete = false;
        trajectory = paths.get(GalacticSearchPaths.PATH_B_BLUE);
        if (robotSettings.ENABLE_IMU) {
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