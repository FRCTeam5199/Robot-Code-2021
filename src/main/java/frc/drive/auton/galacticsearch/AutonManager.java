package frc.drive.auton.galacticsearch;


import frc.drive.DriveManager;
import frc.misc.ISubsystem;
import org.photonvision.PhotonCamera;

public class AutonManager implements ISubsystem {
    DriveManager driveManager;
    PhotonCamera vision;

    @Override
    public void init() {
        driveManager.init();

    }

    public void initAuton() {

    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {
        searchAndHunt();
    }


    @Override
    public void updateGeneric() {

    }

    private void spinInPlace(){
        driveManager.drive(0, 0.25);
    }

    private void searchAndHunt(){
        if (vision.hasTargets()) {

        } else {
            spinInPlace();
        }
    }
}