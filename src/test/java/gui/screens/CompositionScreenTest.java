package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import javax.sound.midi.MidiSystem;
import exporters.DraftMusicCreator;
import exporters.MidiFileExporter;
import exporters.MusicExportPipeline;
import gui.JavaFXTestBase;
import gui.widgets.VoiceWidget;
import input.InputReader;
import input.TXTInputReader;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import music.VoicePreset;
import parsers.MusicToMidiParser;
import translators.TextToMusic.TextToMusicTranslator;

class CompositionScreenTest extends JavaFXTestBase {

    private static class TestableCompositionScreen extends CompositionScreen {
        String lastErrorMessage;

        TestableCompositionScreen(
                InputReader reader,
                MusicExportPipeline<?> exporter,
                DraftMusicCreator draftCreator,
                Consumer<Path> onListenDraft
        ) {
            super(reader, exporter, draftCreator, onListenDraft, path -> {});
        }

        @Override
        protected void showErrorMessage(String message) {
            this.lastErrorMessage = message;
        }
    }

    private MusicExportPipeline<?> createPipeline() {
        return new MusicExportPipeline<>(
                new TextToMusicTranslator(),
                new MusicToMidiParser(),
                new MidiFileExporter()
        );
    }

    private List<Button> findButtons(Node root) {
        List<Button> buttons = new ArrayList<>();
        if (root instanceof Button button) {
            buttons.add(button);
        }
        if (root instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            buttons.addAll(findButtons(scrollPane.getContent()));
        } else if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                buttons.addAll(findButtons(child));
            }
        }
        return buttons;
    }

    private Button findButtonByText(Node root, String text) {
        return findButtons(root).stream()
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Botão não encontrado: " + text));
    }

    private VoiceWidget findFirstVoiceWidget(Node root) {
        if (root instanceof VoiceWidget voiceWidget) {
            return voiceWidget;
        }
        if (root instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            VoiceWidget found = findFirstVoiceWidget(scrollPane.getContent());
            if (found != null) return found;
        } else if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                VoiceWidget found = findFirstVoiceWidget(child);
                if (found != null) return found;
            }
        }
        return null;
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

    @Test
    void initializesCompositionScreenWithSingleVoice() {
        CompositionScreen screen = new CompositionScreen(
                new TXTInputReader(),
                createPipeline(),
                (text, presets) -> Path.of("draft.mid"),
                path -> {},
                path -> {}
        );

        assertThat(screen.getChildren()).isNotEmpty();
        assertThat(screen.getChildren().getFirst()).isInstanceOf(VBox.class);

        VBox mainContent = (VBox) screen.getChildren().getFirst();
        assertThat(mainContent.getId()).isEqualTo("main");
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(1);
    }

    @Test
    void rejectsNullDependenciesInConstructor() {
        InputReader reader = new TXTInputReader();
        MusicExportPipeline<?> pipeline = createPipeline();
        DraftMusicCreator draftCreator = (text, presets) -> Path.of("draft.mid");
        Consumer<Path> onListenDraft = path -> {};

        assertThrows(IllegalArgumentException.class,
                () -> new CompositionScreen(null, pipeline, draftCreator, onListenDraft, path -> {}));
        assertThrows(IllegalArgumentException.class,
                () -> new CompositionScreen(reader, null, draftCreator, onListenDraft, path -> {}));
        assertThrows(IllegalArgumentException.class,
                () -> new CompositionScreen(reader, pipeline, null, onListenDraft, path -> {}));
        assertThrows(IllegalArgumentException.class,
                () -> new CompositionScreen(reader, pipeline, draftCreator, null, path -> {}));
    }

    @Test
    void containsListenDraftButtonAlongsideSaveAndImport() {
        CompositionScreen screen = new CompositionScreen(
                new TXTInputReader(),
                createPipeline(),
                (text, presets) -> Path.of("draft.mid"),
                path -> {},
                path -> {}
        );

        List<String> buttonTexts = findButtons(screen).stream()
                .map(Button::getText)
                .toList();

        assertThat(buttonTexts).contains("Salvar música", "Importar música", "Ouvir rascunho");
    }

    @Test
    void savingMusicNotifiesCallbackAfterWritingMidi(@TempDir Path tempDir) throws Exception {
        Path target = tempDir.resolve("saved.mid");
        AtomicReference<Path> savedPath = new AtomicReference<>();
        AtomicReference<Exception> callbackError = new AtomicReference<>();
        CompositionScreen screen = new CompositionScreen(
                new TXTInputReader(), createPipeline(),
                (text, presets) -> Path.of("unused.mid"), path -> {},
                path -> {
                    try {
                        MidiSystem.getSequence(path.toFile());
                        savedPath.set(path);
                    } catch (Exception e) {
                        callbackError.set(e);
                    }
                }
        ) {
            @Override
            protected File openMusicSaveDialog() {
                return target.toFile();
            }
        };

        runOnFxThread(() -> {
            findFirstVoiceWidget(screen).setVoiceText("C D E");
            findButtonByText(screen, "Salvar música").fire();
        });

        assertThat(callbackError.get()).isNull();
        assertThat(savedPath.get()).isEqualTo(target);
    }

    @Test
    void listenDraftCreatesDraftAndInvokesNavigationCallback() throws Exception {
        Path expectedDraftPath = Path.of("rascunho-teste.mid");
        AtomicReference<String> capturedText = new AtomicReference<>();
        AtomicReference<List<VoicePreset>> capturedPresets = new AtomicReference<>();
        AtomicReference<Path> navigatedPath = new AtomicReference<>();

        DraftMusicCreator fakeCreator = (text, presets) -> {
            capturedText.set(text);
            capturedPresets.set(presets);
            return expectedDraftPath;
        };

        TestableCompositionScreen screen = new TestableCompositionScreen(
                new TXTInputReader(),
                createPipeline(),
                fakeCreator,
                navigatedPath::set
        );

        runOnFxThread(() -> {
            VoiceWidget firstVoice = findFirstVoiceWidget(screen);
            assertThat(firstVoice).isNotNull();
            firstVoice.setVoiceText("C D E");

            findButtonByText(screen, "Ouvir rascunho").fire();
        });

        assertThat(capturedText.get()).isEqualTo("C D E");
        assertThat(capturedPresets.get()).hasSize(1);
        assertThat(navigatedPath.get()).isEqualTo(expectedDraftPath);
        assertThat(screen.lastErrorMessage).isNull();
    }

    @Test
    void listenDraftShowsErrorAndDoesNotNavigateWhenCompositionIsEmpty() throws Exception {
        AtomicReference<Path> navigatedPath = new AtomicReference<>();

        TestableCompositionScreen screen = new TestableCompositionScreen(
                new TXTInputReader(),
                createPipeline(),
                new exporters.TempMidiDraftService(createPipeline()),
                navigatedPath::set
        );

        runOnFxThread(() -> findButtonByText(screen, "Ouvir rascunho").fire());

        assertThat(navigatedPath.get()).isNull();
        assertThat(screen.lastErrorMessage).startsWith("Falha ao gerar rascunho:");
    }

    @Test
    void listenDraftShowsErrorAndDoesNotNavigateOnIOException() throws Exception {
        AtomicReference<Path> navigatedPath = new AtomicReference<>();
        DraftMusicCreator failingCreator = (text, presets) -> {
            throw new IOException("Erro de disco");
        };

        TestableCompositionScreen screen = new TestableCompositionScreen(
                new TXTInputReader(),
                createPipeline(),
                failingCreator,
                navigatedPath::set
        );

        runOnFxThread(() -> {
            VoiceWidget firstVoice = findFirstVoiceWidget(screen);
            assertThat(firstVoice).isNotNull();
            firstVoice.setVoiceText("C D E");

            findButtonByText(screen, "Ouvir rascunho").fire();
        });

        assertThat(navigatedPath.get()).isNull();
        assertThat(screen.lastErrorMessage).isEqualTo("Falha ao gerar rascunho: Erro de disco");
    }
}
