package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class InstrumentTypeTest {

    @Test
    void containsBoundariesAndRejectsOutsideValues() {
        for (InstrumentType type : InstrumentType.values()){
            assertThat(type.getDefaultInstrument()).isEqualTo(type.getMinInstrument());
            assertThat(type.contains(type.getMinInstrument())).isTrue();
            assertThat(type.contains(type.getMaxInstrument())).isTrue();
            assertThat(type.contains(type.getMinInstrument() - 1)).isFalse();
            assertThat(type.contains(type.getMaxInstrument() + 1)).isFalse();
        }
    }

    @Test
    void everyMidiInstrumentBelongsToExactlyOneFamily() {
        for (int instrument = 0; instrument <= 127; instrument++){
            int families = 0;
            for (InstrumentType type : InstrumentType.values()){
                if (type.contains(instrument)) families++;
            }
            assertThat(families).as("instrumento %d", instrument).isEqualTo(1);
        }
    }
}
