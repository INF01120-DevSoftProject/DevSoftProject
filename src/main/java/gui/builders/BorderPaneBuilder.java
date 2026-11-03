package gui.builders;

import javafx.scene.layout.BorderPane;

import java.util.function.Consumer;

public class BorderPaneBuilder {

    private final BorderPane border;

    public BorderPaneBuilder() {
        this.border = new BorderPane();
    }

    public BorderPaneBuilder addClass(String className) {
        if (className == null) throw new NullPointerException("className can't be null");
        border.getStyleClass().add(className);

        return this;
    }

    public BorderPaneBuilder apply(Consumer<BorderPane> action) {
        action.accept(border);
        return this;
    }


    public BorderPaneBuilder  setId(String idName) {
        if (idName == null) throw new NullPointerException("idName can't be null");
        border.setId("main");

        return this;
    }

    public BorderPane build() {
        return this.border;
    }
}
