package musicElements;

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

    public int getBPM(){
        return this.bpm;
    }

}
