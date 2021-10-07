package frc.drive.auton;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.drive.auton.followtrajectory.Trajectories;
import frc.drive.auton.galacticsearch.GalacticSearchPaths;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.robot.Robot;

import java.io.IOException;
import java.util.HashMap;

import static frc.robot.Robot.robotSettings;

/**
 * If you have a custom auton that needs to be implemented, extend this class. Since every Auton Manager needs to have a
 * {@link DriveManagerStandard drivetrain} and a {@link Timer timer}, they are here along with {@link ISubsystem}.
 *
 * @see ISubsystem
 * @see frc.drive.auton.followtrajectory.AutonManager
 * @see frc.drive.auton.galacticsearch.AutonManager
 * @see frc.drive.auton.galacticsearchtest.AutonManager
 */
public abstract class AbstractAutonManager implements ISubsystem {
    /**
     * During development, we had a bug where upon auton enable, the root would vibe for 5 seconds before driving, To
     * prevent this, we statically load all possible trajectories at init-time here
     */
    protected static final HashMap<IAutonEnumPath, Trajectory> paths;

    static {
        paths = new HashMap<>();
        //TODO add barrel racing/other auton paths here
        for (GalacticSearchPaths path : GalacticSearchPaths.values()) {
            try {
                paths.put(path, TrajectoryUtil.fromPathweaverJson(Filesystem.getDeployDirectory().toPath().resolve("paths/" + path.getDeployLocation() + ".wpilib.json")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (Trajectories path : Trajectories.values()) {
            try {
                paths.put(path, TrajectoryUtil.fromPathweaverJson(Filesystem.getDeployDirectory().toPath().resolve("paths/" + path.getDeployLocation() + ".wpilib.json")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected final Timer timer = new Timer();
    protected final AbstractDriveManager DRIVING_CHILD;
    protected final RamseteController controller = new RamseteController();
    protected Trajectory trajectory;
    protected IAutonEnumPath autonPath;

    /**
     * Initializes the auton manager and stores the reference to the drivetrain object
     *
     * @param driveManager the drivetrain object created for the robot
     */
    protected AbstractAutonManager(AbstractDriveManager driveManager) {
        addToMetaList();
        DRIVING_CHILD = driveManager;
        init();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return DRIVING_CHILD.getSubsystemStatus() == SubsystemStatus.NOMINAL && DRIVING_CHILD.guidance.getSubsystemStatus() == SubsystemStatus.NOMINAL ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    /**
     * Runs the auton path. When complete, sets a flag in {@link frc.robot.robotconfigs.DefaultConfig#autonComplete} and
     * runs {@link #onFinish()}
     */
    @Override
    public void updateAuton() {
        if (!robotSettings.autonComplete) {
            Trajectory.State goal = trajectory.sample(timer.get());
            if (robotSettings.ENABLE_IMU) {
                System.out.println("I am currently at (" + DRIVING_CHILD.guidance.fieldX() + "," + DRIVING_CHILD.guidance.fieldY() + ")\nI am going to (" + goal.poseMeters.getX() + "," + goal.poseMeters.getY() + ")");
                DRIVING_CHILD.driveWithChassisSpeeds(controller.calculate(DRIVING_CHILD.guidance.robotPose, goal));
            }
            if (timer.get() > trajectory.getTotalTimeSeconds()) {
                onFinish();
            }
        }
    }

    /**
     * When the path finishes, we have flags to set, brakes to prime, and music to jam to
     */
    protected void onFinish() {
        robotSettings.autonComplete = true;
        if (robotSettings.ENABLE_MUSIC && !robotSettings.AUTON_COMPLETE_NOISE.equals("")) {
            DRIVING_CHILD.setBrake(true);
            Robot.chirp.loadMusic(robotSettings.AUTON_COMPLETE_NOISE);
            Robot.chirp.play();
        }
    }

    /**
     * On enable, unset finished flag, and prime the path and reset the timer
     */
    @Override
    public void initAuton() {
        robotSettings.autonComplete = false;
        trajectory = paths.get(autonPath);
        if (robotSettings.ENABLE_IMU) {
            DRIVING_CHILD.guidance.resetOdometry();
            if (trajectory != null) {
                Transform2d transform = DRIVING_CHILD.guidance.robotPose.minus(trajectory.getInitialPose());
                trajectory = trajectory.transformBy(transform);
            }
        }
        timer.stop();
        timer.reset();
        timer.start();
    }

    @Override
    public String getSubsystemName() {
        return "Auton manager";
    }
}