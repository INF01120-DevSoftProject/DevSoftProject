package gui.widgets;

import java.util.List;

import input.MIDIPathUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ComboBox;
import java.nio.file.Path;

import javafx.util.Duration;

public class MusicSelectionWidget extends ComboBox<String> {

    private MIDIPathUtils midiInputReader;
    private Timeline musicListUpdater;

    public MusicSelectionWidget(MIDIPathUtils midiPathUtils) {
        this.midiInputReader = midiPathUtils;
        List<String> musics = midiPathUtils.getFileNamesFromPaths(midiPathUtils.getAllMidiPathInResources());

        getItems().addAll(musics);
        getSelectionModel().selectFirst();
        startMusicListUpdater();
    }

    public void stopUpdater() {
        musicListUpdater.stop();
    }

    private void startMusicListUpdater() {
        musicListUpdater = new Timeline(
            new KeyFrame(
                Duration.seconds(2),
                e -> refreshMusicList()
            )
        );

        musicListUpdater.setCycleCount(Animation.INDEFINITE);
        musicListUpdater.play();
    }

    private void refreshMusicList() {
        List<Path> paths = midiInputReader.getAllMidiPathInResources();
        List<String> musics = midiInputReader.getFileNamesFromPaths(paths);

        if (getItems().equals(musics)) return;

        String selectedMusic = getValue();


       getItems().setAll(musics);

        if (selectedMusic != null && musics.contains(selectedMusic)) {
            setValue(selectedMusic);
            return;
        }

        getSelectionModel().selectFirst();
    }
}
