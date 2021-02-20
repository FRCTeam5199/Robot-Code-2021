package frc.misc;

import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import frc.motors.TalonMotor;

import java.util.ArrayList;

public class Chirp extends Orchestra {
    public static final ArrayList<TalonMotor> talonMotorArrayList = new ArrayList<>();

    public Chirp() {
        initChirp();
    }

    public void initChirp() {
        for (TalonMotor motor : talonMotorArrayList) {
            motor.addToOrchestra(this);
        }
    }

    public void loadSound(String soundName) {
        loadMusic(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString());
    }
}