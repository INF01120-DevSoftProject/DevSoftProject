package gui.widgets.voicePresetWidget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoicePresetPluckedWidget extends VoicePresetWidget {

    public Map<String, Integer> createInstrumentTypes() {
        Map<String, Integer> instruments = new LinkedHashMap<>();

        instruments.put("Violão de Nylon", 24);
        instruments.put("Violão de Aço", 25);
        instruments.put("Guitarra Jazz", 26);
        instruments.put("Guitarra Limpa", 27);
        instruments.put("Guitarra Abafada", 28);
        instruments.put("Guitarra Overdrive", 29);
        instruments.put("Guitarra Distorcida", 30);
        instruments.put("Harmônicos de Guitarra", 31);
        instruments.put("Baixo Acústico", 32);
        instruments.put("Baixo Elétrico com Dedos", 33);
        instruments.put("Baixo Elétrico com Palheta", 34);
        instruments.put("Baixo Fretless", 35);
        instruments.put("Slap Bass 1", 36);
        instruments.put("Slap Bass 2", 37);
        instruments.put("Baixo Sintetizado 1", 38);
        instruments.put("Baixo Sintetizado 2", 39);

        return instruments;
    }

    public Map<String, Integer> createOctaveTypes() {
        Map<String, Integer> octaves = new LinkedHashMap<>();

        octaves.put("Primeira Oitava", 1);
        octaves.put("Segunda Oitava", 2);
        octaves.put("Terceira Oitava", 3);
        octaves.put("Quarta Oitava", 4);

        return octaves;
    }

    public String getDefaultOctave() {
        return new ArrayList<>(createOctaveTypes().keySet()).get(1);
    }

    public int getDefaultVolume() {
        return 72;

    }

    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }

    public  List<String> getOctaveNames() {
        return List.copyOf(createOctaveTypes().keySet());
    }

}