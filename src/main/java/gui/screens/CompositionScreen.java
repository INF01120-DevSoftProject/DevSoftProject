package gui.screens;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import java.nio.file.Path;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import music.VoicePreset;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.factory.ControlFactory;
import gui.widgets.VoiceWidget;
import gui.widgets.BPMSliderWidget;
import gui.widgets.voicePresetWidget.*;
import input.InputReader;
import exporters.DraftMusicCreator;
import exporters.MusicExportPipeline;

public class CompositionScreen extends Screen {

    private static final int MAX_VOICES = 15;

    private final VBox voicesContainer;
    private final BPMSliderWidget bpmSlider;
    
    private final MusicExportPipeline<?> exporter;
    private final InputReader<String> reader;

    private final DraftMusicCreator draftCreator;

    private final Consumer<Path> onListenDraft;
    private final Consumer<Path> onMusicSaved;


    public CompositionScreen(
        InputReader<String> reader, 
        MusicExportPipeline<?> exporter, 
        DraftMusicCreator draftCreator, 
        Consumer<Path> onListenDraft,
        Consumer<Path> onMusicSaved
    ) {

        if (reader == null)  throw new IllegalArgumentException("Reader cannot be null.");
        if (exporter == null)  throw new IllegalArgumentException("Exporter cannot be null.");
        if (draftCreator == null)  throw new IllegalArgumentException("Draft creator cannot be null.");
        if (onListenDraft == null)  throw new IllegalArgumentException("Consumer(onListenDraft) cannot be null.");
    
        this.exporter = exporter;
        this.reader = reader;
        this.draftCreator = draftCreator;
        this.onListenDraft = onListenDraft;
        this.onMusicSaved = onMusicSaved;

        VoiceWidget.restartCurrentVoiceNum();

        bpmSlider = new BPMSliderWidget();
        Label bpmLabel = new Label();
        bpmLabel.textProperty().bind(bpmSlider.valueProperty().asString("BPM: %.0f"));

        HBox bpmControls = new BoxBuilder<>(new HBox(15))
                                    .addClass("bpm-controls")
                                    .add(bpmLabel)
                                    .add(bpmSlider)
                                    .apply(b -> HBox.setHgrow(bpmSlider, Priority.ALWAYS))
                                    .build();

        voicesContainer = new BoxBuilder<>(new VBox(20))
                                    .add(createVoiceWidget())
                                    .build();

        ScrollPane voicesContainerScroll = ControlFactory.createScrollPane(voicesContainer);

        HBox plusMinusButtons = new BoxBuilder<>(new HBox(20))
                                    .addClass("fixed-button-container")
                                    .add(new ButtonBuilder("+")
                                            .onClickAddToContainer(() -> createVoiceWidget(), voicesContainer, MAX_VOICES)
                                            .build())
                                    .add(new ButtonBuilder("-")
                                            .onClick(e -> removeLastVoice())
                                            .build())
                                    .build();

        HBox compositionControls = new BoxBuilder<>(new HBox(20))
                                    .add(plusMinusButtons)
                                    .add(bpmControls)
                                    .apply(b -> HBox.setHgrow(bpmControls, Priority.ALWAYS))
                                    .build();

        HBox actionButtons = new BoxBuilder<>(new HBox(20))
                                    .addClass("fill-button-container")
                                    .add(new ButtonBuilder("Salvar música")
                                            .onClick(b -> saveMusic())
                                            .build())
                                    .add(new ButtonBuilder("Importar música")
                                            .onClick(b -> importMusic())                                            
                                            .build())
                                    .add(new ButtonBuilder("Ouvir rascunho")
                                            .onClick(b -> listenDraft())      
                                            .build())
                                    .build();

        VBox mainContent = new BoxBuilder<>(new VBox(25))
                                    .setId("main")
                                    .add(new Label("Composição"))
                                    .add(voicesContainerScroll)
                                    .add(compositionControls)
                                    .add(ControlFactory.createVerticalGrower())
                                    .add(actionButtons)
                                    .build();
        
        setScreenMainContent(mainContent);
    }

