package gui.screens;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parsers.MusicToMIDIParser;
import javafx.scene.Node;
import translators.TextToMusic.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.StringJoiner;

import javax.sound.midi.Sequence;

import exporters.MIDIFileExporter;
import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.ControlFactory;
import gui.nodes.VoiceNode;
import music.Music;

public class CompositionScreen extends Screen {

    public CompositionScreen(Stage stage) {
        setScreenNode(createScreen(stage)); 
    }

    private VBox voicesContainer;

    public Pane createScreen(Stage stage) {
        VoiceNode.restartCurrentVoiceNum();

        voicesContainer = new BoxNode<>(new VBox(20))
                                    .add(new VoiceNode())
                                    .build();

        HBox plusMinusButtons = new BoxNode<>(new HBox(20))
                                    .addClass("plus-minus-container")
                                    .add(new ButtonNode("+")
                                            .onClickAddToContainer(() -> new VoiceNode(), voicesContainer)
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
                                    .add(new ButtonNode("Importar música").build())
                                    .build();

        VBox mainContent = new BoxNode<>(new VBox(25))
                                    .setId("main")
                                    .add(new Label("Composição"))
                                    .add(voicesContainer)
                                    .add(plusMinusButtons)
                                    .add(ControlFactory.createVerticalGrower())
                                    .add(saveImportButtons)
                                    .build();
        
        return mainContent;
    }

    public void saveMIDI(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar MIDI");
        fileChooser.setInitialFileName("musica.mid");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivo MIDI (*.mid)", "*.mid")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        StringJoiner musicText = new StringJoiner("\n");
        for (Node node : voicesContainer.getChildren()) {
            VoiceNode voiceNode = (VoiceNode) node;
            musicText.add(voiceNode.getVoiceText());
        }

        TextToMusicTranslator translator = new TextToMusicTranslator();
        MusicToMIDIParser parser = new MusicToMIDIParser();
        MIDIFileExporter exporter = new MIDIFileExporter(file.getParent());

        Music music = translator.textToMusic("Nome legal", musicText.toString());
        Sequence sequence = parser.convertMusicToMIDI(music);

        try {
            exporter.save(sequence, file.getName());
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar o arquivo MIDI", e);
        }
    }


    public void playVoice() {
        // FAZER
    }



}