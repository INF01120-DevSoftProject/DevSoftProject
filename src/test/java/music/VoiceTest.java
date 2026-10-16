package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import musicElements.InstrumentType;

public class VoiceTest {

    @Test
    void keepsSheetMusicPresetAndInstrumentType() {
        SheetMusic sheet = new SheetMusic();
        VoicePreset preset = new VoicePreset();

        Voice voice = new Voice(sheet, preset, InstrumentType.STRINGS);

        assertThat(voice.getSheetMusic()).isSameAs(sheet);
        assertThat(voice.getVoicePreset()).isSameAs(preset);
        assertThat(voice.getInstrumentType()).isEqualTo(InstrumentType.STRINGS);
    }

    @Test
    void nullValuesAreRejected() {
        SheetMusic sheet = new SheetMusic();
        VoicePreset preset = new VoicePreset();

        assertThrows(IllegalArgumentException.class, () -> new Voice(null, preset, InstrumentType.PIANO));
        assertThrows(IllegalArgumentException.class, () -> new Voice(sheet, null, InstrumentType.PIANO));
        assertThrows(IllegalArgumentException.class, () -> new Voice(sheet, preset, null));
    }
}
