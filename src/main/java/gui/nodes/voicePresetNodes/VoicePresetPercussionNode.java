package gui.nodes.voicePresetNodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoicePresetPercussionNode extends VoicePresetNode {

    public Map<String, Integer> createInstrumentTypes() {
        Map<String, Integer> instruments = new LinkedHashMap<>();

        instruments.put("Lead 1 (Onda Quadrada)", 80);
        instruments.put("Lead 2 (Onda Dente de Serra)", 81);
        instruments.put("Lead 3 (Calliope)", 82);
        instruments.put("Lead 4 (Chiff)", 83);
        instruments.put("Lead 5 (Charang)", 84);
        instruments.put("Lead 6 (Voz)", 85);
        instruments.put("Lead 7 (Quintas)", 86);
        instruments.put("Lead 8 (Baixo e Lead)", 87);

        instruments.put("Pad 1 (New Age)", 88);
        instruments.put("Pad 2 (Quente)", 89);
        instruments.put("Pad 3 (Polissintetizador)", 90);
        instruments.put("Pad 4 (Coro)", 91);
        instruments.put("Pad 5 (Arco)", 92);
        instruments.put("Pad 6 (Metálico)", 93);
        instruments.put("Pad 7 (Halo)", 94);
        instruments.put("Pad 8 (Sweep)", 95);

        instruments.put("FX 1 (Chuva)", 96);
        instruments.put("FX 2 (Trilha Sonora)", 97);
        instruments.put("FX 3 (Cristal)", 98);
        instruments.put("FX 4 (Atmosfera)", 99);
        instruments.put("FX 5 (Brilho)", 100);
        instruments.put("FX 6 (Goblins)", 101);
        instruments.put("FX 7 (Ecos)", 102);
        instruments.put("FX 8 (Ficção Científica)", 103);

        instruments.put("Sitar", 104);
        instruments.put("Banjo", 105);
        instruments.put("Shamisen", 106);
        instruments.put("Koto", 107);
        instruments.put("Kalimba", 108);
        instruments.put("Gaita de Foles", 109);
        instruments.put("Rabeca", 110);
        instruments.put("Shanai", 111);

        instruments.put("Sino", 112);
        instruments.put("Agogô", 113);
        instruments.put("Tambor de Aço", 114);
        instruments.put("Bloco de Madeira", 115);
        instruments.put("Taiko", 116);
        instruments.put("Tom Melódico", 117);
        instruments.put("Tambor Sintetizado", 118);
        instruments.put("Prato Reverso", 119);

        instruments.put("Ruído de Traste de Guitarra", 120);
        instruments.put("Som de Respiração", 121);
        instruments.put("Som de Ondas do Mar", 122);
        instruments.put("Canto de Pássaros", 123);
        instruments.put("Toque de Telefone", 124);
        instruments.put("Helicóptero", 125);
        instruments.put("Aplausos", 126);
        instruments.put("Disparo de Arma", 127);

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