package gui.builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

class BorderPaneBuilderTest extends JavaFXTestBase {

    @Test
    void buildsEmptyBorderPane() {
        BorderPane pane = new BorderPaneBuilder().build();

        assertThat(pane).isNotNull();
    }

    @Test
    void addsStyleClass() {
        BorderPane pane = new BorderPaneBuilder()
                .addClass("custom-border")
                .build();

        assertThat(pane.getStyleClass()).contains("custom-border");
    }

    @Test
    void throwsExceptionWhenClassNameIsNull() {
        BorderPaneBuilder builder = new BorderPaneBuilder();

        assertThrows(NullPointerException.class, () -> builder.addClass(null));
    }

    @Test
    void appliesConfigurationConsumer() {
        Label centerNode = new Label("Center");
        BorderPane pane = new BorderPaneBuilder()
                .apply(b -> b.setCenter(centerNode))
                .build();

        assertThat(pane.getCenter()).isSameAs(centerNode);
    }

    @Test
    void setsId() {
        BorderPane pane = new BorderPaneBuilder()
                .setId("custom-id")
                .build();

        assertThat(pane.getId()).isEqualTo("main");
    }

    @Test
    void throwsExceptionWhenIdIsNull() {
        BorderPaneBuilder builder = new BorderPaneBuilder();

        assertThrows(NullPointerException.class, () -> builder.setId(null));
    }
}
