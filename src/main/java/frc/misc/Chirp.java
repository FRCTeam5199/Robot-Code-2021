package frc.misc;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.motors.TalonMotorController;
import frc.robot.Robot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
     * K: Title. V: All files matching (differs in motors required)
     */
    private static HashMap<String, List<String>> songnames;
    public static final SendableChooser<List<String>> MUSIC_SELECTION = getSongs();

    /**
     * Loads up songs for {@link #songnames} and {@link UserInterface#MUSIC_SELECTOR}
     *
     * @return listObject a SendableChooser with all the songs
     */
    public static SendableChooser<List<String>> getSongs() {
        SendableChooser<List<String>> listObject = new SendableChooser<>();
        songnames = new HashMap<>();
        File[] files = Filesystem.getDeployDirectory().toPath().resolve("sounds").toFile().listFiles();
        for (File file : files) {
            String filename = file.getName().split("\\.")[0];
            try {
                Integer.parseInt(filename.split("_")[1]);
                Integer.parseInt(filename.split("_")[2]);
            } catch (Exception e) {
                continue;
            }
            if (!songnames.containsKey(filename.split("_")[0])) {
                songnames.put(filename.split("_")[0], new ArrayList<String>(Collections.singleton(filename)));
            } else {
                songnames.get(filename.split("_")[0]).add(filename);
            }
        }
        List<String> filenames = new ArrayList<>();
        for (int i = 0; i < songnames.keySet().size(); i++)
            filenames.add((String) songnames.keySet().toArray()[i]);
        filenames.sort(String::compareTo);
        for (String key : filenames) {
            //System.out.println(key);
            listObject.setDefaultOption(key, songnames.get(key)); //WORKING: listObject.addOption(key, songnames.get(key));
        }
        return listObject;
    }

    public Chirp() {
        init();
        addToMetaList();
    }

    /**
     * Add instruments and get ready to rumble
     */
    @Override
    public void init() {
        for (TalonMotorController motor : talonMotorArrayList) {
            motor.addToOrchestra(this);
        }
    }

    /**
     * Autoplays music while taking suggestions. A few important things:
     * <p>
     * - Music names must be in format {@code <name>_<instruments>_<playtime in millis>.chrp}
     * <p>
     * - Songs will not be played if the {@link #talonMotorArrayList instrument registry} doesnt contain enough
     * instruments
     * <p>
     * - Autoplay will choose a new random song after the time specified in the file name
     */
    @Override
    public void updateTest() {
        List<String> selected = MUSIC_SELECTION.getSelected();
        String songName = "";//Robot.songTab.getString("");
        if (selected != null && selected.size() > 0) {
            for (String str : selected) {
                if (songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size())
                    songName = str;
                else if (!songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size() && Integer.parseInt(songName.split("_")[1]) < Integer.parseInt(str.split("_")[1]))
                    songName = str;
            }
        }
        UserInterface.MUSIC_FOUND_SONG.getEntry().setBoolean(new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists());
        if (!songName.equals("") && new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists() && !songName.equals(Robot.lastFoundSong)) {
            Robot.lastFoundSong = songName;
            loadMusic(songName);
            ErrorCode e = play();
            if (e != ErrorCode.OK) {
                System.out.println("Music Error: " + e);
            }
            System.out.println("Playing song " + songName + " on " + Chirp.talonMotorArrayList.size() + " motors.");
        } else {
            if (!isPlaying()) {
                String randomSong = getRandomSong();
                System.out.println("Playing random song: " + randomSong);
                loadMusic(randomSong);
                play();
            }
        }
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

    @Override
    public String getSubsystemName() {
        return "Music";
    }

    /**
     * Loads a song from the provided name assuming it is in the sounds deploy directory (deploy/sounds). Songs must be
     * in format {@code <name>_<instruments>_<playtime in millis>.chrp}
     *
     * @param soundName The name of the file (less extension, less path, just name) to get sound from (should be a .chrp
     *                  file)
     * @return Error code per super call
     */
    @Override
    public ErrorCode loadMusic(String soundName) {
        ErrorCode e = super.loadMusic(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString());
        if (e != ErrorCode.OK) {
            System.out.println("Failed to load " + Filesystem.getDeployDirectory().toPath().resolve("sounds/" + soundName + ".chrp").toString() + ": " + e);
        }
        return e;
    }

    /**
     * Overrides super to include if the playing song has ended as per given song length specified in file name
     *
     * @return if motors are in music mode and there is a song loaded that has yet to end
     */
    @Override
    public boolean isPlaying() {
        try {
            return super.isPlaying() && (!Robot.lastFoundSong.equals("") && getCurrentTime() < Integer.parseInt(Robot.lastFoundSong.split("_")[2]));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Selects a random song name from options loaded at runtime in {@link Robot}
     *
     * @return song name from {@link #songnames}
     */
    public String getRandomSong() {
        String songName = "";
        for (String str : songnames.get(songnames.keySet().toArray()[Robot.RANDOM.nextInt(songnames.keySet().toArray().length)])) {
            if (songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size())
                songName = str;
            else if (!songName.equals("") && Integer.parseInt(str.split("_")[1]) <= talonMotorArrayList.size() && Integer.parseInt(songName.split("_")[1]) < Integer.parseInt(str.split("_")[1]))
                songName = str;
        }
        return songName;
    }
}