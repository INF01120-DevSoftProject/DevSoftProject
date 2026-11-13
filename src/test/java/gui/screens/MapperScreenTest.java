package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.layout.BorderPane;
import translators.TextToMusic.TextToMusicRules;

class MapperScreenTest extends JavaFXTestBase {

    @BeforeEach
    @AfterEach
    void resetRules() {
        TextToMusicRules.resetMappings();
    }

    @Test
    void initializesMapperScreenWithMainContent() {
        MapperScreen screen = new MapperScreen();

        assertThat(screen.getChildren()).isNotEmpty();
        assertThat(screen.getChildren().getFirst()).isInstanceOf(BorderPane.class);

        BorderPane mainContent = (BorderPane) screen.getChildren().getFirst();
        assertThat(mainContent.getId()).isEqualTo("main");
        assertThat(mainContent.getTop()).isNotNull();
        assertThat(mainContent.getCenter()).isNotNull();
        assertThat(mainContent.getBottom()).isNotNull();
    }
}
