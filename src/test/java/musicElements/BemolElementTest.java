package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BemolElementTest {

    private final BemolElement bemol = new BemolElement();

    @Test
    void naturalNoteBecomesFlat() {
        NoteElement result = bemol.applyTo(new NoteElement("C", DurationTime.QUARTER));

        assertThat(result.getNote()).isEqualTo("Cb");
        assertThat(result.isFlat()).isTrue();
        assertThat(result.isSharp()).isFalse();
    }

    @Test
    void everyNaturalNoteBecomesFlat() {
        for (char base = 'A'; base <= 'G'; base++){
            NoteElement result = bemol.applyTo(new NoteElement(String.valueOf(base), DurationTime.QUARTER));

            assertThat(result.getNote()).isEqualTo(base + "b");
        }
    }

    @Test
    void sharpNoteBecomesNatural() {
        NoteElement result = bemol.applyTo(new NoteElement("F#", DurationTime.QUARTER));

        assertThat(result.getNote()).isEqualTo("F");
        assertThat(result.isFlat()).isFalse();
        assertThat(result.isSharp()).isFalse();
    }

    @Test
    void flatNoteStaysTheSame() {
        NoteElement flat = new NoteElement("Bb", DurationTime.QUARTER);

        assertThat(bemol.applyTo(flat)).isSameAs(flat);
    }

    @Test
    void durationIsKept() {
        NoteElement natural = bemol.applyTo(new NoteElement("D", DurationTime.HALF));
        NoteElement sharp = bemol.applyTo(new NoteElement("D#", DurationTime.SIXTEENTH));

        assertThat(natural.getDuration()).isEqualTo(DurationTime.HALF);
        assertThat(sharp.getDuration()).isEqualTo(DurationTime.SIXTEENTH);
    }

    @Test
    void originalNoteIsNotChanged() {
        NoteElement original = new NoteElement("E", DurationTime.QUARTER);

        bemol.applyTo(original);

        assertThat(original.getNote()).isEqualTo("E");
    }

    @Test
    void nullNoteIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> bemol.applyTo(null));
    }

    @Test
    void isAMusicElement() {
        assertThat(bemol).isInstanceOf(MusicElement.class);
    }
}
