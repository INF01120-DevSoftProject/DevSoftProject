package musicElements;

public class OctaveElement implements  MusicElement {

    public static final int MIN_OCTAVE = 0;
    public static final int MAX_OCTAVE = 8;

    private final int octave;

    public OctaveElement(int newOctave){

        if (newOctave < MIN_OCTAVE || newOctave > MAX_OCTAVE){
             throw new IllegalArgumentException("Oitava invalida") ; 
        } 

        this.octave = newOctave;
    }
    
    public int getOctave(){
        return this.octave;
    }

}
