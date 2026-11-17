package musicElements;

import builders.MidiSequenceBuilder;

public class VolumeElement implements MusicElement{
    
    private final int volume;

    public static final int MIN_VOLUME = 0;
    public static final int MAX_VOLUME = 127;

    public VolumeElement(int newVolume){

        if(newVolume < MIN_VOLUME || newVolume > MAX_VOLUME ){
            throw new IllegalArgumentException("Volume invalido");
        }

        this.volume  = newVolume;
    }

    @Override
    public void addMusicElementToMIDI(MidiSequenceBuilder builder) {
        builder.setVolume(this);
    }


    public int getVolume(){
        return this.volume;
    }
}
