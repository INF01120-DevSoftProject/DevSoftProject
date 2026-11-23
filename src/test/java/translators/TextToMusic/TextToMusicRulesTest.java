package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import music.Music;
import music.Voice;
import musicElements.Accidentals;
import musicElements.BPMElement;
import musicElements.DurationTime;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.MusicElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.VolumeElement;

class TextToMusicRulesTest {

    private final TextToMusicRules rules = new TextToMusicRules();
    private final List<Voice> voices = new Music("Teste", Music.MIN_VOICES).getVoices();

    private TextToMusicContext newContext() {
        return new TextToMusicContext(voices);
    }

    private void apply(MappingType group, TextToMusicContext context) {
        rules.getElementsActionMapping().get(group).apply(null, context);
    }

    // elementos da primeira voz depois do preset (instrumento, volume e BPM)
    private List<MusicElement> firstVoiceSheet() {
        List<MusicElement> sheet = voices.get(0).getSheetMusic().getSheetMusic();
        return sheet.subList(3, sheet.size());
    }

    @Test
    void convertsEveryNoteLetter() {
        assertThat(rules.getNote("A")).isEqualTo(Notes.A);
        assertThat(rules.getNote("B")).isEqualTo(Notes.B);
        assertThat(rules.getNote("C")).isEqualTo(Notes.C);
        assertThat(rules.getNote("D")).isEqualTo(Notes.D);
        assertThat(rules.getNote("E")).isEqualTo(Notes.E);
        assertThat(rules.getNote("F")).isEqualTo(Notes.F);
        assertThat(rules.getNote("G")).isEqualTo(Notes.G);
    }

    @Test
    void convertsEveryDurationDigit() {
        assertThat(rules.getDurationTime("1")).isEqualTo(DurationTime.WHOLE);
        assertThat(rules.getDurationTime("2")).isEqualTo(DurationTime.HALF);
        assertThat(rules.getDurationTime("3")).isEqualTo(DurationTime.QUARTER);
        assertThat(rules.getDurationTime("4")).isEqualTo(DurationTime.EIGHTH);
        assertThat(rules.getDurationTime("5")).isEqualTo(DurationTime.SIXTEENTH);
        assertThat(rules.getDurationTime("6")).isEqualTo(DurationTime.THIRTY_SECOND);
        assertThat(rules.getDurationTime("7")).isEqualTo(DurationTime.SIXTY_FOURTH);
    }

    @Test
    void missingDurationIsAQuarter() {
        assertThat(rules.getDurationTime(null)).isEqualTo(DurationTime.QUARTER);
    }

    @Test
    void convertsAccidentals() {
        assertThat(rules.getAccidental("#")).isEqualTo(Accidentals.SHARP);
        assertThat(rules.getAccidental("b")).isEqualTo(Accidentals.FLAT);
        assertThat(rules.getAccidental(null)).isEqualTo(Accidentals.NONE);
    }

