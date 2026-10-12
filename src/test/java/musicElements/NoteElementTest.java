package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NoteElementTest {

    @Test
    void eachInstanceKeepsItsOwnNote() {
        NoteElement a = new NoteElement("A");
        NoteElement b = new NoteElement("B");
        NoteElement c = new NoteElement("Ab");
        NoteElement d = new NoteElement("B#");

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
    void invalidNote(){
        assertThrows(IllegalArgumentException.class, () -> {
            NoteElement a = new NoteElement("a");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            NoteElement b = new NoteElement("H");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            NoteElement b = new NoteElement(" ");
        });

         assertThrows(IllegalArgumentException.class, () -> {
            NoteElement b = new NoteElement(" b");
        });

         assertThrows(IllegalArgumentException.class, () -> {
            NoteElement b = new NoteElement("#");
        });

         assertThrows(IllegalArgumentException.class, () -> {
            NoteElement b = new NoteElement("Fa");
        });
    }

    @Test
    void isAMusicElement() {
        assertThat(new NoteElement("C")).isInstanceOf(MusicElement.class);
    }
}