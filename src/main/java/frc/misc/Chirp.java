package frc.misc;

import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import frc.drive.DriveManager;
import frc.robot.RobotToggles;

import java.nio.file.Path;

public class Chirp {
    private final DriveManager DRIVING_CHILD;
    private final Path filePath;
    private final Orchestra orchestra;

    public Chirp(DriveManager driveManager, String soundName) {
        orchestra = new Orchestra();
        this.DRIVING_CHILD = driveManager;
        this.filePath = Filesystem.getDeployDirectory().toPath().resolve("Sounds/" + soundName);
        orchestra.loadMusic(this.filePath.toString());
    }

    public void initChirp() {
        if (!RobotToggles.DRIVE_USE_SPARKS) {
            orchestra.addInstrument(DRIVING_CHILD.leaderLTalon);
            orchestra.addInstrument(DRIVING_CHILD.leaderRTalon);
            DRIVING_CHILD.followerLTalon.addInstrument(orchestra);
            DRIVING_CHILD.followerRTalon.addInstrument(orchestra);
        }
    }

    private void playChirp() {
        orchestra.play();
    }

    private void stopChirp() {
        orchestra.stop();
    }
}