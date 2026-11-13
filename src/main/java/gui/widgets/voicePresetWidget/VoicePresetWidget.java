package gui.widgets.voicePresetWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gui.builders.BoxBuilder;
import gui.factory.ControlFactory;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;
import javafx.scene.Node;

public abstract class VoicePresetWidget {
    protected   ComboBox<String> instrumentSelector;
    protected ComboBox<String> octaveController;
    protected Slider volumeController;

    private static final int MAX_VOLUME = 100;
    private static final int MIN_VOLUME = 0;

    public HBox createVoicePresetContainer() {
        
        instrumentSelector = ControlFactory.createComboBox(
            "Insira seu instrumento",
            getInstrumentsNames()
        );

        volumeController = ControlFactory.createVolumeSlider(
            new VolumeElement(MIN_VOLUME), 
            new VolumeElement(MAX_VOLUME), 
            getDefaultVolume()
        );

        octaveController = ControlFactory.createComboBox(
            "Insira sua oitava", 
            getOctaveNames()
        );

        instrumentSelector.setValue(getDefaultInstrument());
        octaveController.setValue(getDefaultOctave());

        VBox instrumentContainer = createControlWithLabel(instrumentSelector, "Instrumento");
        VBox volumeContainer = createControlWithLabel(volumeController, "Volume");
        VBox octaveContainer = createControlWithLabel(octaveController, "Oitava");

        HBox voicePresetContainer = new BoxBuilder<>(new HBox(15))
                                    .addClass("voice-preset-container")
                                    .add(instrumentContainer)
                                    .add(ControlFactory.createHorizontalGrower())
                                    .add(volumeContainer)
                                    .add(ControlFactory.createHorizontalGrower())
                                    .add(octaveContainer)
                                    .apply(b -> b.setAlignment(Pos.CENTER))
                                    .build();

        return voicePresetContainer;
    }

    public VolumeElement getVolume() {
        return new VolumeElement(
            (int) Math.round(volumeController.getValue())
        );
    }

    public InstrumentElement getInstrument() {
        return createInstrumentTypes().get(instrumentSelector.getValue());
    }

    public Octave getOctave() {
        return createOctaveTypes().get(octaveController.getValue());
    }

    public List<String> getOctaveNames() {
        return List.copyOf(createOctaveTypes().keySet());
    }

    public String getDefaultOctave() {
        return new ArrayList<>(createOctaveTypes().keySet()).get(1);
    }

    public abstract VolumeElement getDefaultVolume();
    public abstract String getDefaultInstrument();
    public abstract InstrumentType getInstrumentType();

    protected abstract Map<String, InstrumentElement> createInstrumentTypes();
    protected abstract Map<String, Octave> createOctaveTypes();


    private  List<String> getInstrumentsNames() {
        return List.copyOf(createInstrumentTypes().keySet());
    }

    private static VBox createControlWithLabel(Node control, String title) {
        return new BoxBuilder<>(new VBox(0))
            .add(new Label(title))
            .add(control)
            .build();
    }
}
