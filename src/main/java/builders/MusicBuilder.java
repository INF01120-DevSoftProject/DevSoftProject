package builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import music.Music;
import music.SheetMusic;
import music.Voice;
import musicElements.BPMElement;
import musicElements.InstrumentType;

public class MusicBuilder {

    public static final int DEFAULT_BPM = 120;
    public static final int DEFAULT_NUMBER_OF_VOICES = 4;

    // TODO: definir com o grupo as familias das vozes 1, 2, 3 e de 5 a 15
    private static final Map<Integer, InstrumentType> VOICE_INSTRUMENT_TYPES = Map.of(
        0, InstrumentType.STRINGS,
        4, InstrumentType.PERCUSSION
    );

    // TODO: tipo provisorio para as vozes ainda nao definidas
    private static final InstrumentType UNDEFINED_VOICE_INSTRUMENT_TYPE = InstrumentType.PIANO;

    private String name;
    private BPMElement defaultBPM;
    private final List<Voice> voices;

    public MusicBuilder(){
        this.defaultBPM = new BPMElement(DEFAULT_BPM);
        this.voices = new ArrayList<>();
    }

    public static InstrumentType getInstrumentTypeForVoice(int voiceIndex){

        if (voiceIndex < 0 || voiceIndex >= Music.MAX_VOICES) throw new IllegalArgumentException("Voz invalida");

        return VOICE_INSTRUMENT_TYPES.getOrDefault(voiceIndex, UNDEFINED_VOICE_INSTRUMENT_TYPE);
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

    // TODO: com 4 vozes padrao (0 a 3), a voz 4 (PERCUSSION) nao aparece; confirmar com o grupo
    private List<Voice> createDefaultVoices(){

        List<Voice> defaultVoices = new ArrayList<>();

        for (int i = 0; i < DEFAULT_NUMBER_OF_VOICES; i++){
            defaultVoices.add(new VoiceBuilder()
                .setSheetMusic(new SheetMusic())
                .setInstrumentType(getInstrumentTypeForVoice(i))
                .buildVoice());
        }

        return defaultVoices;
    }
}
