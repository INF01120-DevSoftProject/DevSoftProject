package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotesTest {

    @Test
    void hasAllSevenNotes() {
        assertThat(Notes.values()).containsExactly(
            Notes.C,
            Notes.D,
            Notes.E,
            Notes.F,
            Notes.G,
            Notes.A,
            Notes.B
        );
    }

    @Test
    void eachNoteKeepsItsOwnString() {
        assertThat(Notes.C.getString()).isEqualTo("C");
        assertThat(Notes.D.getString()).isEqualTo("D");
        assertThat(Notes.E.getString()).isEqualTo("E");
        assertThat(Notes.F.getString()).isEqualTo("F");
        assertThat(Notes.G.getString()).isEqualTo("G");
        assertThat(Notes.A.getString()).isEqualTo("A");
        assertThat(Notes.B.getString()).isEqualTo("B");
    }

    @Test
    void stringMatchesEnumName() {
        for (Notes note : Notes.values()) {
            assertThat(note.getString()).isEqualTo(note.name());
        }
    }
}
