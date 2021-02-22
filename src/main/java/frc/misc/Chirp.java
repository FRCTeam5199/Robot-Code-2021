package frc.misc;

import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import frc.motors.TalonMotorController;

import java.util.ArrayList;

/**
 * This is where we play our gnarly tunez. Although it cant be seen from here, there is a delete deploy directory method
 * in {@link frc.robot.Robot} that will help remove ghost files. Use this like an {@link Orchestra} with extra steps
 */
public class Chirp extends Orchestra {
    /**
     * Contains all of the talons created in {@link TalonMotorController#TalonMotorController(int)} that can be used to
     * play awesome tunez
     */
    public static final ArrayList<TalonMotorController> talonMotorArrayList = new ArrayList<>();

    public Chirp() {
        initChirp();
    }

    /**
     * pulls the talons from {@link #talonMotorArrayList the meta talon registry} and adds them to the orchestra
     */
    public void initChirp() {
        for (TalonMotorController motor : talonMotorArrayList) {
            motor.addToOrchestra(this);
        }
    }

    /**
     * See the delete deploy dir method in Robot for help clearing ghost files
     *
     * @param soundName The name of the file (less extansion, less path, just name) to get sound from (should be .chrp
     *                  file)
     */
    public void loadSound(String soundName) {
        loadMusic(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString());
    }
}