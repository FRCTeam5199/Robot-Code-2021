package frc.drive.auton.butbetternow;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.drive.DriveManager;
import frc.drive.auton.RobotTelemetry;
import frc.misc.ISubsystem;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Check back later for some fun and fresh auton routines!
 */
public class AutonManager implements ISubsystem {
    private final DriveManager DRIVING_CHILD;
    private final RobotTelemetry telem;
    private final Path routinePath;
    private ChassisSpeeds chassisSpeeds;
    private RamseteController controller = new RamseteController();
    private Trajectory Trajectory = new Trajectory();
    private Timer timer = new Timer();

    public AutonManager(String routine, DriveManager driveObject) { //Routine should be in the form of "YourPath" (paths/YourPath.wpilib.json)
        routinePath = Filesystem.getDeployDirectory().toPath().resolve("paths/" + (routine).trim() + ".wpilib.json");
        DRIVING_CHILD = driveObject;
        telem = DRIVING_CHILD.guidance;
        init();
    }

    @Override
    public void init() {
        try {
            Trajectory = TrajectoryUtil.fromPathweaverJson(routinePath);
            telem.resetOdometry(Trajectory.getInitialPose(), telem.yawAbs());
            Trajectory.relativeTo(telem.robotPose);
        } catch (IOException e) {
            DriverStation.reportError("Unable to open trajectory: " + routinePath, e.getStackTrace());
        }
        timer.reset();
        timer.start();
        //System.out.println("Starting timer.");
    }

    @Override
    public void updateTest() { }

    @Override
    public void updateTeleop() { }

    @Override
    public void updateAuton() {
        telem.updateAuton();
        //RamseteCommand ramseteCommand = new RamseteCommand(Trajectory, () -> telem.robotPose, controller, DRIVING_CHILD.kinematics, DRIVING_CHILD::driveFPS);
        Trajectory.State goal = Trajectory.sample(timer.get());
        chassisSpeeds = controller.calculate(telem.robotPose, goal);
        DRIVING_CHILD.drivePure(chassisSpeeds.vxMetersPerSecond, chassisSpeeds.omegaRadiansPerSecond);
    }

    @Override
    public void updateGeneric() { }
}