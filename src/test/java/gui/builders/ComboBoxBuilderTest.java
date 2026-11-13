package gui.builders;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.control.ComboBox;

class ComboBoxBuilderTest extends JavaFXTestBase {

    @Test
    void buildsComboBox() {
        ComboBox<String> comboBox = new ComboBoxBuilder<String>().build();

        assertThat(comboBox).isNotNull();
    }

    @Test
    void addsStyleClass() {
        ComboBox<String> comboBox = new ComboBoxBuilder<String>()
                .addClass("custom-combo")
                .build();

        assertThat(comboBox.getStyleClass()).contains("custom-combo");
    }

    @Test
    void addsAllItems() {
        List<String> items = List.of("Option 1", "Option 2", "Option 3");
        ComboBox<String> comboBox = new ComboBoxBuilder<String>()
                .addAll(items)
                .build();

        assertThat(comboBox.getItems()).containsExactly("Option 1", "Option 2", "Option 3");
    }

    @Test
    void setsOnActionHandler() {
        AtomicReference<String> selected = new AtomicReference<>();
        ComboBox<String> comboBox = new ComboBoxBuilder<String>()
                .addAll(List.of("A", "B"))
                .setOnAction(box -> selected.set(box.getValue()))
                .build();

        comboBox.setValue("B");
        comboBox.getOnAction().handle(null);

        assertThat(selected.get()).isEqualTo("B");
    }

    @Test
    void appliesConfigurationConsumer() {
        ComboBox<String> comboBox = new ComboBoxBuilder<String>()
                .apply(box -> box.setPromptText("Choose"))
                .build();

        assertThat(comboBox.getPromptText()).isEqualTo("Choose");
    }
}
