package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
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

class TextToMusicTranslatorTest {

    // cada teste tem o seu próprio mapeamento: nenhum teste interfere em outro
    private final CharacterMapping mapping = new CharacterMapping();
    private final TextToMusicTranslator translator = new TextToMusicTranslator(mapping);

    private List<Voice> voices(String text) {
        return translator.textToMusic("Teste", text).getVoices();
    }

    // instrumento, volume e BPM do preset no começo de cada voz
    private static final int PRESET_SIZE = 3;

    // elementos da voz depois do preset
    private List<MusicElement> elements(Voice voice) {
        List<MusicElement> sheet = voice.getSheetMusic().getSheetMusic();
        return sheet.subList(PRESET_SIZE, sheet.size());
    }

    private List<MusicElement> firstVoiceElements(String text) {
        return elements(voices(text).get(0));
    }

    private NoteElement noteAt(List<MusicElement> elements, int index) {
        assertThat(elements.get(index)).isInstanceOf(NoteElement.class);
        return (NoteElement) elements.get(index);
    }

    @Test
    void translatesEachNoteLetter() {
        List<MusicElement> elements = firstVoiceElements("ABCDEFG");

        assertThat(elements).hasSize(7);
        assertThat(noteAt(elements, 0).getNote()).isEqualTo(Notes.A);
        assertThat(noteAt(elements, 1).getNote()).isEqualTo(Notes.B);
        assertThat(noteAt(elements, 2).getNote()).isEqualTo(Notes.C);
        assertThat(noteAt(elements, 3).getNote()).isEqualTo(Notes.D);
        assertThat(noteAt(elements, 4).getNote()).isEqualTo(Notes.E);
        assertThat(noteAt(elements, 5).getNote()).isEqualTo(Notes.F);
        assertThat(noteAt(elements, 6).getNote()).isEqualTo(Notes.G);
    }

    @Test
    void noteWithoutAccidentalOrDurationIsANaturalQuarter() {
        NoteElement note = noteAt(firstVoiceElements("A"), 0);

        assertThat(note.getAccidental()).isEqualTo(Accidentals.NONE);
        assertThat(note.getDuration()).isEqualTo(DurationTime.QUARTER);
    }

    @Test
    void readsAccidentals() {
        List<MusicElement> elements = firstVoiceElements("F#Bb");

        assertThat(elements).hasSize(2);
        assertThat(noteAt(elements, 0).getAccidental()).isEqualTo(Accidentals.SHARP);
        assertThat(noteAt(elements, 1).getAccidental()).isEqualTo(Accidentals.FLAT);
    }

    @Test
    void readsEveryDurationAfterAColon() {
        List<MusicElement> elements = firstVoiceElements("A:1A:2A:3A:4A:5A:6A:7");

        assertThat(elements).hasSize(7);
        assertThat(noteAt(elements, 0).getDuration()).isEqualTo(DurationTime.WHOLE);
        assertThat(noteAt(elements, 1).getDuration()).isEqualTo(DurationTime.HALF);
        assertThat(noteAt(elements, 2).getDuration()).isEqualTo(DurationTime.QUARTER);
        assertThat(noteAt(elements, 3).getDuration()).isEqualTo(DurationTime.EIGHTH);
        assertThat(noteAt(elements, 4).getDuration()).isEqualTo(DurationTime.SIXTEENTH);
        assertThat(noteAt(elements, 5).getDuration()).isEqualTo(DurationTime.THIRTY_SECOND);
        assertThat(noteAt(elements, 6).getDuration()).isEqualTo(DurationTime.SIXTY_FOURTH);
    }

    @Test
    void readsNoteWithAccidentalAndDurationTogether() {
        NoteElement note = noteAt(firstVoiceElements("C#:2"), 0);

        assertThat(note.getNote()).isEqualTo(Notes.C);
        assertThat(note.getAccidental()).isEqualTo(Accidentals.SHARP);
        assertThat(note.getDuration()).isEqualTo(DurationTime.HALF);
    }

    @Test
    void lowercaseLettersAreQuarterPauses() {
        List<MusicElement> elements = firstVoiceElements("acdefgh");

        assertThat(elements).hasSize(7);
        for (MusicElement element : elements) {
            assertThat(element).isInstanceOf(PauseElement.class);
            assertThat(((PauseElement) element).getDuration()).isEqualTo(DurationTime.QUARTER);
        }
    }

