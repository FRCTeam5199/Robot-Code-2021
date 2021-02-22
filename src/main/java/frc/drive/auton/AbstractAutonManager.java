package frc.drive.auton;

import edu.wpi.first.wpilibj.Timer;
import frc.drive.DriveManager;
import frc.misc.ISubsystem;

/**
 * If you have a custom auton that needs to be implemented, extend this class.
 * Since every Auton Manager needs to have a {@link DriveManager drivetrain} and a {@link Timer timer}, they are here
 * along with {@link ISubsystem}.
 *
 * @see ISubsystem
 * @see frc.drive.auton.pointtopoint.AutonManager
 * @see frc.drive.auton.butbetternow.AutonManager
 * @see frc.drive.auton.galacticsearch.AutonManager
 * @see frc.drive.auton.galacticsearchscam.AutonManager
 */
public abstract class AbstractAutonManager implements ISubsystem {
    protected final Timer timer = new Timer();
    protected final DriveManager DRIVING_CHILD;

    /**
     * Initializes the auton manager and stores the reference to the drivetrain object
     *
     * @param driveManager the drivetrain object created for the robot
     */
    protected AbstractAutonManager(DriveManager driveManager) {
        addToMetaList();
        DRIVING_CHILD = driveManager;
    }
}
