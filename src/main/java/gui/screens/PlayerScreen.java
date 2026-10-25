package gui.screens;

import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.ComboBoxNode;
import gui.nodes.SheetMusicNode;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.net.URL;

import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URI;
import com.sheetmusic4j.core.io.ScoreFile;

import player.MidiPlayer;
import java.util.List;

import java.util.stream.Stream;

public class PlayerScreen extends Screen {


    private static MidiPlayer midiPlayer;
    private static Slider timeSlider;

    private SheetMusicNode sheetMusicNode;
    private Button currentButton;
    private Button playButton;
    private Button pauseButton;
    private HBox playerButtons;
    private Label musicTitle;
    ComboBox<String> musicSelectionBox;


    private Timeline sliderUpdater;


    public PlayerScreen(Stage stage) {
        setScreenNode(createScreen(stage));

        stage.addEventHandler(
            WindowEvent.WINDOW_HIDDEN,
            e -> endProgram()
        );
    }

    public Pane createScreen(Stage stage) {

        midiPlayer = new MidiPlayer();
        startSliderUpdater();

        List<String> musics = getAllMidiInResourceFolder("/musics");

        if (musics.isEmpty()) {
            throw new IllegalStateException("Nenhuma música MIDI encontrada");
        }

        Path midiPath = generatePathToResources("/musics/" + musics.getFirst());

        midiPlayer.load(midiPath);
        sheetMusicNode = new SheetMusicNode(midiPath, midiPlayer.getSequencer());

        playButton = new ButtonNode("▶")
                                    .onClick(e -> midiPlayer.play())
                                    .onClick(e -> setCurrentButton(pauseButton))
                                    .build();

        pauseButton = new ButtonNode("⏸")
                                    .onClick(e -> midiPlayer.pause())
                                    .onClick(e -> setCurrentButton(playButton))
                                    .build();
        currentButton = playButton;

        musicSelectionBox = new ComboBoxNode<String>()
                                    .addAll(getAllMidiInResourceFolder("/musics"))
                                    .setOnAction(e -> changeMusic(e.getValue()))
                                    .apply(box -> box.getSelectionModel().selectFirst())
                                    .build();
            
        musicTitle = new Label(musicSelectionBox.getValue());
        timeSlider = createTimeSlider();


        playerButtons = new BoxNode<>(new HBox(25))
                                    .addClass("player-buttons")
                                    .add(new ButtonNode("⏮")
                                        .onClick(e -> changeToPreviousMusic())
                                        .build())
                                    .add(currentButton)
                                    .add(new ButtonNode("⏭")
                                        .onClick(e -> changeToNextMusic())
                                        .build())
                                    .build();


        VBox playerControler = new BoxNode<>(new VBox(25))
                                    .addClass("player-controller")
                                    .add(musicTitle)
                                    .add(timeSlider)
                                    .add(playerButtons)
                                    .build();

        VBox mainContent = new BoxNode<>(new VBox(25))
                                    .setId("main")
                                    .add(new Label("Player"))
                                    .add(musicSelectionBox)
                                    .add(sheetMusicNode.getSheetView())
                                    .add(playerControler)
                                    .build();

        return mainContent;
    }

    private void endProgram() {
        if (sheetMusicNode.getAnimation() != null) {
            sheetMusicNode.getAnimation().stop();
        }

        if (midiPlayer != null) {
            midiPlayer.close();
        }
    }

    private Slider createTimeSlider() {

        long duration = midiPlayer.getDuration();

        Slider slider = new Slider(0, duration, 0);

        slider.setOnMouseReleased(e ->
            midiPlayer.seek(Math.round(slider.getValue()))
        );

        slider.setOnKeyReleased(e ->
            midiPlayer.seek(Math.round(slider.getValue()))
        );

        return slider;
    }


    private void startSliderUpdater() {
        sliderUpdater = new Timeline(
            new KeyFrame(Duration.millis(1), e -> updateTimeSlider())
        );

        sliderUpdater.setCycleCount(Animation.INDEFINITE);
        sliderUpdater.play();
    }

    private void updateTimeSlider() {
        if (timeSlider.isValueChanging() || timeSlider.isPressed()) {
            return;
        }

        timeSlider.setValue(midiPlayer.getCurrentTime());
    }

    private void changeMusic(String musicName) {
            if (musicName == null) return;

            Path path = generatePathToResources("/musics/" + musicName);

            musicTitle.setText(musicName);
            midiPlayer.stop();
            midiPlayer.load(path);

            sheetMusicNode.getSheetView().setScore(ScoreFile.load(path));

            timeSlider.setMax(midiPlayer.getDuration());
            timeSlider.setValue(0);
    }

    private void changeToNextMusic() {
        musicSelectionBox.getSelectionModel().selectNext();
    }

    private void changeToPreviousMusic() {
        musicSelectionBox.getSelectionModel().selectPrevious();
    }

    private List<String> getAllMidiInResourceFolder(String string) {

        Path folder = generatePathToResources(string);

        try (Stream<Path> files = Files.list(folder)) {

            List<String> musics = files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".mid") || name.endsWith(".midi"))
                    .sorted()
                    .toList();

            return musics;
                    
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao listar músicas", e);
        }
    }

    private Path generatePathToResources(String string) {
        URL resource = Objects.requireNonNull(
            getClass().getResource(string)
        );

        return Path.of(URI.create(resource.toExternalForm()));
    }

    private void setCurrentButton(Button button) {
        currentButton = button;
        playerButtons.getChildren().set(1, button);
    }

 
}