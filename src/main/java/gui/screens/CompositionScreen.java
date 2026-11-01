package gui.screens;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.sound.midi.Sequence;

import music.Music;
import music.VoicePreset;
import parsers.MusicToMIDIParser;
import translators.TextToMusic.TextToMusicTranslator;
import exporters.FileExporter;
import gui.ControlFactory;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.widgets.VoiceWidget;
import gui.widgets.voicePresetWidget.*;
import input.InputReader;

public class CompositionScreen extends Screen {

    private static final int MAX_VOICES = 15;
    private static final int NUM_OF_VOICE_PRESETS = 5;

    private final VBox voicesContainer;
    private final TextToMusicTranslator translator;
    private final MusicToMIDIParser parser;
    private final FileExporter<Sequence>  exporter;
    private final InputReader reader;

    public CompositionScreen(InputReader reader, FileExporter<Sequence> exporter) {
        translator = new TextToMusicTranslator();
        parser = new MusicToMIDIParser();
        
        this.exporter = exporter;
        this.reader = reader;

        VoiceWidget.restartCurrentVoiceNum();

        voicesContainer = new BoxBuilder<>(new VBox(20))
                                    .add(createVoiceWidget())
                                    .build();

        ScrollPane voicesContainerScroll = ControlFactory.createScrollPane(voicesContainer);

        HBox plusMinusButtons = new BoxBuilder<>(new HBox(20))
                                    .addClass("plus-minus-container")
                                    .add(new ButtonBuilder("+")
                                            .onClickAddToContainer(() -> createVoiceWidget(), voicesContainer, MAX_VOICES)
                                            .build())
                                    .add(new ButtonBuilder("-")
                                            .onClickRemoveLastFromContainer(voicesContainer)
                                            .onClick(e -> VoiceWidget.decrementCurrentVoiceNum())
                                            .build())
                                    .build();

        HBox saveImportButtons = new BoxBuilder<>(new HBox(20))
                                    .addClass("save-import-container")
                                    .add(new ButtonBuilder("Salvar música")
                                            .onClick(b -> saveMusic())
                                            .build())
                                    .add(new ButtonBuilder("Importar música")
                                            .onClick(b -> importMusic())                                            
                                            .build())
                                    .build();

        VBox mainContent = new BoxBuilder<>(new VBox(25))
                                    .setId("main")
                                    .add(new Label("Composição"))
                                    .add(voicesContainerScroll)
                                    .add(plusMinusButtons)
                                    .add(ControlFactory.createVerticalGrower())
                                    .add(saveImportButtons)
                                    .build();
        
        setScreenMainContent(mainContent);
    }

    private void saveMusic() {
        File file = openFileChooserWindow();
        if (file == null) return;

        try {
            VoiceWidgetData data = getVoiceWidgetData();

            if (data.text().isBlank()) throw new IllegalArgumentException("A composição está vazia.");

            Music music = translator.textToMusic(
                file.getName(),
                data.text(),
                data.presets()
            );

            Sequence sequence = parser.convertMusicToMIDI(music);

            exporter.save(
                sequence,
                file.toPath().getParent(), //File folder path
                file.getName()
            );

        } catch (IOException | IllegalArgumentException e) {
            showErrorMessage("Falha ao salvar o arquivo MIDI: " + e.getMessage());
        }
    }

    private void importMusic() {
        File file = openFileChooserWindow();
        if (file == null) return;

        try {
            List<String> lines = reader.readLines(file.toPath());

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

    private record VoiceWidgetData (
        String text,
        List<VoicePreset> presets
    ) {}
    
    private VoiceWidgetData getVoiceWidgetData() {
        // TODO: Fazer ele pegar do mapeamento
        StringJoiner musicText = new StringJoiner("\n");
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

        return new VoiceWidgetData(musicText.toString(), voicePresets);
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
        // TODO: Fazer ele iterar por uma lista sem ter que passar o NUM_PRESET, alguem vai esquecer de atualizar isso
        int currentPreset = VoiceWidget.getCurrentVoiceNum() % NUM_OF_VOICE_PRESETS;
        
        VoiceWidget voiceWidget = switch (currentPreset) {
            case 0 -> new VoiceWidget(new VoicePresetKeysWidget());
            case 1 -> new VoiceWidget(new VoicePresetPluckedWidget());
            case 2 -> new VoiceWidget(new VoicePresetStringsWidget());
            case 3 -> new VoiceWidget(new VoicePresetWindsWidget());
            case 4 -> new VoiceWidget(new VoicePresetPercussionWidget());
            default -> new VoiceWidget(new VoicePresetKeysWidget());
        };

        return voiceWidget;
    }
}