package musicElements;

public class InstrumentElement implements MusicElement{

    private final int instrument;

    public static final int MIN_INSTRUMENT = 0;
    public static final int MAX_INSTRUMENT = 127;

    public InstrumentElement(int newInstrument){

        if(newInstrument < MIN_INSTRUMENT || newInstrument > MAX_INSTRUMENT){
            throw new IllegalArgumentException("Instrumento inválido");
        }

        this.instrument = newInstrument;
    }

    public int getInstrument(){
        return this.instrument;
    }
}
