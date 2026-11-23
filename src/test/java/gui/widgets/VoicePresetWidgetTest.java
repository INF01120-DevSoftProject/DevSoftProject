
package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import input.JsonInputReader;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), new Octave(5));

        assertThat(widget.getStyleClass()).contains("voice-preset-container");
        assertThat(widget.getChildren()).hasSize(5);
        assertInstrumentsMatchJson(widget, InstrumentType.KEYS);
        assertThat(widget.getDefaultOctave()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(66);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getVolume().getVolume()).isEqualTo(66);

        assertThat(widget.getOctaveNames())
            .contains("Oitava 4 - Central", "Oitava 5 - Soprano", "Oitava 3 - Tenor", "Oitava 2 - Grave");
    }

    @Test
    void percussionPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(82), InstrumentType.PERCUSSION_AND_SYNTH, new Octave(2), new Octave(5));

        assertThat(widget.getStyleClass()).contains("voice-preset-container");
        assertThat(widget.getChildren()).hasSize(5);
        assertInstrumentsMatchJson(widget, InstrumentType.PERCUSSION_AND_SYNTH);
        assertThat(widget.getDefaultOctave()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(82);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getVolume().getVolume()).isEqualTo(82);
    }

    @Test
    void pluckedPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(72), InstrumentType.PLUCKED, new Octave(2), new Octave(5));

        assertThat(widget.getStyleClass()).contains("voice-preset-container");
        assertThat(widget.getChildren()).hasSize(5);
        assertInstrumentsMatchJson(widget, InstrumentType.PLUCKED);
        assertThat(widget.getDefaultOctave()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(72);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getVolume().getVolume()).isEqualTo(72);
    }

    @Test
    void stringsPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(83), InstrumentType.STRINGS, new Octave(2), new Octave(5));

        assertThat(widget.getStyleClass()).contains("voice-preset-container");
        assertThat(widget.getChildren()).hasSize(5);
        assertInstrumentsMatchJson(widget, InstrumentType.STRINGS);
        assertThat(widget.getDefaultOctave()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(83);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getVolume().getVolume()).isEqualTo(83);
    }

    @Test
    void windsPresetProvidesExpectedDefaultsAndBuildsContainer() {
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(83), InstrumentType.WINDS, new Octave(2), new Octave(5));

        assertThat(widget.getStyleClass()).contains("voice-preset-container");
        assertThat(widget.getChildren()).hasSize(5);
        assertInstrumentsMatchJson(widget, InstrumentType.WINDS);
        assertThat(widget.getDefaultOctave()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getDefaultVolume().getVolume()).isEqualTo(83);

        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Oitava 5 - Soprano");
        assertThat(widget.getVolume().getVolume()).isEqualTo(83);
    }

    @Test
    void updatesValueWhenControlsChange() {
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), new Octave(5));

        List<Map.Entry<String, Integer>> keys = instrumentsFor(InstrumentType.KEYS);
        assertThat(keys).hasSizeGreaterThan(1);
        Map.Entry<String, Integer> selectedInstrument = keys.get(1);
        widget.instrumentSelector.setValue(selectedInstrument.getKey());
        widget.octaveController.setValue("Oitava 4 - Central");
        widget.volumeController.setValue(90);

        assertThat(widget.getInstrument().getInstrument()).isEqualTo(selectedInstrument.getValue());
        assertThat(widget.getOctave()).isNotNull();
        assertThat(widget.octaveController.getValue()).isEqualTo("Oitava 4 - Central");
        assertThat(widget.getVolume().getVolume()).isEqualTo(90);
    }

    @ParameterizedTest
    @CsvSource({"0, 8, 5", "3, 4, 4", "6, 8, 6", "0, 0, 0"})
    void supportsInclusiveOctaveRangesAndKeepsDefaultWithinRange(int min, int max, int defaultOctave) {
        VoicePresetWidget widget = new VoicePresetWidget(new VolumeElement(50), InstrumentType.STRINGS, new Octave(min), new Octave(max));

        assertThat(widget.octaveController.getItems()).hasSize(max - min + 1);
        assertThat(widget.getOctave().getOctaveInt()).isEqualTo(defaultOctave);
        assertThat(widget.getVolume().getVolume()).isEqualTo(50);
        for (int octave = min; octave <= max; octave++) {
            widget.octaveController.getSelectionModel().select(octave - min);
            assertThat(widget.getOctave().getOctaveInt()).isEqualTo(octave);
        }
    }

    @Test
    void rejectsReversedOctaveRange() {
        assertThatThrownBy(() -> new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(5), new Octave(2)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Intervalo de oitavas inválido.");
    }

    @Test
    void rejectsNullConfiguration() {
        assertThatThrownBy(() -> new VoicePresetWidget(null, InstrumentType.KEYS, new Octave(2), new Octave(5)))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("Volume padrão obrigatório.");
        assertThatThrownBy(() -> new VoicePresetWidget(new VolumeElement(66), null, new Octave(2), new Octave(5)))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("Tipo de instrumento obrigatório.");
        assertThatThrownBy(() -> new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, null, new Octave(5)))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("Oitava mínima obrigatória.");
        assertThatThrownBy(() -> new VoicePresetWidget(new VolumeElement(66), InstrumentType.KEYS, new Octave(2), null))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("Oitava máxima obrigatória.");
    }
}
