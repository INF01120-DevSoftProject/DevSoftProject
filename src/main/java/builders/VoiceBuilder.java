package builders;

import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.InstrumentType;

public class VoiceBuilder {

    private SheetMusic sheetMusic;
    private VoicePreset voicePreset;
    private InstrumentType instrumentType;

    public VoiceBuilder(){
        this.voicePreset = new VoicePreset();
    }

    public VoiceBuilder setSheetMusic(SheetMusic newSheetMusic){
        this.sheetMusic = newSheetMusic;
        return this;
    }

    public VoiceBuilder setVoicePreset(VoicePreset newVoicePreset){
        this.voicePreset = newVoicePreset;
        return this;
    }

    public VoiceBuilder setInstrumentType(InstrumentType newInstrumentType){
        this.instrumentType = newInstrumentType;
        return this;
    }

    public Voice buildVoice(){
        return new Voice(this.sheetMusic, this.voicePreset, this.instrumentType);
    }
}
