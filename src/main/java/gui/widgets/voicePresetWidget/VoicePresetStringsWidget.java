package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetStringsWidget extends VoicePresetWidget {

    @Override 
    public Map<String, InstrumentElement> createInstrumentTypes() {
        Map<String, InstrumentElement> instruments = new LinkedHashMap<>();

        instruments.put("Violino", new InstrumentElement(40, getInstrumentType()));
        instruments.put("Viola", new InstrumentElement(41, getInstrumentType()));
        instruments.put("Violoncelo", new InstrumentElement(42, getInstrumentType()));
        instruments.put("Contrabaixo", new InstrumentElement(43, getInstrumentType()));
        instruments.put("Cordas em Tremolo", new InstrumentElement(44, getInstrumentType()));
        instruments.put("Cordas em Pizzicato", new InstrumentElement(45, getInstrumentType()));
        instruments.put("Harpa Orquestral", new InstrumentElement(46, getInstrumentType()));
        instruments.put("Tímpanos", new InstrumentElement(47, getInstrumentType()));
        instruments.put("Conjunto de Cordas 1", new InstrumentElement(48, getInstrumentType()));
        instruments.put("Conjunto de Cordas 2", new InstrumentElement(49, getInstrumentType()));
        instruments.put("Cordas Sintetizadas 1", new InstrumentElement(50, getInstrumentType()));
        instruments.put("Cordas Sintetizadas 2", new InstrumentElement(51, getInstrumentType()));
        instruments.put("Coro Aahs", new InstrumentElement(52, getInstrumentType()));
        instruments.put("Voz Oohs", new InstrumentElement(53, getInstrumentType()));
        instruments.put("Voz Sintetizada", new InstrumentElement(54, getInstrumentType()));
        instruments.put("Ataque Orquestral", new InstrumentElement(55, getInstrumentType()));

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
        return InstrumentType.STRINGS;
    }

    @Override 
    public VolumeElement getDefaultVolume() {
        return new VolumeElement(83);
    }
    
    @Override 
    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }
}