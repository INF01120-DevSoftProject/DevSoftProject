package builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.InstrumentElement;
import musicElements.OctaveElement;
import musicElements.VolumeElement;

public class VoiceBuilderTest {

    @Test
    void buildsVoiceWithSheetMusicAndPreset() {
        SheetMusic sheet = new SheetMusic();
        VoicePreset preset = new VoicePreset(new InstrumentElement(24), new VolumeElement(60), new OctaveElement(3));

        Voice voice = new VoiceBuilder().setSheetMusic(sheet).setVoicePreset(preset).buildVoice();

        assertThat(voice.getSheetMusic()).isSameAs(sheet);
        assertThat(voice.getVoicePreset()).isSameAs(preset);
    }

    @Test
    void usesDefaultPresetWhenNoneIsSet() {
        Voice voice = new VoiceBuilder().setSheetMusic(new SheetMusic()).buildVoice();

        VoicePreset preset = voice.getVoicePreset();
        assertThat(preset.getInstrument().getInstrument()).isEqualTo(VoicePreset.DEFAULT_INSTRUMENT);
        assertThat(preset.getVolume().getVolume()).isEqualTo(VoicePreset.DEFAULT_VOLUME);
        assertThat(preset.getOctave().getOctave()).isEqualTo(VoicePreset.DEFAULT_OCTAVE);
    }

    @Test
    void eachBuildCreatesANewVoice() {
        VoiceBuilder builder = new VoiceBuilder().setSheetMusic(new SheetMusic());

        assertThat(builder.buildVoice()).isNotSameAs(builder.buildVoice());
    }

    @Test
    void missingValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new VoiceBuilder().buildVoice());
        assertThrows(IllegalArgumentException.class,
            () -> new VoiceBuilder().setSheetMusic(new SheetMusic()).setVoicePreset(null).buildVoice());
    }
}
