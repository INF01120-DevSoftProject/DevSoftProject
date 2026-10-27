package gui.screens;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.sound.midi.Sequence;
import exporters.MIDIFileExporter;
import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.ControlFactory;
import gui.nodes.VoiceNode;
import gui.nodes.voicePresetNodes.VoicePresetKeysNode;
import gui.nodes.voicePresetNodes.VoicePresetNode;
import gui.nodes.voicePresetNodes.VoicePresetPercussionNode;
import gui.nodes.voicePresetNodes.VoicePresetPluckedNode;
import gui.nodes.voicePresetNodes.VoicePresetStringsNode;
import gui.nodes.voicePresetNodes.VoicePresetWindsNode;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import music.Music;
import parsers.MusicToMIDIParser;
import translators.TextToMusic.TextToMusicTranslator;
import music.VoicePreset;

public class CompositionScreen extends Screen {

    public CompositionScreen(Stage stage) {
        setScreenNode(createScreen(stage)); 
    }

    private VBox voicesContainer;

    public Pane createScreen(Stage stage) {
        VoiceNode.restartCurrentVoiceNum();

        voicesContainer = new BoxNode<>(new VBox(20))
                                    .add(createVoiceWithVoicePresset())
                                    .build();

        ScrollPane voicesContainerScroll = ControlFactory.createScrollPane(voicesContainer);

        HBox plusMinusButtons = new BoxNode<>(new HBox(20))
                                    .addClass("plus-minus-container")
                                    .add(new ButtonNode("+")
                                            .onClickAddToContainer(() -> createVoiceWithVoicePresset(), voicesContainer, 15)
                                            .build())
                                    .add(new ButtonNode("-")
                                            .onClickRemoveLastFromContainer(voicesContainer)
                                            .onClick(e -> VoiceNode.decrementCurrentVoiceNum())
                                            .build())
                                    .build();

        HBox saveImportButtons = new BoxNode<>(new HBox(20))
                                    .addClass("save-import-container")
                                    .add(new ButtonNode("Salvar música")
                                            .onClick(b -> saveMIDI(stage))
                                            .build())
                                    .add(new ButtonNode("Importar música")
                                            .onClick(b -> importMusic(stage))                                            
                                            .build())
                                    .build();

        VBox mainContent = new BoxNode<>(new VBox(25))
                                    .setId("main")
                                    .add(new Label("Composição"))
                                    .add(voicesContainerScroll)
                                    .add(plusMinusButtons)
                                    .add(ControlFactory.createVerticalGrower())
                                    .add(saveImportButtons)
                                    .build();
        
        return mainContent;
    }

    public void saveMIDI(Stage stage) {
        StringJoiner musicText = new StringJoiner("\n");
        List<VoicePreset> voicePresets = new ArrayList<>();

        for (Node node : voicesContainer.getChildren()) {
            VoiceNode voiceNode = (VoiceNode) node;
            
            voicePresets.add(new VoicePreset(voiceNode.getInstrument(), voiceNode.getVolume(), voiceNode.getOctave()));

            String text = voiceNode.getVoiceText();

            if (text == null || text.isBlank()) {
                continue;
            }

            // Remove as quebras de linha
            text = text.replace("\r", "").replace("\n", "");

            musicText.add(text);
        }

        // Verifica se existe alguma música para salvar
        if (musicText.toString().isBlank()) {
            new Alert(
                Alert.AlertType.ERROR,
                "Não é possível salvar uma música vazia."
            ).showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar MIDI");
        fileChooser.setInitialFileName("musica.mid");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivo MIDI (*.mid)", "*.mid")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try {
            TextToMusicTranslator translator = new TextToMusicTranslator();
            MusicToMIDIParser parser = new MusicToMIDIParser();
            MIDIFileExporter exporter = new MIDIFileExporter(file.getParent());

            Music music = translator.textToMusic(
                "Default",
                musicText.toString(),
                voicePresets
            );

            Sequence sequence = parser.convertMusicToMIDI(music);

            exporter.save(sequence, file.getName());

        } catch (IOException e) {
            new Alert(
                Alert.AlertType.ERROR,
                "Falha ao salvar o arquivo MIDI: " + e.getMessage()
            ).showAndWait();
        }
    }
    private void importMusic(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar música");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivo TXT (*.txt)", "*.txt")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;

        try {
            List<String> lines = Files.readAllLines(
                file.toPath(),
                StandardCharsets.UTF_8
            );

            if (lines.size() > 15) {
                new Alert(
                    Alert.AlertType.WARNING,
                    "O arquivo possui mais de 15 vozes."
                ).showAndWait();
                return;
            }

            for (int i = 0; i < lines.size(); i++) {
                VoiceNode voiceNode;

                if (i < voicesContainer.getChildren().size()) {
                    voiceNode = (VoiceNode) voicesContainer.getChildren().get(i);
                } else {
                    voiceNode = createVoiceWithVoicePresset();
                    voicesContainer.getChildren().add(voiceNode);
                }

                voiceNode.setVoiceText(lines.get(i));
            }

        } catch (IOException e) {
            new Alert(
                Alert.AlertType.ERROR,
                "Erro ao importar música: " + e.getMessage()
            ).showAndWait();
        }
    }

    private VoiceNode createVoiceWithVoicePresset() {
        int currentPreset = (VoiceNode.getCurrentVoiceNum() + 1) % 5;
        
        VoiceNode preset = switch (currentPreset) {
            case 0 -> new VoiceNode(new VoicePresetKeysNode());
            case 1 -> new VoiceNode(new VoicePresetPluckedNode());
            case 2 -> new VoiceNode(new VoicePresetStringsNode());
            case 3 -> new VoiceNode(new VoicePresetWindsNode());
            case 4 -> new VoiceNode(new VoicePresetPercussionNode());
            default -> new VoiceNode(new VoicePresetKeysNode());
        };

        return preset;
    }


    public void playVoice() {
        // FAZER
    }



}