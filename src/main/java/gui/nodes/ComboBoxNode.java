package gui.nodes;

import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.ComboBox;

public class ComboBoxNode<T> {
    private final ComboBox<T> box;

    public ComboBoxNode() {
        box = new ComboBox<T>();
    }

    public ComboBoxNode<T> addClass(String className) {

        box.getStyleClass().add(className);
        return this;
    }

    public ComboBoxNode<T> addAll(List<T> list) {
        box.getItems().addAll(list);
        return this;
    }

    public ComboBoxNode<T> setOnAction(Consumer<ComboBox<T>> action) {
        box.setOnAction(event -> action.accept(box));
        return this;
    }

    public ComboBoxNode<T> apply(Consumer<ComboBox<T>> action) {
        action.accept(box);
        return this;
    }

    public ComboBox<T> build() {
        return box;
    }

}
