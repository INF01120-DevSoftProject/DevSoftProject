package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class InstrumentElementTest {

    @Test
    void eachInstanceKeepsItsOwnInstrument() {
        InstrumentElement a = new InstrumentElement(0);
        InstrumentElement b = new InstrumentElement(127);

        assertThat(a.getInstrument()).isEqualTo(0);
        assertThat(b.getInstrument()).isEqualTo(127);
    }

    @Test
    void invalidInstrument(){
        assertThrows(IllegalArgumentException.class, () -> {
            new InstrumentElement(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new InstrumentElement(128);
        });
    }

    @Test
    void isAMusicElement() {
        assertThat(new InstrumentElement(0)).isInstanceOf(MusicElement.class);
    }
}
