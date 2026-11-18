package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import musicElements.Notes;
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

    @Test
    void savingAHalfSwapFailsAndUndoesTheSave() {
        CharacterMapping mapping = new CharacterMapping();
        MapperScreen screen = new MapperScreen(mapping);
        FlowPane notes = (FlowPane) ((BorderPane) screen.getChildren().getFirst()).getCenter();

        field(notes, "Dó").setText("D");

        assertThatThrownBy(screen::saveMappings)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mapeamento incompleto");

        // o salvamento foi desfeito: o mapeamento e os campos voltam ao que eram
        FlowPane notesAfter = (FlowPane) ((BorderPane) screen.getChildren().getFirst()).getCenter();
        assertThat(mapping.isComplete()).isTrue();
        assertThat(mapping.getNote("C")).isEqualTo(Notes.C);
        assertThat(mapping.getNote("D")).isEqualTo(Notes.D);
        assertThat(field(notesAfter, "Dó").getText()).isEqualTo("C");
        assertThat(field(notesAfter, "Ré").getText()).isEqualTo("D");
    }

    @Test
    void savingACompleteSwapWorks() {
        CharacterMapping mapping = new CharacterMapping();
        MapperScreen screen = new MapperScreen(mapping);
        FlowPane notes = (FlowPane) ((BorderPane) screen.getChildren().getFirst()).getCenter();

        field(notes, "Dó").setText("D");
        field(notes, "Ré").setText("C");
        screen.saveMappings();

        assertThat(mapping.getNote("D")).isEqualTo(Notes.C);
        assertThat(mapping.getNote("C")).isEqualTo(Notes.D);
    }

    private TextField field(FlowPane container, String label) {
        for (Node node : container.getChildren()) {
            MappingWidget widget = (MappingWidget) node;
            if (((Label) widget.getChildren().get(0)).getText().equals(label)) {
                return (TextField) widget.getChildren().get(1);
            }
        }
        throw new AssertionError("Sem campo " + label);
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
