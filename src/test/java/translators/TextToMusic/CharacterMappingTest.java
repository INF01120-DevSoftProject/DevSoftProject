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
    void addGroupIfAbsentKeepsExistingGroups() {
        CharacterMapping mapping = new CharacterMapping();

        mapping.addGroupIfAbsent("note", "XYZ");
        mapping.addGroupIfAbsent("tempo", "T");

        assertThat(mapping.getChars("note")).isEqualTo("ABCDEFG");
        assertThat(mapping.getChars("tempo")).isEqualTo("T");
    }
}
