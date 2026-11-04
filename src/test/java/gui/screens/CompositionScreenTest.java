package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import exporters.MidiFileExporter;
import exporters.MusicExportPipeline;
import gui.JavaFXTestBase;
import gui.widgets.VoiceWidget;
import input.TXTInputReader;
import javafx.scene.layout.VBox;
import parsers.MusicToMidiParser;
import translators.TextToMusic.TextToMusicTranslator;
import utils.MidiMusicPathUtils;

class CompositionScreenTest extends JavaFXTestBase {

    @Test
    void initializesCompositionScreenWithSingleVoice() {
        CompositionScreen screen = new CompositionScreen(
                new TXTInputReader(),
                new MidiMusicPathUtils(),
                new MusicExportPipeline<>(new TextToMusicTranslator(), new MusicToMidiParser(), new MidiFileExporter())
        );

        assertThat(screen.getChildren()).isNotEmpty();
        assertThat(screen.getChildren().getFirst()).isInstanceOf(VBox.class);

        VBox mainContent = (VBox) screen.getChildren().getFirst();
        assertThat(mainContent.getId()).isEqualTo("main");
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(1);
    }
}
