package musicElements;

public enum InstrumentType {
    PIANO(0, 7),
    CHROMATIC_PERCUSSION(8, 15),
    ORGAN(16, 23),
    GUITAR(24, 31),
    BASS(32, 39),
    STRINGS(40, 47),
    ENSEMBLE(48, 55),
    BRASS(56, 63),
    REED(64, 71),
    PIPE(72, 79),
    SYNTH_LEAD(80, 87),
    SYNTH_PAD(88, 95),
    SYNTH_EFFECTS(96, 103),
    ETHNIC(104, 111),
    // TODO: sao instrumentos melodicos de percussao; a bateria de verdade fica no canal 10 do MIDI
    PERCUSSION(112, 119),
    SOUND_EFFECTS(120, 127);

    private final int minInstrument;
    private final int maxInstrument;

    InstrumentType(int minInstrument, int maxInstrument){
        this.minInstrument = minInstrument;
        this.maxInstrument = maxInstrument;
    }

    public int getMinInstrument(){
        return this.minInstrument;
    }

    public int getMaxInstrument(){
        return this.maxInstrument;
    }

    public boolean contains(int instrument){
        return instrument >= this.minInstrument && instrument <= this.maxInstrument;
    }
}
