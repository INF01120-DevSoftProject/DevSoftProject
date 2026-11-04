package gui.builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

class ButtonBuilderTest extends JavaFXTestBase {

    @Test
    void buildsButtonWithText() {
        Button button = new ButtonBuilder("Click me").build();

        assertThat(button).isNotNull();
        assertThat(button.getText()).isEqualTo("Click me");
    }

    @Test
    void throwsExceptionWhenButtonTextIsNull() {
        assertThrows(NullPointerException.class, () -> new ButtonBuilder(null));
    }

    @Test
    void addsStyleClass() {
        Button button = new ButtonBuilder("OK")
                .addClass("primary-btn")
                .build();

        assertThat(button.getStyleClass()).contains("primary-btn");
    }

    @Test
    void triggersOnClickAction() {
        AtomicBoolean clicked = new AtomicBoolean(false);
        Button button = new ButtonBuilder("Fire")
                .onClick(event -> clicked.set(true))
                .build();

        button.fire();

        assertThat(clicked.get()).isTrue();
    }

    @Test
    void addsComponentToContainerOnButtonClick() {
        VBox container = new VBox();
        Button button = new ButtonBuilder("Add")
                .onClickAddToContainer(() -> new Label("Item"), container)
                .build();

        button.fire();
        button.fire();

        assertThat(container.getChildren()).hasSize(2);
    }

    @Test
    void addsComponentToContainerUpToMaxLimit() {
        VBox container = new VBox();
        int max = 2;
        Button button = new ButtonBuilder("Add Max")
                .onClickAddToContainer(() -> new Label("Item"), container, max)
                .build();

        button.fire();
        button.fire();
        button.fire(); // should not exceed max

        assertThat(container.getChildren()).hasSize(2);
    }

    @Test
    void removesLastElementFromContainerOnlyIfSizeGreaterThanOne() {
        VBox container = new VBox();
        Label first = new Label("1");
        Label second = new Label("2");
        container.getChildren().addAll(first, second);

        Button button = new ButtonBuilder("Remove")
                .onClickRemoveLastFromContainer(container)
                .build();

        button.fire();
        assertThat(container.getChildren()).containsExactly(first);

        button.fire(); // only 1 left, should NOT remove it
        assertThat(container.getChildren()).containsExactly(first);
    }
}
