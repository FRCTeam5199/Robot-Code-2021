package frc.drive.auton.galacticsearchscam;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.galacticsearch.GalacticSearchPaths;
import frc.robot.RobotSettings;
import frc.telemetry.RobotTelemetry;

/**
 * This is for running a preselected galactic search path
 */
public class AutonManager extends AbstractAutonManager {
    private final RobotTelemetry telem;
    private final RamseteController controller = new RamseteController();
    private Trajectory trajectory = new Trajectory();

    public AutonManager(DriveManager driveManager) {
        super(driveManager);
        telem = DRIVING_CHILD.guidance;
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

    @Override
    public void updateAuton() {
        if (!RobotSettings.autonComplete) {
            Trajectory.State goal = trajectory.sample(timer.get());
            if (timer.get() > trajectory.getTotalTimeSeconds()) {
                RobotSettings.autonComplete = true;
            }
            if (RobotSettings.ENABLE_IMU) {
                System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
                ChassisSpeeds chassisSpeeds = controller.calculate(telem.robotPose, goal);
                DRIVING_CHILD.drivePure(Units.metersToFeet(chassisSpeeds.vxMetersPerSecond), chassisSpeeds.omegaRadiansPerSecond);
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
        RobotSettings.autonComplete = false;
        trajectory = paths.get(GalacticSearchPaths.PATH_A_RED);
        Transform2d transform = telem.robotPose.minus(trajectory.getInitialPose());
        trajectory = trajectory.transformBy(transform);
        if (RobotSettings.ENABLE_IMU) {
            telem.resetOdometry();
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