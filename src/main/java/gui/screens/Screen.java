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
        return createFileChooser(initialDirectory).showSaveDialog(getCurrentWindow());
    }

    protected File openMidiSaveDialog(Path initialDirectory) {
        FileChooser fileChooser = createFileChooser(initialDirectory);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivos MIDI (*.mid, *.midi)", "*.mid", "*.midi")
        );
        return fileChooser.showSaveDialog(getCurrentWindow());
    }

    private FileChooser createFileChooser(Path initialDirectory) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Salvar MIDI");
        fileChooser.setInitialFileName("musica.mid");

        if (initialDirectory != null && Files.isDirectory(initialDirectory)) {
            fileChooser.setInitialDirectory(initialDirectory.toFile());
        }

        return fileChooser;
    }
}
