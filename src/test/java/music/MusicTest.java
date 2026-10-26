package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import musicElements.BPMElement;
import musicElements.InstrumentType;

public class MusicTest {

    @Test
    void defaultMusicHasFourVoicesDefaultBpmAndBaseCycle() {
        Music music = new Music("Minha musica");

        assertThat(music.getName()).isEqualTo("Minha musica");
        assertThat(music.getDefaultBPMElement().getBPM()).isEqualTo(Music.DEFAULT_BPM);
        assertThat(music.getVoices())
            .extracting(Voice::getInstrumentType)
            .containsExactly(
                InstrumentType.STRINGS,
                InstrumentType.KEYS,
                InstrumentType.PLUCKED,
                InstrumentType.WINDS);
    }

    @Test
    void cycleRepeatsThroughAllInstrumentTypes() {
        Music music = new Music("Minha musica", new BPMElement(90), 6);

        assertThat(music.getDefaultBPMElement().getBPM()).isEqualTo(90);
        assertThat(music.getVoices())
            .extracting(Voice::getInstrumentType)
            .containsExactly(
                InstrumentType.STRINGS,
                InstrumentType.KEYS,
                InstrumentType.PLUCKED,
                InstrumentType.WINDS,
                InstrumentType.PERCUSSION_AND_SYNTH,
                InstrumentType.STRINGS);
    }

    @Test
    void voiceCountBoundaries() {
        assertThat(new Music("Minha musica", Music.MIN_VOICES).getVoices()).hasSize(4);
        assertThat(new Music("Minha musica", Music.MAX_VOICES).getVoices()).hasSize(15);

        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", Music.MIN_VOICES - 1));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", Music.MAX_VOICES + 1));
    }

    @Test
    void invalidArgumentsAndVoiceListMutationAreRejected() {
        Music music = new Music("Minha musica");

        assertThrows(IllegalArgumentException.class, () -> new Music(null));
        assertThrows(IllegalArgumentException.class, () -> new Music("  "));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", null, 4));
        assertThrows(IllegalArgumentException.class, () -> music.setName(" "));
        assertThrows(UnsupportedOperationException.class, () -> music.getVoices().add(new Voice(InstrumentType.KEYS)));
    }
}