    @Test
    void tokenReadsAWholeNoteAtOnce() {
        Matcher matcher = Pattern.compile(rules.getREGEXToken()).matcher("Bb:4");

        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group()).isEqualTo("Bb:4");
        assertThat(matcher.group(MappingType.NOTE.getGroupName())).isEqualTo("B");
        assertThat(matcher.group(MappingType.ACCIDENTAL.getGroupName())).isEqualTo("b");
        assertThat(matcher.group(MappingType.DURATION.getGroupName())).isEqualTo("4");
    }

    @Test
    void tokenMatchesExactlyOneGroupPerCharacter() {
        Matcher matcher = Pattern.compile(rules.getREGEXToken()).matcher("a?V>< \n2O");
        MappingType[] expectedGroups = {MappingType.PAUSE, MappingType.OCTAVE_UP, MappingType.OCTAVE_DOWN, MappingType.BPM_UP, MappingType.BPM_DOWN, MappingType.DOUBLE_VOLUME, MappingType.NEW_VOICE, MappingType.INSTRUMENT, MappingType.ELSE};

        for (MappingType expected : expectedGroups) {
            assertThat(matcher.find()).isTrue();
            for (MappingType group : rules.getElementsActionMapping().keySet()) {
                assertThat(matcher.group(group.getGroupName()) != null).as("grupo %s em '%s'", group, matcher.group()).isEqualTo(group == expected);
            }
        }
        assertThat(matcher.find()).isFalse();
    }

    @Test
    void startDelayAndNoteActionsComeFirstAndElseComesLast() {
        Map<MappingType, TextToMusicActions> actions = rules.getElementsActionMapping();
        List<MappingType> groups = List.copyOf(actions.keySet());

        assertThat(groups.get(0)).isEqualTo(MappingType.START_DELAY);
        assertThat(groups.get(1)).isEqualTo(MappingType.NOTE);
        assertThat(groups.get(groups.size() - 1)).isEqualTo(MappingType.ELSE);
    }

    @Test
    void doubleVolumeActionDoublesTheCurrentVolume() {
        TextToMusicContext context = newContext();
        context.setCurrentVolume(new VolumeElement(30));

        apply(MappingType.DOUBLE_VOLUME, context);

        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(60);
        assertThat(((VolumeElement) firstVoiceSheet().get(0)).getVolume()).isEqualTo(60);
    }

    @Test
    void octaveUpActionGoesBackToTheBaseOctaveAboveTheMaximum() {
        TextToMusicContext context = newContext();
        context.setCurrentOctave(new Octave(Octave.MAX_OCTAVE));

        apply(MappingType.OCTAVE_UP, context);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(context.getBaseOctave().getOctaveInt());
    }

    @Test
    void octaveDownActionStopsAtTheMinimum() {
        TextToMusicContext context = newContext();
        context.setCurrentOctave(new Octave(Octave.MIN_OCTAVE));

        apply(MappingType.OCTAVE_DOWN, context);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(Octave.MIN_OCTAVE);
    }

    @Test
    void instrumentActionMovesToTheNextInstrumentOfTheSameType() {
        TextToMusicContext context = newContext();
        context.setCurrentInstrument(new InstrumentElement(60, InstrumentType.WINDS));

        apply(MappingType.INSTRUMENT, context);
        InstrumentElement newInstrument = context.getCurrentInstrument();

        assertThat(newInstrument.getInstrument()).isEqualTo(61);
        assertThat(newInstrument.getType()).isEqualTo(InstrumentType.WINDS);
        assertThat(firstVoiceSheet().get(0)).isSameAs(newInstrument);
    }

    @Test
    void instrumentActionGoesBackToTheFirstOfTheTypeAfterTheLast() {
        TextToMusicContext context = newContext();
        for (InstrumentType type : InstrumentType.values()) {
            context.setCurrentInstrument(new InstrumentElement(type.getMaxInstrument(), type));

            apply(MappingType.INSTRUMENT, context);

            assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(type.getMinInstrument());
            assertThat(context.getCurrentInstrument().getType()).isEqualTo(type);
        }
    }

    @Test
    void eachInstanceUsesItsOwnMapping() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.replaceChar(MappingType.NOTE, "A", "J");

        assertThat(new TextToMusicRules(mapping).getNote("J")).isEqualTo(Notes.A);
        assertThat(new TextToMusicRules().getNote("J")).isNull();
    }

    @Test
    void customActionGoesBeforeElse() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.addGroupIfAbsent(MappingType.TEMPO, "T");

        TextToMusicRules customRules = new TextToMusicRules(mapping, Map.of(MappingType.TEMPO, (matcher, context) -> {}));
        List<MappingType> groups = List.copyOf(customRules.getElementsActionMapping().keySet());

        assertThat(groups.get(groups.size() - 2)).isEqualTo(MappingType.TEMPO);
        assertThat(groups.get(groups.size() - 1)).isEqualTo(MappingType.ELSE);
        assertThat(rules.getElementsActionMapping()).doesNotContainKey(MappingType.TEMPO);
    }

    @Test
    void newCommandWorksWithoutChangingThisClass() {
        // comando novo "T": BPM fixo em 200
        CharacterMapping mapping = new CharacterMapping();
        mapping.addGroupIfAbsent(MappingType.TEMPO, "T");
        TextToMusicActions setTempo = (matcher, context) -> {
            context.setBpm(200);
            context.appendControlElement(new BPMElement(200));
        };
        TextToMusicTranslator translator = new TextToMusicTranslator(mapping, Map.of(MappingType.TEMPO, setTempo));

        List<MusicElement> sheet = translator.textToMusic("Teste", "T>").getVoices().get(0).getSheetMusic().getSheetMusic();
        List<MusicElement> elements = sheet.subList(3, sheet.size());

        assertThat(((BPMElement) elements.get(0)).getBPM()).isEqualTo(200);
        assertThat(((BPMElement) elements.get(1)).getBPM()).isEqualTo(210);
    }

    @Test
    void customActionNeedsCharactersAndANewName() {
        CharacterMapping mapping = new CharacterMapping();
        TextToMusicActions nothing = (matcher, context) -> {};

        assertThrows(IllegalArgumentException.class, () -> new TextToMusicRules(mapping, Map.of(MappingType.TEMPO, nothing)));
        assertThrows(IllegalArgumentException.class, () -> new TextToMusicRules(mapping, Map.of(MappingType.NOTE, nothing)));
        assertThrows(IllegalArgumentException.class, () -> new TextToMusicRules(mapping, Map.of(MappingType.ELSE, nothing)));
    }
}
