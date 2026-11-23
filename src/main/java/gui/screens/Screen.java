package gui.screens;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import utils.MidiMusicPathUtils;
import utils.MusicPathUtils;

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

    protected boolean showWarningMessage(String message) {
        ButtonType yes = new ButtonType("Sim", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert warning = new Alert(Alert.AlertType.WARNING, message, yes, no);
        warning.setTitle("Aviso");
        warning.setHeaderText(null);

        return warning.showAndWait().orElse(no) == yes;
    }

    protected File openFileChooserWindow() {
        FileChooser fileChooser = createFileChooser();
        fileChooser.setTitle("Importar texto musical");
    
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivos de Texto", "*.txt")
        );


        return fileChooser.showOpenDialog(getCurrentWindow());
    }

    protected File openMusicSaveDialog() {
        List<MusicPathUtils> formats = List.of(
            new MidiMusicPathUtils()
        );

        List<String> extensions = formats.stream()
                .flatMap(format -> format.getSupportedExtensions().stream())
                .distinct()
                .toList();

        FileChooser fileChooser = createFileChooser();

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivos de música", extensions)
        );

        fileChooser.setTitle("Salvar música");
        fileChooser.setInitialFileName("musica.midi");

        return fileChooser.showSaveDialog(getCurrentWindow());
    }

    private FileChooser createFileChooser() {

        Path initialDirectory = MusicPathUtils.getMusicsFolderPath();
        FileChooser fileChooser = new FileChooser();

        if (initialDirectory != null && Files.isDirectory(initialDirectory)) {
            fileChooser.setInitialDirectory(initialDirectory.toFile());
        }

        return fileChooser;
    }


}
