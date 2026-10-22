package gui.nodes;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ButtonNode {

    private final Button button;

    public ButtonNode(String buttonText) {

        if (buttonText == null) throw new NullPointerException("button text can't be null");

        button = new Button(buttonText);
    }

    public ButtonNode addClass(String className) {

        button.getStyleClass().add(className);
        return this;
    }

    public ButtonNode onClick(Consumer<ActionEvent> action) {
        button.addEventHandler(ActionEvent.ACTION, event -> action.accept(event));
        return this;
    }

    public ButtonNode onClickAddToContainer(Supplier<? extends Node> component, Pane container) {
        button.addEventHandler(ActionEvent.ACTION, event -> container.getChildren().add(component.get()));
        return this;
    }

    public ButtonNode onClickRemoveLastFromContainer(Pane container) {
        button.addEventHandler(ActionEvent.ACTION, event -> {
            var children = container.getChildren();

            if (!children.isEmpty()) {
                
                children.remove(children.size() - 1);
            }
        });
        return this;
    }

    public Button build() {
        return button;
    }


}
