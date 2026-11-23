package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gui.JavaFXTestBase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import translators.TextToMusic.CharacterMapping;

class MappingHelpWidgetTest extends JavaFXTestBase {

    private Map<String, String> explanations(MappingHelpWidget widget) {
        VBox content = (VBox) ((ScrollPane) widget.getContent()).getContent();
        Map<String, String> rows = new LinkedHashMap<>();
        content.getChildren().stream().map(VBox.class::cast)
            .flatMap(section -> section.getChildren().stream())
            .filter(HBox.class::isInstance).map(HBox.class::cast)
            .forEach(row -> rows.put(((Label) row.getChildren().get(0)).getText(),
                ((Label) row.getChildren().get(1)).getText()));
        return rows;
    }

    @Test
    void refreshKeepsDescriptionsWithTheirMusicalValuesAfterSwappingCharacters() {
        CharacterMapping mapping = new CharacterMapping();
        MappingHelpWidget widget = new MappingHelpWidget(mapping);

        mapping.replaceChar("note", "C", "D");
        mapping.replaceChar("note", "D", "C");
        mapping.replaceChar("duration", "1", "7");
        mapping.replaceChar("duration", "7", "1");
        mapping.replaceChar("bpmUp", ">", "+");
        widget.refreshMapping();

        Map<String, String> rows = explanations(widget);
        assertThat(rows.get("D")).contains("Dó");
        assertThat(rows.get("C")).contains("Ré");
        assertThat(rows.get(":7")).contains("Semibreve");
        assertThat(rows.get(":1")).contains("Semifusa");
        assertThat(rows.get("+")).contains("Aumenta o andamento");
        assertThat(rows).doesNotContainKey(">");
    }

    @Test
    void refreshReadsChangesAndRestoredDefaultsWhileTheGuideStaysOpen() {
        CharacterMapping mapping = new CharacterMapping();
        MappingHelpWidget widget = new MappingHelpWidget(mapping);
        assertThat(widget.isExpanded()).isTrue();
        assertThat(widget.isCollapsible()).isFalse();

        mapping.replaceChar("note", "C", "K");
        mapping.replaceChar("doubleVolume", " ", "_");
        mapping.replaceChar("pause", "a", " ");
        widget.refreshMapping();

        Map<String, String> rows = explanations(widget);
        assertThat(rows.get("K")).contains("Dó");
        assertThat(rows.get("_")).contains("Dobra o volume");
        assertThat(rows.entrySet()).anySatisfy(row -> {
            assertThat(row.getKey()).contains("SPACE");
            assertThat(row.getValue()).contains("pausa de um tempo");
        });

        mapping.reset();
        widget.refreshMapping();

        assertThat(explanations(widget).get("C")).contains("Dó");
        assertThat(explanations(widget).get("SPACE")).contains("Dobra o volume");
        assertThat(explanations(widget)).doesNotContainKeys("K", "_");
    }
}
