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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import exporters.DraftMusicCreator;
import exporters.MidiFileExporter;
import exporters.MusicExportPipeline;
import gui.JavaFXTestBase;
import gui.widgets.VoiceWidget;
import gui.widgets.BPMSliderWidget;
import gui.widgets.MappingHelpWidget;
import input.InputReader;
import input.TXTInputReader;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import music.VoicePreset;
import musicElements.InstrumentType;
import parsers.MusicToMidiParser;
import translators.TextToMusic.TextToMusicTranslator;
import translators.TextToMusic.CharacterMapping;
import translators.TextToMusic.MappingType;

class CompositionScreenTest extends JavaFXTestBase {

    @Test
    void refreshesHelpBesideTheVoicesUsingTheSharedMapping() throws Exception {
        runOnFxThread(() -> {
            CharacterMapping mapping = new CharacterMapping();
            MusicExportPipeline<?> pipeline = createPipeline();
            CompositionScreen screen = new CompositionScreen(new TXTInputReader(), pipeline,
                new exporters.TempMidiDraftService(pipeline), path -> {}, path -> {}, mapping);
            VBox main = (VBox) screen.getChildren().getFirst();
            HBox body = (HBox) ((ScrollPane) main.getChildren().get(1)).getContent();
            VBox voicesColumn = (VBox) ((ScrollPane) body.getChildren().getFirst()).getContent();
            assertThat(voicesColumn.getChildren().getFirst())
                .isSameAs(findFirstVoiceWidget(screen).getParent());
            assertThat(findButtonByText(voicesColumn, "+")).isNotNull();
            assertThat(findButtonByText(voicesColumn, "-")).isNotNull();
            assertThat(voicesColumn.getChildren().get(1).lookup(".bpm-slider")).isInstanceOf(BPMSliderWidget.class);
            MappingHelpWidget help = (MappingHelpWidget) body.getChildren().get(1);

            mapping.replaceChar(MappingType.NOTE, "C", "K");
            screen.refreshMappingHelp();

            VBox sections = (VBox) ((ScrollPane) help.getContent()).getContent();
            assertThat(sections.getChildren().stream().map(TitledPane.class::cast).map(section -> (VBox) section.getContent())
                .flatMap(section -> section.getChildren().stream())
                .filter(HBox.class::isInstance).map(HBox.class::cast))
                .anySatisfy(row -> {
                    assertThat(((Label) row.getChildren().get(0)).getText()).isEqualTo("K");
                    assertThat(((Label) row.getChildren().get(1)).getText()).contains("Dó");
                });
        });
    }

    private static class TestableCompositionScreen extends CompositionScreen {
        String lastErrorMessage;
        boolean confirmWarning;
        int warningCount;

