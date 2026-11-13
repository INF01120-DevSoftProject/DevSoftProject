package gui.widgets.voicePresetWidget;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import javafx.scene.layout.HBox;

class VoicePresetWidgetTest extends JavaFXTestBase {

    @Test
    void keysPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetKeysWidget widget = new VoicePresetKeysWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertThat(widget.getDefaultInstrument()).isEqualTo("Piano");
        assertThat(widget.getDefaultOctave()).isEqualTo("Terceira Oitava");
        assertThat(widget.getDefaultVolume()).isEqualTo(66);

        assertThat(widget.getInstrument()).isEqualTo(0);
        assertThat(widget.getOctave()).isEqualTo(3);
        assertThat(widget.getVolume()).isEqualTo(66.0);
        assertThat(widget.getOctaveNames()).contains("Segunda Oitava", "Terceira Oitava", "Quarta Oitava");
    }

    @Test
    void percussionPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetPercussionWidget widget = new VoicePresetPercussionWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertThat(widget.getDefaultInstrument()).isEqualTo("Lead 1 (Onda Quadrada)");
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume()).isEqualTo(82);

        assertThat(widget.getInstrument()).isEqualTo(80);
        assertThat(widget.getOctave()).isEqualTo(2);
        assertThat(widget.getVolume()).isEqualTo(82.0);
    }

    @Test
    void pluckedPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetPluckedWidget widget = new VoicePresetPluckedWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertThat(widget.getDefaultInstrument()).contains("Nylon");
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume()).isEqualTo(72);

        assertThat(widget.getInstrument()).isEqualTo(24);
        assertThat(widget.getOctave()).isEqualTo(2);
        assertThat(widget.getVolume()).isEqualTo(72.0);
    }

    @Test
    void stringsPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetStringsWidget widget = new VoicePresetStringsWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertThat(widget.getDefaultInstrument()).isEqualTo("Violino");
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume()).isEqualTo(83);

        assertThat(widget.getInstrument()).isEqualTo(40);
        assertThat(widget.getOctave()).isEqualTo(2);
        assertThat(widget.getVolume()).isEqualTo(83.0);
    }

    @Test
    void windsPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetWindsWidget widget = new VoicePresetWindsWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertThat(widget.getDefaultInstrument()).isEqualTo("Trompete");
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume()).isEqualTo(83);

        assertThat(widget.getInstrument()).isEqualTo(56);
        assertThat(widget.getOctave()).isEqualTo(2);
        assertThat(widget.getVolume()).isEqualTo(83.0);
    }

    @Test
    void updatesValueWhenControlsChange() {
        VoicePresetKeysWidget widget = new VoicePresetKeysWidget();
        widget.createVoicePresetContainer();

        widget.instrumentController.setValue("Cravo");
        widget.octaveController.setValue("Quarta Oitava");
        widget.volumeController.setValue(90);

        assertThat(widget.getInstrument()).isEqualTo(6);
        assertThat(widget.getOctave()).isEqualTo(4);
        assertThat(widget.getVolume()).isEqualTo(90.0);
    }
}
