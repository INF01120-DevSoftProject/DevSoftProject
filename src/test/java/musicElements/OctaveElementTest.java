package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class OctaveElementTest {
    
    @Test 
    void eachInstanceKeepsItsOwnOctave(){
        OctaveElement a = new OctaveElement(0);
        OctaveElement b = new OctaveElement(4);
        OctaveElement c = new OctaveElement(8);

        assertThat(a.getOctave()).isEqualTo(0);
        assertThat(b.getOctave()).isEqualTo(4);
        assertThat(c.getOctave()).isEqualTo(8);
    }

    @Test
    void octaveLimitsAreZeroAndEight(){
        assertThat(OctaveElement.MIN_OCTAVE).isEqualTo(0);
        assertThat(OctaveElement.MAX_OCTAVE).isEqualTo(8);
    }

    @Test 
    void invalidOctave(){
        assertThrows(IllegalArgumentException.class, () -> {
            new OctaveElement(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new OctaveElement(9);
        });
    }

    @Test
    void isAMusicElement() {
        assertThat(new OctaveElement(4)).isInstanceOf(MusicElement.class);
    }

}
