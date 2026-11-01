package gui.widgets.voicePresetWidget;

import java.util.List;
import java.util.Map;

import gui.ControlFactory;
import gui.builders.BoxBuilder;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public abstract class VoicePresetWidget {
    protected  ComboBox<String> instrumentController;
    protected ComboBox<String> octaveController;
    protected Slider volumeController;

    public HBox createVoicePresetContainer() {
        instrumentController = ControlFactory.createComboBox("Insira seu instrumento", getInstrumentsNames());
        volumeController = ControlFactory.createSlider(0, 100, getDefaultVolume());
        octaveController = ControlFactory.createComboBox("Insira sua oitava", getOctaveNames());

        instrumentController.setValue(getDefaultInstrument());
        octaveController.setValue(getDefaultOctave());

        VBox instrumentContainer = createControlWithLabel(instrumentController, "Instrumento");
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

    protected abstract Map<String, Integer> createInstrumentTypes();
    protected abstract Map<String, Integer> createOctaveTypes();

    public abstract String getDefaultOctave();
    public abstract int getDefaultVolume();
    public abstract String getDefaultInstrument();

    public List<String> getOctaveNames() {
        return List.copyOf(createOctaveTypes().keySet());
    }

    public double getVolume() {
        return volumeController.getValue();
    }

    public int getInstrument() {
        return createInstrumentTypes().get(instrumentController.getValue());
    }

    public int getOctave() {
        return createOctaveTypes().get(octaveController.getValue());
    }

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
