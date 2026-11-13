package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

class ScreenTest extends JavaFXTestBase {

    static class DummyScreen extends Screen {
        public void setContent(Pane pane) {
            setScreenMainContent(pane);
        }
    }

    @Test
    void setsScreenMainContentAndBindsSizeProperties() {
        DummyScreen screen = new DummyScreen();
        VBox content = new VBox();

        screen.setContent(content);

        assertThat(screen.getChildren()).contains(content);
        assertThat(content.prefWidthProperty().isBound()).isTrue();
        assertThat(content.prefHeightProperty().isBound()).isTrue();
    }
}
