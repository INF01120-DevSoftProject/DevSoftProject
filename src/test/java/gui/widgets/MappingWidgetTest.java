package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import translators.TextToMusic.TextToMusicRules;

class MappingWidgetTest extends JavaFXTestBase {

    @BeforeEach
    @AfterEach
    void resetRules() {
        TextToMusicRules.resetMappings();
    }

    @Test
    void initializesWithLabelAndTextField() {
        MappingWidget widget = new MappingWidget("note", "Nota D\u00f3", "A");

        assertThat(widget.getStyleClass()).contains("mapping-node");
        assertThat(widget.getChildren()).hasSize(2);

        Label label = (Label) widget.getChildren().get(0);
        TextField field = (TextField) widget.getChildren().get(1);

        assertThat(label.getText()).isEqualTo("Nota D\u00f3");
        assertThat(field.getText()).isEqualTo("A");
    }

    @Test
    void savesChangeWhenCharacterIsModified() {
        MappingWidget widget = new MappingWidget("note", "Nota D\u00f3", "A");
        TextField field = (TextField) widget.getChildren().get(1);

        field.setText("K");
        widget.saveChange();

        assertThat(TextToMusicRules.getMappingCharacters("note")).contains("K");
    }

    @Test
    void doesNotModifyRulesWhenCharacterIsNotChanged() {
        MappingWidget widget = new MappingWidget("note", "Nota D\u00f3", "A");

        widget.saveChange();

        assertThat(TextToMusicRules.getMappingCharacters("note")).contains("A");
    }
}
