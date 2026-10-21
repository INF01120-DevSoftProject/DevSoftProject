package gui;


import javafx.scene.layout.Pane;
import java.util.function.Consumer;
import javafx.scene.Node;

public class BoxNode<T extends Pane> {

    private final T box;

    public BoxNode(T box) {
        this.box = box;
    }

    public BoxNode<T> add(Node component) {
        box.getChildren().add(component);
        return this;
    }

    public BoxNode<T> apply(Consumer<T> action) {
        action.accept(box);
        return this;
    }

    public BoxNode<T> addClass(String className) {
        if (className == null) throw new NullPointerException("className can't be null");
        box.getStyleClass().add(className);

        return this;
    }

    public BoxNode<T>  setId(String idName) {
        if (idName == null) throw new NullPointerException("idName can't be null");
        box.setId("main");

        return this;
    }

    public T build() {
        return box;
    }
}
