package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import musicElements.BPMElement;

public class MusicTest {

    private Voice newVoice(){
        return new Voice(new SheetMusic(), new VoicePreset());
    }

    @Test
    void keepsNameBpmAndVoices() {
        BPMElement bpm = new BPMElement(120);
        Voice voice1 = newVoice();
        Voice voice2 = newVoice();

        Music music = new Music("Minha musica", bpm, List.of(voice1, voice2));

        assertThat(music.getName()).isEqualTo("Minha musica");
        assertThat(music.getDefaultBPM()).isSameAs(bpm);
        assertThat(music.getVoices()).containsExactly(voice1, voice2);
    }

    @Test
    void changingOriginalListDoesNotChangeMusic() {
        List<Voice> voices = new ArrayList<>();
        voices.add(newVoice());

        Music music = new Music("Minha musica", new BPMElement(120), voices);
        voices.add(newVoice());

        assertThat(music.getVoices()).hasSize(1);
    }

    @Test
    void voicesCannotBeModified() {
        Music music = new Music("Minha musica", new BPMElement(120), List.of(newVoice()));

        assertThrows(UnsupportedOperationException.class, () -> music.getVoices().add(newVoice()));
    }

    @Test
    void invalidValuesAreRejected() {
        BPMElement bpm = new BPMElement(120);
        List<Voice> voices = List.of(newVoice());

        assertThrows(IllegalArgumentException.class, () -> new Music(null, bpm, voices));
        assertThrows(IllegalArgumentException.class, () -> new Music("  ", bpm, voices));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", null, voices));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", bpm, null));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", bpm, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", bpm, Arrays.asList(newVoice(), null)));
    }

    @Test
    void acceptsUpToMaxVoices() {
        List<Voice> voices = new ArrayList<>();
        for (int i = 0; i < Music.MAX_VOICES; i++){
            voices.add(newVoice());
        }

        assertThat(new Music("Minha musica", new BPMElement(120), voices).getVoices()).hasSize(Music.MAX_VOICES);

        voices.add(newVoice());
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", new BPMElement(120), voices));
    }
}
