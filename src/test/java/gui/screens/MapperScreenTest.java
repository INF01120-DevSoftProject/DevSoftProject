package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import gui.widgets.MappingWidget;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
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

    @Test
    void noteLabelsComeFromTheMappingInsteadOfThePosition() {
        MapperScreen screen = new MapperScreen(new CharacterMapping());
        BorderPane mainContent = (BorderPane) screen.getChildren().getFirst();

        // o centro começa mostrando as notas; o caractere "A" é Lá, não Dó
        assertThat(labelToCharacter((FlowPane) mainContent.getCenter())).containsExactly(
            Map.entry("Dó", "C"), Map.entry("Ré", "D"), Map.entry("Mi", "E"), Map.entry("Fá", "F"),
            Map.entry("Sol", "G"), Map.entry("Lá", "A"), Map.entry("Si", "B"));
    }

    private Map<String, String> labelToCharacter(FlowPane container) {
        Map<String, String> widgets = new LinkedHashMap<>();
        for (Node node : container.getChildren()) {
            MappingWidget widget = (MappingWidget) node;
            Label label = (Label) widget.getChildren().get(0);
            TextField field = (TextField) widget.getChildren().get(1);
            widgets.put(label.getText(), field.getText());
        }
        return widgets;
    }
}
