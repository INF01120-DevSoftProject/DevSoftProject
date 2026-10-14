package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VolumeElementTest {

    @Test
    void eachInstanceKeepsItsOwnVolume() {
        VolumeElement a = new VolumeElement(0);
        VolumeElement b = new VolumeElement(127);

        assertThat(a.getVolume()).isEqualTo(0);
        assertThat(b.getVolume()).isEqualTo(127);
    }

     @Test 
    void invalidNote(){
        assertThrows(IllegalArgumentException.class, () -> {
            new VolumeElement(-10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new VolumeElement(144);
        });
        
    }

    @Test
    void isAMusicElement() {
        assertThat(new VolumeElement(0)).isInstanceOf(MusicElement.class);
    }
}
