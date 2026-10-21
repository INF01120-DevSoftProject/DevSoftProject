package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import musicElements.Accidentals;
import musicElements.DurationTime;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.MusicElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.VolumeElement;

class TextToMusicRulesTest {

    private final TextToMusicRules rules = new TextToMusicRules();

    private void apply(String group, TextToMusicContext context) {
        rules.getElementsActionMapping().get(group).apply(null, context);
    }

    private List<MusicElement> sheetOf(TextToMusicContext context) {
        return context.finish().get(0).getSheetMusic().getSheetMusic();
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
        TextToMusicContext context = new TextToMusicContext();
        context.setCurrentVolume(new VolumeElement(30));

        apply("doubleVolume", context);

        assertThat(context.getCurrentVolume().getVolume()).isEqualTo(60);
        assertThat(((VolumeElement) sheetOf(context).get(0)).getVolume()).isEqualTo(60);
    }

    @Test
    void octaveUpActionGoesBackToTheBaseOctaveAboveTheMaximum() {
        TextToMusicContext context = new TextToMusicContext();
        context.setCurrentOctave(new Octave(Octave.MAX_OCTAVE));

        apply("octaveUp", context);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(context.getBaseOctave().getOctaveInt());
    }

    @Test
    void octaveDownActionStopsAtTheMinimum() {
        TextToMusicContext context = new TextToMusicContext();
        context.setCurrentOctave(new Octave(Octave.MIN_OCTAVE));

        apply("octaveDown", context);

        assertThat(context.getCurrentOctave().getOctaveInt()).isEqualTo(Octave.MIN_OCTAVE);
    }

    @Test
    void instrumentActionMovesToTheNextInstrumentOfTheSameType() {
        TextToMusicContext context = new TextToMusicContext();
        context.setCurrentInstrument(new InstrumentElement(60, InstrumentType.WINDS));

        apply("instrument", context);
        InstrumentElement newInstrument = context.getCurrentInstrument();

        assertThat(newInstrument.getInstrument()).isEqualTo(61);
        assertThat(newInstrument.getType()).isEqualTo(InstrumentType.WINDS);
        assertThat(sheetOf(context).get(0)).isSameAs(newInstrument);
    }

    @Test
    void instrumentActionGoesBackToTheFirstOfTheTypeAfterTheLast() {
        TextToMusicContext context = new TextToMusicContext();
        for (InstrumentType type : InstrumentType.values()) {
            context.setCurrentInstrument(new InstrumentElement(type.getMaxInstrument(), type));

            apply("instrument", context);

            assertThat(context.getCurrentInstrument().getInstrument()).isEqualTo(type.getMinInstrument());
            assertThat(context.getCurrentInstrument().getType()).isEqualTo(type);
        }
    }
}
