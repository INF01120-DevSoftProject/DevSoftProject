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

    @Test
    void acceptsInstrumentInsideItsType() {
        assertThat(new InstrumentElement(40, InstrumentType.STRINGS).getInstrument()).isEqualTo(40);
        assertThat(new InstrumentElement(47, InstrumentType.STRINGS).getInstrument()).isEqualTo(47);
    }

    @Test
    void rejectsInstrumentOutsideItsType() {
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(39, InstrumentType.STRINGS));
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(48, InstrumentType.STRINGS));
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(0, InstrumentType.PERCUSSION));
    }

    @Test
    void typedConstructorKeepsTheMidiLimit() {
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(-1, InstrumentType.PIANO));
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(128, InstrumentType.SOUND_EFFECTS));
    }

    @Test
    void typedConstructorRejectsNullType() {
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(0, null));
    }
}
