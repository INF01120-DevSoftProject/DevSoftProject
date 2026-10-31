package gui.builders;


import javafx.scene.layout.Pane;
import java.util.function.Consumer;
import javafx.scene.Node;

public class BoxBuilder<T extends Pane> {

    private final T box;

    public BoxBuilder(T box) {
        this.box = box;
    }

    public BoxBuilder<T> add(Node component) {
        box.getChildren().add(component);
        return this;
    }

    public BoxBuilder<T> apply(Consumer<T> action) {
        action.accept(box);
        return this;
    }

    public BoxBuilder<T> addClass(String className) {
        if (className == null) throw new NullPointerException("className can't be null");
        box.getStyleClass().add(className);

        return this;
    }

    public BoxBuilder<T> setId(String idName) {
        if (idName == null) throw new NullPointerException("idName can't be null");
        box.setId("main");

        return this;
    }

    public T build() {
        return box;
    }
}
