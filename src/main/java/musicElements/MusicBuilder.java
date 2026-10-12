package musicElements;

import java.util.ArrayList;
import java.util.List;

public class MusicBuilder {

    public static final int DEFAULT_BPM = 120;
    public static final int DEFAULT_NUMBER_OF_VOICES = 3;

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

        if (this.voices.isEmpty()){
            return new Music(this.name, this.defaultBPM, createDefaultVoices());
        }

        return new Music(this.name, this.defaultBPM, this.voices);
    }

    private List<Voice> createDefaultVoices(){

        List<Voice> defaultVoices = new ArrayList<>();

        for (int i = 0; i < DEFAULT_NUMBER_OF_VOICES; i++){
            defaultVoices.add(new VoiceBuilder().setSheetMusic(new SheetMusic()).buildVoice());
        }

        return defaultVoices;
    }
}
