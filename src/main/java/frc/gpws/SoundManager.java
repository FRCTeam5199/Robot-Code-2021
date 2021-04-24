package frc.gpws;

import frc.misc.ServerSide;
import frc.robot.ClientServerPipeline;
import frc.robot.Main;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Literally everything here is serverside. For clientside audio support, please refer to {@link frc.misc.Chirp}.
 * <p>
 * Everything here is ServerSide
 *
 * @see Alarms
 * @see Sound
 */
@ServerSide
public class SoundManager implements Runnable {
    private static final Clip currentInput = getClipWrapped();
    public static ArrayList<Sound> liveMessages = new ArrayList<>();
    private static boolean playingMessage = false;

    /**
     * Adds a message to the queue.
     *
     * @param sound the sound to add to the queue
     */
    public static void enqueueSound(Sound sound) {
        liveMessages.add(sound);
        if (liveMessages.size() == 1) {
            resetAudioStream(liveMessages.get(0));
        }
    }

    /**
     * By "reset" we just mean to go to the start of the input and play it
     *
     * @param soundToPlay the sound to play
     */
    public static void resetAudioStream(Sound soundToPlay) {
        stop();
        try {
            currentInput.open(AudioSystem.getAudioInputStream(new File("sounds/" + soundToPlay.soundPack.getFolder() + "/" + soundToPlay.getCurrentSound() + ".wav").getAbsoluteFile()));
        } catch (Exception e) {
            throw new RuntimeException("Something bad has happened and i dont know why. Good luck fixing it");
        }
        currentInput.start();
    }

    /**
     * Stops playing and sets position to 0
     */
    public static void stop() {
        currentInput.stop();
        currentInput.close();
        currentInput.setMicrosecondPosition(0);
    }

    /**
     * Unsets all alarm flags
     */
    public static void resolveAllAlarms() {
        for (Alarms alarm : Alarms.values())
            alarm.setActive(false);
    }

    @ServerSide
    public static void init() {
    }

    /**
     * Heavy lifting funtion. Determines what alarms should sound, when, and when to deliver messages.
     */
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
                                resetAudioStream(liveMessages.get(0));
                            }
                        }
                    } else {
                        resetAudioStream(liveMessages.get(0));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Dont mind this
     *
     * @return {@link AudioSystem#getClip()}
     */
    private static Clip getClipWrapped() {
        try {
            return AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If there is no message being broadcast, plays most applicable alarm, otherwise does nothing.
     *
     * @return true if playing an alarm, false otherwise
     */
    private static boolean playAlarm() {
        Alarms alarm = Alarms.getAlarmToPlay();
        if (alarm != null && !playingMessage) {
            if (currentInput.getMicrosecondLength() <= currentInput.getMicrosecondPosition()) {
                try {
                    if (alarm.getMySound().goNext() == null) {
                        return false;
                    } else {
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

    /**
     * DO NOT RUN MORE THAN ONE SERVERSIDE THREAD UNLESS THE TWO THREADS DO NOT INTERACT!!! We need to be able to create
     * a new thread to prevent the program from closing, but other than that, there is no other need. <b>use this method
     * when testing the sound manager on the server</b>. Otherwise please refer to {@link ClientServerPipeline#run()}
     */
    @Override
    public void run() {
        init();
        while (true) {
            try {
                Thread.sleep(20);
                update();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * If you want different people to say different things, add that here and then add a new file in the runtime dir of
     * the server inside a folder named {@code <enumname>pack}
     */
    public enum SoundPacks implements Serializable {
        Jojo, TTS, Random;

        public String getFolder() {
            if (this == Random) {
                return SoundPacks.values()[Main.RANDOM.nextInt(SoundPacks.values().length)].getFolder();
            }
            return this.name() + "pack";
        }
    }

    /**
     * All the words that the DS can say. Every sound must be implemented in every pack, otherwise bad stuff will
     * happen
     */
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
