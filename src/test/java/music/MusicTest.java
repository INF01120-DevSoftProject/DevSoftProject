package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import musicElements.BPMElement;
import musicElements.InstrumentType;

public class MusicTest {

    private Voice newVoice(){
        return new Voice(new SheetMusic(), new VoicePreset(), InstrumentType.PIANO);
    }

    private List<Voice> voices(int count){
        List<Voice> voices = new ArrayList<>();
        for (int i = 0; i < count; i++){
            voices.add(newVoice());
        }
        return voices;
    }

    @Test
    void keepsNameBpmAndVoices() {
        BPMElement bpm = new BPMElement(120);
        List<Voice> voices = voices(4);

        Music music = new Music("Minha musica", bpm, voices);

        assertThat(music.getName()).isEqualTo("Minha musica");
        assertThat(music.getDefaultBPMElement()).isSameAs(bpm);
        assertThat(music.getVoices()).containsExactlyElementsOf(voices);
    }

    @Test
    void changingOriginalListDoesNotChangeMusic() {
        List<Voice> voices = voices(4);

        Music music = new Music("Minha musica", new BPMElement(120), voices);
        voices.add(newVoice());

        assertThat(music.getVoices()).hasSize(4);
    }

    @Test
    void voicesCannotBeModified() {
        Music music = new Music("Minha musica", new BPMElement(120), voices(4));

        assertThrows(UnsupportedOperationException.class, () -> music.getVoices().add(newVoice()));
    }

    @Test
    void invalidValuesAreRejected() {
        BPMElement bpm = new BPMElement(120);
        List<Voice> voices = voices(4);

        assertThrows(IllegalArgumentException.class, () -> new Music(null, bpm, voices));
        assertThrows(IllegalArgumentException.class, () -> new Music("  ", bpm, voices));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", null, voices));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", bpm, null));
        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", bpm, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new Music("Minha musica", bpm, Arrays.asList(newVoice(), newVoice(), newVoice(), null)));
    }

    @Test
    void acceptsFromFourToSixteenVoices() {
        assertThat(Music.MIN_VOICES).isEqualTo(4);
        assertThat(Music.MAX_VOICES).isEqualTo(16);

        for (int count = 4; count <= 16; count++){
            assertThat(new Music("Minha musica", new BPMElement(120), voices(count)).getVoices()).hasSize(count);
        }
    }

    @Test
    void rejectsFewerThanFourVoices() {
        for (int count = 1; count <= 3; count++){
            List<Voice> voices = voices(count);
            assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", new BPMElement(120), voices));
        }
    }

    @Test
    void rejectsMoreThanSixteenVoices() {
        List<Voice> voices = voices(17);

        assertThrows(IllegalArgumentException.class, () -> new Music("Minha musica", new BPMElement(120), voices));
    }
}
