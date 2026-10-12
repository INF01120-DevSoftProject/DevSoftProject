package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTempoElementTest {

    @Test
    void keepsItsDuration() {
        assertThat(new NoteTempoElement(DurationTime.EIGHTH).getDuration()).isEqualTo(DurationTime.EIGHTH);
        assertThat(new NoteTempoElement(DurationTime.WHOLE).getDuration()).isEqualTo(DurationTime.WHOLE);
    }

    @Test
    void changesDurationAndKeepsNote() {
        NoteTempoElement eighth = new NoteTempoElement(DurationTime.EIGHTH);

        NoteElement result = eighth.applyTo(new NoteElement("C#", DurationTime.QUARTER));

        assertThat(result.getNote()).isEqualTo("C#");
        assertThat(result.isSharp()).isTrue();
        assertThat(result.getDuration()).isEqualTo(DurationTime.EIGHTH);
    }

    @Test
    void worksWithEveryDuration() {
        NoteElement note = new NoteElement("A", DurationTime.QUARTER);

        for (DurationTime duration : DurationTime.values()){
            assertThat(new NoteTempoElement(duration).applyTo(note).getDuration()).isEqualTo(duration);
        }
    }

    @Test
    void originalNoteIsNotChanged() {
        NoteElement original = new NoteElement("G", DurationTime.QUARTER);

        new NoteTempoElement(DurationTime.HALF).applyTo(original);

        assertThat(original.getDuration()).isEqualTo(DurationTime.QUARTER);
    }

    @Test
    void invalidValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new NoteTempoElement(null));
        assertThrows(IllegalArgumentException.class, () -> new NoteTempoElement(DurationTime.HALF).applyTo(null));
    }

    @Test
    void isAMusicElement() {
        assertThat(new NoteTempoElement(DurationTime.QUARTER)).isInstanceOf(MusicElement.class);
    }
}