    @Test
    void questionMarkAndDotRaiseTheOctave() {
        List<MusicElement> elements = firstVoiceElements("A?A.A");

        assertThat(noteAt(elements, 0).getOctaveValue()).isEqualTo(6);
        assertThat(noteAt(elements, 1).getOctaveValue()).isEqualTo(7);
        assertThat(noteAt(elements, 2).getOctaveValue()).isEqualTo(8);
    }

    @Test
    void raisingAboveTheMaximumOctaveGoesBackToTheVoiceBase() {
        List<MusicElement> elements = firstVoiceElements("???A");

        assertThat(noteAt(elements, 0).getOctaveValue()).isEqualTo(6);
    }

    @Test
    void capitalVLowersTheOctaveUntilTheMinimum() {
        List<MusicElement> elements = firstVoiceElements("VAVVVVVVVVA");

        assertThat(noteAt(elements, 0).getOctaveValue()).isEqualTo(5);
        assertThat(noteAt(elements, 1).getOctaveValue()).isEqualTo(0);
    }

    @Test
    void spaceDoublesTheVolumeUpToTheMaximum() {
        List<MusicElement> elements = firstVoiceElements("  ");

        assertThat(elements).hasSize(2);
        assertThat(((VolumeElement) elements.get(0)).getVolume()).isEqualTo(VolumeElement.MAX_VOLUME);
        assertThat(((VolumeElement) elements.get(1)).getVolume()).isEqualTo(VolumeElement.MAX_VOLUME);
    }

    @Test
    void greaterAndLessThanChangeTheBpmByTen() {
        List<MusicElement> elements = firstVoiceElements(">><");

        assertThat(((BPMElement) elements.get(0)).getBPM()).isEqualTo(130);
        assertThat(((BPMElement) elements.get(1)).getBPM()).isEqualTo(140);
        assertThat(((BPMElement) elements.get(2)).getBPM()).isEqualTo(130);
    }

    @Test
    void bpmStaysWithinItsLimits() {
        List<MusicElement> slowest = firstVoiceElements("<".repeat(20));
        List<MusicElement> fastest = firstVoiceElements(">".repeat(100));

        assertThat(((BPMElement) slowest.get(slowest.size() - 1)).getBPM()).isEqualTo(20);
        assertThat(((BPMElement) fastest.get(fastest.size() - 1)).getBPM()).isEqualTo(999);
    }

    @Test
    void eachLineIsAVoiceAndMissingVoicesStayEmpty() {
        List<Voice> voices = voices("A\nB\nC");

        assertThat(voices).hasSize(Music.MIN_VOICES);
        assertThat(voices.get(3).getSheetMusic().getSheetMusic()).isEmpty();
        assertThat(noteAt(elements(voices.get(0)), 0).getNote()).isEqualTo(Notes.A);
        assertThat(noteAt(elements(voices.get(1)), 0).getNote()).isEqualTo(Notes.B);
        assertThat(noteAt(elements(voices.get(2)), 0).getNote()).isEqualTo(Notes.C);
    }

    @Test
    void voicesFollowTheInstrumentTypeCycle() {
        List<Voice> voices = voices("A\nA\nA\nA\nA\nA");

        assertThat(voices.get(0).getInstrumentType()).isEqualTo(InstrumentType.STRINGS);
        assertThat(voices.get(1).getInstrumentType()).isEqualTo(InstrumentType.KEYS);
        assertThat(voices.get(2).getInstrumentType()).isEqualTo(InstrumentType.PLUCKED);
        assertThat(voices.get(3).getInstrumentType()).isEqualTo(InstrumentType.WINDS);
        assertThat(voices.get(4).getInstrumentType()).isEqualTo(InstrumentType.PERCUSSION_AND_SYNTH);
        assertThat(voices.get(5).getInstrumentType()).isEqualTo(InstrumentType.STRINGS);
    }

