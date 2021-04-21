package frc.gpws;

import java.io.Serializable;

public class Sound implements Serializable {
    public final SoundManager.Sounds[] mySounds;
    public final SoundManager.SoundPacks soundPack;
    private final long sysTime = System.currentTimeMillis();
    private transient int windex = 0;

    public Sound(SoundManager.SoundPacks soundPack, SoundManager.Sounds... sounds) {
        this.soundPack = soundPack;
        mySounds = sounds;
    }

    public SoundManager.Sounds goNext() {
        if (++windex == mySounds.length)
            return null;
        return mySounds[windex];
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Sounds: ");
        for (SoundManager.Sounds sound : mySounds) {
            out.append(sound.toString());
        }
        out.append("\n").append("Currently playing: ").append(mySounds[windex]);
        return out.toString();
    }

    public SoundManager.Sounds getCurrentSound() {
        return mySounds[windex];
    }
}