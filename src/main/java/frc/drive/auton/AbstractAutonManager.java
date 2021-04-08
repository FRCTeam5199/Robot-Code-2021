package frc.drive.auton;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.drive.auton.followtrajectory.Trajectories;
import frc.drive.auton.galacticsearch.GalacticSearchPaths;
import frc.misc.ISubsystem;
import frc.robot.Robot;
import frc.telemetry.RobotTelemetry;

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
 * @see frc.drive.auton.galacticsearchscam.AutonManager
 */
public abstract class AbstractAutonManager implements ISubsystem {
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
    protected final RobotTelemetry telem;

    /**
     * Initializes the auton manager and stores the reference to the drivetrain object
     *
     * @param driveManager the drivetrain object created for the robot
     */
    protected AbstractAutonManager(AbstractDriveManager driveManager) {
        addToMetaList();
        DRIVING_CHILD = driveManager;
        if (DRIVING_CHILD.guidance != null)
            telem = DRIVING_CHILD.guidance;
        else
            telem = null;
    }

    protected void onFinish() {
        robotSettings.autonComplete = true;
        if (robotSettings.ENABLE_MUSIC && !robotSettings.AUTON_COMPLETE_NOISE.equals("")) {
            DRIVING_CHILD.setBrake(true);
            Robot.chirp.loadMusic(robotSettings.AUTON_COMPLETE_NOISE);
            Robot.chirp.play();
        }
    }

    @Override
    public String getSubsystemName() {
        return "Auton manager";
    }
}
