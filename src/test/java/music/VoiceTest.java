package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import musicElements.InstrumentType;

public class VoiceTest {

    @Test
    void createsEmptySheetMusicAndKeepsPreset() {
        Voice byType = new Voice(InstrumentType.STRINGS);
        assertThat(byType.getSheetMusic().getSheetMusic()).isEmpty();
        assertThat(byType.getInstrumentType()).isEqualTo(InstrumentType.STRINGS);

        VoicePreset customPreset = new VoicePreset(InstrumentType.KEYS);
        Voice byPreset = new Voice(customPreset);
        assertThat(byPreset.getVoicePreset()).isSameAs(customPreset);
        assertThat(byPreset.getInstrumentType()).isEqualTo(InstrumentType.KEYS);
    }

    @Test
    void nullValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Voice((InstrumentType) null));
        assertThrows(IllegalArgumentException.class, () -> new Voice((VoicePreset) null));
    }
}
