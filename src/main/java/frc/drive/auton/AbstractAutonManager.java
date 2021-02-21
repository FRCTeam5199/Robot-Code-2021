package frc.drive.auton;

import edu.wpi.first.wpilibj.Timer;
import frc.drive.DriveManager;
import frc.misc.ISubsystem;

public abstract class AbstractAutonManager implements ISubsystem {
    protected final Timer timer = new Timer();
    protected final DriveManager DRIVING_CHILD;

    protected AbstractAutonManager(DriveManager driveManager) {
        addToMetaList();
        DRIVING_CHILD = driveManager;
    }
}
