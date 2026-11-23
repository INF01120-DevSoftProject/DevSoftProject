package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import gui.JavaFXTestBase;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;

class VoiceWidgetTest extends JavaFXTestBase {

    @Test
    void displaysTheVoiceNumberProvidedByTheCaller() {
        VoicePresetWidget preset = new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), new Octave(5));
        VoiceWidget widget = new VoiceWidget(preset, 3);
        BorderPane labelContainer = (BorderPane) widget.getLeft();

        assertThat(((Label) labelContainer.getCenter()).getText()).isEqualTo("Voz 3");
    }

    @Test
    void setsAndGetsVoiceText() {
        VoiceWidget widget = new VoiceWidget(new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), new Octave(5)), 1);

        widget.setVoiceText("C D E F G");

        assertThat(widget.getVoiceText()).isEqualTo("C D E F G");
    }

    @Test
    void delegatesPresetValuesCorrectly() {
        VoicePresetWidget preset = new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), new Octave(5));
        VoiceWidget widget = new VoiceWidget(preset, 1);

        assertThat(((VBox) widget.getCenter()).getChildren().getFirst()).isSameAs(preset);

        assertThat(widget.getInstrument().getInstrument())
            .isEqualTo(preset.getInstrument().getInstrument());

        assertThat(widget.getInstrument().getType())
            .isEqualTo(preset.getInstrument().getType());

        assertThat(widget.getVolume().getVolume())
            .isEqualTo(preset.getVolume().getVolume());

        assertThat(widget.getOctave())
            .usingRecursiveComparison()
            .isEqualTo(preset.getOctave());
    }

    @Test
    void hasExpectedStyleClassAndLayout() {
        VoiceWidget widget = new VoiceWidget(new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), new Octave(5)), 1);

        assertThat(widget.getStyleClass()).contains("voice-node");
        assertThat(widget.getLeft()).isNotNull();
        assertThat(widget.getCenter()).isNotNull();
    }
}
