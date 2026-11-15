package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import musicElements.Accidentals;
import musicElements.DurationTime;
import musicElements.Notes;

class CharacterMappingTest {

    @Test
    void startsWithTheCharactersOfTheDefaultRules() {
        CharacterMapping mapping = new CharacterMapping();

        assertThat(mapping.getChars("note")).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars("pause")).isEqualTo("acdefgh");
        assertThat(mapping.getChars("newVoice")).isEqualTo("\n");
        assertThat(mapping.getMappingCharacters("accidental")).containsExactly("#", "b");
    }

    @Test
    void twoMappingsDoNotShareState() {
        CharacterMapping first = new CharacterMapping();
        CharacterMapping second = new CharacterMapping();

        first.replaceChar("note", "A", "J");
        first.replaceChar("accidental", "#", "s");

        assertThat(first.getNote("J")).isEqualTo(Notes.A);
        assertThat(second.getNote("J")).isNull();
        assertThat(second.getNote("A")).isEqualTo(Notes.A);
        assertThat(second.getAccidental("#")).isEqualTo(Accidentals.SHARP);
    }

    @Test
    void replacedCharacterKeepsItsMusicalValue() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar("duration", "2", "m");

        assertThat(mapping.getDurationTime("m")).isEqualTo(DurationTime.HALF);
        assertThat(mapping.getDurationTime("2")).isEqualTo(DurationTime.QUARTER);
        assertThat(mapping.getChars("duration")).isEqualTo("1m34567");
    }

    @Test
    void resetUndoesEveryChange() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.mapNote("J", Notes.A);
        mapping.setElementChars("bpmUp", "+");

        mapping.reset();

        assertThat(mapping.getChars("note")).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars("bpmUp")).isEqualTo(">");
        assertThat(mapping.getNote("J")).isNull();
    }

    @Test
    void customDefaultsAreUsedAndRestored() {
        CharacterMapping mapping = new CharacterMapping(Map.of("bpmUp", "+"));
        mapping.setElementChars("bpmUp", "]");

        mapping.reset();

        assertThat(mapping.getChars("bpmUp")).isEqualTo("+");
    }

    @Test
    void unknownGroupIsRejected() {
        CharacterMapping mapping = new CharacterMapping();

        assertThrows(IllegalArgumentException.class, () -> mapping.getChars("grupoQueNaoExiste"));
        assertThat(mapping.getMappingCharacters("grupoQueNaoExiste")).isEmpty();
    }

    @Test
    void swapsTwoNotesOneChangeAtATime() {
        // como a tela de Mapeamento faz: primeiro "A -> B", depois "B -> A"
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar("note", "A", "B");
        assertThat(mapping.isComplete()).isFalse();
        mapping.replaceChar("note", "B", "A");

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getChars("note")).isEqualTo("BACDEFG");
        assertThat(mapping.getNote("B")).isEqualTo(Notes.A);
        assertThat(mapping.getNote("A")).isEqualTo(Notes.B);
    }

    @Test
    void rotatesThreeCharacters() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar("note", "A", "B");
        mapping.replaceChar("note", "B", "C");
        mapping.replaceChar("note", "C", "A");

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getNote("B")).isEqualTo(Notes.A);
        assertThat(mapping.getNote("C")).isEqualTo(Notes.B);
        assertThat(mapping.getNote("A")).isEqualTo(Notes.C);
    }

    @Test
    void swapsDurationDigits() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar("duration", "2", "3");
        mapping.replaceChar("duration", "3", "2");

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getDurationTime("3")).isEqualTo(DurationTime.HALF);
        assertThat(mapping.getDurationTime("2")).isEqualTo(DurationTime.QUARTER);
    }

    @Test
    void halfSwapLeavesTheMappingIncomplete() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar("note", "A", "B");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, mapping::checkComplete);
        assertThat(error.getMessage()).contains("incompleto").contains("'B'");
    }

    @Test
    void resetClearsAHalfSwap() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.replaceChar("note", "A", "B");

        mapping.reset();

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getChars("note")).isEqualTo("ABCDEFG");
    }

    @Test
    void characterOfAnotherGroupIsStillRejected() {
        CharacterMapping mapping = new CharacterMapping();

        assertThrows(IllegalArgumentException.class, () -> mapping.replaceChar("note", "A", "a"));
        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getChars("note")).isEqualTo("ABCDEFG");
    }

    @Test
    void addGroupIfAbsentKeepsExistingGroups() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.addGroupIfAbsent("note", "XYZ");
        mapping.addGroupIfAbsent("tempo", "T");

        assertThat(mapping.getChars("note")).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars("tempo")).isEqualTo("T");
    }
}
