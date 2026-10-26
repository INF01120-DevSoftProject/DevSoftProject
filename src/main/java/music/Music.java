package music;

import java.util.ArrayList;
import java.util.List;

import musicElements.BPMElement;
import musicElements.InstrumentType;

public class Music {

    public static final int DEFAULT_BPM = 120;
    public static final int MIN_VOICES = 4;
    public static final int MAX_VOICES = 15;

    private static final List<InstrumentType> VOICE_INSTRUMENT_CYCLE = List.of(
        InstrumentType.STRINGS,
        InstrumentType.KEYS,
        InstrumentType.PLUCKED,
        InstrumentType.WINDS,
        InstrumentType.PERCUSSION_AND_SYNTH);

    private String name;
    private final BPMElement defaultBPM;
    private final List<Voice> voices;

    public Music(String newName){
        this(newName, new BPMElement(DEFAULT_BPM), MIN_VOICES);
    }

    public Music(String newName, int numberOfVoices){
        this(newName, new BPMElement(DEFAULT_BPM), numberOfVoices);
    }

    public Music(String newName, BPMElement newDefaultBPM, int numberOfVoices){

        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Nome invalido");
        if (newDefaultBPM == null) throw new IllegalArgumentException("BPM invalido");
        if (numberOfVoices < MIN_VOICES) throw new IllegalArgumentException("Musica com vozes de menos");
        if (numberOfVoices > MAX_VOICES) throw new IllegalArgumentException("Musica com vozes demais");

        this.name = newName;
        this.defaultBPM = newDefaultBPM;
        this.voices = createVoices(numberOfVoices);
    }

    private static List<Voice> createVoices(int numberOfVoices){

        List<Voice> createdVoices = new ArrayList<>();

        for (int i = 0; i < numberOfVoices; i++){
            InstrumentType type = VOICE_INSTRUMENT_CYCLE.get(i % VOICE_INSTRUMENT_CYCLE.size());
            createdVoices.add(new Voice(type));
        }

        return List.copyOf(createdVoices);
    }

    public String getName(){
        return this.name;
    }

    public void setName(String newName){

        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Nome invalido");

        this.name = newName;
    }

    public BPMElement getDefaultBPMElement(){
        return this.defaultBPM;
    }

    public List<Voice> getVoices(){
        return this.voices;
    }
}
