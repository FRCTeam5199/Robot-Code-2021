package frc.gpws;

import frc.misc.InitializationFailureException;
import frc.misc.ServerSide;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@ServerSide
public class MusicStuff implements Runnable {
    public static ArrayList<Sound> queue = new ArrayList<>();
    // current status of clip
    private static String status;
    private static HashMap<Sounds, AudioInputStream> ALL_SOUNDS = new HashMap<>();
    private static Long currentFrame;
    private static Clip currentInput;

    static {
        try {
            currentInput = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to pause the audio
    public static void pause() {
        if (status.equals("paused")) {
            System.out.println("audio is already paused");
            return;
        }
        currentFrame = currentInput.getMicrosecondPosition();
        currentInput.stop();
        status = "paused";
    }

    // Method to resume the audio
    public static void resumeAudio() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        if (status.equals("play")) {
            System.out.println("Audio is already " +
                    "being played");
            return;
        }
        currentInput.close();
        resetAudioStream();
        currentInput.setMicrosecondPosition(currentFrame);
        play();
    }

    // Method to reset audio stream
    public static void resetAudioStream() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        currentInput.open(ALL_SOUNDS.get(queue.get(0).currentSound));
        currentInput.loop(0);
    }

    public static void play() {
        currentInput.start();
        status = "play";
    }

    // Method to restart the audio
    public static void restart() throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        currentInput.stop();
        currentInput.close();
        resetAudioStream();
        currentFrame = 0L;
        currentInput.setMicrosecondPosition(0);
        play();
    }

    // Method to jump over a specific part
    public static void jump(long c) throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        if (c > 0 && c < currentInput.getMicrosecondLength()) {
            currentInput.stop();
            currentInput.close();
            resetAudioStream();
            currentFrame = c;
            currentInput.setMicrosecondPosition(c);
            play();
        }
    }

    @Override
    public void run() {
        init();
        try {
            resetAudioStream();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(20);
                update();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void init() {
        System.out.println("Pulling from " + new File(".").getAbsolutePath());
        ALL_SOUNDS = new HashMap<>();
        for (Sounds sound : Sounds.values()) {
            try {
                ALL_SOUNDS.put(sound, AudioSystem.getAudioInputStream(new File("sounds/" + sound + ".wav").getAbsoluteFile()));
            } catch (Exception e) {
                throw new InitializationFailureException("Failed to load " + sound, "Just load it smh");
            }
        }
    }

    public static void update() {
        if (queue.size() > 0 && currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
            try {
                if (queue.get(0).goNext() == null) {
                    queue.remove(0);
                    if (queue.size() > 0) {
                        stop();
                        resetAudioStream();
                    }
                } else {
                    stop();
                    resetAudioStream();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void stop() {
        currentFrame = 0L;
        currentInput.stop();
        currentInput.close();
    }

    public enum Sounds implements Serializable {
        BATTERY, LOW, MOTOR, DISCONNECTED
    }
}
