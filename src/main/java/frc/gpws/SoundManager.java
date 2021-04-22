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
    private static boolean playingMessage = false;
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
        soundToPlay.beginPlaying();
        currentInput.open(AudioSystem.getAudioInputStream(new File("sounds/" + soundToPlay.soundPack.getFolder() + "/" + soundToPlay.getCurrentSound() + ".wav").getAbsoluteFile()));
        currentInput.loop(0);
    }

    public static void soundAlarm(Alarms alarms) {
        alarms.setActive(true);
    }

    public static void resolveAlarm(Alarms alarm) {
        alarm.setActive(false);
    }

    public static void resolveAllAlarms() {
        for (Alarms alarm : Alarms.values())
            alarm.setActive(false);
    }

    @Override
    public void run() {
        init();
        /*try {
            resetAudioStream();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }*/
        Alarms.Brownout.setActive(true);
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
        if (!playAlarm() && liveMessages.size() > 0) {
            playingMessage = true;
            if (currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
                try {
                    System.out.println(liveMessages.get(0).toString() + " (" + currentInput.getMicrosecondLength() + ", " + currentInput.getMicrosecondPosition() + ")");
                    if (liveMessages.get(0).goNext() == null) {
                        liveMessages.remove(0);
                        playingMessage = false;
                        if (!playAlarm()) {
                            playingMessage = true;
                            if (liveMessages.size() > 0) {
                                stop();
                                resetAudioStream(liveMessages.get(0));
                            }
                        }
                    } else {
                        stop();
                        resetAudioStream(liveMessages.get(0));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static boolean playAlarm() {
        Alarms alarm = Alarms.getAlarmToPlay();
        if (alarm != null && !playingMessage) {
            if (currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
                try {
                    if (alarm.getMySound().goNext() == null){
                        alarm.getMySound().reset();
                        return false;
                    }else {
                        stop();
                        resetAudioStream(alarm.getMySound());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return false;
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
