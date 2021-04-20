package frc.gpws;

import java.io.Serializable;

public class Sound implements Serializable {
    public final MusicStuff.Sounds[] mySounds;
    private transient int windex = 0;
    private final long sysTime = System.currentTimeMillis();

    public Sound(MusicStuff.Sounds... sounds) {
        mySounds = sounds;
    }

    public MusicStuff.Sounds goNext(){
        if (++windex == mySounds.length)
            return null;
        return mySounds[windex];
    }

    @Override
    public String toString(){
        StringBuilder out = new StringBuilder("Sounds: ");
        for (MusicStuff.Sounds sound : mySounds){
            out.append(sound.toString());
        }
        out.append("\n").append("Currently playing: ").append(mySounds[windex]);
        return out.toString();
    }

    public MusicStuff.Sounds getCurrentSound() {
        return mySounds[windex];
    }
}