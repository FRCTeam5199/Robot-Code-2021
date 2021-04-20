package frc.gpws;

import java.io.Serializable;

public class Sound implements Serializable {
    public final MusicStuff.Sounds[] mySounds;
    public transient MusicStuff.Sounds currentSound;
    private transient int windex = 0;

    public Sound(MusicStuff.Sounds... sounds) {
        mySounds = sounds;
        currentSound = sounds[0];
    }

    public MusicStuff.Sounds goNext(){
        if (++windex == mySounds.length)
            return null;
        return currentSound = mySounds[windex];
    }
}
