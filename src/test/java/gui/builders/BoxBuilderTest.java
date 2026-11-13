package gui.builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class BoxBuilderTest extends JavaFXTestBase {

    @Test
    void wrapsAndBuildsPane() {
        VBox vbox = new VBox();
        VBox built = new BoxBuilder<>(vbox).build();

        assertThat(built).isSameAs(vbox);
    }

    @Test
    void addsChildrenToPane() {
        Label label1 = new Label("1");
        Label label2 = new Label("2");

        HBox hbox = new BoxBuilder<>(new HBox())
                .add(label1)
                .add(label2)
                .build();

        assertThat(hbox.getChildren()).containsExactly(label1, label2);
    }

    @Test
    void addsStyleClass() {
        VBox vbox = new BoxBuilder<>(new VBox())
                .addClass("custom-box")
                .build();

        assertThat(vbox.getStyleClass()).contains("custom-box");
    }

    @Test
    void throwsExceptionWhenClassNameIsNull() {
        BoxBuilder<VBox> builder = new BoxBuilder<>(new VBox());

        assertThrows(NullPointerException.class, () -> builder.addClass(null));
    }

    @Test
    void appliesConsumerAction() {
        HBox hbox = new BoxBuilder<>(new HBox())
                .apply(box -> box.setAlignment(Pos.CENTER))
                .build();

        assertThat(hbox.getAlignment()).isEqualTo(Pos.CENTER);
    }

    @Test
    void setsId() {
        VBox vbox = new BoxBuilder<>(new VBox())
                .setId("custom-id")
                .build();

        assertThat(vbox.getId()).isEqualTo("main");
    }

    @Test
    void throwsExceptionWhenIdIsNull() {
        BoxBuilder<VBox> builder = new BoxBuilder<>(new VBox());

        assertThrows(NullPointerException.class, () -> builder.setId(null));
    }
}
