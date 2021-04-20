package frc.gpws;

import frc.misc.ServerSide;
import frc.robot.Main;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

@ServerSide
public class SoundManager implements Runnable {
    public static ArrayList<Sound> queue = new ArrayList<>();
    // current status of clip
    private static String status;
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
            System.out.println("Audio is already " + "being played");
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
        currentInput.open(AudioSystem.getAudioInputStream(new File("sounds/" + queue.get(0).soundPack + "/" + queue.get(0).getCurrentSound() + ".wav").getAbsoluteFile()));
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

    public static void enqueueSound(Sound sound) {
        if (queue.size() == 0) {
            queue.add(sound);
            stop();
            try {
                resetAudioStream();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        } else {
            queue.add(sound);
        }
    }

    public static void stop() {
        currentInput.stop();
        currentInput.close();
        currentFrame = 0L;
        currentInput.setMicrosecondPosition(0);
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

    @ServerSide
    public static void init() {
    }

    public static void update() {
        if (queue.size() > 0 && currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
            try {
                System.out.println(queue.get(0).toString() + " (" + currentInput.getMicrosecondLength() + ", " + currentInput.getMicrosecondPosition() + ")");
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

    public enum SoundPacks implements Serializable {
        Jojo, TTS, Random;

        public String getFolder() {
            if (this == Random) {
                return SoundPacks.values()[Main.RANDOM.nextInt(SoundPacks.values().length)].getFolder();
            }
            return this.name() + "pack";
        }
    }

    public enum Sounds implements Serializable {
        AliStopBeingCringe,
        Backward,
        Battery,
        Brownout,
        Camera,
        Disable,
        Disconnected,
        Distance,
        Drive,
        Failed,
        Forward,
        IMU,
        Immediately,
        Inspect,
        Left,
        Low,
        Motor,
        Moving,
        NonOperational,
        NonResponsive,
        Now,
        One,
        Overheat,
        Reconnect,
        Reconnected,
        Replace,
        Right,
        Robot,
        Sensor,
        Shooting,
        System,
        Three,
        Turning,
        Two,
    }
}
