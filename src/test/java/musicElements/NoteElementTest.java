package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NoteElementTest {

    private static final OctaveElement OCTAVE = new OctaveElement(4);

    @Test
    void eachInstanceKeepsItsOwnNote() {
        NoteElement a = new NoteElement(Notes.A, OCTAVE, DurationTime.QUARTER, Accidentals.NONE);
        NoteElement b = new NoteElement(Notes.B, OCTAVE, DurationTime.QUARTER, Accidentals.NONE);
        NoteElement c = new NoteElement(Notes.A, OCTAVE, DurationTime.QUARTER, Accidentals.FLAT);
        NoteElement d = new NoteElement(Notes.B, OCTAVE, DurationTime.QUARTER, Accidentals.SHARP);

        assertThat(a.getNote()).isEqualTo(Notes.A);
        assertThat(b.getNote()).isEqualTo(Notes.B);
        assertThat(c.getNote()).isEqualTo(Notes.A);
        assertThat(d.getNote()).isEqualTo(Notes.B);

        assertThat(a.getNoteString()).isEqualTo("A");
        assertThat(b.getNoteString()).isEqualTo("B");
        assertThat(c.getNoteString()).isEqualTo("A");
        assertThat(d.getNoteString()).isEqualTo("B");
    }

    @Test
    void eachInstanceKeepsItsOwnAccidental() {
        NoteElement natural = new NoteElement(Notes.A, OCTAVE, DurationTime.QUARTER, Accidentals.NONE);
        NoteElement flat = new NoteElement(Notes.A, OCTAVE, DurationTime.QUARTER, Accidentals.FLAT);
        NoteElement sharp = new NoteElement(Notes.B, OCTAVE, DurationTime.QUARTER, Accidentals.SHARP);

        assertThat(natural.getAccidental()).isEqualTo(Accidentals.NONE);
        assertThat(flat.getAccidental()).isEqualTo(Accidentals.FLAT);
        assertThat(sharp.getAccidental()).isEqualTo(Accidentals.SHARP);
    }

    @Test
    void acceptsEveryNoteWithEveryAccidentalInEveryOctave() {
        for (int octaveValue = OctaveElement.MIN_OCTAVE; octaveValue <= OctaveElement.MAX_OCTAVE; octaveValue++) {
            OctaveElement octave = new OctaveElement(octaveValue);

            for (Notes note : Notes.values()) {
                for (Accidentals accidental : Accidentals.values()) {
                    NoteElement element = new NoteElement(note, octave, DurationTime.QUARTER, accidental);

                    assertThat(element.getNote()).isEqualTo(note);
                    assertThat(element.getOctaveValue()).isEqualTo(octaveValue);
                    assertThat(element.getAccidental()).isEqualTo(accidental);
                }
            }
        }
    }

    @Test
    void eachInstanceKeepsItsOwnOctave() {
        OctaveElement low = new OctaveElement(OctaveElement.MIN_OCTAVE);
        OctaveElement mid = new OctaveElement(4);
        OctaveElement high = new OctaveElement(OctaveElement.MAX_OCTAVE);

        NoteElement a = new NoteElement(Notes.C, low, DurationTime.QUARTER, Accidentals.NONE);
        NoteElement b = new NoteElement(Notes.C, mid, DurationTime.QUARTER, Accidentals.NONE);
        NoteElement c = new NoteElement(Notes.C, high, DurationTime.QUARTER, Accidentals.NONE);

        assertThat(a.getOctaveElement()).isSameAs(low);
        assertThat(b.getOctaveElement()).isSameAs(mid);
        assertThat(c.getOctaveElement()).isSameAs(high);

        assertThat(a.getOctaveValue()).isEqualTo(0);
        assertThat(b.getOctaveValue()).isEqualTo(4);
        assertThat(c.getOctaveValue()).isEqualTo(8);
    }

    @Test
    void invalidNote() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(null, OCTAVE, DurationTime.QUARTER, Accidentals.NONE);
        });
    }

    @Test
    void invalidOctave() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(Notes.C, null, DurationTime.QUARTER, Accidentals.NONE);
        });
    }

    @Test
    void invalidAccidental() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(Notes.C, OCTAVE, DurationTime.QUARTER, null);
        });
    }

    @Test
    void eachInstanceKeepsItsOwnDuration() {
        NoteElement a = new NoteElement(Notes.A, OCTAVE, DurationTime.WHOLE, Accidentals.NONE);
        NoteElement b = new NoteElement(Notes.B, OCTAVE, DurationTime.HALF, Accidentals.NONE);
        NoteElement c = new NoteElement(Notes.A, OCTAVE, DurationTime.EIGHTH, Accidentals.FLAT);
        NoteElement d = new NoteElement(Notes.B, OCTAVE, DurationTime.SIXTY_FOURTH, Accidentals.SHARP);

        assertThat(a.getDuration()).isEqualTo(DurationTime.WHOLE);
        assertThat(b.getDuration()).isEqualTo(DurationTime.HALF);
        assertThat(c.getDuration()).isEqualTo(DurationTime.EIGHTH);
        assertThat(d.getDuration()).isEqualTo(DurationTime.SIXTY_FOURTH);
    }

    @Test
    void acceptsEveryDuration() {
        for (DurationTime duration : DurationTime.values()) {
            NoteElement note = new NoteElement(Notes.C, OCTAVE, duration, Accidentals.NONE);

            assertThat(note.getDuration()).isEqualTo(duration);
            assertThat(note.getNote()).isEqualTo(Notes.C);
            assertThat(note.getNoteString()).isEqualTo("C");
        }
    }

    @Test
    void durationDoesNotAffectNote() {
        for (DurationTime duration : DurationTime.values()) {
            NoteElement natural = new NoteElement(Notes.D, OCTAVE, duration, Accidentals.NONE);
            NoteElement flat = new NoteElement(Notes.D, OCTAVE, duration, Accidentals.FLAT);
            NoteElement sharp = new NoteElement(Notes.D, OCTAVE, duration, Accidentals.SHARP);

            assertThat(natural.getNote()).isEqualTo(Notes.D);
            assertThat(flat.getNote()).isEqualTo(Notes.D);
            assertThat(sharp.getNote()).isEqualTo(Notes.D);

            assertThat(natural.getAccidental()).isEqualTo(Accidentals.NONE);
            assertThat(flat.getAccidental()).isEqualTo(Accidentals.FLAT);
            assertThat(sharp.getAccidental()).isEqualTo(Accidentals.SHARP);
        }
    }

    @Test
    void sameNoteWithDifferentDurationsKeepsEachOne() {
        NoteElement quarter = new NoteElement(Notes.C, OCTAVE, DurationTime.QUARTER, Accidentals.NONE);
        NoteElement half = new NoteElement(Notes.C, OCTAVE, DurationTime.HALF, Accidentals.NONE);

        assertThat(quarter.getNote()).isEqualTo(half.getNote());
        assertThat(quarter.getDuration()).isNotEqualTo(half.getDuration());
    }

    @Test
    void invalidDuration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(Notes.A, OCTAVE, null, Accidentals.NONE);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(Notes.A, OCTAVE, null, Accidentals.FLAT);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new NoteElement(Notes.B, OCTAVE, null, Accidentals.SHARP);
        });
    }

    @Test
    void isAMusicElement() {
        assertThat(new NoteElement(Notes.C, OCTAVE, DurationTime.QUARTER, Accidentals.NONE))
            .isInstanceOf(MusicElement.class);
    }
}