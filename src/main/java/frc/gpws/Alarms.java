package frc.gpws;

import frc.robot.ClientServerPipeline;

import javax.annotation.Nullable;
import java.io.Serializable;

import static frc.gpws.SoundManager.SoundPacks;
import static frc.gpws.SoundManager.Sounds;

/**
 * An alarm is something that is repeated frequently on the drive station. For example, {@link #Brownout} because it
 * should constantly repeat until until it is either recalled or the bot shuts off. Otherwise, reffer to {@link
 * SoundManager#enqueueSound(Sound)} or {@link frc.robot.ClientServerPipeline#sendSound(Sound)}
 * <p>
 * All of the member fields are transient because there is no way to run them differently on server and client so
 * sending over that extra data is pointless
 */
public enum Alarms implements Serializable {
    /**
     * Overheat, overheat, overheat, disable, disable now. Lowest prio alarm, repeats every 10 seconds
     */
    Overheat(new Sound(SoundPacks.Jojo, Sounds.Overheat, Sounds.Overheat, Sounds.Overheat, Sounds.Disable, Sounds.Disable, Sounds.Now), 0, 5000),
    /**
     * Brownout, brownout, brownout, replace battery, replace battery. Highest prio, repeats every 20 seconds
     */
    Brownout(new Sound(SoundPacks.Jojo, Sounds.Brownout, Sounds.Brownout, Sounds.Brownout, Sounds.Replace, Sounds.Battery, Sounds.Replace, Sounds.Battery), 1, 20000);

    public transient final long frequency;
    public transient final int urgency;
    private transient final Sound mySound;
    public transient long lastSoundingTime = 0;
    private transient boolean active;

    /**
     * Simple constructor
     *
     * @param alarm     when this alarm plays, this sound will be played
     * @param urgency   the level of prio this alarm should get. Higher numbers will take precedance over lower numbers
     *                  when both are overdue
     * @param frequency millisecond time to wait between calls
     */
    Alarms(Sound alarm, int urgency, long frequency) {
        mySound = alarm;
        this.urgency = urgency;
        this.frequency = frequency;
    }

    /**
     * Reads through all of the alarms and returns the best candidate to play. Filters by alarm currently sounding in
     * {@link SoundManager}, then by highest priority alarm overdue to play, then by most overdue highest prio alarm.
     *
     * @return The alarm that should be sounded right now
     */
    @Nullable
    public static Alarms getAlarmToPlay() {
        for (Alarms alarm : Alarms.values())
            if (alarm.mySound.isPlaying)
                return alarm;
        int highestPrio = -1;
        long longestOverdue = Long.MAX_VALUE;
        Alarms bestAlarm = null;
        for (Alarms alarm : Alarms.values()) {
            if (!alarm.isActive())
                continue;
            if (System.currentTimeMillis() - alarm.lastSoundingTime > alarm.frequency) {
                if (alarm.urgency > highestPrio) {
                    longestOverdue = 0;
                    highestPrio = alarm.urgency;
                }
                if (System.currentTimeMillis() - alarm.lastSoundingTime > longestOverdue) {
                    bestAlarm = alarm;
                    longestOverdue = System.currentTimeMillis() - alarm.lastSoundingTime;
                }
            }
        }
        return bestAlarm;
    }

    /**
     * Getter for {@link #active}
     *
     * @return if this alarm should be actively sounding
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for {@link #active}. Used by {@link ClientServerPipeline#readAlarms()}
     *
     * @param active if this alarm should be enabled or not
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter for {@link #mySound}. This is why we use getters, kids! every time something needs to read the alarm
     * sound, we can assume the alarm is playing so we also update {@link #lastSoundingTime} as well. This keeps
     * everything working as intended and the code remains clean.
     *
     * @return {@link #mySound this alarm's sound}
     */
    public Sound getMySound() {
        lastSoundingTime = System.currentTimeMillis();
        return mySound;
    }
}
