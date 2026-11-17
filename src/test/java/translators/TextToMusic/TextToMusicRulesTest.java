package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
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

    private void apply(String group, TextToMusicContext context) {
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
        assertThat(matcher.group("note")).isEqualTo("B");
        assertThat(matcher.group("accidental")).isEqualTo("b");
        assertThat(matcher.group("duration")).isEqualTo("4");
    }

    @Test
    void tokenMatchesExactlyOneGroupPerCharacter() {
        Matcher matcher = Pattern.compile(rules.getREGEXToken()).matcher("a?V>< \n2O");
        String[] expectedGroups = {"pause", "octaveUp", "octaveDown", "bpmUp", "bpmDown", "doubleVolume", "newVoice", "instrument", "else"};

        for (String expected : expectedGroups) {
            assertThat(matcher.find()).isTrue();
            for (String group : rules.getElementsActionMapping().keySet()) {
                assertThat(matcher.group(group) != null).as("grupo %s em '%s'", group, matcher.group()).isEqualTo(group.equals(expected));
            }
        }
        assertThat(matcher.find()).isFalse();
    }

    @Test
    void startDelayAndNoteActionsComeFirstAndElseComesLast() {
        Map<String, TextToMusicActions> actions = rules.getElementsActionMapping();
        List<String> groups = List.copyOf(actions.keySet());

        assertThat(groups.get(0)).isEqualTo("startDelay");
        assertThat(groups.get(1)).isEqualTo("note");
        assertThat(groups.get(groups.size() - 1)).isEqualTo("else");
    }

    @Test
    void doubleVolumeActionDoublesTheCurrentVolume() {
        TextToMusicContext context = newContext();
        context.setCurrentVolume(new VolumeElement(30));

        apply("doubleVolume", context);

        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(60);
        assertThat(((VolumeElement) firstVoiceSheet().get(0)).getVolume()).isEqualTo(60);
    }

    @Test
    void octaveUpActionGoesBackToTheBaseOctaveAboveTheMaximum() {
        TextToMusicContext context = newContext();
        context.setCurrentOctave(new Octave(Octave.MAX_OCTAVE));

        apply("octaveUp", context);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(context.getBaseOctave().getOctaveInt());
    }

    @Test
    void octaveDownActionStopsAtTheMinimum() {
        TextToMusicContext context = newContext();
        context.setCurrentOctave(new Octave(Octave.MIN_OCTAVE));

        apply("octaveDown", context);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(Octave.MIN_OCTAVE);
    }

    @Test
    void instrumentActionMovesToTheNextInstrumentOfTheSameType() {
        TextToMusicContext context = newContext();
        context.setCurrentInstrument(new InstrumentElement(60, InstrumentType.WINDS));

        apply("instrument", context);
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

            apply("instrument", context);

            assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(type.getMinInstrument());
            assertThat(context.getCurrentInstrument().getType()).isEqualTo(type);
        }
    }

    @AfterEach
    @SuppressWarnings("deprecation")
    void restoreSharedMapping() {
        TextToMusicRules.resetMappings();
    }

    @Test
    void eachInstanceUsesItsOwnMapping() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.replaceChar("note", "A", "J");

        assertThat(new TextToMusicRules(mapping).getNote("J")).isEqualTo(Notes.A);
        assertThat(new TextToMusicRules().getNote("J")).isNull();
    }

    @Test
    void customActionGoesBeforeElse() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.addGroupIfAbsent("tempo", "T");

        TextToMusicRules customRules = new TextToMusicRules(mapping, Map.of("tempo", (matcher, context) -> {}));
        List<String> groups = List.copyOf(customRules.getElementsActionMapping().keySet());

        assertThat(groups.get(groups.size() - 2)).isEqualTo("tempo");
        assertThat(groups.get(groups.size() - 1)).isEqualTo("else");
        assertThat(rules.getElementsActionMapping()).doesNotContainKey("tempo");
    }

    @Test
    void newCommandWorksWithoutChangingThisClass() {
        // comando novo "T": BPM fixo em 200
        CharacterMapping mapping = new CharacterMapping();
        mapping.addGroupIfAbsent("tempo", "T");
        TextToMusicActions setTempo = (matcher, context) -> {
            context.setBpm(200);
            context.appendControlElement(new BPMElement(200));
        };
        TextToMusicTranslator translator = new TextToMusicTranslator(mapping, Map.of("tempo", setTempo));

        List<MusicElement> sheet = translator.textToMusic("Teste", "T>").getVoices().get(0).getSheetMusic().getSheetMusic();
        List<MusicElement> elements = sheet.subList(3, sheet.size());

        assertThat(((BPMElement) elements.get(0)).getBPM()).isEqualTo(200);
        assertThat(((BPMElement) elements.get(1)).getBPM()).isEqualTo(210);
    }

    @Test
    void customActionNeedsCharactersAndANewName() {
        CharacterMapping mapping = new CharacterMapping();
        TextToMusicActions nothing = (matcher, context) -> {};

        assertThrows(IllegalArgumentException.class, () -> new TextToMusicRules(mapping, Map.of("tempo", nothing)));
        assertThrows(IllegalArgumentException.class, () -> new TextToMusicRules(mapping, Map.of("note", nothing)));
        assertThrows(IllegalArgumentException.class, () -> new TextToMusicRules(mapping, Map.of("else", nothing)));
    }

    @Test
    @SuppressWarnings("deprecation")
    void legacyStaticMethodsChangeTheMappingOfTheDefaultTranslator() {
        // a GUI atual ainda usa os métodos estáticos; eles valem para o TextToMusicTranslator padrão
        TextToMusicRules.replaceChar("bpmUp", ">", "+");

        List<MusicElement> sheet = new TextToMusicTranslator().textToMusic("Teste", "+").getVoices().get(0).getSheetMusic().getSheetMusic();

        assertThat(TextToMusicRules.getMappingCharacters("bpmUp")).containsExactly("+");
        assertThat(sheet).anyMatch(element -> element instanceof BPMElement bpm && bpm.getBPM() == 130);
    }
}
