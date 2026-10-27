package gui.nodes.voicePresetNodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoicePresetWindsNode extends VoicePresetNode {

    public Map<String, Integer> createInstrumentTypes() {
        Map<String, Integer> instruments = new LinkedHashMap<>();

        instruments.put("Trompete", 56);
        instruments.put("Trombone", 57);
        instruments.put("Tuba", 58);
        instruments.put("Trompete com Surdina", 59);
        instruments.put("Trompa", 60);
        instruments.put("Seção de Metais", 61);
        instruments.put("Metais Sintetizados 1", 62);
        instruments.put("Metais Sintetizados 2", 63);
        instruments.put("Saxofone Soprano", 64);
        instruments.put("Saxofone Alto", 65);
        instruments.put("Saxofone Tenor", 66);
        instruments.put("Saxofone Barítono", 67);
        instruments.put("Oboé", 68);
        instruments.put("Corne Inglês", 69);
        instruments.put("Fagote", 70);
        instruments.put("Clarinete", 71);
        instruments.put("Piccolo", 72);
        instruments.put("Flauta", 73);
        instruments.put("Flauta Doce", 74);
        instruments.put("Flauta de Pã", 75);
        instruments.put("Sopro em Garrafa", 76);
        instruments.put("Shakuhachi", 77);
        instruments.put("Apito", 78);
        instruments.put("Ocarina", 79);

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