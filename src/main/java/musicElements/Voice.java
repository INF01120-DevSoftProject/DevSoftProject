package musicElements;

public class Voice {

    private final SheetMusic sheetMusic;
    private final VoicePreset voicePreset;

    public Voice(SheetMusic newSheetMusic, VoicePreset newVoicePreset){

        if (newSheetMusic == null) throw new IllegalArgumentException("Partitura invalida");
        if (newVoicePreset == null) throw new IllegalArgumentException("Preset invalido");

        this.sheetMusic = newSheetMusic;
        this.voicePreset = newVoicePreset;
    }

    public SheetMusic getSheetMusic(){
        return this.sheetMusic;
    }

    public VoicePreset getVoicePreset(){
        return this.voicePreset;
    }
}