        TestableCompositionScreen(
                InputReader<String> reader,
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

        @Override
        protected boolean showWarningMessage(String message) {
            warningCount++;
            return confirmWarning;
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

    private BPMSliderWidget findBpmSlider(Node root) {
        if (root instanceof BPMSliderWidget slider) return slider;
        if (root instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            return findBpmSlider(scrollPane.getContent());
        }
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                BPMSliderWidget slider = findBpmSlider(child);
                if (slider != null) return slider;
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
                (text, presets, initialBpm) -> Path.of("draft.mid"),
                path -> {},
                path -> {}
        );

        assertThat(screen.getChildren()).isNotEmpty();
        assertThat(screen.getChildren().getFirst()).isInstanceOf(VBox.class);

        VBox mainContent = (VBox) screen.getChildren().getFirst();
        assertThat(mainContent.getId()).isEqualTo("main");
        assertThat(((VBox) findFirstVoiceWidget(screen).getParent()).getChildren()).hasSize(1);
    }

    @Test
    void keepsVoiceNumbersAndPresetsIndependentAcrossScreensAndAfterRemoval() throws Exception {
        runOnFxThread(() -> {
            CompositionScreen firstScreen = new CompositionScreen(
                new TXTInputReader(), createPipeline(),
                (text, presets, initialBpm) -> Path.of("draft.mid"), path -> {}, path -> {}
            );
            findButtonByText(firstScreen, "+").fire();

            CompositionScreen secondScreen = new CompositionScreen(
                new TXTInputReader(), createPipeline(),
                (text, presets, initialBpm) -> Path.of("draft.mid"), path -> {}, path -> {}
            );
            findButtonByText(firstScreen, "+").fire();
            findButtonByText(firstScreen, "-").fire();
            findButtonByText(firstScreen, "+").fire();
            findButtonByText(secondScreen, "+").fire();

            VBox firstVoices = (VBox) findFirstVoiceWidget(firstScreen).getParent();
            VBox secondVoices = (VBox) findFirstVoiceWidget(secondScreen).getParent();
            assertThat(firstVoices.getChildren().stream()
                .map(VoiceWidget.class::cast)
                .map(voice -> ((Label) ((BorderPane) voice.getLeft()).getCenter()).getText()))
                .containsExactly("Voz 1", "Voz 2", "Voz 3");
            assertThat(secondVoices.getChildren().stream()
                .map(VoiceWidget.class::cast)
                .map(voice -> ((Label) ((BorderPane) voice.getLeft()).getCenter()).getText()))
                .containsExactly("Voz 1", "Voz 2");
            assertThat(firstVoices.getChildren().stream()
                .map(VoiceWidget.class::cast).map(voice -> voice.getInstrument().getType()))
                .containsExactly(InstrumentType.KEYS, InstrumentType.PLUCKED, InstrumentType.STRINGS);
            assertThat(secondVoices.getChildren().stream()
                .map(VoiceWidget.class::cast).map(voice -> voice.getInstrument().getType()))
                .containsExactly(InstrumentType.KEYS, InstrumentType.PLUCKED);
        });
    }

    @Test
    void rejectsNullDependenciesInConstructor() {
        InputReader<String> reader = new TXTInputReader();
        MusicExportPipeline<?> pipeline = createPipeline();
        DraftMusicCreator draftCreator = (text, presets, initialBpm) -> Path.of("draft.mid");
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
                (text, presets, initialBpm) -> Path.of("draft.mid"),
                path -> {},
                path -> {}
        );

        List<String> buttonTexts = findButtons(screen).stream()
                .map(Button::getText)
                .toList();

        assertThat(buttonTexts).contains("Salvar música", "Importar música", "Ouvir rascunho");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void removingVoiceWithTextRequiresConfirmation(boolean confirm) throws Exception {
        runOnFxThread(() -> {
            TestableCompositionScreen screen = new TestableCompositionScreen(
                    new TXTInputReader(), createPipeline(),
                    (text, presets, initialBpm) -> Path.of("draft.mid"), path -> {}
            );
            screen.confirmWarning = confirm;
            findButtonByText(screen, "+").fire();
            VBox voices = (VBox) findFirstVoiceWidget(screen).getParent();
            VoiceWidget lastVoice = (VoiceWidget) voices.getChildren().getLast();
            lastVoice.setVoiceText("C D E");

            findButtonByText(screen, "-").fire();

            assertThat(screen.warningCount).isEqualTo(1);
            assertThat(voices.getChildren()).hasSize(confirm ? 1 : 2);
            if (!confirm) {
                assertThat(voices.getChildren().getLast()).isSameAs(lastVoice);
                assertThat(lastVoice.getVoiceText()).isEqualTo("C D E");
            }
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " \t\n"})
    void removingEmptyVoiceDoesNotAskForConfirmation(String text) throws Exception {
        runOnFxThread(() -> {
            TestableCompositionScreen screen = new TestableCompositionScreen(
                    new TXTInputReader(), createPipeline(),
                    (input, presets, initialBpm) -> Path.of("draft.mid"), path -> {}
            );
            findFirstVoiceWidget(screen).setVoiceText("C D E");
            findButtonByText(screen, "+").fire();
            VBox voices = (VBox) findFirstVoiceWidget(screen).getParent();
            ((VoiceWidget) voices.getChildren().getLast()).setVoiceText(text);

            findButtonByText(screen, "-").fire();

            assertThat(screen.warningCount).isZero();
            assertThat(voices.getChildren()).hasSize(1);
            assertThat(findFirstVoiceWidget(screen).getVoiceText()).isEqualTo("C D E");
        });
    }

    @Test
    void doesNotRemoveTheOnlyVoiceOrAskForConfirmation() throws Exception {
        runOnFxThread(() -> {
            TestableCompositionScreen screen = new TestableCompositionScreen(
                    new TXTInputReader(), createPipeline(),
                    (text, presets, initialBpm) -> Path.of("draft.mid"), path -> {}
            );
            findFirstVoiceWidget(screen).setVoiceText("C D E");

            findButtonByText(screen, "-").fire();

            assertThat(screen.warningCount).isZero();
            assertThat(((VBox) findFirstVoiceWidget(screen).getParent()).getChildren()).hasSize(1);
            assertThat(findFirstVoiceWidget(screen).getVoiceText()).isEqualTo("C D E");
        });
    }

    @Test
    void savingMusicNotifiesCallbackAfterWritingMidi(@TempDir Path tempDir) throws Exception {
        Path target = tempDir.resolve("saved.mid");
        AtomicReference<Path> savedPath = new AtomicReference<>();
        AtomicReference<Exception> callbackError = new AtomicReference<>();
        CompositionScreen screen = new CompositionScreen(
                new TXTInputReader(), createPipeline(),
                (text, presets, initialBpm) -> Path.of("unused.mid"), path -> {},
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
            findBpmSlider(screen).setValue(90);
            findFirstVoiceWidget(screen).setVoiceText("C D E");
            findButtonByText(screen, "Salvar música").fire();
        });

        assertThat(callbackError.get()).isNull();
        assertThat(savedPath.get()).isEqualTo(target);
        Sequence sequence = MidiSystem.getSequence(target.toFile());
        assertThat(sequence.getMicrosecondLength()).isEqualTo(3L * (60_000_000 / 90));
        MetaMessage tempo = null;
        Track track = sequence.getTracks()[0];
        for (int i = 0; i < track.size(); i++) {
            if (track.get(i).getMessage() instanceof MetaMessage meta && meta.getType() == 0x51) {
                tempo = meta;
            }
        }
        assertThat(tempo).isNotNull();
        assertThat(tempo.getData()).containsExactly((byte) 0x0A, (byte) 0x2C, (byte) 0x2A);
    }

    @Test
    void listenDraftCreatesDraftAndInvokesNavigationCallback() throws Exception {
        Path expectedDraftPath = Path.of("rascunho-teste.mid");
        AtomicReference<String> capturedText = new AtomicReference<>();
        AtomicReference<List<VoicePreset>> capturedPresets = new AtomicReference<>();
        AtomicReference<Integer> capturedBpm = new AtomicReference<>();
        AtomicReference<Path> navigatedPath = new AtomicReference<>();

        DraftMusicCreator fakeCreator = (text, presets, initialBpm) -> {
            capturedText.set(text);
            capturedPresets.set(presets);
            capturedBpm.set(initialBpm);
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
            findBpmSlider(screen).setValue(185);

            findButtonByText(screen, "Ouvir rascunho").fire();
        });

        assertThat(capturedText.get()).isEqualTo("C D E");
        assertThat(capturedPresets.get()).hasSize(1);
        assertThat(capturedBpm.get()).isEqualTo(185);
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
        DraftMusicCreator failingCreator = (text, presets, initialBpm) -> {
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
