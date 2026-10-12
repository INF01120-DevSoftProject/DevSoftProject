package musicElements;

import java.util.ArrayList;
import java.util.List;

public class MusicBuilder {

    public static final int DEFAULT_BPM = 120;

    private String name;
    private BPMElement defaultBPM;
    private final List<Voice> voices;

    public MusicBuilder(){
        this.defaultBPM = new BPMElement(DEFAULT_BPM);
        this.voices = new ArrayList<>();
    }

    public MusicBuilder setName(String newName){
        this.name = newName;
        return this;
    }

    public MusicBuilder setDefaultBPM(BPMElement newDefaultBPM){
        this.defaultBPM = newDefaultBPM;
        return this;
    }

    public MusicBuilder addVoice(VoiceBuilder voiceBuilder){

        if (voiceBuilder == null) throw new IllegalArgumentException("Construtor de voz invalido");

        this.voices.add(voiceBuilder.buildVoice());
        return this;
    }

    public Music buildMusic(){
        return new Music(this.name, this.defaultBPM, this.voices);
    }
}
