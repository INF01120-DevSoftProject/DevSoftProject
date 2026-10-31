
package gui.screens;

import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.widgets.MusicPlayButtonWidget;
import gui.widgets.MusicSelectionWidget;
import gui.widgets.MusicSliderWidget;
import gui.widgets.SheetMusicWidget;
import input.MIDIInputReader;
import player.MidiPlayer;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.nio.file.Path;


public class PlayerScreen extends Screen {

    private final MidiPlayer midiPlayer;
    private final MIDIInputReader midiInputReader;

    private final Label musicTitle;

    private final SheetMusicWidget sheetMusicWidget;
    private final MusicSliderWidget musicSlider;
    private final MusicSelectionWidget musicSelectionBox;
    private final MusicPlayButtonWidget playPauseButton;


    public PlayerScreen(Stage stage) {
        midiPlayer = new MidiPlayer();
        midiInputReader = new MIDIInputReader();
        
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> endProgram());

        midiPlayer.load(midiInputReader.getAllMidiInResourceFolder().getFirst());

        sheetMusicWidget = new SheetMusicWidget(midiPlayer);
        playPauseButton = new MusicPlayButtonWidget(midiPlayer);
        musicSelectionBox = new MusicSelectionWidget(midiInputReader);
        musicSelectionBox.setOnAction(e -> selectMusic());

        musicTitle = new Label(musicSelectionBox.getValue());
        musicSlider = new MusicSliderWidget(midiPlayer);

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
                        .add(sheetMusicWidget)
                        .add(playerController)
                        .build();

        setScreenMainContent(mainContent);
    }

    private void selectMusic() {
        String musicName = musicSelectionBox.getValue();

        if (musicName == null || musicName.equals(midiPlayer.getCurrentMusicName())) return;

        changeMusic(midiInputReader.getMidiFilePathFromName(musicName));
    }

    private void changeMusic(Path path) {
        if (path == null) return;

        midiPlayer.load(path);

        sheetMusicWidget.loadMusic(midiPlayer);

        musicTitle.setText(midiPlayer.getCurrentMusicName());

        musicSlider.setMax(midiPlayer.getDuration());
        musicSlider.setValue(0);

        playPauseButton.setCurrentButtonToPlay();
    }

    private void changeToNextMusic() {
        changeMusic(midiInputReader.getNextMidiFilePath(midiPlayer.getCurrentMusicPath()));
        musicSelectionBox.getSelectionModel().selectNext();
    }

    private void changeToPreviousMusic() {
        changeMusic(midiInputReader.getPreviousMidiFilePath(midiPlayer.getCurrentMusicPath()));
        musicSelectionBox.getSelectionModel().selectPrevious();
    }

    private void endProgram() {
        musicSlider.stopUpdater();
        musicSelectionBox.stopUpdater();
        sheetMusicWidget.stopAnimation();
        midiPlayer.close();
    }
}
