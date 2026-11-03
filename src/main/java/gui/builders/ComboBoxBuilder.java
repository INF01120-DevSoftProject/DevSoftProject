package gui.builders;

import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.ComboBox;

public class ComboBoxBuilder<T> {
    private final ComboBox<T> box;

    public ComboBoxBuilder() {
        box = new ComboBox<T>();
    }

    public ComboBoxBuilder<T> addClass(String className) {

        box.getStyleClass().add(className);
        return this;
    }

    public ComboBoxBuilder<T> addAll(List<T> list) {
        box.getItems().addAll(list);
        return this;
    }

    public ComboBoxBuilder<T> setOnAction(Consumer<ComboBox<T>> action) {
        box.setOnAction(event -> action.accept(box));
        return this;
    }

    public ComboBoxBuilder<T> apply(Consumer<ComboBox<T>> action) {
        action.accept(box);
        return this;
    }

    public ComboBox<T> build() {
        return box;
    }

}
