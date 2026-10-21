package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parsers.MusicToMIDIParser;
import javafx.scene.Node;
import javafx.scene.Parent;
import translators.TextToMusic.*;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import music.Music;

public class CompositionScene extends Application {

    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT= 400;

    private VBox voicesContainer;

    @Override 
    public void start(Stage stage) {
        voicesContainer = new BoxNode<>(new VBox(20))
                                    .add(new VoiceNode())
                                    .build();

        HBox plusMinusButtons = new BoxNode<>(new HBox(20))
                                    .add(new ButtonNode("+")
                                            .onClickAddToContainer(() -> new VoiceNode(), voicesContainer)
                                            .build())
                                    .add(new ButtonNode("-")
                                            .onClickRemoveLastFromContainer(voicesContainer)
                                            .onClick(e -> VoiceNode.decrementCurrentVoiceNum())
                                            .build())
                                    .build();

        HBox saveImportButtons = new BoxNode<>(new HBox(20))
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
                
        VBox sideBar = new BoxNode<>(new VBox(10))
                                    .addClass("sidebar")
                                    .add(new ButtonNode("Composição").build())
                                    .add(new ButtonNode("Mapeamento").build())
                                    .add(new ButtonNode("Player").build())
                                    .build();

        BorderPane layout = new BorderNode()
                                    .apply(b -> b.setLeft(sideBar))
                                    .apply(b -> b.setCenter(mainContent))
                                    .build();

        Scene scene = createScene(stage, layout);
        addCSStoScene(scene, "/styles/style.css");
    }

    // FEITO SO PARA TESTE, NÃO DEVERIA ESTAR AQUI, NÃO DEVERIA SER TÃO FEIO
    public void saveMIDI(Stage stage) {
        String musicText = ""; 

        for (Node node : voicesContainer.getChildren()) {
            VoiceNode voiceNode = (VoiceNode) node;
            String voiceText = voiceNode.getVoiceText();
            musicText += voiceText + "\n";
        }

        TextToMusicTranslator translator = new TextToMusicTranslator();
        MusicToMIDIParser parser = new MusicToMIDIParser();

        Music music = translator.textToMusic("Nome legal", musicText);
        Sequence sequence = parser.convertMusicToMIDI(music);

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Salvar MIDI");
        fileChooser.setInitialFileName("musica.mid");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivo MIDI (*.mid)", "*.mid")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (sequence == null) throw new NullPointerException("file name can't be null");
        try {
            MidiSystem.write(sequence, 1, file);
        } catch (IOException e) {
            // TODO: handle exception
        }
    }


    public void playVoice() {
        // FAZER
    }

    private Scene createScene(Stage stage, Parent layout) { //PASSAR PARA UM CLASSE ABSTRATA ISSO
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.show();

        return scene;
    }

    private void addCSStoScene(Scene scene, String path) { //PASSAR PARA UM CLASSE ABSTRATA ISSO
        scene.getStylesheets().add(
            getClass().getResource(path).toExternalForm()
        );
    }


}