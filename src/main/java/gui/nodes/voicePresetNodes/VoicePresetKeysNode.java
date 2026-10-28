package gui.nodes.voicePresets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoicePresetKeysNode extends VoicePresetNode {

    public Map<String, Integer> createInstrumentTypes() {
        Map<String, Integer> instruments = new LinkedHashMap<>();

        instruments.put("Piano", 0);
        instruments.put("Piano Brilhante", 1);
        instruments.put("Piano de Cauda Elétrico", 2);
        instruments.put("Piano Honky-tonk", 3);
        instruments.put("Piano Elétrico 1", 4);
        instruments.put("Piano Elétrico 2", 5);
        instruments.put("Cravo", 6);
        instruments.put("Clavinet", 7);

        return instruments;
    }

    public Map<String, Integer> createOctaveTypes() {
        Map<String, Integer> octaves = new LinkedHashMap<>();

        octaves.put("Segunda Oitava", 2);
        octaves.put("Terceira Oitava", 3);
        octaves.put("Quarta Oitava", 4);

        return octaves;
    }

    public String getDefaultOctave() {
        return new ArrayList<>(createOctaveTypes().keySet()).get(1);
    }

    public int getDefaultVolume() {
        return 56;

    }

    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }

    public  List<String> getOctaveNames() {
        return List.copyOf(createOctaveTypes().keySet());
    }



}