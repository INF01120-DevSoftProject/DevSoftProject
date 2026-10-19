package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class InstrumentElementTest {

    @Test
    void typeConstructorUsesDefaultInstrument() {
        for (InstrumentType type : InstrumentType.values()){
            InstrumentElement element = new InstrumentElement(type);

            assertThat(element.getType()).isEqualTo(type);
            assertThat(element.getInstrument()).isEqualTo(type.getDefaultInstrument());
        }
    }

    @Test
    void acceptsBoundariesAndFallsBackToDefaultOutsideRange() {
        assertThat(new InstrumentElement(40, InstrumentType.STRINGS).getInstrument()).isEqualTo(40);
        assertThat(new InstrumentElement(55, InstrumentType.STRINGS).getInstrument()).isEqualTo(55);

        assertThat(new InstrumentElement(39, InstrumentType.STRINGS).getInstrument()).isEqualTo(40);
        assertThat(new InstrumentElement(56, InstrumentType.STRINGS).getInstrument()).isEqualTo(40);
    }

    @Test
    void rejectsNegativeInstrumentAndNullType() {
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(-1, InstrumentType.KEYS));
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(null));
        assertThrows(IllegalArgumentException.class, () -> new InstrumentElement(0, null));
    }
}
