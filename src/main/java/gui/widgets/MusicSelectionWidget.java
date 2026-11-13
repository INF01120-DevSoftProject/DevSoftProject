package gui.widgets;

import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ComboBox;
import java.nio.file.Path;

import javafx.util.Duration;
import utils.MusicPathUtils;

public class MusicSelectionWidget extends ComboBox<String> {

    private static final int MAX_VISIBLE_ROWS = 5;

    private MusicPathUtils midiInputReader;
    private Timeline musicListUpdater;

    public MusicSelectionWidget(MusicPathUtils midiPathUtils) {
        this.midiInputReader = midiPathUtils;
        List<String> musics = midiPathUtils.getMusicNamesFromPaths(midiPathUtils.getAllMusicsPathInResources());

        setVisibleRowCount(MAX_VISIBLE_ROWS);
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

    public void refreshMusicList() {
        List<Path> paths = midiInputReader.getAllMusicsPathInResources();
        List<String> musics = midiInputReader.getMusicNamesFromPaths(paths);

        if (getItems().equals(musics)) return;

        boolean wasEmpty = getItems().isEmpty();
        String selectedMusic = getValue();

        getItems().setAll(musics);

        if (selectedMusic != null && musics.contains(selectedMusic)) {
            setValue(selectedMusic);
            return;
        }

        if (selectedMusic == null && !wasEmpty) {
            getSelectionModel().clearSelection();
            return;
        }

        getSelectionModel().selectFirst();
    }
}
