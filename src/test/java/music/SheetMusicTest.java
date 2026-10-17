package music;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import musicElements.Accidentals;
import musicElements.DurationTime;
import musicElements.NoteElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.PauseElement;
import musicElements.VolumeElement;

public class SheetMusicTest {

    @Test
    void startsEmpty() {
        assertThat(new SheetMusic().getSheetMusic()).isEmpty();
    }

    @Test
    void keepsElementsInAppendOrder() {
        SheetMusic sheet = new SheetMusic();
        NoteElement note = new NoteElement(Notes.C, new Octave(4), DurationTime.QUARTER, Accidentals.NONE);
        PauseElement pause = new PauseElement(DurationTime.HALF);
        VolumeElement volume = new VolumeElement(80);

        sheet.appendToSheetMusic(note);
        sheet.appendToSheetMusic(pause);
        sheet.appendToSheetMusic(volume);

        assertThat(sheet.getSheetMusic()).containsExactly(note, pause, volume);
    }

    @Test
    void nullElementIsRejected() {
        SheetMusic sheet = new SheetMusic();

        assertThrows(IllegalArgumentException.class, () -> sheet.appendToSheetMusic(null));
    }

    @Test
    void returnedListCannotBeModified() {
        SheetMusic sheet = new SheetMusic();

        assertThrows(UnsupportedOperationException.class,
            () -> sheet.getSheetMusic().add(new VolumeElement(10)));
    }
}
