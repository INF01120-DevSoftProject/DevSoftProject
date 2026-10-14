package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PauseElementTest {
    
    @Test 
    void eachInstanceKeepsItsOwnDuration(){

        PauseElement pause1 = new PauseElement(DurationTime.WHOLE);
        PauseElement pause2 = new PauseElement(DurationTime.SIXTEENTH);

        assertThat(pause1.getDuration()).isEqualTo(DurationTime.WHOLE);
        assertThat(pause2.getDuration()).isEqualTo(DurationTime.SIXTEENTH);
    }

    @Test
    void invalidDuration(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PauseElement(null);
        });
    }

    @Test 
    void isAMusicElement() {
        assertThat(new PauseElement(DurationTime.EIGHTH)).isInstanceOf(MusicElement.class);
    }

}
