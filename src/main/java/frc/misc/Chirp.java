package frc.misc;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import frc.motors.TalonMotorController;
import frc.robot.Robot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    private static final Random musicRand = new Random(System.currentTimeMillis());

    public Chirp() {
        init();
        addToMetaList();
    }

    @Override
    public void init() {
        for (TalonMotorController motor : talonMotorArrayList) {
            motor.addToOrchestra(this);
        }
    }

    @Override
    public void updateTest() {
        updateDisabled();
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

    /**
     * Runs all of the cool orchestra stuff assuming you aren't playing your own cool tunes, while the robot is disabled
     * (duh.)
     */
    public void updateDisabled() {
        List<String> selected = Robot.leest.getSelected();
        String songName = "";//Robot.songTab.getString("");
        if (selected != null && selected.size() > 0) {
            for (String str : selected) {
                if (songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size())
                    songName = str;
                else if (!songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size() && Integer.parseInt(songName.split("_")[1]) < Integer.parseInt(str.split("_")[1]))
                    songName = str;
            }
        }
        //String songName = "CoconutMall_4";
        Robot.foundSong.setBoolean(new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists());
        if (!songName.equals("") && new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists() && !songName.equals(Robot.lastFoundSong)) {
            Robot.lastFoundSong = songName;
            loadSound(songName);
            ErrorCode e = play();
            if (e != ErrorCode.OK) {
                System.out.println("Music Error: " + e);
            }
            System.out.println("Playing song " + songName + " on " + Chirp.talonMotorArrayList.size() + " motors.");
        } else {
            if (!isPlaying()) {
                String randomSong = getRandomSong();
                System.out.println("Playing random song: " + randomSong);
                loadSound(randomSong);
                play();
            }
        }
    }

    /**
     * Loads a song from the provided name assuming it is in the sounds deploy directory (deploy/sounds).
     *
     * @param soundName The name of the file (less extension, less path, just name) to get sound from (should be a .chrp
     *                  file)
     */
    public void loadSound(String soundName) {
        ErrorCode e = loadMusic(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString());
        if (e != ErrorCode.OK) {
            System.out.println("Failed to load " + Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString() + ": " + e);
        }
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying() && getCurrentTime() < Integer.parseInt(Robot.lastFoundSong.split("_")[2]);
    }

    /**
     * Selects a random song name from our previously mentioned hardcoded list
     *
     * @return song name from musicNames
     */
    public String getRandomSong() {
        String songName = "";//Robot.songTab.getString("");
        for (String str : Robot.songnames.get(Robot.songnames.keySet().toArray()[musicRand.nextInt(Robot.songnames.keySet().toArray().length)])) {
            if (songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size())
                songName = str;
            else if (!songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size() && Integer.parseInt(songName.split("_")[1]) < Integer.parseInt(str.split("_")[1]))
                songName = str;
        }
        return songName;
    }
}