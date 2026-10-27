package gui.nodes;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

public class VoiceNode extends BorderPane {

    private static int currentVoiceNum;

    private final TextArea inputBox;
    private final VoicePresetNode voicePresetNode;
        
    public VoiceNode(VoicePresetNode voicePresetNode) {
        this.voicePresetNode = voicePresetNode;
        currentVoiceNum++;


        HBox voicePresetContainer = voicePresetNode.createVoicePresetContainer();


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

    public int getInstrument() {
        return voicePresetNode.getInstrument();
    }

    public int getVolume() {
        return (int) Math.round(voicePresetNode.getVolume());
    }

    public int getOctave() {
        return voicePresetNode.getOctave();
    }

    public void setVoiceText(String text) {
        inputBox.setText(text);
    }

    public static int getCurrentVoiceNum() {
        return currentVoiceNum;
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


}
