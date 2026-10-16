package musicElements;

public class InstrumentElement implements MusicElement{

    private final int instrument;
    private final InstrumentType type;

    public static final int MIN_INSTRUMENT = 0;
    public static final int MAX_INSTRUMENT = 127;

    public InstrumentElement(int newInstrument){
        this(newInstrument, findType(newInstrument));
    }

    public InstrumentElement(int newInstrument, InstrumentType newType){

        if(newInstrument < MIN_INSTRUMENT || newInstrument > MAX_INSTRUMENT){
            throw new IllegalArgumentException("Instrumento invalido");
        }

        if (newType == null) throw new IllegalArgumentException("Tipo de instrumento invalido");
        if (!newType.contains(newInstrument)) throw new IllegalArgumentException("Instrumento fora do tipo");

        this.instrument = newInstrument;
        this.type = newType;
    }

    private static InstrumentType findType(int instrument){

        for (InstrumentType type : InstrumentType.values()){
            if (type.contains(instrument)) return type;
        }

        return null;
    }

    public int getInstrument(){
        return this.instrument;
    }

    public InstrumentType getType(){
        return this.type;
    }
}
