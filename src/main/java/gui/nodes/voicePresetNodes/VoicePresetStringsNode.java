package gui.nodes.voicePresets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoicePresetStringsNode extends VoicePresetNode {

    public Map<String, Integer> createInstrumentTypes() {
        Map<String, Integer> instruments = new LinkedHashMap<>();

        instruments.put("Violino", 40);
        instruments.put("Viola", 41);
        instruments.put("Violoncelo", 42);
        instruments.put("Contrabaixo", 43);
        instruments.put("Cordas em Tremolo", 44);
        instruments.put("Cordas em Pizzicato", 45);
        instruments.put("Harpa Orquestral", 46);
        instruments.put("Tímpanos", 47);
        instruments.put("Conjunto de Cordas 1", 48);
        instruments.put("Conjunto de Cordas 2", 49);
        instruments.put("Cordas Sintetizadas 1", 50);
        instruments.put("Cordas Sintetizadas 2", 51);
        instruments.put("Coro Aahs", 52);
        instruments.put("Voz Oohs", 53);
        instruments.put("Voz Sintetizada", 54);
        instruments.put("Ataque Orquestral", 55);

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
        return 32;

    }

    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }

    public  List<String> getOctaveNames() {
        return List.copyOf(createOctaveTypes().keySet());
    }

}