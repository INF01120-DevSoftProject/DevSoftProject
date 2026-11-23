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

        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars(MappingType.PAUSE)).isEqualTo("acdefgh");
        assertThat(mapping.getChars(MappingType.NEW_VOICE)).isEqualTo("\n");
        assertThat(mapping.getMappingCharacters(MappingType.ACCIDENTAL)).containsExactly("#", "b");
    }

    @Test
    void twoMappingsDoNotShareState() {
        CharacterMapping first = new CharacterMapping();
        CharacterMapping second = new CharacterMapping();

        first.replaceChar(MappingType.NOTE, "A", "J");
        first.replaceChar(MappingType.ACCIDENTAL, "#", "s");

        assertThat(first.getNote("J")).isEqualTo(Notes.A);
        assertThat(second.getNote("J")).isNull();
        assertThat(second.getNote("A")).isEqualTo(Notes.A);
        assertThat(second.getAccidental("#")).isEqualTo(Accidentals.SHARP);
    }

    @Test
    void replacedCharacterKeepsItsMusicalValue() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar(MappingType.DURATION, "2", "m");

        assertThat(mapping.getDurationTime("m")).isEqualTo(DurationTime.HALF);
        assertThat(mapping.getDurationTime("2")).isEqualTo(DurationTime.QUARTER);
        assertThat(mapping.getChars(MappingType.DURATION)).isEqualTo("1m34567");
    }

    @Test
    void resetUndoesEveryChange() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.mapNote("J", Notes.A);
        mapping.setElementChars(MappingType.BPM_UP, "+");

        mapping.reset();

        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars(MappingType.BPM_UP)).isEqualTo(">");
        assertThat(mapping.getNote("J")).isNull();
    }

    @Test
    void customDefaultsAreUsedAndRestored() {
        CharacterMapping mapping = new CharacterMapping(Map.of(MappingType.BPM_UP, "+"));
        mapping.setElementChars(MappingType.BPM_UP, "]");

        mapping.reset();

        assertThat(mapping.getChars(MappingType.BPM_UP)).isEqualTo("+");
    }

    @Test
    void unregisteredGroupIsRejected() {
        CharacterMapping mapping = new CharacterMapping();

        assertThrows(IllegalArgumentException.class, () -> mapping.getChars(MappingType.TEMPO));
        assertThat(mapping.getMappingCharacters(MappingType.TEMPO)).isEmpty();
    }

    @Test
    void swapsTwoNotesOneChangeAtATime() {
        // como a tela de Mapeamento faz: primeiro "A -> B", depois "B -> A"
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar(MappingType.NOTE, "A", "B");
        assertThat(mapping.isComplete()).isFalse();
        mapping.replaceChar(MappingType.NOTE, "B", "A");

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("BACDEFG");
        assertThat(mapping.getNote("B")).isEqualTo(Notes.A);
        assertThat(mapping.getNote("A")).isEqualTo(Notes.B);
    }

    @Test
    void rotatesThreeCharacters() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar(MappingType.NOTE, "A", "B");
        mapping.replaceChar(MappingType.NOTE, "B", "C");
        mapping.replaceChar(MappingType.NOTE, "C", "A");

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getNote("B")).isEqualTo(Notes.A);
        assertThat(mapping.getNote("C")).isEqualTo(Notes.B);
        assertThat(mapping.getNote("A")).isEqualTo(Notes.C);
    }

    @Test
    void swapsDurationDigits() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar(MappingType.DURATION, "2", "3");
        mapping.replaceChar(MappingType.DURATION, "3", "2");

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getDurationTime("3")).isEqualTo(DurationTime.HALF);
        assertThat(mapping.getDurationTime("2")).isEqualTo(DurationTime.QUARTER);
    }

    @Test
    void halfSwapLeavesTheMappingIncomplete() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.replaceChar(MappingType.NOTE, "A", "B");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, mapping::checkComplete);
        assertThat(error.getMessage()).contains("incompleto").contains("'B'");
    }

    @Test
    void resetClearsAHalfSwap() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.replaceChar(MappingType.NOTE, "A", "B");

        mapping.reset();

        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
    }

    @Test
    void characterOfAnotherGroupIsStillRejected() {
        CharacterMapping mapping = new CharacterMapping();

        assertThrows(IllegalArgumentException.class, () -> mapping.replaceChar(MappingType.NOTE, "A", "a"));
        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
    }

    @Test
    void noteCharacterWithoutANoteIsRejected() {
        CharacterMapping mapping = new CharacterMapping();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> mapping.addElementChars(MappingType.NOTE, "Z"));
        assertThat(error.getMessage()).contains("mapNote");
        assertThrows(IllegalArgumentException.class, () -> mapping.setElementChars(MappingType.NOTE, "ABZ"));
        assertThrows(IllegalArgumentException.class, () -> mapping.addElementChars(MappingType.DURATION, "9"));
        assertThrows(IllegalArgumentException.class, () -> mapping.addElementChars(MappingType.ACCIDENTAL, "s"));

        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
    }

    @Test
    void noteCharactersWithANoteCanBeSet() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.setElementChars(MappingType.NOTE, "ABC");
        mapping.mapNote("Z", Notes.D);

        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCZ");
        assertThat(mapping.getNote("Z")).isEqualTo(Notes.D);
    }

    @Test
    void mapNoteRejectsTheCharacterOfAnotherNote() {
        CharacterMapping mapping = new CharacterMapping();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> mapping.mapNote("B", Notes.A));

        assertThat(error.getMessage()).contains("'B'").contains("B");
        assertThat(mapping.getNote("B")).isEqualTo(Notes.B);
        assertThat(mapping.getNote("A")).isEqualTo(Notes.A);
        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
    }

    @Test
    void mapNoteToTheSameCharacterChangesNothing() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.mapNote("A", Notes.A);

        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
        assertThat(mapping.getNote("A")).isEqualTo(Notes.A);
    }

    @Test
    void digitOfAnotherStandaloneGroupIsRejected() {
        CharacterMapping mapping = new CharacterMapping();

        // "2" já troca o instrumento: não pode também subir o BPM
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> mapping.addElementChars(MappingType.BPM_UP, "2"));
        assertThat(error.getMessage()).contains(MappingType.INSTRUMENT.getGroupName());
        assertThrows(IllegalArgumentException.class, () -> mapping.replaceChar(MappingType.BPM_DOWN, "<", "4"));

        assertThat(mapping.getChars(MappingType.BPM_UP)).isEqualTo(">");
    }

    @Test
    void digitCanBeSharedWithAContextualGroup() {
        CharacterMapping mapping = new CharacterMapping();

        // "9" só é usado no atraso "[n]", e "3" só na duração ":3" e no atraso
        mapping.addElementChars(MappingType.BPM_UP, "9");
        mapping.addElementChars(MappingType.INSTRUMENT, "3");

        assertThat(mapping.getChars(MappingType.BPM_UP)).isEqualTo(">9");
        assertThat(mapping.getChars(MappingType.INSTRUMENT)).isEqualTo("024683");
    }

    @Test
    void addGroupIfAbsentKeepsExistingGroups() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.addGroupIfAbsent(MappingType.NOTE, "XYZ");
        mapping.addGroupIfAbsent(MappingType.TEMPO, "T");

        assertThat(mapping.getChars(MappingType.NOTE)).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars(MappingType.TEMPO)).isEqualTo("T");
    }

    @Test
    void restoreGoesBackToACopyIncludingHalfSwaps() {
        CharacterMapping mapping = new CharacterMapping();
        mapping.replaceChar(MappingType.NOTE, "C", "D");
        CharacterMapping saved = mapping.copy();

        mapping.reset();
        mapping.replaceChar(MappingType.NOTE, "A", "J");
        mapping.restore(saved);

        assertThat(mapping.getNote("D")).isEqualTo(Notes.C);
        assertThat(mapping.getNote("J")).isNull();
        assertThat(mapping.isComplete()).isFalse();

        // a cópia é independente: terminar a troca no original não mexe nela
        mapping.replaceChar(MappingType.NOTE, "D", "C");
        assertThat(mapping.isComplete()).isTrue();
        assertThat(saved.isComplete()).isFalse();
    }
}
