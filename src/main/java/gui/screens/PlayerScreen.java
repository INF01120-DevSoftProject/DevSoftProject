package gui.screens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
    private final HBox draftActionsContainer;

    private final SheetMusicWidget sheetMusicWidget;
    private final MusicSliderWidget musicSlider;
    private final MusicSelectionWidget musicSelectionWidget;
    private final MusicPlayButtonWidget playPauseButton;

    private final Button nextMusicButton;
    private final Button previousMusicButton;

    public PlayerScreen(
            Stage stage,
            Player<?> player,
            MusicPathUtils musicPathUtils,
            DraftMusicManager draftManager) {
        if (stage == null)
            throw new IllegalArgumentException("Stage cannot be null.");
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null.");
        if (musicPathUtils == null)
            throw new IllegalArgumentException("Music path utils cannot be null.");
        if (draftManager == null)
            throw new IllegalArgumentException("Draft manager cannot be null.");

        this.player = player;
        this.musicPathUtils = musicPathUtils;
        this.draftManager = draftManager;

        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> endProgram());

        player.load(musicPathUtils.getAllMusicsPathInResources().getFirst());

        sheetMusicWidget = new SheetMusicWidget(player.getCurrentMusicPath(), player::getMusicPosition);
        playPauseButton = new MusicPlayButtonWidget(player);

        musicSelectionWidget = new MusicSelectionWidget(musicPathUtils);
        musicSelectionWidget.setOnAction(e -> selectMusic());

        musicTitle = new Label(player.getCurrentMusicName());
        musicSlider = new MusicSliderWidget(player);

        draftStatusLabel = new Label();
        draftStatusLabel.getStyleClass().add("draft-status-label");
        setNodeVisibility(draftStatusLabel, false);

        nextMusicButton = new ButtonBuilder("⏮")
                .onClick(e -> changeToPreviousMusic())
                .build();

        previousMusicButton = new ButtonBuilder("⏭")
                .onClick(e -> changeToNextMusic())
                .build();

        HBox playerButtons = new BoxBuilder<>(new HBox(25))
                .addClass("player-buttons")
                .add(nextMusicButton)
                .add(playPauseButton)
                .add(previousMusicButton)
                .build();

        draftActionsContainer = new BoxBuilder<>(new HBox(10))
                .addClass("player-buttons")
                .add(new ButtonBuilder("Salvar rascunho")
                        .onClick(e -> saveCurrentDraft())
                        .build())
                .build();

        setNodeVisibility(draftActionsContainer, false);

        VBox playerController = new BoxBuilder<>(new VBox(25))
                .addClass("player-controller")
                .add(musicTitle)
                .add(musicSlider)
                .add(playerButtons)
                .add(draftActionsContainer)
                .add(draftStatusLabel)
                .build();

        VBox mainContent = new BoxBuilder<>(new VBox(25))
                .setId("main")
                .add(new Label("Player"))
                .add(musicSelectionWidget)
                .add(ControlFactory.createScrollPane(sheetMusicWidget))
                .add(playerController)
                .build();

        setScreenMainContent(mainContent);
    }

    /**
     * Atualiza imediatamente a lista de musicas salvas exibida no player.
     */
    public void refreshMusicList() {
        musicSelectionWidget.refreshMusicList();
    }

    public void loadDraft(Path draftPath) {
        if (draftPath == null || !draftManager.isDraft(draftPath)) {
            throw new IllegalArgumentException("Rascunho inválido ou não encontrado.");
        }

        if (!loadMusic(draftPath))
            return;

        musicSelectionWidget.clearCurrentBoxSelection();
        setNodeVisibility(draftStatusLabel, false);
        setNodeVisibility(previousMusicButton, false);
        setNodeVisibility(nextMusicButton, false);
    }

    public void onMusicSaved(Path savedPath) {
        refreshMusicList();

        Path currentPath = player.getCurrentMusicPath();
        if (currentPath == null)
            return;

        if (currentPath.toAbsolutePath().normalize().equals(savedPath.toAbsolutePath().normalize())) {
            loadMusic(savedPath);
        }
    }

    private void setNodeVisibility(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void saveCurrentDraft() {
        Path currentPath = player.getCurrentMusicPath();
        if (!draftManager.isDraft(currentPath))
            return;

        File targetFile = openMusicSaveDialog();
        if (targetFile == null)
            return;

        try {
            Path targetPath = targetFile.toPath();

            if (!musicPathUtils.isSupportedFile(targetPath)) {
                throw new IllegalArgumentException("Formato de arquivo não suportado: " + targetPath.getFileName());
            }

            draftManager.saveDraft(currentPath, targetPath);

            if (!loadMusic(targetPath)) {
                throw new IllegalArgumentException("Erro ao carregar arquivo no player: " + targetPath.getFileName());
            }

            String savedFileName = targetPath.getFileName().toString();
            musicTitle.setText(savedFileName);
            draftStatusLabel.setText("Versão atual salva com sucesso (" + savedFileName + ")");

            musicSelectionWidget.refreshMusicList();
            musicSelectionWidget.selectMusicSilently(savedFileName);

            setNodeVisibility(draftStatusLabel, true);
            setNodeVisibility(previousMusicButton, true);
            setNodeVisibility(nextMusicButton, true);

        } catch (IOException | IllegalArgumentException e) {
            showErrorMessage("Falha ao salvar o rascunho: " + e.getMessage());
        }
    }

    private void selectMusic() {
        String musicName = musicSelectionWidget.getValue();
        if (musicName == null)
            return;

        boolean isDraft = draftManager.isDraft(player.getCurrentMusicPath());
        if (!isDraft && musicName.equals(player.getCurrentMusicName()))
            return;

        try {
            loadMusic(musicPathUtils.getMusicPathFromName(musicName));
        } catch (IllegalArgumentException e) {
            changeToNextMusic();
            showErrorMessage(e.getMessage());
        }
    }

    private boolean loadMusic(Path path) {
        if (path == null)
            return false;

        try {
            player.load(path);
            updatePlayerUI();
            return true;
        } catch (RuntimeException e) {
            showErrorMessage("Não foi possível gerar a musica. Verifique o arquivo \n" + e.getMessage());
            return false;
        }
    }

    private void updatePlayerUI() {
        Path currentPath = player.getCurrentMusicPath();
        boolean isDraft = draftManager.isDraft(currentPath);

        sheetMusicWidget.loadMusic(currentPath);

        musicTitle.setText(isDraft ? "Rascunho (não salvo)" : player.getCurrentMusicName());
        musicSlider.resetSlider();

        playPauseButton.setCurrentButtonToPlay();

        setNodeVisibility(draftActionsContainer, isDraft);

        if (!isDraft) {
            setNodeVisibility(draftStatusLabel, false);
        }
    }

    private void changeToNextMusic() {
        navigateMusic(musicPathUtils.getNextMusicPath(player.getCurrentMusicPath()));
    }

    private void changeToPreviousMusic() {
        navigateMusic(musicPathUtils.getPreviousMusicPath(player.getCurrentMusicPath()));
    }

    private void navigateMusic(Path path) {
        if (!loadMusic(path))
            return;

        musicSelectionWidget.setValue(player.getCurrentMusicName());
    }

    private void endProgram() {
        musicSlider.stopUpdater();
        musicSelectionWidget.stopUpdater();
        sheetMusicWidget.stopAnimation();
        player.close();
        draftManager.cleanUp();
    }
}
