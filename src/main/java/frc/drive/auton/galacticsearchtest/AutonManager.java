package frc.drive.auton.galacticsearchtest;

import frc.drive.AbstractDriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.galacticsearch.GalacticSearchPaths;

/**
 * This is for running a preselected galactic search path
 */
public class AutonManager extends AbstractAutonManager {
    public AutonManager(AbstractDriveManager driveManager) {
        super(driveManager);
    }

    @Override
    public void init() {
        autonPath = GalacticSearchPaths.PATH_B_BLUE;
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