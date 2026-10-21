package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import music.Music;
import music.Voice;
import music.VoicePreset;
import musicElements.Accidentals;
import musicElements.BPMElement;
import musicElements.DurationTime;
import musicElements.NoteElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.PauseElement;
import musicElements.VolumeElement;

class TextToMusicContextTest {

    private static final NoteElement A = new NoteElement(Notes.A, new Octave(4), DurationTime.QUARTER, Accidentals.NONE);

    private final List<Voice> voices = new Music("Teste", Music.MIN_VOICES).getVoices();

    @Test
    void startsWithTheValuesOfTheFirstVoicePreset() {
        TextToMusicContext context = new TextToMusicContext(voices);
        VoicePreset first = voices.get(0).getVoicePreset();

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(first.getDefaultOctave().getOctaveInt());
        assertThat(context.getBaseOctave().getOctaveInt()).isEqualTo(first.getDefaultOctave().getOctaveInt());
        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(first.getVolumeElement().getVolume());
        assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(first.getInstrumentElement().getInstrument());
        assertThat(context.getBpm()).isEqualTo(TextToMusicContext.DEFAULT_BPM);
        assertThat(context.getLastMusicElement()).isNull();
    }

    @Test
    void appendToSheetMusicKeepsTheLastElement() {
        TextToMusicContext context = new TextToMusicContext(voices);
        PauseElement pause = new PauseElement(DurationTime.QUARTER);

        context.appendToSheetMusic(A);
        assertThat(context.getLastMusicElement()).isSameAs(A);

        context.appendToSheetMusic(pause);
        assertThat(context.getLastMusicElement()).isSameAs(pause);
    }

    @Test
    void appendControlElementDoesNotChangeTheLastElement() {
        TextToMusicContext context = new TextToMusicContext(voices);

        context.appendToSheetMusic(A);
        context.appendControlElement(new VolumeElement(80));
        context.appendControlElement(new BPMElement(130));

        assertThat(context.getLastMusicElement()).isSameAs(A);
    }

    @Test
    void bothAppendsWriteToTheCurrentVoice() {
        TextToMusicContext context = new TextToMusicContext(voices);
        VolumeElement volume = new VolumeElement(80);

        context.appendToSheetMusic(A);
        context.appendControlElement(volume);

        assertThat(voices.get(0).getSheetMusic().getSheetMusic()).containsExactly(A, volume);
    }

    @Test
    void closeVoiceStartsTheNextVoiceFromItsPreset() {
        TextToMusicContext context = new TextToMusicContext(voices);
        VoicePreset second = voices.get(1).getVoicePreset();

        context.appendToSheetMusic(A);
        context.setCurrentOctave(new Octave(8));
        context.setBpm(200);
        context.closeVoice();

        assertThat(context.getLastMusicElement()).isNull();
        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(second.getDefaultOctave().getOctaveInt());
        assertThat(context.getBaseOctave().getOctaveInt()).isEqualTo(second.getDefaultOctave().getOctaveInt());
        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(second.getVolumeElement().getVolume());
        assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(second.getInstrumentElement().getInstrument());
        assertThat(context.getBpm()).isEqualTo(TextToMusicContext.DEFAULT_BPM);
    }

    @Test
    void eachVoiceGetsItsOwnElements() {
        TextToMusicContext context = new TextToMusicContext(voices);
        PauseElement pause = new PauseElement(DurationTime.HALF);

        context.appendToSheetMusic(A);
        context.closeVoice();
        context.appendToSheetMusic(pause);

        assertThat(voices.get(0).getSheetMusic().getSheetMusic()).containsExactly(A);
        assertThat(voices.get(1).getSheetMusic().getSheetMusic()).containsExactly(pause);
        assertThat(voices.get(2).getSheetMusic().getSheetMusic()).isEmpty();
    }
}
