package gui.screens;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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

    protected File openFileChooserWindow(Path initialDirectory) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Salvar MIDI");
        fileChooser.setInitialFileName("musica.mid");

        if (initialDirectory != null && Files.isDirectory(initialDirectory)) {
            fileChooser.setInitialDirectory(initialDirectory.toFile());
        }

        return fileChooser.showSaveDialog(getCurrentWindow());
    }
}
