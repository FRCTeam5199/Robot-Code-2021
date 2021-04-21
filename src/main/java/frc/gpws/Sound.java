package frc.gpws;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Sound implements Serializable {
    public final SoundManager.Sounds[] mySounds;
    public final SoundManager.SoundPacks soundPack;
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

    public SoundManager.Sounds goNextWrapped() {
        return mySounds[(windex = (windex + 1) % mySounds.length)];
    }

    public SoundManager.Sounds getCurrentSound() {
        return mySounds[windex];
    }

    /**
     * Ignore this, its something that you are meant to override when overriding {@link #equals(Object)} so just pretend
     * it isnt here
     *
     * @return dont worry about it
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(soundPack);
        result = 31 * result + Arrays.hashCode(mySounds);
        return result;
    }

    /**
     * True if two sounds have the same metadata (disregards {@link #windex playing index})
     *
     * @param other a different sound object to compare
     * @return true if the two sounds are (for all intents and purposes) the same
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Sound sound = (Sound) other;
        return Arrays.equals(mySounds, sound.mySounds) && soundPack == sound.soundPack;
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

    public void reset() {
        windex = 0;
    }
}