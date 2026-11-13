package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetKeysWidget extends VoicePresetWidget {

    @Override 
    public Map<String, InstrumentElement> createInstrumentTypes() {
        Map<String, InstrumentElement> instruments = new LinkedHashMap<>();

        instruments.put("Piano", new InstrumentElement(0, getInstrumentType()));
        instruments.put("Piano Brilhante", new InstrumentElement(1, getInstrumentType()));
        instruments.put("Piano de Cauda Elétrico", new InstrumentElement(2, getInstrumentType()));
        instruments.put("Piano Honky-tonk", new InstrumentElement(3, getInstrumentType()));
        instruments.put("Piano Elétrico 1", new InstrumentElement(4, getInstrumentType()));
        instruments.put("Piano Elétrico 2", new InstrumentElement(5, getInstrumentType()));
        instruments.put("Cravo", new InstrumentElement(6, getInstrumentType()));
        instruments.put("Clavinet", new InstrumentElement(7, getInstrumentType()));

        return instruments;
    }

    @Override 
    public Map<String, Octave> createOctaveTypes() {
        Map<String, Octave> octaves = new LinkedHashMap<>();

        octaves.put("Segunda Oitava", new Octave(2));
        octaves.put("Terceira Oitava", new Octave(3));
        octaves.put("Quarta Oitava", new Octave(4));

        return octaves;
    }

    @Override 
    public InstrumentType getInstrumentType() {
        return InstrumentType.KEYS;
    }

    @Override 
    public VolumeElement getDefaultVolume() {
        return new VolumeElement(66);
    }

    @Override 
    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }
}