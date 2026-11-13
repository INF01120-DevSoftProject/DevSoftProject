package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;

class SheetMusicWidgetTest extends JavaFXTestBase {

    @Test
    void createsSheetMusicWidgetWithValidMidi() {
        Path midiPath = Path.of("src", "main", "resources", "musics", "musica.mid");
        SheetMusicWidget widget = new SheetMusicWidget(midiPath, () -> 0.0);

        try {
            assertThat(widget.getSheetView()).isNotNull();
            assertThat(widget.getSheetViewAnimation()).isNotNull();
        } finally {
            widget.stopAnimation();
        }
    }
}
