package music;

import musicElements.InstrumentType;

public class Voice {

    private final SheetMusic sheetMusic;
    private final VoicePreset voicePreset;
    private final InstrumentType instrumentType;

    public Voice(SheetMusic newSheetMusic, VoicePreset newVoicePreset, InstrumentType newInstrumentType){

        if (newSheetMusic == null) throw new IllegalArgumentException("Partitura invalida");
        if (newVoicePreset == null) throw new IllegalArgumentException("Preset invalido");
        if (newInstrumentType == null) throw new IllegalArgumentException("Tipo de instrumento invalido");

        this.sheetMusic = newSheetMusic;
        this.voicePreset = newVoicePreset;
        this.instrumentType = newInstrumentType;
    }

    public SheetMusic getSheetMusic(){
        return this.sheetMusic;
    }

    public VoicePreset getVoicePreset(){
        return this.voicePreset;
    }

    public InstrumentType getInstrumentType(){
        return this.instrumentType;
    }
}
