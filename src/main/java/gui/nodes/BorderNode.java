package gui.nodes;

import javafx.scene.layout.BorderPane;

import java.util.function.Consumer;

public class BorderNode {

    private final BorderPane border;

    public BorderNode() {
        this.border = new BorderPane();
    }

    public BorderNode addClass(String className) {
        if (className == null) throw new NullPointerException("className can't be null");
        border.getStyleClass().add(className);

        return this;
    }

    public BorderNode apply(Consumer<BorderPane> action) {
        action.accept(border);
        return this;
    }


    public BorderNode  setId(String idName) {
        if (idName == null) throw new NullPointerException("idName can't be null");
        border.setId("main");

        return this;
    }

    public BorderPane build() {
        return this.border;
    }
}
