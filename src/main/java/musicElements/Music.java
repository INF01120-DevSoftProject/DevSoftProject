package musicElements;

import java.util.List;

public class Music {

    private final String name;
    private final BPMElement defaultBPM;
    private final List<Voice> voices;

    public Music(String newName, BPMElement newDefaultBPM, List<Voice> newVoices){

        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Nome invalido");
        if (newDefaultBPM == null) throw new IllegalArgumentException("BPM invalido");
        if (newVoices == null || newVoices.isEmpty()) throw new IllegalArgumentException("Musica sem vozes");
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

    public BPMElement getDefaultBPM(){
        return this.defaultBPM;
    }

    public List<Voice> getVoices(){
        return this.voices;
    }
}
