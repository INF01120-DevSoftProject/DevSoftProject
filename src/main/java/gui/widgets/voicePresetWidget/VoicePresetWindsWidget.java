package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetWindsWidget extends VoicePresetWidget {

    @Override 
    public Map<String, InstrumentElement> createInstrumentTypes() {
        Map<String, InstrumentElement> instruments = new LinkedHashMap<>();

        instruments.put("Trompete", new InstrumentElement(56, getInstrumentType()));
        instruments.put("Trombone", new InstrumentElement(57, getInstrumentType()));
        instruments.put("Tuba", new InstrumentElement(58, getInstrumentType()));
        instruments.put("Trompete com Surdina", new InstrumentElement(59, getInstrumentType()));
        instruments.put("Trompa", new InstrumentElement(60, getInstrumentType()));
        instruments.put("Seção de Metais", new InstrumentElement(61, getInstrumentType()));
        instruments.put("Metais Sintetizados 1", new InstrumentElement(62, getInstrumentType()));
        instruments.put("Metais Sintetizados 2", new InstrumentElement(63, getInstrumentType()));
        instruments.put("Saxofone Soprano", new InstrumentElement(64, getInstrumentType()));
        instruments.put("Saxofone Alto", new InstrumentElement(65, getInstrumentType()));
        instruments.put("Saxofone Tenor", new InstrumentElement(66, getInstrumentType()));
        instruments.put("Saxofone Barítono", new InstrumentElement(67, getInstrumentType()));
        instruments.put("Oboé", new InstrumentElement(68, getInstrumentType()));
        instruments.put("Corne Inglês", new InstrumentElement(69, getInstrumentType()));
        instruments.put("Fagote", new InstrumentElement(70, getInstrumentType()));
        instruments.put("Clarinete", new InstrumentElement(71, getInstrumentType()));
        instruments.put("Piccolo", new InstrumentElement(72, getInstrumentType()));
        instruments.put("Flauta", new InstrumentElement(73, getInstrumentType()));
        instruments.put("Flauta Doce", new InstrumentElement(74, getInstrumentType()));
        instruments.put("Flauta de Pã", new InstrumentElement(75, getInstrumentType()));
        instruments.put("Sopro em Garrafa", new InstrumentElement(76, getInstrumentType()));
        instruments.put("Shakuhachi", new InstrumentElement(77, getInstrumentType()));
        instruments.put("Apito", new InstrumentElement(78, getInstrumentType()));
        instruments.put("Ocarina", new InstrumentElement(79, getInstrumentType()));

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
        return InstrumentType.WINDS;
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