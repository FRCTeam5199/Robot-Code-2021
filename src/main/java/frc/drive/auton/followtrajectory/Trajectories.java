package frc.drive.auton.followtrajectory;

import frc.drive.auton.IAutonEnumPath;

/**
 * Simply vibing here. This is for non-galactic search
 */
public enum Trajectories implements IAutonEnumPath {
    TEST_PATH("OutlineField"),
    FORWARD("ForwardTinyBit"),
    BACKWARD("BackwardTinyBit"),
    SLALOM("Slalom"),
    SLALOM2("Slalom_0");

    /**
     * The file name that holds this trajectory. Leave off the generic file extension stuff
     */
    public final String PATH_FILE_LOCATION;

    Trajectories(String pathloc) {
        PATH_FILE_LOCATION = pathloc;
    }

    @Override
    public String getDeployLocation() {
        return PATH_FILE_LOCATION;
    }
}
