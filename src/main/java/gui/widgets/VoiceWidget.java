package gui.widgets;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import musicElements.InstrumentElement;
import musicElements.Octave;
import musicElements.VolumeElement;
import gui.builders.BorderPaneBuilder;
import gui.builders.BoxBuilder;
import gui.factory.ControlFactory;
import gui.widgets.voicePresetWidget.VoicePresetWidget;
import javafx.scene.control.*;

public class VoiceWidget extends BorderPane {

    private static int currentVoiceNum;

    private final TextArea inputBox;
    private final VoicePresetWidget voicePresetWidget;
        
    public VoiceWidget(VoicePresetWidget voicePresetNode) {
        this.voicePresetWidget = voicePresetNode;
        currentVoiceNum++;


        HBox voicePresetContainer = voicePresetNode.createVoicePresetContainer();


        inputBox = ControlFactory.createTextArea(3, "Digite sua partitura");

        VBox voiceMain = new BoxBuilder<>(new VBox())
                                    .addClass("voice-main-container")
                                    .add(voicePresetContainer)
                                    .add(inputBox)
                                    .build();


        BorderPane voiceLabelContainer = new BorderPaneBuilder()
                                    .addClass("voice-label-container")
                                    .apply(b -> b.setCenter(new Label("Voz " + currentVoiceNum)))
                                    .build();
        
        setLeft(voiceLabelContainer);
        setCenter(voiceMain);
        getStyleClass().add("voice-node");
    }

    public InstrumentElement getInstrument() {
        return voicePresetWidget.getInstrument();
    }

    public VolumeElement getVolume() {
        return voicePresetWidget.getVolume();
    }

    public Octave getOctave() {
        return voicePresetWidget.getOctave();
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
