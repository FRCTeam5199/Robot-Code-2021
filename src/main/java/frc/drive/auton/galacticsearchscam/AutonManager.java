package frc.drive.auton.galacticsearchscam;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.robot.RobotToggles;
import frc.telemetry.RobotTelemetry;

import java.io.IOException;
import java.nio.file.Path;

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
        DRIVING_CHILD.init();
        RobotToggles.autonComplete = false;
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {
        if (!RobotToggles.autonComplete) {
            telem.updateAuton();
            //RamseteCommand ramseteCommand = new RamseteCommand(Trajectory, () -> telem.robotPose, controller, DRIVING_CHILD.kinematics, DRIVING_CHILD::driveFPS);
            Trajectory.State goal = trajectory.sample(timer.get());
            System.out.println("I am currently at (" + telem.fieldX() + "," + telem.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
            ChassisSpeeds chassisSpeeds = controller.calculate(telem.robotPose, goal);
            DRIVING_CHILD.drivePure(chassisSpeeds.vxMetersPerSecond, chassisSpeeds.omegaRadiansPerSecond);
            if (timer.get() > trajectory.getTotalTimeSeconds()) {
                RobotToggles.autonComplete = true;
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
        RobotToggles.autonComplete = false;
        Path routinePath = Filesystem.getDeployDirectory().toPath().resolve("paths/PathARed.wpilib.json");
        try {
            trajectory = TrajectoryUtil.fromPathweaverJson(routinePath);
        } catch (IOException e) {
            DriverStation.reportError("Unable to open trajectory: " + routinePath, e.getStackTrace());
        }
        timer.start();
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }
}