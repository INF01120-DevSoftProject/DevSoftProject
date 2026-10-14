package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AccidentalsTest {

    @Test
    void hasAllThreeAccidentals() {
        assertThat(Accidentals.values()).containsExactly(
            Accidentals.NONE,
            Accidentals.FLAT,
            Accidentals.SHARP
        );
    }
}
