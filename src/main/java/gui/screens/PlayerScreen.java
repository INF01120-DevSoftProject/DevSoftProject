
package gui.screens;

import java.nio.file.Path;

import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.factory.ControlFactory;
import gui.widgets.MusicPlayButtonWidget;
import gui.widgets.MusicSelectionWidget;
import gui.widgets.MusicSliderWidget;
import gui.widgets.SheetMusicWidget;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import player.Player;
import utils.MusicPathUtils;

public class PlayerScreen extends Screen {

    private final Player<?> player;
    private final MusicPathUtils musicPathUtils;

    private final Label musicTitle;

    private final SheetMusicWidget sheetMusicWidget;
    private final MusicSliderWidget musicSlider;
    private final MusicSelectionWidget musicSelectionBox;
    private final MusicPlayButtonWidget playPauseButton;

    public PlayerScreen(Stage stage, Player<?> player, MusicPathUtils musicPathUtils) {
        this.player = player;
        this.musicPathUtils = musicPathUtils;
        
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> endProgram());

        player.load(musicPathUtils.getAllMusicsPathInResources().getFirst());

        sheetMusicWidget = new SheetMusicWidget(player.getCurrentMusicPath(), player::getMusicPosition);
        ScrollPane sheetMusicWidgetScroll = ControlFactory.createScrollPane(sheetMusicWidget);

        playPauseButton = new MusicPlayButtonWidget(player);
        musicSelectionBox = new MusicSelectionWidget(musicPathUtils);
        musicSelectionBox.setOnAction(e -> selectMusic());

        musicTitle = new Label(musicSelectionBox.getValue());
        musicSlider = new MusicSliderWidget(player);

        HBox playerButtons = new BoxBuilder<>(new HBox(25))
                        .addClass("player-buttons")
                        .add(new ButtonBuilder("⏮")
                                .onClick(e -> changeToPreviousMusic())
                                .build())
                        .add(playPauseButton)
                        .add(new ButtonBuilder("⏭")
                                .onClick(e -> changeToNextMusic())
                                .build())
                        .build();

        VBox playerController = new BoxBuilder<>(new VBox(25))
                        .addClass("player-controller")
                        .add(musicTitle)
                        .add(musicSlider)
                        .add(playerButtons)
                        .build();

        VBox mainContent = new BoxBuilder<>(new VBox(25))
                        .setId("main")
                        .add(new Label("Player"))
                        .add(musicSelectionBox)
                        .add(sheetMusicWidgetScroll)
                        .add(playerController)
                        .build();

        setScreenMainContent(mainContent);
    }

    private void selectMusic() {
        String musicName = musicSelectionBox.getValue();
        if (musicName == null || musicName.equals(player.getCurrentMusicName())) return;

        try {
            changeMusic(musicPathUtils.getMusicPathFromName(musicName));
        } catch(IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void changeMusic(Path path) {
        if (path == null) return;

        try {
            player.load(path);
            updatePlayerUI();
        } catch (RuntimeException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void updatePlayerUI() {
        sheetMusicWidget.loadMusic(player.getCurrentMusicPath());

        musicTitle.setText(player.getCurrentMusicName());

        musicSlider.setMax(player.getDuration());
        musicSlider.setValue(0);

        playPauseButton.setCurrentButtonToPlay();
    }

    private void changeToNextMusic() {
        changeMusic(musicPathUtils.getNextMusicPath(player.getCurrentMusicPath()));
        musicSelectionBox.getSelectionModel().selectNext();
    }

    private void changeToPreviousMusic() {
        changeMusic(musicPathUtils.getPreviousMusicPath(player.getCurrentMusicPath()));
        musicSelectionBox.getSelectionModel().selectPrevious();
    }

    private void endProgram() {
        musicSlider.stopUpdater();
        musicSelectionBox.stopUpdater();
        sheetMusicWidget.stopAnimation();
        player.close();
    }
}
