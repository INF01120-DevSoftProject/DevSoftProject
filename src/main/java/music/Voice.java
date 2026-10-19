package music;

import musicElements.InstrumentType;

public class Voice {

    private final SheetMusic sheetMusic;
    private final VoicePreset voicePreset;

    public Voice(InstrumentType type){
        this(new VoicePreset(type));
    }

    public Voice(VoicePreset newVoicePreset){

        if (newVoicePreset == null) throw new IllegalArgumentException("Preset invalido");

        this.sheetMusic = new SheetMusic();
        this.voicePreset = newVoicePreset;
    }

    public SheetMusic getSheetMusic(){
        return this.sheetMusic;
    }

    public VoicePreset getVoicePreset(){
        return this.voicePreset;
    }

    public InstrumentType getInstrumentType(){
        return this.voicePreset.getInstrumentElement().getType();
    }
}
