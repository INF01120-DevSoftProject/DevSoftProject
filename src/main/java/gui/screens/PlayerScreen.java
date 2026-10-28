
package gui.screens;

import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.ComboBoxNode;
import gui.nodes.SheetMusicNode;

import player.MidiPlayer;

import com.sheetmusic4j.core.io.ScoreFile;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class PlayerScreen extends Screen {

    private final MidiPlayer midiPlayer = new MidiPlayer();

    private final Path musicFolder =
        generatePathToResources("/musics");

    private Slider timeSlider;
    private SheetMusicNode sheetMusicNode;

    private Button currentButton;
    private Button playButton;
    private Button pauseButton;

    private HBox playerButtons;
    private Label musicTitle;
    private ComboBox<String> musicSelectionBox;

    private Timeline sliderUpdater;
    private Timeline musicListUpdater;

    private boolean updatingMusicList = false;

    public PlayerScreen(Stage stage) {
        setScreenNode(createScreen(stage));

        stage.addEventHandler(
            WindowEvent.WINDOW_HIDDEN,
            e -> endProgram()
        );
    }

    public Pane createScreen(Stage stage) {

        List<String> musics = getAllMidiInResourceFolder();

        if (musics.isEmpty()) {
            throw new IllegalStateException(
                "Nenhuma música MIDI encontrada em: " + musicFolder
            );
        }

        Path midiPath = musicFolder.resolve(musics.getFirst());

        midiPlayer.load(midiPath);

        sheetMusicNode = new SheetMusicNode(
            midiPath,
            midiPlayer.getSequencer()
        );

        playButton = new ButtonNode("▶")
            .onClick(e -> {
                midiPlayer.play();
                setCurrentButton(pauseButton);
            })
            .build();

        pauseButton = new ButtonNode("⏸")
            .onClick(e -> {
                midiPlayer.pause();
                setCurrentButton(playButton);
            })
            .build();

        currentButton = playButton;

        musicSelectionBox = new ComboBoxNode<String>()
            .addAll(musics)
            .build();

        musicSelectionBox.getSelectionModel().selectFirst();

        musicSelectionBox.setOnAction(e -> {
            if (!updatingMusicList) {
                changeMusic(musicSelectionBox.getValue());
            }
        });

        musicTitle = new Label(musicSelectionBox.getValue());
        timeSlider = createTimeSlider();

        playerButtons = new BoxNode<>(new HBox(25))
            .addClass("player-buttons")
            .add(
                new ButtonNode("⏮")
                    .onClick(e -> changeToPreviousMusic())
                    .build()
            )
            .add(currentButton)
            .add(
                new ButtonNode("⏭")
                    .onClick(e -> changeToNextMusic())
                    .build()
            )
            .build();

        VBox playerController = new BoxNode<>(new VBox(25))
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
            .add(playerController)
            .build();

        startSliderUpdater();
        startMusicListUpdater();

        return mainContent;
    }


    private void changeMusic(String musicName) {
        if (musicName == null) {
            return;
        }

        Path path = musicFolder.resolve(musicName);

        if (!Files.isRegularFile(path)) {
            return;
        }

        try {
            midiPlayer.stop();
            midiPlayer.load(path);

            sheetMusicNode.getSheetView().setScore(
                ScoreFile.load(path)
            );

            musicTitle.setText(musicName);

            timeSlider.setMax(midiPlayer.getDuration());
            timeSlider.setValue(0);

            setCurrentButton(playButton);

        } catch (Exception e) {
            System.err.println(
                "Erro ao carregar música: " + musicName
            );
            e.printStackTrace();
        }
    }

    private void changeToNextMusic() {
        musicSelectionBox.getSelectionModel().selectNext();
    }

    private void changeToPreviousMusic() {
        musicSelectionBox.getSelectionModel().selectPrevious();
    }

    private void setCurrentButton(Button button) {
        if (playerButtons == null || button == null) {
            return;
        }

        currentButton = button;
        playerButtons.getChildren().set(1, button);
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
            new KeyFrame(
                Duration.millis(100),
                e -> updateTimeSlider()
            )
        );

        sliderUpdater.setCycleCount(Animation.INDEFINITE);
        sliderUpdater.play();
    }

    private void updateTimeSlider() {
        if (timeSlider == null) {
            return;
        }

        if (timeSlider.isValueChanging() || timeSlider.isPressed()) {
            return;
        }

        timeSlider.setValue(midiPlayer.getCurrentTime());
    }


    private List<String> getAllMidiInResourceFolder() {
        try (Stream<Path> files = Files.list(musicFolder)) {

            return files
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .filter(this::isMidiFile)
                .sorted()
                .toList();

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao listar músicas", e
            );
        }
    }

    private boolean isMidiFile(String name) {
        String lowerName = name.toLowerCase(Locale.ROOT);

        return lowerName.endsWith(".mid")
            || lowerName.endsWith(".midi");
    }

    private Path generatePathToResources(String resourcePath) {
        URL resource = Objects.requireNonNull(
            getClass().getResource(resourcePath),
            "Recurso não encontrado: " + resourcePath
        );

        try {
            return Path.of(resource.toURI());

        } catch (Exception e) {
            throw new IllegalStateException(
                "Não foi possível acessar a pasta: "
                    + resourcePath,
                e
            );
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

    private void refreshMusicList() {
        List<String> musics = getAllMidiInResourceFolder();

        if (musicSelectionBox.getItems().equals(musics)) {
            return;
        }

        String selectedMusic = musicSelectionBox.getValue();

        updatingMusicList = true;

        try {
            musicSelectionBox.getItems().setAll(musics);

            if (selectedMusic != null
                    && musics.contains(selectedMusic)) {

                musicSelectionBox.setValue(selectedMusic);
                return;
            }

            if (musics.isEmpty()) {
                musicSelectionBox.getSelectionModel()
                    .clearSelection();

                midiPlayer.stop();

                musicTitle.setText("Nenhuma música disponível");

                timeSlider.setValue(0);
                timeSlider.setMax(0);

                setCurrentButton(playButton);
                return;
            }

            musicSelectionBox.getSelectionModel().selectFirst();

        } finally {
            updatingMusicList = false;
        }

        changeMusic(musicSelectionBox.getValue());
    }

    private void endProgram() {
        if (sliderUpdater != null) {
            sliderUpdater.stop();
        }

        if (musicListUpdater != null) {
            musicListUpdater.stop();
        }

        if (sheetMusicNode != null
                && sheetMusicNode.getAnimation() != null) {

            sheetMusicNode.getAnimation().stop();
        }

        midiPlayer.close();
    }
}
