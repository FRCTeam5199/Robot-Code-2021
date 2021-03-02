package frc.drive.auton.followtrajectory;

import frc.drive.auton.IAutonEnumPath;

public enum Trajectories implements IAutonEnumPath {
    TEST_PATH("OutlineField"),
    FORWARD("ForwardTinyBit"),
    BACKWARD("BackwardTinyBit");

    public final String PATH_FILE_LOCATION;

    Trajectories(String pathloc) {
        PATH_FILE_LOCATION = pathloc;
    }

    @Override
    public String getDeployLocation() {
        return PATH_FILE_LOCATION;
    }
}