    @Test
    void notesUseTheOctaveOfTheirVoicePreset() {
        List<Voice> voices = voices("A\nA\nA\nA");

        for (Voice voice : voices) {
            int presetOctave = voice.getVoicePreset().getDefaultOctave().getOctaveInt();
            assertThat(noteAt(elements(voice), 0).getOctaveValue()).isEqualTo(presetOctave);
        }
    }

    @Test
    void otherCharacterRepeatsTheLastNote() {
        List<MusicElement> elements = firstVoiceElements("AO");

        assertThat(elements).hasSize(2);
        assertThat(elements.get(1)).isSameAs(elements.get(0));
    }

    @Test
    void otherCharacterIsAPauseWhenThereIsNoNoteBefore() {
        List<MusicElement> start = firstVoiceElements("O");
        List<MusicElement> afterPause = firstVoiceElements("aO");

        assertThat(start.get(0)).isInstanceOf(PauseElement.class);
        assertThat(afterPause.get(1)).isInstanceOf(PauseElement.class);
    }

    @Test
    void volumeAndBpmDoNotChangeTheNoteToRepeat() {
        List<MusicElement> elements = firstVoiceElements("A >O");

        assertThat(elements).hasSize(4);
        assertThat(elements.get(3)).isSameAs(elements.get(0));
    }

    @Test
    void letterHIsNotANote() {
        assertThat(firstVoiceElements("H").get(0)).isInstanceOf(PauseElement.class);
    }

    @Test
    void evenDigitMovesToTheNextInstrumentOfTheVoiceType() {
        List<MusicElement> elements = firstVoiceElements("0246");

        assertThat(elements).hasSize(4);
        for (int i = 0; i < elements.size(); i++) {
            InstrumentElement instrument = (InstrumentElement) elements.get(i);
            assertThat(instrument.getInstrument()).isEqualTo(41 + i);
            assertThat(instrument.getType()).isEqualTo(InstrumentType.STRINGS);
        }
    }

    @Test
    void instrumentGoesBackToTheFirstOfTheTypeAfterTheLast() {
        List<MusicElement> elements = firstVoiceElements("2".repeat(16));

        assertThat(((InstrumentElement) elements.get(14)).getInstrument()).isEqualTo(InstrumentType.STRINGS.getMaxInstrument());
        assertThat(((InstrumentElement) elements.get(15)).getInstrument()).isEqualTo(InstrumentType.STRINGS.getMinInstrument());
    }

    @Test
    void instrumentChangeStaysInsideEachVoiceType() {
        List<Voice> voices = voices("8\n8\n8\n8");

        assertThat(((InstrumentElement) elements(voices.get(0)).get(0)).getInstrument()).isEqualTo(41);
        assertThat(((InstrumentElement) elements(voices.get(1)).get(0)).getInstrument()).isEqualTo(1);
        assertThat(((InstrumentElement) elements(voices.get(2)).get(0)).getInstrument()).isEqualTo(25);
        assertThat(((InstrumentElement) elements(voices.get(3)).get(0)).getInstrument()).isEqualTo(57);
    }

    @Test
    void oddDigitDoesNotChangeTheInstrument() {
        List<MusicElement> elements = firstVoiceElements("A3");

        assertThat(elements).hasSize(2);
        assertThat(elements.get(1)).isSameAs(elements.get(0));
    }

    @Test
    void startDelayAddsQuarterPausesBeforeTheVoice() {
        List<MusicElement> elements = firstVoiceElements("[2] A");

        assertThat(elements).hasSize(3);
        assertThat(((PauseElement) elements.get(0)).getDuration()).isEqualTo(DurationTime.QUARTER);
        assertThat(((PauseElement) elements.get(1)).getDuration()).isEqualTo(DurationTime.QUARTER);
        assertThat(noteAt(elements, 2).getNote()).isEqualTo(Notes.A);
    }

    @Test
    void startDelayDoesNotNeedASpaceAfterIt() {
        List<MusicElement> elements = firstVoiceElements("[3]A");

        assertThat(elements).hasSize(4);
        assertThat(elements.subList(0, 3)).allMatch(element -> element instanceof PauseElement);
        assertThat(noteAt(elements, 3).getNote()).isEqualTo(Notes.A);
    }

