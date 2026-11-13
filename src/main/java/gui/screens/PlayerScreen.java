package gui.screens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

import exporters.DraftMusicManager;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.factory.ControlFactory;
import gui.widgets.MusicPlayButtonWidget;
import gui.widgets.MusicSelectionWidget;
import gui.widgets.MusicSliderWidget;
import gui.widgets.SheetMusicWidget;

import javafx.scene.Node;
import javafx.scene.control.Button;
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
    private final DraftMusicManager draftManager;

    private final Label musicTitle;
    private final Label draftStatusLabel;
    private final Button saveDraftButton;
    private final HBox draftActions;

    private final SheetMusicWidget sheetMusicWidget;
    private final MusicSliderWidget musicSlider;
    private final MusicSelectionWidget musicSelectionBox;
    private final MusicPlayButtonWidget playPauseButton;

    private boolean updatingSelection = false;

    public PlayerScreen(Stage stage, Player<?> player, MusicPathUtils musicPathUtils) {
        this(stage, player, musicPathUtils, new DraftMusicManager() {
            @Override
            public boolean isDraft(Path path) {
                return false;
            }

            @Override
            public void saveDraft(Path draftPath, Path targetPath) {
                throw new UnsupportedOperationException("Gerenciador de rascunho não configurado.");
            }

            @Override
            public void cleanUp() {}
        });
    }

    public PlayerScreen(
        Stage stage,
        Player<?> player,
        MusicPathUtils musicPathUtils,
        DraftMusicManager draftManager
    ) {
        if (stage == null) throw new IllegalArgumentException("Stage cannot be null.");
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        if (musicPathUtils == null) throw new IllegalArgumentException("Music path utils cannot be null.");
        if (draftManager == null) throw new IllegalArgumentException("Draft manager cannot be null.");

        this.player = player;
        this.musicPathUtils = musicPathUtils;
        this.draftManager = draftManager;
        
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> endProgram());

        player.load(musicPathUtils.getAllMusicsPathInResources().getFirst());

        sheetMusicWidget = new SheetMusicWidget(player.getCurrentMusicPath(), player::getMusicPosition);
        ScrollPane sheetMusicWidgetScroll = ControlFactory.createScrollPane(sheetMusicWidget);

        playPauseButton = new MusicPlayButtonWidget(player);
        musicSelectionBox = new MusicSelectionWidget(musicPathUtils);
        musicSelectionBox.setOnAction(e -> selectMusic());

        musicTitle = new Label(musicSelectionBox.getValue());
        musicSlider = new MusicSliderWidget(player);

        saveDraftButton = new ButtonBuilder("Salvar rascunho")
                        .onClick(e -> saveCurrentDraft())
                        .build();

        draftStatusLabel = new Label();
        draftStatusLabel.getStyleClass().add("draft-status-label");
        setNodeVisible(draftStatusLabel, false);

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

        draftActions = new BoxBuilder<>(new HBox(10))
                        .addClass("player-buttons")
                        .add(saveDraftButton)
                        .build();
        setSaveDraftVisible(false);

        VBox playerController = new BoxBuilder<>(new VBox(25))
                        .addClass("player-controller")
                        .add(musicTitle)
                        .add(musicSlider)
                        .add(playerButtons)
                        .add(draftActions)
                        .add(draftStatusLabel)
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


    /**
     * Atualiza imediatamente a lista de musicas salvas exibida no player.
     */
    public void refreshMusicList() {
        updateSelectionSilently(musicSelectionBox::refreshMusicList);
    }

    public void loadDraft(Path draftPath) {
        if (draftPath == null || !draftManager.isDraft(draftPath)) {
            throw new IllegalArgumentException("Rascunho inválido ou não encontrado.");
        }

        if (!changeMusic(draftPath)) return;

        updateSelectionSilently(() -> musicSelectionBox.getSelectionModel().clearSelection());
        setNodeVisible(draftStatusLabel, false);
    }

    protected File chooseDraftSaveFile(Path initialDirectory) {
        return openMidiSaveDialog(initialDirectory);
    }

    private void setSaveDraftVisible(boolean visible) {
        setNodeVisible(draftActions, visible);
        setNodeVisible(saveDraftButton, visible);
    }

    private void setNodeVisible(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void updateSelectionSilently(Runnable selectionUpdate) {
        updatingSelection = true;
        try {
            selectionUpdate.run();
        } finally {
            updatingSelection = false;
        }
    }

    private void saveCurrentDraft() {
        Path currentPath = player.getCurrentMusicPath();
        if (!draftManager.isDraft(currentPath)) return;

        File targetFile = chooseDraftSaveFile(musicPathUtils.getMusicsFolderPath());
        if (targetFile == null) return;

        try {
            Path targetPath = ensureMidiExtension(targetFile.toPath());
            String savedFileName = targetPath.getFileName().toString();
            draftManager.saveDraft(currentPath, targetPath);
            musicTitle.setText(savedFileName);
            draftStatusLabel.setText("Versão atual salva com sucesso (" + savedFileName + ")");
            setNodeVisible(draftStatusLabel, true);
            refreshSavedMusicList(targetPath, savedFileName);
        } catch (IOException | IllegalArgumentException e) {
            showErrorMessage("Falha ao salvar o rascunho: " + e.getMessage());
        }
    }

    private Path ensureMidiExtension(Path path) {
        String fileName = path.getFileName().toString();
        String lowerName = fileName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".mid") || lowerName.endsWith(".midi")) {
            return path;
        }
        return path.resolveSibling(fileName + ".mid");
    }

    private void refreshSavedMusicList(Path savedPath, String fileName) {
        Path musicsDir = musicPathUtils.getMusicsFolderPath().toAbsolutePath().normalize();
        Path savedParent = savedPath.toAbsolutePath().normalize().getParent();

        updateSelectionSilently(() -> {
            musicSelectionBox.refreshMusicList();
            if (musicsDir.equals(savedParent) && musicSelectionBox.getItems().contains(fileName)) {
                musicSelectionBox.setValue(fileName);
            }
        });
    }

    private void selectMusic() {
        if (updatingSelection) return;

        String musicName = musicSelectionBox.getValue();
        if (musicName == null) return;

        boolean isDraft = draftManager.isDraft(player.getCurrentMusicPath());
        if (!isDraft && musicName.equals(player.getCurrentMusicName())) return;

        try {
            changeMusic(musicPathUtils.getMusicPathFromName(musicName));
        } catch(IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private boolean changeMusic(Path path) {
        if (path == null) return false;

        try {
            player.load(path);
            updatePlayerUI();
            return true;
        } catch (RuntimeException e) {
            showErrorMessage(e.getMessage());
            return false;
        }

    }

    private void updatePlayerUI() {
        Path currentPath = player.getCurrentMusicPath();
        boolean isDraft = draftManager.isDraft(currentPath);

        sheetMusicWidget.loadMusic(currentPath);

        musicTitle.setText(isDraft ? "Rascunho (não salvo)" : player.getCurrentMusicName());

        musicSlider.setMax(player.getDuration());
        musicSlider.setValue(0);

        playPauseButton.setCurrentButtonToPlay();

        setSaveDraftVisible(isDraft);
        if (!isDraft) {
            setNodeVisible(draftStatusLabel, false);
        }
    }

    private void changeToNextMusic() {
        navigateMusic(musicPathUtils.getNextMusicPath(player.getCurrentMusicPath()));
    }

    private void changeToPreviousMusic() {
        navigateMusic(musicPathUtils.getPreviousMusicPath(player.getCurrentMusicPath()));
    }

    private void navigateMusic(Path targetPath) {
        if (!changeMusic(targetPath)) return;

        updateSelectionSilently(() -> musicSelectionBox.setValue(musicPathUtils.getMusicNameFromPath(targetPath)));
    }

    private void endProgram() {
        musicSlider.stopUpdater();
        musicSelectionBox.stopUpdater();
        sheetMusicWidget.stopAnimation();
        player.close();
        draftManager.cleanUp();
    }
}
