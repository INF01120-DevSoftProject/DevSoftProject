package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.bytebuddy.dynamic.scaffold.TypeInitializer.None;

class AccidentalsTest {

    @Test
    void hasAllThreeAccidentals() {
        assertThat(Accidentals.values()).containsExactly(
            Accidentals.NONE,
            Accidentals.FLAT,
            Accidentals.SHARP
        );
    }

    @Test
    void checkConstructor(){
        
        assertThat(Accidentals.valueOf("FLAT")).isEqualTo(Accidentals.FLAT);
        
    }
}
