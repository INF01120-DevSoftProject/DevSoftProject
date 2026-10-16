package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class InstrumentTypeTest {

    @Test
    void hasSixteenFamilies() {
        assertThat(InstrumentType.values()).hasSize(16);
    }

    @Test
    void containsItsMinAndMax() {
        for (InstrumentType type : InstrumentType.values()){
            assertThat(type.contains(type.getMinInstrument())).isTrue();
            assertThat(type.contains(type.getMaxInstrument())).isTrue();
        }
    }

    @Test
    void doesNotContainValuesJustOutsideItsLimits() {
        for (InstrumentType type : InstrumentType.values()){
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

    @Test
    void familiesFollowGeneralMidi() {
        assertThat(InstrumentType.PIANO.getMinInstrument()).isEqualTo(0);
        assertThat(InstrumentType.PIANO.getMaxInstrument()).isEqualTo(7);
        assertThat(InstrumentType.STRINGS.getMinInstrument()).isEqualTo(40);
        assertThat(InstrumentType.STRINGS.getMaxInstrument()).isEqualTo(47);
        assertThat(InstrumentType.PERCUSSION.getMinInstrument()).isEqualTo(112);
        assertThat(InstrumentType.PERCUSSION.getMaxInstrument()).isEqualTo(119);
        assertThat(InstrumentType.SOUND_EFFECTS.getMaxInstrument()).isEqualTo(127);
    }
}
