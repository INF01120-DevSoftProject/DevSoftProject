package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VoiceTest {

    @Test
    void keepsSheetMusicAndPreset() {
        SheetMusic sheet = new SheetMusic();
        VoicePreset preset = new VoicePreset();

        Voice voice = new Voice(sheet, preset);

        assertThat(voice.getSheetMusic()).isSameAs(sheet);
        assertThat(voice.getVoicePreset()).isSameAs(preset);
    }

    @Test
    void nullValuesAreRejected() {
        SheetMusic sheet = new SheetMusic();
        VoicePreset preset = new VoicePreset();

        assertThrows(IllegalArgumentException.class, () -> new Voice(null, preset));
        assertThrows(IllegalArgumentException.class, () -> new Voice(sheet, null));
    }
}
