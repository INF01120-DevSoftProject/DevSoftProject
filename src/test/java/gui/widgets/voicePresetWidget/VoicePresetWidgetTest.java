
package gui.widgets.voicePresetWidget;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import input.JsonInputReader;
import javafx.scene.layout.HBox;
import musicElements.InstrumentType;
import utils.MusicPathUtils;

class VoicePresetWidgetTest extends JavaFXTestBase {

    private static LinkedHashMap<String, Integer> instruments;

    @BeforeAll
    static void loadInstruments() throws IOException {
        instruments = new JsonInputReader<>(String.class, Integer.class).readFile(
            MusicPathUtils.getResourceFolderPath("instruments/instruments.json")
        );
        assertThat(instruments).isNotEmpty();
    }

    private static List<Map.Entry<String, Integer>> instrumentsFor(InstrumentType type) {
        return instruments.entrySet().stream()
            .filter(entry -> type.contains(entry.getValue()))
            .toList();
    }

    private static void assertInstrumentsMatchJson(VoicePresetWidget widget, InstrumentType type) {
        List<Map.Entry<String, Integer>> expected = instrumentsFor(type);
        assertThat(expected).isNotEmpty();
        assertThat(widget.getInstrumentType()).isEqualTo(type);
        assertThat(widget.instrumentSelector.getItems())
            .containsExactlyElementsOf(expected.stream().map(Map.Entry::getKey).toList());
        assertThat(widget.getDefaultInstrument()).isEqualTo(expected.getFirst().getKey());
        assertThat(widget.instrumentSelector.getValue()).isEqualTo(expected.getFirst().getKey());
        assertThat(widget.getInstrument().getInstrument()).isEqualTo(expected.getFirst().getValue());
    }

    @Test
    void keysPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetKeysWidget widget = new VoicePresetKeysWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertInstrumentsMatchJson(widget, InstrumentType.KEYS);
        assertThat(widget.getDefaultOctave()).isEqualTo("Terceira Oitava");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(66);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Terceira Oitava");
        assertThat(widget.getVolume().getVolume()).isEqualTo(66);

        assertThat(widget.getOctaveNames())
            .contains("Segunda Oitava", "Terceira Oitava", "Quarta Oitava");
    }

    @Test
    void percussionPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetPercussionWidget widget = new VoicePresetPercussionWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertInstrumentsMatchJson(widget, InstrumentType.PERCUSSION_AND_SYNTH);
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(82);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Segunda Oitava");
        assertThat(widget.getVolume().getVolume()).isEqualTo(82);
    }

    @Test
    void pluckedPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetPluckedWidget widget = new VoicePresetPluckedWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertInstrumentsMatchJson(widget, InstrumentType.PLUCKED);
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(72);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Segunda Oitava");
        assertThat(widget.getVolume().getVolume()).isEqualTo(72);
    }

    @Test
    void stringsPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetStringsWidget widget = new VoicePresetStringsWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertInstrumentsMatchJson(widget, InstrumentType.STRINGS);
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(83);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Segunda Oitava");
        assertThat(widget.getVolume().getVolume()).isEqualTo(83);
    }

    @Test
    void windsPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetWindsWidget widget = new VoicePresetWindsWidget();
        HBox container = widget.createVoicePresetContainer();

        assertThat(container).isNotNull();
        assertInstrumentsMatchJson(widget, InstrumentType.WINDS);
        assertThat(widget.getDefaultOctave()).isEqualTo("Segunda Oitava");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(83);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Segunda Oitava");
        assertThat(widget.getVolume().getVolume()).isEqualTo(83);
    }

    @Test
    void updatesValueWhenControlsChange() {
        VoicePresetKeysWidget widget = new VoicePresetKeysWidget();
        widget.createVoicePresetContainer();

        List<Map.Entry<String, Integer>> keys = instrumentsFor(InstrumentType.KEYS);
        assertThat(keys).hasSizeGreaterThan(1);
        Map.Entry<String, Integer> selectedInstrument = keys.get(1);
        widget.instrumentSelector.setValue(selectedInstrument.getKey());
        widget.octaveController.setValue("Quarta Oitava");
        widget.volumeController.setValue(90);

        assertThat(widget.getInstrument().getInstrument()).isEqualTo(selectedInstrument.getValue());
        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Quarta Oitava");
        assertThat(widget.getVolume().getVolume()).isEqualTo(90);
    }
}
