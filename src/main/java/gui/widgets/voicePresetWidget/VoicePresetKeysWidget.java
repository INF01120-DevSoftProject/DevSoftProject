package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetKeysWidget extends VoicePresetWidget {

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
}