package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import musicElements.InstrumentElement;
import musicElements.OctaveElement;
import musicElements.VolumeElement;

public class VoicePresetTest {

    @Test
    void defaultPresetUsesDefaultValues() {
        VoicePreset preset = new VoicePreset();

        assertThat(preset.getInstrument().getInstrument()).isEqualTo(VoicePreset.DEFAULT_INSTRUMENT);
        assertThat(preset.getVolume().getVolume()).isEqualTo(VoicePreset.DEFAULT_VOLUME);
        assertThat(preset.getOctave().getOctave()).isEqualTo(VoicePreset.DEFAULT_OCTAVE);
    }

    @Test
    void customPresetKeepsItsValues() {
        InstrumentElement instrument = new InstrumentElement(24);
        VolumeElement volume = new VolumeElement(60);
        OctaveElement octave = new OctaveElement(3);

        VoicePreset preset = new VoicePreset(instrument, volume, octave);

        assertThat(preset.getInstrument()).isSameAs(instrument);
        assertThat(preset.getVolume()).isSameAs(volume);
        assertThat(preset.getOctave()).isSameAs(octave);
    }

    @Test
    void nullValuesAreRejected() {
        InstrumentElement instrument = new InstrumentElement(0);
        VolumeElement volume = new VolumeElement(100);
        OctaveElement octave = new OctaveElement(5);

        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(null, volume, octave));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(instrument, null, octave));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(instrument, volume, null));
    }
}
