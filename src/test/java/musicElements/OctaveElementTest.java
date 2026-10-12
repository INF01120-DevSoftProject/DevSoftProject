package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class OctaveElementTest {
    
    @Test 
    void numberPreserved(){
        OctaveElement a = new OctaveElement(0);
        OctaveElement b = new OctaveElement(8);

        assertThat(a.getOctave()).isEqualTo(0);
        assertThat(b.getOctave()).isEqualTo(8);
    }

    @Test 
    void invalidOctave(){
        assertThrows(IllegalArgumentException.class, () -> {
            OctaveElement a = new OctaveElement(-2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OctaveElement b = new OctaveElement(10);
        });
    }

      @Test
    void isAMusicElement() {
        assertThat(new OctaveElement(4)).isInstanceOf(MusicElement.class);
    }

}
