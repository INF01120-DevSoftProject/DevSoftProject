package builders;

import music.SheetMusic;
import music.Voice;
import music.VoicePreset;

public class VoiceBuilder {

    private SheetMusic sheetMusic;
    private VoicePreset voicePreset;

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

    public Voice buildVoice(){
        return new Voice(this.sheetMusic, this.voicePreset);
    }
}
