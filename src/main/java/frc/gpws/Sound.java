package frc.gpws;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Used in {@link SoundManager} as the base playable sound. Supports concatenating {@link SoundManager.Sounds} together
 * to make many, many, many different sounds
 *
 * @see SoundManager
 */
public class Sound implements Serializable {
    public final SoundManager.Sounds[] mySounds;
    public final SoundManager.SoundPacks soundPack;
    transient boolean isPlaying = false;
    private transient int windex = -1;

    public Sound(SoundManager.SoundPacks soundPack, SoundManager.Sounds... sounds) {
        this.soundPack = soundPack;
        mySounds = sounds;
    }

    /**
     * When the current track finishes, call this method to progress the current playing index
     *
     * @return the next sound to play, null if finished
     */
    @Nullable
    public SoundManager.Sounds goNext() {
        if (++windex == mySounds.length) {
            reset();
            return null;
        }
        return mySounds[windex];
    }

    /**
     * Call this when stopping playing of this sound
     */
    public void reset() {
        windex = -1;
        isPlaying = false;
    }

    /**
     * Woooooooo getters! Updates {@link #isPlaying} as well as returning the current track to play
     *
     * @return the current sound to play
     */
    public SoundManager.Sounds getCurrentSound() {
        isPlaying = true;
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

    /**
     * Returns a string that holds all of the sounds held here and the voice pack
     *
     * @return a pretty string representation
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Sounds: ");
        for (SoundManager.Sounds sound : mySounds) {
            out.append(sound.toString()).append(" ");
        }
        out.append("\n").append("Currently playing: ").append(mySounds[windex]);
        return out.toString();
    }
}