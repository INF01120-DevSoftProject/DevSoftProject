package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import exporters.DraftMusicManager;
import exporters.MidiFileExporter;
import exporters.MusicExportPipeline;
import exporters.TempMidiDraftService;
import gui.JavaFXTestBase;
import gui.TestPlayer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import music.VoicePreset;
import musicElements.InstrumentType;
import parsers.MusicToMidiParser;
import player.Player;
import translators.TextToMusic.TextToMusicTranslator;
import utils.MidiMusicPathUtils;
import utils.MusicPathUtils;

class PlayerScreenTest extends JavaFXTestBase {

    @TempDir
    Path tempDir;

    private TempMidiDraftService draftService;

    private static class TestablePlayerScreen extends PlayerScreen {
        File nextChosenFile;
        String lastErrorMessage;

        TestablePlayerScreen(
                Stage stage,
                Player<?> player,
                MusicPathUtils musicPathUtils,
                DraftMusicManager draftManager
        ) {
            super(stage, player, musicPathUtils, draftManager);
        }

        @Override
        protected File chooseDraftSaveFile(Path initialDirectory) {
            return nextChosenFile;
        }

        @Override
        protected void showErrorMessage(String message) {
            this.lastErrorMessage = message;
        }
    }

    @AfterEach
    void cleanUpDrafts() {
        for (Stage stage : openedStages) {
            try {
                runOnFxThread(() -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
            } catch (Exception ignored) {
            }
        }
        openedStages.clear();
        if (draftService != null) {
            draftService.cleanUp();
        }
    }

    private final List<Stage> openedStages = new ArrayList<>();

    private Stage createManagedStage() {
        Stage stage = new Stage();
        openedStages.add(stage);
        return stage;
    }

    private TempMidiDraftService createDraftService() {
        draftService = new TempMidiDraftService(
                new MusicExportPipeline<>(
                        new TextToMusicTranslator(),
                        new MusicToMidiParser(),
                        new MidiFileExporter()
                )
        );
        return draftService;
    }

    private void runOnFxThread(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                errorRef.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        if (errorRef.get() != null) {
            throw new RuntimeException(errorRef.get());
        }
    }

    private List<Node> collectAllNodes(Node root) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(root);
        if (root instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            nodes.addAll(collectAllNodes(scrollPane.getContent()));
        } else if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                nodes.addAll(collectAllNodes(child));
            }
        }
        return nodes;
    }

    private Button findButtonByText(Node root, String text) {
        return collectAllNodes(root).stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Botão não encontrado: " + text));
    }

    private Label findLabelByStyleClass(Node root, String styleClass) {
        return collectAllNodes(root).stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .filter(label -> label.getStyleClass().contains(styleClass))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Label não encontrado com classe: " + styleClass));
    }

    private Label findMusicTitleLabel(Node root) {
        return collectAllNodes(root).stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .filter(label -> !"Player".equals(label.getText())
                        && !label.getStyleClass().contains("draft-status-label"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Label de título da música não encontrado."));
    }

    @SuppressWarnings("unchecked")
    private ComboBox<String> findMusicSelectionBox(Node root) {
        return collectAllNodes(root).stream()
                .filter(ComboBox.class::isInstance)
                .map(box -> (ComboBox<String>) box)
                .findFirst()
                .orElseThrow(() -> new AssertionError("ComboBox de seleção não encontrado."));
    }

    @Test
    void initializesPlayerScreenInsideFxThread() throws Exception {
        AtomicReference<PlayerScreen> screenRef = new AtomicReference<>();

        runOnFxThread(() -> {
            Stage stage = createManagedStage();
            TestPlayer player = new TestPlayer();
            Path testMidi = Path.of("src", "main", "resources", "musics", "musica.mid");
            player.load(testMidi);

            MidiMusicPathUtils pathUtils = new MidiMusicPathUtils();
            PlayerScreen screen = new PlayerScreen(stage, player, pathUtils);
            screenRef.set(screen);
        });

        assertThat(screenRef.get()).isNotNull();
        assertThat(screenRef.get().getChildren()).isNotEmpty();
    }

    @Test
    void rejectsNullDependenciesInConstructor() throws Exception {
        TempMidiDraftService service = createDraftService();
        MidiMusicPathUtils pathUtils = new MidiMusicPathUtils();

        runOnFxThread(() -> {
            Stage stage = createManagedStage();
            TestPlayer player = new TestPlayer();

            assertThrows(IllegalArgumentException.class,
                    () -> new PlayerScreen(null, player, pathUtils, service));
            assertThrows(IllegalArgumentException.class,
                    () -> new PlayerScreen(stage, null, pathUtils, service));
            assertThrows(IllegalArgumentException.class,
                    () -> new PlayerScreen(stage, player, null, service));
            assertThrows(IllegalArgumentException.class,
                    () -> new PlayerScreen(stage, player, pathUtils, null));
        });
    }

    @Test
    void hidesSaveDraftButtonAndStatusWhenPlayingSavedMusic() throws Exception {
        TempMidiDraftService service = createDraftService();
        AtomicReference<Button> saveButtonRef = new AtomicReference<>();
        AtomicReference<Label> statusLabelRef = new AtomicReference<>();

        runOnFxThread(() -> {
            PlayerScreen screen = new PlayerScreen(createManagedStage(), new TestPlayer(), new MidiMusicPathUtils(), service);
            saveButtonRef.set(findButtonByText(screen, "Salvar rascunho"));
            statusLabelRef.set(findLabelByStyleClass(screen, "draft-status-label"));
        });

        assertThat(saveButtonRef.get().isVisible()).isFalse();
        assertThat(saveButtonRef.get().isManaged()).isFalse();
        assertThat(statusLabelRef.get().isVisible()).isFalse();
        assertThat(statusLabelRef.get().isManaged()).isFalse();
    }

    @Test
    void loadDraftLoadsPausedDraftShowsSaveButtonAndClearsSelection() throws Exception {
        TempMidiDraftService service = createDraftService();
        Path draft = service.createDraft("C D E", List.of(new VoicePreset(InstrumentType.KEYS)));

        TestPlayer player = new TestPlayer();
        AtomicReference<TestablePlayerScreen> screenRef = new AtomicReference<>();

        runOnFxThread(() -> {
            TestablePlayerScreen screen = new TestablePlayerScreen(
                    createManagedStage(), player, new MidiMusicPathUtils(), service
            );
            screen.loadDraft(draft);
            screenRef.set(screen);
        });

        TestablePlayerScreen screen = screenRef.get();
        assertThat(player.getCurrentMusicPath()).isEqualTo(draft);
        assertThat(player.playCalled).isFalse();
        assertThat(player.isPlaying()).isFalse();
        assertThat(findMusicTitleLabel(screen).getText()).isEqualTo("Rascunho (não salvo)");
        assertThat(findMusicSelectionBox(screen).getValue()).isNull();
        assertThat(findButtonByText(screen, "Salvar rascunho").isVisible()).isTrue();
        assertThat(findLabelByStyleClass(screen, "draft-status-label").isVisible()).isFalse();
    }

    @Test
    void loadDraftRejectsNullOrUnmanagedPath() throws Exception {
        TempMidiDraftService service = createDraftService();
        Path unmanaged = Files.writeString(tempDir.resolve("nao_rascunho.mid"), "x");

        runOnFxThread(() -> {
            PlayerScreen screen = new PlayerScreen(createManagedStage(), new TestPlayer(), new MidiMusicPathUtils(), service);
            assertThrows(IllegalArgumentException.class, () -> screen.loadDraft(null));
            assertThrows(IllegalArgumentException.class, () -> screen.loadDraft(unmanaged));
        });
    }

    @Test
    void saveDraftSavesFileShowsSuccessIndicatorAndAllowsSavingAgain() throws Exception {
        TempMidiDraftService service = createDraftService();
        Path draft = service.createDraft("C D E", List.of(new VoicePreset(InstrumentType.KEYS)));
        Path firstTarget = tempDir.resolve("primeiro_salvamento.mid");
        Path secondTarget = tempDir.resolve("segundo_salvamento.mid");

        AtomicReference<TestablePlayerScreen> screenRef = new AtomicReference<>();

        runOnFxThread(() -> {
            TestablePlayerScreen screen = new TestablePlayerScreen(
                    createManagedStage(), new TestPlayer(), new MidiMusicPathUtils(), service
            );
            screen.loadDraft(draft);

            Button saveButton = findButtonByText(screen, "Salvar rascunho");
            screen.nextChosenFile = firstTarget.toFile();
            saveButton.fire();

            screen.nextChosenFile = secondTarget.toFile();
            saveButton.fire();
            screenRef.set(screen);
        });

        TestablePlayerScreen screen = screenRef.get();
        Label statusLabel = findLabelByStyleClass(screen, "draft-status-label");

        assertThat(firstTarget).exists();
        assertThat(secondTarget).exists();
        assertThat(findMusicTitleLabel(screen).getText()).isEqualTo("segundo_salvamento.mid");
        assertThat(findButtonByText(screen, "Salvar rascunho").isVisible()).isTrue();
        assertThat(statusLabel.isVisible()).isTrue();
        assertThat(statusLabel.getText()).contains("segundo_salvamento.mid");
        assertThat(screen.lastErrorMessage).isNull();
    }

    @Test
    void selectingSavedMusicAfterDraftHidesSaveButtonAndStatus() throws Exception {
        TempMidiDraftService service = createDraftService();
        Path draft = service.createDraft("C D E", List.of(new VoicePreset(InstrumentType.KEYS)));
        Path target = tempDir.resolve("salvo.mid");

        AtomicReference<TestablePlayerScreen> screenRef = new AtomicReference<>();

        runOnFxThread(() -> {
            TestablePlayerScreen screen = new TestablePlayerScreen(
                    createManagedStage(), new TestPlayer(), new MidiMusicPathUtils(), service
            );
            screen.loadDraft(draft);
            screen.nextChosenFile = target.toFile();
            findButtonByText(screen, "Salvar rascunho").fire();

            ComboBox<String> selectionBox = findMusicSelectionBox(screen);
            selectionBox.getSelectionModel().selectFirst();
            selectionBox.fireEvent(new javafx.event.ActionEvent());
            screenRef.set(screen);
        });

        TestablePlayerScreen screen = screenRef.get();
        assertThat(findButtonByText(screen, "Salvar rascunho").isVisible()).isFalse();
        assertThat(findLabelByStyleClass(screen, "draft-status-label").isVisible()).isFalse();
        assertThat(findMusicTitleLabel(screen).getText()).isNotEqualTo("Rascunho (não salvo)");
    }

    @Test
    void savingDraftInMusicsFolderImmediatelyUpdatesAndSelectsInSelectionList() throws Exception {
        Path musicsDir = tempDir.resolve("musics");
        Files.createDirectories(musicsDir);
        Path existingMidi = Path.of("src", "main", "resources", "musics", "musica.mid");
        Files.copy(existingMidi, musicsDir.resolve("musica.mid"));

        MusicPathUtils tempMusicsPathUtils = new MidiMusicPathUtils() {
            @Override
            public Path getMusicsFolderPath() {
                return musicsDir;
            }
        };

        TempMidiDraftService service = createDraftService();
        Path draft = service.createDraft("C D E", List.of(new VoicePreset(InstrumentType.KEYS)));
        Path savedInFolder = musicsDir.resolve("novo_rascunho.mid");
        AtomicReference<TestablePlayerScreen> screenRef = new AtomicReference<>();

        runOnFxThread(() -> {
            TestablePlayerScreen screen = new TestablePlayerScreen(
                    createManagedStage(), new TestPlayer(), tempMusicsPathUtils, service
            );
            screen.loadDraft(draft);
            screen.nextChosenFile = savedInFolder.toFile();
            findButtonByText(screen, "Salvar rascunho").fire();
            screenRef.set(screen);
        });

        TestablePlayerScreen screen = screenRef.get();
        ComboBox<String> selectionBox = findMusicSelectionBox(screen);
        assertThat(savedInFolder).exists();
        assertThat(selectionBox.getItems()).contains("musica.mid", "novo_rascunho.mid");
        assertThat(selectionBox.getValue()).isEqualTo("novo_rascunho.mid");
        assertThat(findMusicTitleLabel(screen).getText()).isEqualTo("novo_rascunho.mid");
    }

    @Test
    void savingDraftWithoutExtensionAppendsMidAndAppearsInSelectionList() throws Exception {
        Path musicsDir = tempDir.resolve("musics_sem_ext");
        Files.createDirectories(musicsDir);
        Path existingMidi = Path.of("src", "main", "resources", "musics", "musica.mid");
        Files.copy(existingMidi, musicsDir.resolve("musica.mid"));

        MusicPathUtils tempMusicsPathUtils = new MidiMusicPathUtils() {
            @Override
            public Path getMusicsFolderPath() {
                return musicsDir;
            }
        };

        TempMidiDraftService service = createDraftService();
        Path draft = service.createDraft("C D E", List.of(new VoicePreset(InstrumentType.KEYS)));
        Path chosenWithoutExtension = musicsDir.resolve("meu_rascunho");
        Path expectedMidiPath = musicsDir.resolve("meu_rascunho.mid");
        AtomicReference<TestablePlayerScreen> screenRef = new AtomicReference<>();

        runOnFxThread(() -> {
            TestablePlayerScreen screen = new TestablePlayerScreen(
                    createManagedStage(), new TestPlayer(), tempMusicsPathUtils, service
            );
            screen.loadDraft(draft);
            screen.nextChosenFile = chosenWithoutExtension.toFile();
            findButtonByText(screen, "Salvar rascunho").fire();
            screenRef.set(screen);
        });

        TestablePlayerScreen screen = screenRef.get();
        ComboBox<String> selectionBox = findMusicSelectionBox(screen);
        assertThat(chosenWithoutExtension).doesNotExist();
        assertThat(expectedMidiPath).exists();
        assertThat(findMusicTitleLabel(screen).getText()).isEqualTo("meu_rascunho.mid");
        assertThat(selectionBox.getItems()).contains("musica.mid", "meu_rascunho.mid");
        assertThat(selectionBox.getValue()).isEqualTo("meu_rascunho.mid");
    }

    @Test
    void windowCloseRequestClosesPlayerAndCleansUpDrafts() throws Exception {
        TempMidiDraftService service = createDraftService();
        Path draft = service.createDraft("C D E", List.of(new VoicePreset(InstrumentType.KEYS)));
        TestPlayer player = new TestPlayer();

        runOnFxThread(() -> {
            Stage stage = new Stage();
            PlayerScreen screen = new PlayerScreen(stage, player, new MidiMusicPathUtils(), service);
            screen.loadDraft(draft);

            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        assertThat(player.closeCalled).isTrue();
        assertThat(draft).doesNotExist();
        assertThat(service.isDraft(draft)).isFalse();
    }
}
