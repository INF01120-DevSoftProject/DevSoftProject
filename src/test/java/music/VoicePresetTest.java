package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoicePresetTest {

    @Test
    void presetByTypeUsesExpectedDefaults() {
        VoicePreset strings = new VoicePreset(InstrumentType.STRINGS);
        assertThat(strings.getInstrumentElement().getInstrument()).isEqualTo(40);
        assertThat(strings.getVolumeElement().getVolume()).isEqualTo(100);
        assertThat(strings.getDefaultOctave().getOctaveInt()).isEqualTo(6);

        VoicePreset keys = new VoicePreset(InstrumentType.KEYS);
        assertThat(keys.getInstrumentElement().getInstrument()).isEqualTo(0);
        assertThat(keys.getVolumeElement().getVolume()).isEqualTo(90);
        assertThat(keys.getDefaultOctave().getOctaveInt()).isEqualTo(4);

        VoicePreset plucked = new VoicePreset(InstrumentType.PLUCKED);
        assertThat(plucked.getInstrumentElement().getInstrument()).isEqualTo(24);
        assertThat(plucked.getVolumeElement().getVolume()).isEqualTo(90);
        assertThat(plucked.getDefaultOctave().getOctaveInt()).isEqualTo(3);

        VoicePreset winds = new VoicePreset(InstrumentType.WINDS);
        assertThat(winds.getInstrumentElement().getInstrument()).isEqualTo(56);
        assertThat(winds.getVolumeElement().getVolume()).isEqualTo(80);
        assertThat(winds.getDefaultOctave().getOctaveInt()).isEqualTo(5);

        VoicePreset percussion = new VoicePreset(InstrumentType.PERCUSSION_AND_SYNTH);
        assertThat(percussion.getInstrumentElement().getInstrument()).isEqualTo(80);
        assertThat(percussion.getVolumeElement().getVolume()).isEqualTo(80);
        assertThat(percussion.getDefaultOctave().getOctaveInt()).isEqualTo(5);
    }

    @Test
    void customPresetKeepsItsValues() {
        InstrumentElement instrument = new InstrumentElement(24, InstrumentType.PLUCKED);
        VolumeElement volume = new VolumeElement(60);
        Octave octave = new Octave(3);

        VoicePreset preset = new VoicePreset(instrument, volume, octave);

        assertThat(preset.getInstrumentElement()).isSameAs(instrument);
        assertThat(preset.getVolumeElement()).isSameAs(volume);
        assertThat(preset.getDefaultOctave()).isSameAs(octave);
    }

    @Test
    void intConstructorDiscoversInstrumentTypeAndFallsBackAboveMax() {
        for (InstrumentType type : InstrumentType.values()) {
            VoicePreset preset = new VoicePreset(type.getMinInstrument(), 75, 4);

            assertThat(preset.getInstrumentElement().getInstrument()).isEqualTo(type.getMinInstrument());
            assertThat(preset.getInstrumentElement().getType()).isEqualTo(type);
            assertThat(preset.getVolumeElement().getVolume()).isEqualTo(75);
            assertThat(preset.getDefaultOctave().getOctaveInt()).isEqualTo(4);
        }

        VoicePreset fallback = new VoicePreset(128, 75, 4);
        assertThat(fallback.getInstrumentElement().getType()).isEqualTo(InstrumentType.KEYS);
        assertThat(fallback.getInstrumentElement().getInstrument()).isEqualTo(InstrumentType.KEYS.getDefaultInstrument());
    }

    @Test
    void invalidIntValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(-1, 80, 4));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(0, -1, 4));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(0, 128, 4));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(0, 80, -1));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(0, 80, 9));
    }

    @Test
    void nullValuesAreRejected() {
        InstrumentElement instrument = new InstrumentElement(InstrumentType.KEYS);
        VolumeElement volume = new VolumeElement(100);
        Octave octave = new Octave(5);

        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(null));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(null, volume, octave));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(instrument, null, octave));
        assertThrows(IllegalArgumentException.class, () -> new VoicePreset(instrument, volume, null));
    }
}
