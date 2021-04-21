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
    public static ArrayList<Sound> liveMessages = new ArrayList<>();
    public static ArrayList<Sound> liveAlarms = new ArrayList<>();
    private static Sound currentSound;
    private static String status;
    private static Long currentFrame;
    private static Clip currentInput;
    private static int currentAlarmIndex = 0;

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
        //resetAudioStream();
        currentInput.setMicrosecondPosition(currentFrame);
        play();
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
        //resetAudioStream();
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
            //resetAudioStream();
            currentFrame = c;
            currentInput.setMicrosecondPosition(c);
            play();
        }
    }

    public static void enqueueSound(Sound sound) {
        liveMessages.add(sound);
        if (liveMessages.size() == 1) {
            stop();
            try {
                resetAudioStream(liveMessages.get(0));
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stop() {
        currentInput.stop();
        currentInput.close();
        currentFrame = 0L;
        currentInput.setMicrosecondPosition(0);
    }

    // Method to reset audio stream
    public static void resetAudioStream(Sound soundToPlay) throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        currentSound = soundToPlay;
        currentInput.open(AudioSystem.getAudioInputStream(new File("sounds/" + soundToPlay.soundPack + "pack/" + soundToPlay.getCurrentSound() + ".wav").getAbsoluteFile()));
        currentInput.loop(0);
    }

    public static void soundAlarm(Sound sound) {
        liveAlarms.add(sound);
        if (liveAlarms.size() == 1 && liveMessages.size() == 0) {
            currentAlarmIndex = 0;
            stop();
            try {
                resetAudioStream(liveAlarms.get(currentAlarmIndex));
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addAlarm(Sound alarm) {
        if (!liveAlarms.contains(alarm))
            liveAlarms.add(alarm);
    }

    public static void cutItOut() {
        stop();
        currentAlarmIndex = 0;
        liveMessages.clear();
        liveAlarms.clear();
    }

    @Override
    public void run() {
        init();
        /*try {
            resetAudioStream();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }*/
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
        if (liveMessages.size() > 0 && (currentSound == null || liveMessages.contains(currentSound))) {
            if (currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
                try {
                    System.out.println(liveMessages.get(0).toString() + " (" + currentInput.getMicrosecondLength() + ", " + currentInput.getMicrosecondPosition() + ")");
                    if (liveMessages.get(0).goNext() == null) {
                        liveMessages.remove(0);
                        if (liveMessages.size() > 0) {
                            stop();
                            resetAudioStream(liveMessages.get(0));
                        }
                    } else {
                        stop();
                        resetAudioStream(liveMessages.get(0));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (liveAlarms.size() > 0) {
            if (currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
                try {
                    currentAlarmIndex = Math.min(currentAlarmIndex, liveAlarms.size() - 1);
                    System.out.println(liveAlarms.get(currentAlarmIndex).toString() + " (" + currentInput.getMicrosecondLength() + ", " + currentInput.getMicrosecondPosition() + ")");
                    if (liveAlarms.get(currentAlarmIndex).goNext() == null) {
                        liveAlarms.get(currentAlarmIndex).reset();
                        currentAlarmIndex = (currentAlarmIndex + 1) % liveAlarms.size();
                    }
                    stop();
                    if (liveMessages.size() > 0) {
                        resetAudioStream(liveMessages.get(0));
                    } else {
                        resetAudioStream(liveAlarms.get(currentAlarmIndex));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
