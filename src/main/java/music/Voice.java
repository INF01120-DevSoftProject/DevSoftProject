package music;

import java.util.List;
import musicElements.InstrumentType;
import musicElements.MusicElement;

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

    public List<MusicElement> getSheetMusicList(){
        return this.sheetMusic.getSheetMusic();
    }

    public VoicePreset getVoicePreset(){
        return this.voicePreset;
    }

    public InstrumentType getInstrumentType(){
        return this.voicePreset.getInstrumentElement().getType();
    }
}
