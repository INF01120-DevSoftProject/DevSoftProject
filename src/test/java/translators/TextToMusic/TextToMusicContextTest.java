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
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.MusicElement;
import musicElements.NoteElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.PauseElement;
import musicElements.VolumeElement;

class TextToMusicContextTest {

    // instrumento, volume e BPM do preset no começo de cada voz
    private static final int PRESET_SIZE = 3;

    private static final NoteElement A = new NoteElement(Notes.A, new Octave(4), DurationTime.QUARTER, Accidentals.NONE);

    private final List<Voice> voices = new Music("Teste", Music.MIN_VOICES).getVoices();

    // elementos da voz depois do preset
    private List<MusicElement> elements(Voice voice) {
        List<MusicElement> sheet = voice.getSheetMusic().getSheetMusic();
        return sheet.subList(PRESET_SIZE, sheet.size());
    }

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
    void eachVoiceUsesItsReceivedPresetOrTheDefault() {
        VoicePreset first = new VoicePreset(new InstrumentElement(InstrumentType.KEYS), new VolumeElement(50), new Octave(2));
        TextToMusicContext context = new TextToMusicContext(voices, List.of(first));

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(2);
        assertThat(context.getBaseOctave().getOctaveInt()).isEqualTo(2);
        assertThat(context.getCurrentVolume()).isSameAs(first.getVolumeElement());
        assertThat(context.getCurrentInstrument()).isSameAs(first.getInstrumentElement());
        context.appendToSheetMusic(A);
        assertThat(voices.get(0).getSheetMusic().getSheetMusic().get(0)).isSameAs(first.getInstrumentElement());

        // a lista só tem um preset: a segunda voz usa o padrão dela
        context.closeVoice();
        VoicePreset second = voices.get(1).getVoicePreset();
        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(second.getDefaultOctave().getOctaveInt());
        assertThat(context.getCurrentInstrument()).isSameAs(second.getInstrumentElement());
        context.appendToSheetMusic(A);
        assertThat(voices.get(1).getSheetMusic().getSheetMusic().get(0)).isSameAs(second.getInstrumentElement());
    }

    @Test
    void presetIsWrittenWithTheFirstElementOfTheVoice() {
        TextToMusicContext context = new TextToMusicContext(voices);
        VoicePreset first = voices.get(0).getVoicePreset();
        List<MusicElement> sheet = voices.get(0).getSheetMusic().getSheetMusic();

        assertThat(sheet).isEmpty();
        context.appendToSheetMusic(A);

        assertThat(sheet).hasSize(PRESET_SIZE + 1);
        assertThat(sheet.get(0)).isSameAs(first.getInstrumentElement());
        assertThat(sheet.get(1)).isSameAs(first.getVolumeElement());
        assertThat(((BPMElement) sheet.get(2)).getBPM()).isEqualTo(TextToMusicContext.DEFAULT_BPM);
        assertThat(sheet.get(3)).isSameAs(A);
    }

    @Test
    void controlElementAlsoWritesThePreset() {
        TextToMusicContext context = new TextToMusicContext(voices);
        VolumeElement volume = new VolumeElement(80);

        context.appendControlElement(volume);

        assertThat(voices.get(0).getSheetMusic().getSheetMusic()).hasSize(PRESET_SIZE + 1);
        assertThat(elements(voices.get(0))).containsExactly(volume);
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

        assertThat(elements(voices.get(0))).containsExactly(A, volume);
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

        assertThat(elements(voices.get(0))).containsExactly(A);
        assertThat(elements(voices.get(1))).containsExactly(pause);
        assertThat(voices.get(2).getSheetMusic().getSheetMusic()).isEmpty();
    }
}