    @Test
    void startDelayWorksAtTheStartOfEveryLine() {
        List<MusicElement> secondVoice = elements(voices("A\n[1] B").get(1));

        assertThat(secondVoice).hasSize(2);
        assertThat(secondVoice.get(0)).isInstanceOf(PauseElement.class);
        assertThat(noteAt(secondVoice, 1).getNote()).isEqualTo(Notes.B);
    }

    @Test
    void bracketsInTheMiddleOfTheLineAreNotADelay() {
        List<MusicElement> elements = firstVoiceElements("A[2]");

        assertThat(elements).noneMatch(element -> element instanceof PauseElement);
    }

    @Test
    void emptyTextGivesTheMinimumOfEmptyVoices() {
        List<Voice> voices = voices("");

        assertThat(voices).hasSize(Music.MIN_VOICES);
        for (Voice voice : voices) {
            assertThat(voice.getSheetMusic().getSheetMusic()).isEmpty();
        }
    }

    @Test
    void emptyLineGivesAnEmptyVoice() {
        List<Voice> voices = voices("A\n\nA");

        assertThat(voices.get(1).getSheetMusic().getSheetMusic()).isEmpty();
        assertThat(noteAt(elements(voices.get(2)), 0).getNote()).isEqualTo(Notes.A);
    }

    @Test
    void presetKeepsItsValuesWhenTheFirstCharacterChangesThem() {
        List<MusicElement> sheet = voices("0 >").get(0).getSheetMusic().getSheetMusic();

        assertThat(((InstrumentElement) sheet.get(0)).getInstrument()).isEqualTo(InstrumentType.STRINGS.getDefaultInstrument());
        assertThat(((VolumeElement) sheet.get(1)).getVolume()).isEqualTo(100);
        assertThat(((BPMElement) sheet.get(2)).getBPM()).isEqualTo(Music.DEFAULT_BPM);
        assertThat(((InstrumentElement) sheet.get(3)).getInstrument()).isEqualTo(41);
    }

    @Test
    void eachVoiceStartsWithItsPreset() {
        List<Voice> voices = voices("A\nA\nA\nA");

        for (Voice voice : voices) {
            List<MusicElement> sheet = voice.getSheetMusic().getSheetMusic();
            assertThat(sheet.get(0)).isSameAs(voice.getVoicePreset().getInstrumentElement());
            assertThat(sheet.get(1)).isSameAs(voice.getVoicePreset().getVolumeElement());
            assertThat(((BPMElement) sheet.get(2)).getBPM()).isEqualTo(Music.DEFAULT_BPM);
        }
    }

    @Test
    void eachReceivedPresetGoesToItsVoice() {
        VoicePreset first = new VoicePreset(new InstrumentElement(InstrumentType.WINDS), new VolumeElement(60), new Octave(3));
        VoicePreset third = new VoicePreset(new InstrumentElement(InstrumentType.KEYS), new VolumeElement(40), new Octave(7));
        List<Voice> voices = translator.textToMusic("Teste", "A\nA\nA", Arrays.asList(first, null, third)).getVoices();

        assertPresetAtStart(voices.get(0), first);
        assertThat(noteAt(elements(voices.get(0)), 0).getOctaveValue()).isEqualTo(3);
        assertPresetAtStart(voices.get(2), third);
        assertThat(noteAt(elements(voices.get(2)), 0).getOctaveValue()).isEqualTo(7);
    }

    @Test
    void voiceWithoutAPresetInTheListUsesItsDefault() {
        VoicePreset first = new VoicePreset(InstrumentType.WINDS);
        // posição null e lista menor que o número de vozes
        List<Voice> voices = translator.textToMusic("Teste", "A\nA\nA", Arrays.asList(first, null)).getVoices();

        assertPresetAtStart(voices.get(0), first);
        assertPresetAtStart(voices.get(1), voices.get(1).getVoicePreset());
        assertPresetAtStart(voices.get(2), voices.get(2).getVoicePreset());
    }

    @Test
    void extraPresetsAreIgnored() {
        List<VoicePreset> presets = Collections.nCopies(Music.MAX_VOICES, new VoicePreset(InstrumentType.WINDS));

        assertThat(translator.textToMusic("Teste", "A", presets).getVoices()).hasSize(Music.MIN_VOICES);
    }

