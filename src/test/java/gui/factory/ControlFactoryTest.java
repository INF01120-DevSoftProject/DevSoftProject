package gui.factory;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

class ControlFactoryTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Toolkit already initialized
        }
    }

    @Test
    void createsSliderWithMinMaxAndValue() {
        Slider slider = ControlFactory.createSlider(0, 100, 50);

        assertThat(slider).isNotNull();
        assertThat(slider.getMin()).isEqualTo(0.0);
        assertThat(slider.getMax()).isEqualTo(100.0);
        assertThat(slider.getValue()).isEqualTo(50.0);
        assertThat(slider.isShowTickLabels()).isTrue();
    }

    @Test
    void createsScrollPaneWithConfiguredPolicies() {
        VBox content = new VBox();
        ScrollPane scrollPane = ControlFactory.createScrollPane(content);

        assertThat(scrollPane).isNotNull();
        assertThat(scrollPane.getContent()).isSameAs(content);
        assertThat(scrollPane.isFitToWidth()).isTrue();
        assertThat(scrollPane.getMinHeight()).isEqualTo(0.0);
        assertThat(scrollPane.getVbarPolicy()).isEqualTo(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        assertThat(scrollPane.getHbarPolicy()).isEqualTo(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @Test
    void createsTextAreaWithDefaultConfiguration() {
        TextArea textArea = ControlFactory.createTextArea(5, "prompt");

        assertThat(textArea).isNotNull();
        assertThat(textArea.getPrefRowCount()).isEqualTo(5);
        assertThat(textArea.getPromptText()).isEqualTo("prompt");
        assertThat(textArea.isWrapText()).isTrue();
    }

    @Test
    void createsComboBoxWithItemsAndPromptText() {
        List<String> items = List.of("Option A", "Option B");
        ComboBox<String> comboBox = ControlFactory.createComboBox("Select one", items);

        assertThat(comboBox).isNotNull();
        assertThat(comboBox.getPromptText()).isEqualTo("Select one");
        assertThat(comboBox.getItems()).containsExactly("Option A", "Option B");
    }

    @Test
    void createsVerticalGrower() {
        Region grower = ControlFactory.createVerticalGrower();

        assertThat(grower).isNotNull();
        assertThat(VBox.getVgrow(grower)).isEqualTo(Priority.ALWAYS);
    }

    @Test
    void createsHorizontalGrower() {
        Region grower = ControlFactory.createHorizontalGrower();

        assertThat(grower).isNotNull();
        assertThat(HBox.getHgrow(grower)).isEqualTo(Priority.ALWAYS);
    }
}
