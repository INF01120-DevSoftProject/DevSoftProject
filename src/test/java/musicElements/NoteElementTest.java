package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NoteElementTest {

    @Test
    void eachInstanceKeepsItsOwnNote() {
        NoteElement a = new NoteElement("A", DurationTime.QUARTER);
        NoteElement b = new NoteElement("B", DurationTime.QUARTER);
        NoteElement c = new NoteElement("Ab", DurationTime.QUARTER);
        NoteElement d = new NoteElement("B#", DurationTime.QUARTER);

        assertThat(a.getNoteBase()).isEqualTo('A');
        assertThat(b.getNoteBase()).isEqualTo('B');
        assertThat(c.getNoteBase()).isEqualTo('A');
        assertThat(d.getNoteBase()).isEqualTo('B');

        assertThat(a.getNote()).isEqualTo("A");
        assertThat(b.getNote()).isEqualTo("B");
        assertThat(c.getNote()).isEqualTo("Ab");
        assertThat(d.getNote()).isEqualTo("B#");
    }

    @Test
    void flatAndSharpAreIdentified() {
        NoteElement natural = new NoteElement("A", DurationTime.QUARTER);
        NoteElement flat = new NoteElement("Ab", DurationTime.QUARTER);
        NoteElement sharp = new NoteElement("B#", DurationTime.QUARTER);

        assertThat(natural.isFlat()).isFalse();
        assertThat(natural.isSharp()).isFalse();

        assertThat(flat.isFlat()).isTrue();
        assertThat(flat.isSharp()).isFalse();

        assertThat(sharp.isFlat()).isFalse();
        assertThat(sharp.isSharp()).isTrue();
    }

    @Test 
    void invalidNote(){
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("a", DurationTime.QUARTER);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("H", DurationTime.QUARTER);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(" ", DurationTime.QUARTER);
        });

         assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(" b", DurationTime.QUARTER);
        });

         assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("#", DurationTime.QUARTER);
        });

         assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("Fa", DurationTime.QUARTER);
        });
    }

    @Test
    void eachInstanceKeepsItsOwnDuration() {
        NoteElement a = new NoteElement("A", DurationTime.WHOLE);
        NoteElement b = new NoteElement("B", DurationTime.HALF);
        NoteElement c = new NoteElement("Ab", DurationTime.EIGHTH);
        NoteElement d = new NoteElement("B#", DurationTime.SIXTY_FOURTH);

        assertThat(a.getDuration()).isEqualTo(DurationTime.WHOLE);
        assertThat(b.getDuration()).isEqualTo(DurationTime.HALF);
        assertThat(c.getDuration()).isEqualTo(DurationTime.EIGHTH);
        assertThat(d.getDuration()).isEqualTo(DurationTime.SIXTY_FOURTH);
    }

    @Test
    void acceptsEveryDuration() {
        for (DurationTime duration : DurationTime.values()) {
            NoteElement note = new NoteElement("C", duration);

            assertThat(note.getDuration()).isEqualTo(duration);
            assertThat(note.getNote()).isEqualTo("C");
        }
    }

    @Test
    void durationDoesNotAffectNote() {
        for (DurationTime duration : DurationTime.values()) {
            NoteElement natural = new NoteElement("D", duration);
            NoteElement flat = new NoteElement("Db", duration);
            NoteElement sharp = new NoteElement("D#", duration);

            assertThat(natural.getNote()).isEqualTo("D");
            assertThat(flat.getNote()).isEqualTo("Db");
            assertThat(sharp.getNote()).isEqualTo("D#");

            assertThat(flat.isFlat()).isTrue();
            assertThat(sharp.isSharp()).isTrue();
        }
    }

    @Test
    void sameNoteWithDifferentDurationsKeepsEachOne() {
        NoteElement quarter = new NoteElement("C", DurationTime.QUARTER);
        NoteElement half = new NoteElement("C", DurationTime.HALF);

        assertThat(quarter.getNote()).isEqualTo(half.getNote());
        assertThat(quarter.getDuration()).isNotEqualTo(half.getDuration());
    }

    @Test
    void invalidDuration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("A", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("Ab", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement("B#", null);
        });
    }

    @Test
    void isAMusicElement() {
        assertThat(new NoteElement("C", DurationTime.QUARTER)).isInstanceOf(MusicElement.class);
    }
}