package gui.screens;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class Screen {

    private Pane screenNode;

    public Pane getScreenNode() {
        return screenNode;
    }

    public void setScreenNode(Pane screenNode) {
        this.screenNode = screenNode;
    }

    public abstract Pane createScreen(Stage stage);
    
}
