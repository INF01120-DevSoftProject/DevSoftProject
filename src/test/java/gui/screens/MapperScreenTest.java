package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import gui.JavaFXTestBase;
import gui.widgets.MappingWidget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import musicElements.Notes;
import translators.TextToMusic.CharacterMapping;

class MapperScreenTest extends JavaFXTestBase {

    private static class TestableMapperScreen extends MapperScreen {
        boolean confirmWarning;
        int warningCount;

        TestableMapperScreen(CharacterMapping mapping) {
            super(mapping);
        }

        @Override
        protected boolean showWarningMessage(String message) {
            warningCount++;
            return confirmWarning;
        }
    }

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
        // o centro começa mostrando as notas; o caractere "A" é Lá, não Dó
        BorderPane mainContent = (BorderPane) screen.getChildren().getFirst();
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

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void restoringDefaultMappingRequiresConfirmation(boolean confirm) {
        CharacterMapping mapping = new CharacterMapping();
        TestableMapperScreen screen = new TestableMapperScreen(mapping);
        BorderPane mainContent = (BorderPane) screen.getChildren().getFirst();
        FlowPane notes = (FlowPane) ((BorderPane) screen.getChildren().getFirst()).getCenter();
        field(notes, "Dó").setText("K");
        screen.saveMappings();
        field(notes, "Dó").setText("L");
        assertThat(screen.warningCount).isZero();
        screen.confirmWarning = confirm;

        Parent buttons = (Parent) mainContent.getBottom();
        for (Node node : buttons.getChildrenUnmodifiable()) {
            if (node instanceof Button button && button.getText().equals("Restaurar ao Padrão")) {
                button.fire();
            }
        }

        assertThat(screen.warningCount).isEqualTo(1);
        FlowPane notesAfter = (FlowPane) ((BorderPane) screen.getChildren().getFirst()).getCenter();
        if (confirm) {
            assertThat(mapping.getNote("C")).isEqualTo(Notes.C);
            assertThat(mapping.getNote("K")).isNull();
            assertThat(field(notesAfter, "Dó").getText()).isEqualTo("C");
        } else {
            assertThat(mapping.getNote("K")).isEqualTo(Notes.C);
            assertThat(field(notesAfter, "Dó").getText()).isEqualTo("L");
        }
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
