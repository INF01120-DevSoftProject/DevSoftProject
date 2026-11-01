package gui.screens;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public abstract class Screen extends Pane {

    protected Window getCurrentWindow() {
        return getScene().getWindow();
    }
    
    protected void setScreenMainContent(Pane content) {
        getChildren().add(content);

        content.prefWidthProperty().bind(widthProperty());
        content.prefHeightProperty().bind(heightProperty());
    }

    protected void showErrorMessage(String message) {
        new Alert(
            Alert.AlertType.ERROR,
            message
        ).showAndWait();
    }

    protected File openFileChooserWindow() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar MIDI");
        fileChooser.setInitialFileName("musica.mid");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivo MIDI (*.mid)", "*.mid")
        );

        File file = fileChooser.showSaveDialog(getCurrentWindow());

        return file;
    }
}
