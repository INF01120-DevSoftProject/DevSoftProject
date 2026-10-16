package musicElements;

public class InstrumentElement implements MusicElement{

    private final int instrument;

    public static final int MIN_INSTRUMENT = 0;
    public static final int MAX_INSTRUMENT = 127;

    public InstrumentElement(int newInstrument){

        if(newInstrument < MIN_INSTRUMENT || newInstrument > MAX_INSTRUMENT){
            throw new IllegalArgumentException("Instrumento invalido");
        }

        this.instrument = newInstrument;
    }

    public InstrumentElement(int newInstrument, InstrumentType type){

        this(newInstrument);

        if (type == null) throw new IllegalArgumentException("Tipo de instrumento invalido");
        if (!type.contains(newInstrument)) throw new IllegalArgumentException("Instrumento fora do tipo");
    }

    public int getInstrument(){
        return this.instrument;
    }
}