    private void assertPresetAtStart(Voice voice, VoicePreset preset) {
        List<MusicElement> sheet = voice.getSheetMusic().getSheetMusic();
        assertThat(sheet.get(0)).isSameAs(preset.getInstrumentElement());
        assertThat(sheet.get(1)).isSameAs(preset.getVolumeElement());
        assertThat(((BPMElement) sheet.get(2)).getBPM()).isEqualTo(Music.DEFAULT_BPM);
    }

    @Test
    void receivedPresetDecidesTheInstrumentFamily() {
        VoicePreset preset = new VoicePreset(InstrumentType.WINDS);
        List<MusicElement> sheet = translator.textToMusic("Teste", "0", List.of(preset)).getVoices().get(0).getSheetMusic().getSheetMusic();

        InstrumentElement next = (InstrumentElement) sheet.get(3);
        assertThat(next.getType()).isEqualTo(InstrumentType.WINDS);
        assertThat(next.getInstrument()).isEqualTo(InstrumentType.WINDS.getDefaultInstrument() + 1);
    }

    @Test
    void withoutPresetListEachVoiceUsesItsDefault() {
        List<Voice> withNull = translator.textToMusic("Teste", "A\nA", null).getVoices();
        List<Voice> withoutArgument = voices("A\nA");

        for (int i = 0; i < 2; i++) {
            assertThat(withNull.get(i).getSheetMusic().getSheetMusic().get(0))
                .isSameAs(withNull.get(i).getVoicePreset().getInstrumentElement());
            assertThat(((InstrumentElement) withoutArgument.get(i).getSheetMusic().getSheetMusic().get(0)).getType())
                .isEqualTo(withNull.get(i).getInstrumentType());
        }
    }

    @Test
    void presetComesBeforeTheStartDelay() {
        List<MusicElement> sheet = voices("[1] A").get(0).getSheetMusic().getSheetMusic();

        assertThat(sheet.get(0)).isInstanceOf(InstrumentElement.class);
        assertThat(sheet.get(1)).isInstanceOf(VolumeElement.class);
        assertThat(sheet.get(2)).isInstanceOf(BPMElement.class);
        assertThat(sheet.get(3)).isInstanceOf(PauseElement.class);
    }

    @Test
    void musicKeepsTheGivenName() {
        assertThat(translator.textToMusic("Fuga", "A").getName()).isEqualTo("Fuga");
    }

    @Test
    void acceptsUpToTheMaximumOfVoices() {
        String text = String.join("\n", Collections.nCopies(Music.MAX_VOICES, "A"));

        assertThat(voices(text)).hasSize(Music.MAX_VOICES);
    }

