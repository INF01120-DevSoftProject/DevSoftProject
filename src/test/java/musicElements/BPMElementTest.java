package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BPMElementTest {
    
    @Test 
    void eachInstanceKeepsItsOwnBPM(){

        BPMElement bpm1 = new BPMElement(67);
        BPMElement bpm2 = new BPMElement(20);
        BPMElement bpm3 = new BPMElement(999);

        assertThat(bpm1.getBPM()).isEqualTo(67);
        assertThat(bpm2.getBPM()).isEqualTo(20);
        assertThat(bpm3.getBPM()).isEqualTo(999);
    }

    @Test
    void BPMsecure(){

        assertThrows(IllegalArgumentException.class, () -> {
            new BPMElement(19);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new BPMElement(1000);
        });
    }
    
    @Test
    void isAMusicElement() {
        assertThat(new BPMElement(22)).isInstanceOf(MusicElement.class);
    }
}
