package musicElements;

import builders.MidiSequenceBuilder;

public class BPMElement implements MusicElement{

    private final int bpm;

    private static final int MAX_BPM = 999;
    private static final int MIN_BPM = 20;

    public BPMElement(int newBPM){

        if ( newBPM < MIN_BPM || newBPM > MAX_BPM){
            throw new IllegalArgumentException("BPM invalido");
        }

        this.bpm = newBPM;
    }

    @Override
    public void addMusicElementToMIDI(MidiSequenceBuilder builder) {
        builder.setBPM(this);
    }

    public int getBPM(){
        return this.bpm;
    }

}
