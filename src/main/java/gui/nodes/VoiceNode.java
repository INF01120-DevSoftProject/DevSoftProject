package gui.nodes;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.*;

public class VoiceNode extends BorderPane {

    private static int currentVoiceNum;
    
    public static List<String> getInstrumentTypes() {
        return List.of("Flauta", "Violão", "Piano");
    }

    public static List<String> getOctaveTypes() {
        return List.of("Oitava 1", "Oitava 2", "Oitava 3");
    }

    private final TextArea inputBox;
    private final ComboBox<String> instrumentController;
    private final Slider volumeController;
    private final ComboBox<String> octaveController;
        
    public VoiceNode() {
        currentVoiceNum++;

        instrumentController = ControlFactory.createComboBox("Insira seu instrumento", getInstrumentTypes());
        volumeController = ControlFactory.createSlider(0, 100, 50);
        octaveController = ControlFactory.createComboBox("Insira sua oitava", getOctaveTypes());

        VBox instrumentContainer = createControlWithLabel(instrumentController, "Instrumento");
        VBox volumeContainer = createControlWithLabel(volumeController, "Volume");
        VBox octaveContainer = createControlWithLabel(octaveController, "Oitava");

        HBox voicePresetContainer = new BoxNode<>(new HBox(15))
                                    .addClass("voice-preset-container")
                                    .add(instrumentContainer)
                                    .add(ControlFactory.createHorizontalGrower())
                                    .add(volumeContainer)
                                    .add(ControlFactory.createHorizontalGrower())
                                    .add(octaveContainer)
                                    .apply(b -> b.setAlignment(Pos.CENTER))
                                    .build();


        inputBox = ControlFactory.createTextArea(3, "Digite sua partitura");

        VBox voiceMain = new BoxNode<>(new VBox())
                                    .addClass("voice-main-container")
                                    .add(voicePresetContainer)
                                    .add(inputBox)
                                    .build();


        BorderPane voiceLabelContainer = new BorderNode()
                                    .addClass("voice-label-container")
                                    .apply(b -> b.setCenter(new Label("Voz " + currentVoiceNum)))
                                    .build();
        
        setLeft(voiceLabelContainer);
        setCenter(voiceMain);
        getStyleClass().add("voice-node");
    }

    public void setVoiceText(String text) {
        inputBox.setText(text);
    }

    private static VBox createControlWithLabel(Node control, String title) {
        return new BoxNode<>(new VBox(0))
            .add(new Label(title))
            .add(control)
            .build();
    }

    public static void restartCurrentVoiceNum() {
        currentVoiceNum = 0;
    }

    public static int decrementCurrentVoiceNum() {
        if (currentVoiceNum > 1) {
            currentVoiceNum -= 1;
        }

        return currentVoiceNum;
    }

    public String getVoiceText() {
        return inputBox.getText();
    }

    public double getVolume() {
        return volumeController.getValue();
    }

    public String getInstrument() {
        return instrumentController.getValue();
    }

    public String getOctave() {
        return octaveController.getValue();
    }
}
