package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.layout.BorderPane;
import translators.TextToMusic.CharacterMapping;

class MapperScreenTest extends JavaFXTestBase {

    @Test
    void initializesMapperScreenWithMainContent() {
        MapperScreen screen = new MapperScreen(new CharacterMapping());

        assertThat(screen.getChildren()).isNotEmpty();
        assertThat(screen.getChildren().getFirst()).isInstanceOf(BorderPane.class);

        BorderPane mainContent = (BorderPane) screen.getChildren().getFirst();
        assertThat(mainContent.getId()).isEqualTo("main");
        assertThat(mainContent.getTop()).isNotNull();
        assertThat(mainContent.getCenter()).isNotNull();
        assertThat(mainContent.getBottom()).isNotNull();
    }
}
