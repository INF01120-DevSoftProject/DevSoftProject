package gui.screens;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.sound.midi.Sequence;
import exporters.MIDIFileExporter;
import gui.ControlFactory;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.widgets.VoiceWidget;
import gui.widgets.voicePresetWidget.*;
import input.TXTInputReader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import music.Music;
import parsers.MusicToMIDIParser;
import translators.TextToMusic.TextToMusicTranslator;
import music.VoicePreset;

public class CompositionScreen extends Screen {

    private static final int MAX_VOICES = 15;
    private static final int NUM_OF_VOICE_PRESETS = 5;

    private final VBox voicesContainer;
    private final TextToMusicTranslator translator;
    private final MusicToMIDIParser parser;
    private final MIDIFileExporter exporter;
    private final TXTInputReader reader;

    public CompositionScreen() {
        translator = new TextToMusicTranslator();
        parser = new MusicToMIDIParser();
        exporter = new MIDIFileExporter();
        reader = new TXTInputReader();

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
        File file = openFileImportWindow();
        if (file == null) return;

        Music music = translator.textToMusic(file.getName(), getTextFromAllVoicesWidgets(), getVoicesPresets());
        Sequence sequence = parser.convertMusicToMIDI(music);

        try {
            exporter.save(sequence, file.toPath().getParent(), file.getName());
        } catch (IOException e) {
            createErrorMessage("Falha ao salvar o arquivo MIDI: " + e.getMessage());
        }
    }

    private void importMusic() {
        File file = openFileImportWindow();
        if (file == null) return;

        try {
            List<String> lines = reader.readLines(file.toPath());

            if (lines.stream().allMatch(String::isBlank)) {
                throw new IllegalArgumentException("O arquivo está vazio.");
            }

            if (lines.size() > MAX_VOICES) {
                throw new IllegalArgumentException("O arquivo possui mais de " + MAX_VOICES + " vozes.");
            }

            updateVoices(lines);

        } catch (IOException e) {
            createErrorMessage("Erro ao importar música: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            createErrorMessage(e.getMessage());
        }
    }

    private String getTextFromAllVoicesWidgets() {
        StringJoiner musicText = new StringJoiner("\n");

        for (Node node : voicesContainer.getChildren()) {
            VoiceWidget voiceNode = (VoiceWidget) node;
            String voiceWidgetText = voiceNode.getVoiceText();

            if (voiceWidgetText == null || voiceWidgetText.isBlank()) {
                continue;
            }

            musicText.add(voiceWidgetText.replace("\r", "").replace("\n", ""));
        }

        return musicText.toString();
    }

    private List<VoicePreset> getVoicesPresets() {
        List<VoicePreset> voicePresets = new ArrayList<>();

        for (Node node : voicesContainer.getChildren()) {
            VoiceWidget voiceNode = (VoiceWidget) node;
            
            voicePresets.add(new VoicePreset(voiceNode.getInstrument(), voiceNode.getVolume(), voiceNode.getOctave()));
        }

        return voicePresets;
    }

    private void updateVoices(List<String> lines) {
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