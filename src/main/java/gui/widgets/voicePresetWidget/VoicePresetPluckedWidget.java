package gui.widgets.voicePresetWidget;

import java.util.LinkedHashMap;
import java.util.Map;

import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetPluckedWidget extends VoicePresetWidget {
    @Override 
    public Map<String, Octave> createOctaveTypes() {
        Map<String, Octave> octaves = new LinkedHashMap<>();

        octaves.put("Oitava 4 - Central", new Octave(4));
        octaves.put("Oitava 5 - Soprano", new Octave(5));
        octaves.put("Oitava 3 - Tenor", new Octave(3));
        octaves.put("Oitava 2 - Grave", new Octave(2));

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
}