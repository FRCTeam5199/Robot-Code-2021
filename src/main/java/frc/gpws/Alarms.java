package frc.gpws;

import javax.annotation.Nullable;
import java.io.Serializable;

import static frc.gpws.SoundManager.SoundPacks;
import static frc.gpws.SoundManager.Sounds;

public enum Alarms implements Serializable {
    Overheat(new Sound(SoundPacks.Jojo, Sounds.Overheat, Sounds.Overheat, Sounds.Overheat, Sounds.Disable, Sounds.Disable, Sounds.Now), 0, 5000),
    Brownout(new Sound(SoundPacks.Jojo, Sounds.Brownout, Sounds.Brownout, Sounds.Brownout, Sounds.Replace, Sounds.Battery, Sounds.Replace, Sounds.Battery), 1, 30000);

    private transient final Sound mySound;
    public transient long lastSoundingTime = 0;
    public transient final long frequency;
    public transient final int urgency;
    private transient boolean active;

    public static @Nullable Alarms getAlarmToPlay() {
        for (Alarms alarm : Alarms.values())
            if (alarm.mySound.isPlaying)
                return alarm;
        int highestPrio = -1;
        long longestOverdue = Long.MAX_VALUE;
        Alarms bestAlarm = null;
        for (Alarms alarm : Alarms.values()){
            if (!alarm.isActive())
                continue;
            if (System.currentTimeMillis() - alarm.lastSoundingTime > alarm.frequency){
                if (alarm.urgency > highestPrio){
                    longestOverdue = 0;
                    highestPrio = alarm.urgency;
                }
                if (System.currentTimeMillis() - alarm.lastSoundingTime > longestOverdue){
                    bestAlarm = alarm;
                    longestOverdue = System.currentTimeMillis() - alarm.lastSoundingTime;
                }
            }
        }
        return bestAlarm;
    }

    Alarms(Sound alarm, int urgency, long frequency) {
        mySound = alarm;
        this.urgency = urgency;
        this.frequency = frequency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Sound getMySound() {
        lastSoundingTime = System.currentTimeMillis();
        return mySound;
    }
}
