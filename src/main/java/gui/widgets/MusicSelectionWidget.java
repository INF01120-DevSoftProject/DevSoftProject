package gui.widgets;

import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;

import java.nio.file.Path;

import javafx.util.Duration;
import utils.MusicPathUtils;

import javafx.event.EventHandler;


public class MusicSelectionWidget extends ComboBox<String> {

    private static final int MAX_VISIBLE_ROWS = 5;

    private final MusicPathUtils midiInputReader;
    private Timeline musicListUpdater;

    public MusicSelectionWidget(MusicPathUtils midiPathUtils) {
        this.midiInputReader = midiPathUtils;

        List<String> musics = midiPathUtils.getMusicNamesFromPaths(
            midiPathUtils.getAllMusicsPathInResources()
        );

        setVisibleRowCount(MAX_VISIBLE_ROWS);
        getItems().addAll(musics);
        getSelectionModel().selectFirst();

        startMusicListUpdater();
    }

    public void stopUpdater() {
        musicListUpdater.stop();
    }

    public boolean isNameInSelectionBox(String name) {
        return getItems().contains(name);
    }

    public void clearCurrentBoxSelection() {
        runWithoutAction(() -> setValue(null));
    }

    public void selectMusicSilently(String musicName) {
        runWithoutAction(() -> setValue(musicName));
    }

    private void runWithoutAction(Runnable action) {
        EventHandler<ActionEvent> currentAction = getOnAction();
        setOnAction(null);

        try {
            action.run();
        } finally {
            setOnAction(currentAction);
        }
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

        String selectedMusic = getValue();

        runWithoutAction(() -> {
            getItems().setAll(musics);
            setValue(musics.contains(selectedMusic) ? selectedMusic : null);
        });
    }
}

