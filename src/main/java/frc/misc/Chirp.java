package frc.misc;

import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import frc.motors.TalonMotor;

import java.util.ArrayList;
import java.util.Random;

public class Chirp extends Orchestra{
    public static final ArrayList<TalonMotor> talonMotorArrayList = new ArrayList<>();
    public static final String[] musicNames = {"BokuNoSensou_4Motors", "CoconutMall_4Motors", "Electroman_Adventures_4Motors", "Imperial_March_4Motors", "kiraTheme_4Motors", "Megalovania_4Motors", "TheOnlyThingTheyFearlsYou_4Motors", "WiiSports_4Motors"};
    Random musicRand = new Random();
    int upperbound = 8;
    int randomMusic = musicRand.nextInt(upperbound);


    public Chirp() {
        initChirp();
    }

    public void disabledChirp(){
        if(!isPlaying()){
            loadMusic(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + musicNames[randomMusic] + ".chrp").toString());
        }
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