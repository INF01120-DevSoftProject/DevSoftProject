package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetPercussionWidget extends VoicePresetWidget {

    @Override 
    public Map<String, InstrumentElement> createInstrumentTypes() {
        Map<String, InstrumentElement> instruments = new LinkedHashMap<>();

        instruments.put("Lead 1 (Onda Quadrada)", new InstrumentElement(80, getInstrumentType()));
        instruments.put("Lead 2 (Onda Dente de Serra)", new InstrumentElement(81, getInstrumentType()));
        instruments.put("Lead 3 (Calliope)", new InstrumentElement(82, getInstrumentType()));
        instruments.put("Lead 4 (Chiff)", new InstrumentElement(83, getInstrumentType()));
        instruments.put("Lead 5 (Charang)", new InstrumentElement(84, getInstrumentType()));
        instruments.put("Lead 6 (Voz)", new InstrumentElement(85, getInstrumentType()));
        instruments.put("Lead 7 (Quintas)", new InstrumentElement(86, getInstrumentType()));
        instruments.put("Lead 8 (Baixo e Lead)", new InstrumentElement(87, getInstrumentType()));
        instruments.put("Pad 1 (New Age)", new InstrumentElement(88, getInstrumentType()));
        instruments.put("Pad 2 (Quente)", new InstrumentElement(89, getInstrumentType()));
        instruments.put("Pad 3 (Polissintetizador)", new InstrumentElement(90, getInstrumentType()));
        instruments.put("Pad 4 (Coro)", new InstrumentElement(91, getInstrumentType()));
        instruments.put("Pad 5 (Arco)", new InstrumentElement(92, getInstrumentType()));
        instruments.put("Pad 6 (Metálico)", new InstrumentElement(93, getInstrumentType()));
        instruments.put("Pad 7 (Halo)", new InstrumentElement(94, getInstrumentType()));
        instruments.put("Pad 8 (Sweep)", new InstrumentElement(95, getInstrumentType()));
        instruments.put("FX 1 (Chuva)", new InstrumentElement(96, getInstrumentType()));
        instruments.put("FX 2 (Trilha Sonora)", new InstrumentElement(97, getInstrumentType()));
        instruments.put("FX 3 (Cristal)", new InstrumentElement(98, getInstrumentType()));
        instruments.put("FX 4 (Atmosfera)", new InstrumentElement(99, getInstrumentType()));
        instruments.put("FX 5 (Brilho)", new InstrumentElement(100, getInstrumentType()));
        instruments.put("FX 6 (Goblins)", new InstrumentElement(101, getInstrumentType()));
        instruments.put("FX 7 (Ecos)", new InstrumentElement(102, getInstrumentType()));
        instruments.put("FX 8 (Ficção Científica)", new InstrumentElement(103, getInstrumentType()));
        instruments.put("Sitar", new InstrumentElement(104, getInstrumentType()));
        instruments.put("Banjo", new InstrumentElement(105, getInstrumentType()));
        instruments.put("Shamisen", new InstrumentElement(106, getInstrumentType()));
        instruments.put("Koto", new InstrumentElement(107, getInstrumentType()));
        instruments.put("Kalimba", new InstrumentElement(108, getInstrumentType()));
        instruments.put("Gaita de Foles", new InstrumentElement(109, getInstrumentType()));
        instruments.put("Rabeca", new InstrumentElement(110, getInstrumentType()));
        instruments.put("Shanai", new InstrumentElement(111, getInstrumentType()));
        instruments.put("Sino", new InstrumentElement(112, getInstrumentType()));
        instruments.put("Agogô", new InstrumentElement(113, getInstrumentType()));
        instruments.put("Tambor de Aço", new InstrumentElement(114, getInstrumentType()));
        instruments.put("Bloco de Madeira", new InstrumentElement(115, getInstrumentType()));
        instruments.put("Taiko", new InstrumentElement(116, getInstrumentType()));
        instruments.put("Tom Melódico", new InstrumentElement(117, getInstrumentType()));
        instruments.put("Tambor Sintetizado", new InstrumentElement(118, getInstrumentType()));
        instruments.put("Prato Reverso", new InstrumentElement(119, getInstrumentType()));
        instruments.put("Ruído de Traste de Guitarra", new InstrumentElement(120, getInstrumentType()));
        instruments.put("Som de Respiração", new InstrumentElement(121, getInstrumentType()));
        instruments.put("Som de Ondas do Mar", new InstrumentElement(122, getInstrumentType()));
        instruments.put("Canto de Pássaros", new InstrumentElement(123, getInstrumentType()));
        instruments.put("Toque de Telefone", new InstrumentElement(124, getInstrumentType()));
        instruments.put("Helicóptero", new InstrumentElement(125, getInstrumentType()));
        instruments.put("Aplausos", new InstrumentElement(126, getInstrumentType()));
        instruments.put("Disparo de Arma", new InstrumentElement(127, getInstrumentType()));

        return instruments;
    }

    @Override 
    public Map<String, Octave> createOctaveTypes() {
        Map<String, Octave> octaves = new LinkedHashMap<>();

        octaves.put("Primeira Oitava", new Octave(2));
        octaves.put("Segunda Oitava", new Octave(2));
        octaves.put("Terceira Oitava", new Octave(3));
        octaves.put("Quarta Oitava", new Octave(4));

        return octaves;
    }

    @Override 
    public InstrumentType getInstrumentType() {
        return InstrumentType.PERCUSSION_AND_SYNTH;
    }

    @Override 
    public VolumeElement getDefaultVolume() {
        return new VolumeElement(82);
    }

    @Override 
    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }
}