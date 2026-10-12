package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DefaultElementTest {

    @Test
    void keepsWrappedElement() {
        PauseElement pause = new PauseElement(DurationTime.QUARTER);

        DefaultElement element = new DefaultElement(pause);

        assertThat(element.getTypeElement()).isSameAs(pause);
    }

    @Test
    void nullElementIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultElement(null));
    }

    @Test
    void isAMusicElement() {
        assertThat(new DefaultElement(new VolumeElement(10))).isInstanceOf(MusicElement.class);
    }
}
