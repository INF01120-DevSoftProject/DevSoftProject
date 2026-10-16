package music;

import java.util.List;

import musicElements.BPMElement;

public class Music {

    public static final int MIN_VOICES = 4;
    public static final int MAX_VOICES = 16;


    private String name;
    private final BPMElement defaultBPM;
    private final List<Voice> voices;


    public Music(String newName, BPMElement newDefaultBPM, List<Voice> newVoices){

        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Nome invalido");

        if (newDefaultBPM == null) throw new IllegalArgumentException("BPM invalido");

        if (newVoices == null || newVoices.isEmpty()) throw new IllegalArgumentException("Musica sem vozes");

        if (newVoices.size() < MIN_VOICES) throw new IllegalArgumentException("Musica com vozes de menos");

        if (newVoices.size() > MAX_VOICES) throw new IllegalArgumentException("Musica com vozes demais");

        for (Voice voice : newVoices){
            if (voice == null) throw new IllegalArgumentException("Voz invalida");
        }


        this.name = newName;
        this.defaultBPM = newDefaultBPM;
        this.voices = List.copyOf(newVoices);
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
