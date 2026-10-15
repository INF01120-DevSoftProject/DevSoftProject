package musicElements;

public class Octave {

    public static final int MIN_OCTAVE = 0;
    public static final int MAX_OCTAVE = 8;

    private final int octave;

    public Octave(int newOctave){

        if (newOctave < MIN_OCTAVE || newOctave > MAX_OCTAVE){
             throw new IllegalArgumentException("Oitava invalida") ; 
        } 

        this.octave = newOctave;
    }
    
    public int getOctaveInt(){
        return this.octave;
    }

}