    private void removeLastVoice() {
        if (voicesContainer.getChildren().size() <= 1) return;

        VoiceWidget voice = (VoiceWidget) voicesContainer.getChildren().getLast();
        if (!voice.getVoiceText().isBlank() && !showWarningMessage("Deseja mesmo excluir esta voz? O texto será perdido.")) {
            return;
        }

        voicesContainer.getChildren().removeLast();
        VoiceWidget.decrementCurrentVoiceNum();
    }

    private void saveMusic() {
        File file = openMusicSaveDialog();
        if (file == null) return;

        try {
            MusicInputData data = getMusicInputData();
            exporter.exportMusic(file, data.text(), data.presets(), bpmSlider.getBPM());
            onMusicSaved.accept(file.toPath());

        } catch (IOException | IllegalArgumentException e) {
            showErrorMessage("Falha ao salvar o arquivo MIDI: " + e.getMessage());
        }
    }

    private void importMusic() {
        File file = openFileChooserWindow();
        if (file == null) return;

        try {
            List<String> lines = reader.readFile(file.toPath()).lines().toList();

            if (lines.stream().allMatch(String::isBlank)) {
                throw new IllegalArgumentException("O arquivo está vazio.");
            }

            if (lines.size() > MAX_VOICES) {
                throw new IllegalArgumentException("O arquivo possui mais de " + MAX_VOICES + " vozes.");
            }

            updateVoiceWidgets(lines);

        } catch (IOException e) {
            showErrorMessage("Erro ao importar música: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void listenDraft(){

        MusicInputData data = getMusicInputData();

        try {
            Path draftPath = draftCreator.createDraft(data.text(), data.presets(), bpmSlider.getBPM());
            onListenDraft.accept(draftPath);
        }
        catch (IOException | IllegalArgumentException e) {
            showErrorMessage("Falha ao gerar rascunho: " + e.getMessage());
        }
    }

    private record MusicInputData (
        String text,
        List<VoicePreset> presets
    ) {}
    
    private MusicInputData getMusicInputData() {
        StringJoiner musicText = new StringJoiner(System.lineSeparator());
        List<VoicePreset> voicePresets = new ArrayList<>();

        for (Node node : voicesContainer.getChildren()) {
            VoiceWidget voiceNode = (VoiceWidget) node;
            String voiceText = voiceNode.getVoiceText();

            if (voiceText == null || voiceText.isBlank()) {
                continue;
            }

            musicText.add(voiceText.replace("\r", "").replace("\n", ""));

            voicePresets.add(new VoicePreset(
                voiceNode.getInstrument(),
                voiceNode.getVolume(),
                voiceNode.getOctave()
            ));
        }

        return new MusicInputData(musicText.toString(), voicePresets);
    }

    private void updateVoiceWidgets(List<String> lines) {
        int voiceCount = lines.size();

        while (voicesContainer.getChildren().size() < voiceCount) {
            voicesContainer.getChildren().add(createVoiceWidget());
        }

        while (voicesContainer.getChildren().size() > voiceCount) {
            int lastIndex = voicesContainer.getChildren().size() - 1;
            voicesContainer.getChildren().remove(lastIndex);
            VoiceWidget.decrementCurrentVoiceNum();
        }

        for (int i = 0; i < voiceCount; i++) {
            VoiceWidget voice = (VoiceWidget) voicesContainer.getChildren().get(i);
            voice.setVoiceText(lines.get(i));
        }
    }

    private VoiceWidget createVoiceWidget() {
        List<VoicePresetWidget> presets = List.of(
            new VoicePresetKeysWidget(),
            new VoicePresetPluckedWidget(),
            new VoicePresetStringsWidget(),
            new VoicePresetWindsWidget(),
            new VoicePresetPercussionWidget()
        );

        int currentPreset = VoiceWidget.getCurrentVoiceNum() % presets.size();

        return new VoiceWidget(presets.get(currentPreset));
    }
}
