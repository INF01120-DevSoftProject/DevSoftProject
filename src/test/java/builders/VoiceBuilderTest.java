package builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

public class VoiceBuilderTest {

    @Test
    void buildsVoiceWithSheetMusicPresetAndInstrumentType() {
        SheetMusic sheet = new SheetMusic();
        VoicePreset preset = new VoicePreset(new InstrumentElement(24), new VolumeElement(60), new Octave(3));

        Voice voice = new VoiceBuilder()
            .setSheetMusic(sheet)
            .setVoicePreset(preset)
            .setInstrumentType(InstrumentType.GUITAR)
            .buildVoice();

        assertThat(voice.getSheetMusic()).isSameAs(sheet);
        assertThat(voice.getVoicePreset()).isSameAs(preset);
        assertThat(voice.getInstrumentType()).isEqualTo(InstrumentType.GUITAR);
    }

    @Test
    void usesDefaultPresetWhenNoneIsSet() {
        Voice voice = new VoiceBuilder().setSheetMusic(new SheetMusic()).setInstrumentType(InstrumentType.PIANO).buildVoice();

        VoicePreset preset = voice.getVoicePreset();
        assertThat(preset.getInstrumentElement().getInstrument()).isEqualTo(VoicePreset.DEFAULT_INSTRUMENT);
        assertThat(preset.getVolumeElement().getVolume()).isEqualTo(VoicePreset.DEFAULT_VOLUME);
        assertThat(preset.getDefaultOctave().getOctaveInt()).isEqualTo(VoicePreset.DEFAULT_OCTAVE);
    }

    @Test
    void eachBuildCreatesANewVoice() {
        VoiceBuilder builder = new VoiceBuilder().setSheetMusic(new SheetMusic()).setInstrumentType(InstrumentType.PIANO);

        assertThat(builder.buildVoice()).isNotSameAs(builder.buildVoice());
    }

    @Test
    void missingValuesAreRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new VoiceBuilder().setInstrumentType(InstrumentType.PIANO).buildVoice());
        assertThrows(IllegalArgumentException.class,
            () -> new VoiceBuilder().setSheetMusic(new SheetMusic()).setVoicePreset(null).setInstrumentType(InstrumentType.PIANO).buildVoice());
        assertThrows(IllegalArgumentException.class,
            () -> new VoiceBuilder().setSheetMusic(new SheetMusic()).buildVoice());
    }
}
