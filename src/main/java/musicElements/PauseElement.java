package musicElements;

import builders.MidiSequenceBuilder;

public class PauseElement implements MusicElement{
    
    private final DurationTime duration;

    public PauseElement(DurationTime newDuration){

        if (newDuration == null) throw new IllegalArgumentException("Duracao invalida");

        this.duration = newDuration;
    }

    @Override
    public void addMusicElementToMIDI(MidiSequenceBuilder builder) {
        builder.addPause(this);
    }

    public DurationTime getDuration(){
        return this.duration;
    }
}
