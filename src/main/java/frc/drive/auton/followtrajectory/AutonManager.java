package frc.drive.auton.followtrajectory;

import frc.drive.AbstractDriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.IAutonEnumPath;

/**
 * Check back later for some fun and fresh auton routines!
 */
public class AutonManager extends AbstractAutonManager {
    public AutonManager(IAutonEnumPath autonEnumPath, AbstractDriveManager driveObject) { //Routine should be in the form of "YourPath" (paths/YourPath.wpilib.json)
        super(driveObject);
        autonPath = autonEnumPath;
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
    public void updateGeneric() {
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initDisabled() {
    }

    @Override
    public void initGeneric() {

    }
}