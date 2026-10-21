package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import music.Voice;
import music.VoicePreset;
import musicElements.Accidentals;
import musicElements.BPMElement;
import musicElements.DurationTime;
import musicElements.InstrumentType;
import musicElements.NoteElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.PauseElement;
import musicElements.VolumeElement;

class TextToMusicContextTest {

    private static final NoteElement A = new NoteElement(Notes.A, new Octave(4), DurationTime.QUARTER, Accidentals.NONE);

    @Test
    void startsWithTheValuesOfTheFirstVoicePreset() {
        TextToMusicContext context = new TextToMusicContext();
        VoicePreset strings = new VoicePreset(InstrumentType.STRINGS);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(strings.getDefaultOctave().getOctaveInt());
        assertThat(context.getBaseOctave().getOctaveInt()).isEqualTo(strings.getDefaultOctave().getOctaveInt());
        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(strings.getVolumeElement().getVolume());
        assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(strings.getInstrumentElement().getInstrument());
        assertThat(context.getBpm()).isEqualTo(TextToMusicContext.DEFAULT_BPM);
        assertThat(context.getLastMusicElement()).isNull();
    }

    @Test
    void appendToSheetMusicKeepsTheLastElement() {
        TextToMusicContext context = new TextToMusicContext();
        PauseElement pause = new PauseElement(DurationTime.QUARTER);

        context.appendToSheetMusic(A);
        assertThat(context.getLastMusicElement()).isSameAs(A);

        context.appendToSheetMusic(pause);
        assertThat(context.getLastMusicElement()).isSameAs(pause);
    }

    @Test
    void appendControlElementDoesNotChangeTheLastElement() {
        TextToMusicContext context = new TextToMusicContext();

        context.appendToSheetMusic(A);
        context.appendControlElement(new VolumeElement(80));
        context.appendControlElement(new BPMElement(130));

        assertThat(context.getLastMusicElement()).isSameAs(A);
    }

    @Test
    void bothAppendsWriteToTheCurrentVoice() {
        TextToMusicContext context = new TextToMusicContext();
        VolumeElement volume = new VolumeElement(80);

        context.appendToSheetMusic(A);
        context.appendControlElement(volume);

        assertThat(context.finish().get(0).getSheetMusic().getSheetMusic()).containsExactly(A, volume);
    }

    @Test
    void closeVoiceStartsTheNextVoiceFromItsPreset() {
        TextToMusicContext context = new TextToMusicContext();
        VoicePreset keys = new VoicePreset(InstrumentType.KEYS);

        context.appendToSheetMusic(A);
        context.setCurrentOctave(new Octave(8));
        context.setBpm(200);
        context.closeVoice();

        assertThat(context.getLastMusicElement()).isNull();
        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(keys.getDefaultOctave().getOctaveInt());
        assertThat(context.getBaseOctave().getOctaveInt()).isEqualTo(keys.getDefaultOctave().getOctaveInt());
        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(keys.getVolumeElement().getVolume());
        assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(keys.getInstrumentElement().getInstrument());
        assertThat(context.getBpm()).isEqualTo(TextToMusicContext.DEFAULT_BPM);
    }

    @Test
    void voicesCycleThroughTheInstrumentTypes() {
        TextToMusicContext context = new TextToMusicContext();
        for (int i = 0; i < 4; i++) {
            context.closeVoice();
        }

        List<Voice> voices = context.finish();

        assertThat(voices).hasSize(5);
        assertThat(voices.get(0).getInstrumentType()).isEqualTo(InstrumentType.STRINGS);
        assertThat(voices.get(1).getInstrumentType()).isEqualTo(InstrumentType.KEYS);
        assertThat(voices.get(2).getInstrumentType()).isEqualTo(InstrumentType.PLUCKED);
        assertThat(voices.get(3).getInstrumentType()).isEqualTo(InstrumentType.WINDS);
        assertThat(voices.get(4).getInstrumentType()).isEqualTo(InstrumentType.STRINGS);
    }

    @Test
    void finishReturnsEveryVoiceWithItsOwnElements() {
        TextToMusicContext context = new TextToMusicContext();
        PauseElement pause = new PauseElement(DurationTime.HALF);

        context.appendToSheetMusic(A);
        context.closeVoice();
        context.appendToSheetMusic(pause);
        List<Voice> voices = context.finish();

        assertThat(voices).hasSize(2);
        assertThat(voices.get(0).getSheetMusic().getSheetMusic()).containsExactly(A);
        assertThat(voices.get(1).getSheetMusic().getSheetMusic()).containsExactly(pause);
    }
}
