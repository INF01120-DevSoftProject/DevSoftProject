package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import gui.JavaFXTestBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import translators.TextToMusic.CharacterMapping;

class MappingWidgetTest extends JavaFXTestBase {

    private final CharacterMapping mapping = new CharacterMapping();

    @Test
    void initializesWithLabelAndTextField() {
        MappingWidget widget = new MappingWidget(mapping, "note", "Nota D\u00f3", "A");

        assertThat(widget.getStyleClass()).contains("mapping-node");
        assertThat(widget.getChildren()).hasSize(2);

        Label label = (Label) widget.getChildren().get(0);
        TextField field = (TextField) widget.getChildren().get(1);

        assertThat(label.getText()).isEqualTo("Nota D\u00f3");
        assertThat(field.getText()).isEqualTo("A");
    }

    @Test
    void savesChangeWhenCharacterIsModified() {
        MappingWidget widget = new MappingWidget(mapping, "note", "Nota D\u00f3", "A");
        TextField field = (TextField) widget.getChildren().get(1);

        field.setText("K");
        widget.saveChange();

        assertThat(mapping.getMappingCharacters("note")).contains("K");
    }

    @Test
    void doesNotModifyRulesWhenCharacterIsNotChanged() {
        MappingWidget widget = new MappingWidget(mapping, "note", "Nota D\u00f3", "A");

        widget.saveChange();

        assertThat(mapping.getMappingCharacters("note")).contains("A");
    }

    @Test
    void displaysSpaceWithoutChangingTheDoubleVolumeCharacter() {
        MappingWidget widget = new MappingWidget(mapping, "doubleVolume", "Dobrar Volume", " ");
        TextField field = (TextField) widget.getChildren().get(1);

        assertThat(field.getText()).isEqualTo("SPACE");
        widget.saveChange();

        assertThat(mapping.getChars("doubleVolume")).isEqualTo(" ");
    }

    @Test
    void allowsChangingDoubleVolumeAndReturningToSpace() {
        MappingWidget widget = new MappingWidget(mapping, "doubleVolume", "Dobrar Volume", " ");
        TextField field = (TextField) widget.getChildren().get(1);

        field.setText("_");
        widget.saveChange();
        assertThat(mapping.getChars("doubleVolume")).isEqualTo("_");

        field.setText(" ");
        assertThat(field.getText()).isEqualTo("SPACE");
        widget.saveChange();
        assertThat(mapping.getChars("doubleVolume")).isEqualTo(" ");
    }

    @Test
    void typingOneCharacterReplacesSpaceWithoutSelectingTheWholeLabel() {
        MappingWidget widget = new MappingWidget(mapping, "doubleVolume", "Dobrar Volume", " ");
        TextField field = (TextField) widget.getChildren().get(1);

        field.positionCaret(field.getLength());
        field.replaceSelection("_");
        widget.saveChange();

        assertThat(field.getText()).isEqualTo("_");
        assertThat(mapping.getChars("doubleVolume")).isEqualTo("_");
    }

    @Test
    void deletingSpaceClearsTheWholeLabelAndAllowsAnotherCharacter() {
        MappingWidget widget = new MappingWidget(mapping, "doubleVolume", "Dobrar Volume", " ");
        TextField field = (TextField) widget.getChildren().get(1);

        field.positionCaret(field.getLength());
        field.deletePreviousChar();
        assertThat(field.getText()).isEmpty();

        field.replaceSelection("_");
        widget.saveChange();
        assertThat(mapping.getChars("doubleVolume")).isEqualTo("_");

        field.selectAll();
        field.replaceSelection(" ");
        widget.saveChange();
        assertThat(field.getText()).isEqualTo("SPACE");
        assertThat(mapping.getChars("doubleVolume")).isEqualTo(" ");
    }

    @ParameterizedTest
    @CsvSource({"note,A", "pause,a", "accidental,#", "octaveUp,?", "octaveDown,V",
            "bpmUp,>", "bpmDown,<", "doubleVolume,_", "duration,1", "instrument,0"})
    void anyMappingCanUseSpaceAndReplaceItWithOneCharacter(String group, String character) {
        mapping.replaceChar("doubleVolume", " ", "_");
        MappingWidget widget = new MappingWidget(mapping, group, "Mapeamento", character);
        TextField field = (TextField) widget.getChildren().get(1);

        field.selectAll();
        field.replaceSelection(" ");
        widget.saveChange();
        assertThat(field.getText()).isEqualTo("SPACE");
        assertThat(mapping.getMappingCharacters(group)).contains(" ");

        field.setText("AB");
        assertThat(field.getText()).isEqualTo("SPACE");
        field.positionCaret(field.getLength());
        field.replaceSelection("~");
        widget.saveChange();
        assertThat(field.getText()).isEqualTo("~");
        assertThat(mapping.getMappingCharacters(group)).contains("~").doesNotContain(" ");

        field.selectAll();
        field.replaceSelection(" ");
        widget.saveChange();
        assertThat(field.getText()).isEqualTo("SPACE");
        assertThat(mapping.getMappingCharacters(group)).contains(" ");
    }
}