    @Test
    void rejectsMoreLinesThanTheMaximumOfVoices() {
        String text = String.join("\n", Collections.nCopies(Music.MAX_VOICES + 1, "A"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> voices(text));
        assertThat(error.getMessage()).contains(String.valueOf(Music.MAX_VOICES + 1));
    }

    @Test
    void mappedCharacterBecomesTheChosenNote() {
        mapping.mapNote("J", Notes.A);
        NoteElement note = noteAt(firstVoiceElements("J#:2"), 0);

        assertThat(note.getNote()).isEqualTo(Notes.A);
        assertThat(note.getAccidental()).isEqualTo(Accidentals.SHARP);
        assertThat(note.getDuration()).isEqualTo(DurationTime.HALF);
    }

    @Test
    void replacedNoteLetterMovesToTheNewCharacter() {
        mapping.replaceChar("note", "A", "J");
        List<MusicElement> elements = firstVoiceElements("JA");

        assertThat(noteAt(elements, 0).getNote()).isEqualTo(Notes.A);
        // "A" deixou de ser nota: cai no else e repete a última
        assertThat(elements.get(1)).isSameAs(elements.get(0));
    }

    @Test
    void replacedAccidentalAndDurationKeepTheirMeaning() {
        mapping.replaceChar("accidental", "#", "s");
        mapping.replaceChar("duration", "2", "m");
        NoteElement note = noteAt(firstVoiceElements("Cs:m"), 0);

        assertThat(note.getAccidental()).isEqualTo(Accidentals.SHARP);
        assertThat(note.getDuration()).isEqualTo(DurationTime.HALF);
    }

    @Test
    void replacingADigitOnlyChangesItsGroup() {
        mapping.replaceChar("duration", "2", "m");
        List<MusicElement> elements = firstVoiceElements("C:2");

        // "2" não é mais duração, mas continua trocando o instrumento
        assertThat(noteAt(elements, 0).getDuration()).isEqualTo(DurationTime.QUARTER);
        assertThat(elements).anyMatch(element -> element instanceof InstrumentElement);
    }

    @Test
    void replacingACharacterOutsideItsGroupIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> mapping.replaceChar("pause", "A", "J"));
        assertThrows(IllegalArgumentException.class, () -> mapping.replaceChar("grupoQueNaoExiste", "A", "J"));
    }

    @Test
    void characterAlreadyInUseIsRejected() {
        // "a" já é pausa: não pode virar também subir o BPM
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> mapping.replaceChar("bpmUp", ">", "a"));
        assertThat(error.getMessage()).contains("pause");
        assertThrows(IllegalArgumentException.class, () -> mapping.addElementChars("bpmUp", "V"));
        assertThrows(IllegalArgumentException.class, () -> mapping.mapNote("a", Notes.A));
    }

    @Test
    void digitsCanBeUsedMoreThanOnce() {
        mapping.addElementChars("bpmUp", "9");
        List<MusicElement> elements = firstVoiceElements("9");

        assertThat(((BPMElement) elements.get(0)).getBPM()).isEqualTo(130);
    }

    @Test
    void eachNoteHasOnlyOneCharacter() {
        mapping.mapNote("J", Notes.A);
        List<MusicElement> elements = firstVoiceElements("JA");

        assertThat(noteAt(elements, 0).getNote()).isEqualTo(Notes.A);
        // "A" não é mais nota: cai no else e repete a última nota
        assertThat(elements).hasSize(2);
        assertThat(elements.get(1)).isSameAs(elements.get(0));
    }

    @Test
    void elementCharsCanBeChanged() {
        mapping.setElementChars("bpmUp", "+]");
        List<MusicElement> elements = firstVoiceElements("+]>");

        assertThat(((BPMElement) elements.get(0)).getBPM()).isEqualTo(130);
        assertThat(((BPMElement) elements.get(1)).getBPM()).isEqualTo(140);
        assertThat(elements.get(2)).isInstanceOf(PauseElement.class);
    }

    @Test
    void eachAddedCharacterWorksOnItsOwn() {
        mapping.addElementChars("octaveUp", "^L");
        // desce 3 antes, pra subir sem passar da oitava máxima
        List<MusicElement> elements = firstVoiceElements("VVVA^AL?A");

        int start = noteAt(elements, 0).getOctave().getOctaveInt();
        assertThat(noteAt(elements, 1).getOctave().getOctaveInt()).isEqualTo(start + 1);
        assertThat(noteAt(elements, 2).getOctave().getOctaveInt()).isEqualTo(start + 3);
    }

    @Test
    void removedCharacterStopsWorking() {
        mapping.addElementChars("bpmUp", "+");
        mapping.removeElementChars("bpmUp", ">");
        List<MusicElement> elements = firstVoiceElements(">+");

        assertThat(elements.get(0)).isInstanceOf(PauseElement.class);
        assertThat(((BPMElement) elements.get(1)).getBPM()).isEqualTo(130);
    }

    @Test
    void groupCannotBeLeftWithoutCharacters() {
        assertThrows(IllegalArgumentException.class, () -> mapping.removeElementChars("bpmUp", ">"));
        assertThrows(IllegalArgumentException.class, () -> mapping.setElementChars("bpmUp", ""));
    }

    @Test
    void resetGoesBackToTheDefaultMapping() {
        mapping.mapNote("J", Notes.A);
        mapping.reset();

        assertThat(firstVoiceElements("J").get(0)).isInstanceOf(PauseElement.class);
    }

    @Test
    void trailingLineBreakDoesNotCreateAnExtraVoice() {
        // arquivo .txt do Windows: toda linha, inclusive a ultima, termina com \r\n
        String text = String.join("\r\n", Collections.nCopies(Music.MAX_VOICES, "A")) + "\r\n";

        assertThat(voices(text)).hasSize(Music.MAX_VOICES);
    }
}
