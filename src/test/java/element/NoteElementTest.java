package element;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoteElementTest {

    @Test
    void eachInstanceKeepsItsOwnNote() {
        NoteElement a = new NoteElement("A");
        NoteElement b = new NoteElement("B");

        assertThat(a.getNote()).isEqualTo("A");
        assertThat(b.getNote()).isEqualTo("B");
    }

    @Test
    void isAMusicElement() {
        assertThat(new NoteElement("C")).isInstanceOf(MusicElement.class);
    }
}