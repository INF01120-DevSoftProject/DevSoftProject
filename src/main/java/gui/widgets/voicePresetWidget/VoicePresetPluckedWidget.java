package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetPluckedWidget extends VoicePresetWidget {

    @Override 
    public Map<String, InstrumentElement> createInstrumentTypes() {
        Map<String, InstrumentElement> instruments = new LinkedHashMap<>();

        instruments.put("Violão de Nylon", new InstrumentElement(24, getInstrumentType()));
        instruments.put("Violão de Aço", new InstrumentElement(25, getInstrumentType()));
        instruments.put("Guitarra Jazz", new InstrumentElement(26, getInstrumentType()));
        instruments.put("Guitarra Limpa", new InstrumentElement(27, getInstrumentType()));
        instruments.put("Guitarra Abafada", new InstrumentElement(28, getInstrumentType()));
        instruments.put("Guitarra Overdrive", new InstrumentElement(29, getInstrumentType()));
        instruments.put("Guitarra Distorcida", new InstrumentElement(30, getInstrumentType()));
        instruments.put("Harmônicos de Guitarra", new InstrumentElement(31, getInstrumentType()));
        instruments.put("Baixo Acústico", new InstrumentElement(32, getInstrumentType()));
        instruments.put("Baixo Elétrico com Dedos", new InstrumentElement(33, getInstrumentType()));
        instruments.put("Baixo Elétrico com Palheta", new InstrumentElement(34, getInstrumentType()));
        instruments.put("Baixo Fretless", new InstrumentElement(35, getInstrumentType()));
        instruments.put("Slap Bass 1", new InstrumentElement(36, getInstrumentType()));
        instruments.put("Slap Bass 2", new InstrumentElement(37, getInstrumentType()));
        instruments.put("Baixo Sintetizado 1", new InstrumentElement(38, getInstrumentType()));
        instruments.put("Baixo Sintetizado 2", new InstrumentElement(39, getInstrumentType()));

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
        return InstrumentType.PLUCKED;
    }

    @Override 
    public VolumeElement getDefaultVolume() {
        return new VolumeElement(72);
    }

    @Override 
    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }
}