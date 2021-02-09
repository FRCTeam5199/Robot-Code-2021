package frc.drive.auton;

import frc.drive.DriveManager;
import frc.misc.ISubsystem;

public abstract class AbstractAutonManager implements ISubsystem {
    protected final DriveManager DRIVING_CHILD;

    protected AbstractAutonManager(DriveManager driveManager){
        DRIVING_CHILD = driveManager;
    }
}
