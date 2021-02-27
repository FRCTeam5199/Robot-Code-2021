package frc.misc;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import frc.motors.TalonMotorController;

import java.util.ArrayList;
import java.util.Random;

/**
 * This is where we play our gnarly tunez. Although it cant be seen from here, there is a delete deploy directory method
 * in {@link frc.robot.Robot} that will help remove ghost files. Use this like an {@link Orchestra} with extra steps
 */
public class Chirp extends Orchestra implements ISubsystem {
    /**
     * Contains all of the talons created in {@link TalonMotorController#TalonMotorController(int)} that can be used to
     * play awesome tunez
     */
    public static final ArrayList<TalonMotorController> talonMotorArrayList = new ArrayList<>();

    /**
     * All of the neat songs that we can play, hardcoded for all your non-modular ideas (basically forget about ever
     * becoming modular ever again)
     */
    public static final String[] musicNames = {
            "BokuNoSensou_4Motors", "CoconutMall_4Motors", "Electroman_Adventures_4Motors",
            "Imperial_March_4Motors", "kiraTheme_4Motors", "Megalovania_4Motors",
            "TheOnlyThingTheyFearIsYou_4Motors", "WiiSports_4Motors", "TheSevenSeas_4Motors"
    };

    public Chirp() {
        init();
        addToMetaList();
    }

    /**
     * Loads a song from the provided name assuming it is in the sounds deploy directory (deploy/sounds).
     *
     * @param soundName The name of the file (less extension, less path, just name) to get sound from (should be a .chrp
     *                  file)
     */
    public void loadSound(String soundName) {
        ErrorCode e = loadMusic(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString());
        System.out.println("Playing music. " + e);
        if (e != ErrorCode.OK) {
            System.out.println("Failed to load " + Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString() + ": " + e);
        }
        //new RuntimeException().printStackTrace();
    }

    /**
     * Selects a random song name from our previously mentioned hardcoded list
     *
     * @return song name from musicNames
     */
    public String getRandomSong() {
        Random musicRand = new Random();
        int randomSong = musicRand.nextInt(musicNames.length);
        return musicNames[randomSong];
    }

    @Override
    public void init() {
        for (TalonMotorController motor : talonMotorArrayList) {
            motor.addToOrchestra(this);
            System.out.println("Adding motor to orchestra!");
        }
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {

    }

    /**
     * Runs all of the cool orchestra stuff assuming you aren't playing your own cool tunes, while the robot is disabled
     * (duh.)
     */
    public void updateDisabled() {
        if (!isPlaying()) {
            loadSound(getRandomSong());
            play();
        }
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }
}