package musicElements;

public class OctaveElement implements  MusicElement {

    private final int octave;

    public OctaveElement(int newOctave){

        if (newOctave < -1 || newOctave > 9){
             throw new IllegalArgumentException("Oitava invalida") ; 
        } 

        this.octave = newOctave;
    }
    
    public int getOctave(){
        return this.octave;
    }

}
