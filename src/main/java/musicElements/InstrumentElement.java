package musicElements;

public class InstrumentElement implements MusicElement{

    private final int instrument;
    private final InstrumentType type;

    public InstrumentElement(InstrumentType newType){

        if (newType == null) throw new IllegalArgumentException("Tipo de instrumento invalido");

        this.type = newType;
        this.instrument = newType.getDefaultInstrument();
    }

    public InstrumentElement(int newInstrument, InstrumentType newType){

        if (newType == null) throw new IllegalArgumentException("Tipo de instrumento invalido");
        if (newInstrument < 0) throw new IllegalArgumentException("Instrumento invalido");

        this.type = newType;
        this.instrument = newType.contains(newInstrument) ? newInstrument : newType.getDefaultInstrument();
    }

    public int getInstrument(){
        return this.instrument;
    }

    public InstrumentType getType(){
        return this.type;
    }
}
