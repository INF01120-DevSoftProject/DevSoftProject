package musicElements;

public enum InstrumentType {
    KEYS(0, 23),
    PLUCKED(24, 39),
    STRINGS(40, 55),
    WINDS(56, 79),
    PERCUSSION_AND_SYNTH(80, 127);

    private final int minInstrument;
    private final int maxInstrument;

    InstrumentType(int minInstrument, int maxInstrument){
        this.minInstrument = minInstrument;
        this.maxInstrument = maxInstrument;
    }

    public int getMinInstrument(){
        return this.minInstrument;
    }

    public int getDefaultInstrument(){
        return this.minInstrument;
    }

    public int getMaxInstrument(){
        return this.maxInstrument;
    }

    public boolean contains(int instrument){
        return instrument >= this.minInstrument && instrument <= this.maxInstrument;
    }
}
