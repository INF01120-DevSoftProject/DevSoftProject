package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class OctaveTest {
    
    @Test 
    void eachInstanceKeepsItsOwnOctave(){
        Octave a = new Octave(0);
        Octave b = new Octave(4);
        Octave c = new Octave(8);

        assertThat(a.getOctaveInt()).isEqualTo(0);
        assertThat(b.getOctaveInt()).isEqualTo(4);
        assertThat(c.getOctaveInt()).isEqualTo(8);
    }

    @Test
    void octaveLimitsAreZeroAndEight(){
        assertThat(Octave.MIN_OCTAVE).isEqualTo(0);
        assertThat(Octave.MAX_OCTAVE).isEqualTo(8);
    }

    @Test 
    void invalidOctave(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Octave(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Octave(9);
        });
    }

}
