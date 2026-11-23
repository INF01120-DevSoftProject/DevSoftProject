package gui.widgets;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gui.builders.BoxBuilder;
import gui.factory.ControlFactory;
import input.JsonInputReader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;
import utils.MusicPathUtils;
import javafx.scene.Node;

public class VoicePresetWidget extends HBox {
    protected ComboBox<String> instrumentSelector;
    protected ComboBox<String> octaveController;
    protected Slider volumeController;

    private static final int MAX_VOLUME = 100;
    private static final int MIN_VOLUME = 0;
    private static final int DEFAULT_OCTAVE = 5;

    private final VolumeElement defaultVolume;
    private final InstrumentType instrumentType;
    private final Octave minOctave;
    private final Octave maxOctave;

    public VoicePresetWidget(VolumeElement defaultVolume,
                            InstrumentType instrumentType,
                            Octave minOctave,
                            Octave maxOctave) {
        super(15);

        if (defaultVolume == null) throw new IllegalArgumentException("Volume padrão obrigatório.");
        if (instrumentType == null) throw new IllegalArgumentException("Tipo de instrumento obrigatório.");
        if (minOctave == null) throw new IllegalArgumentException("Oitava mínima obrigatória.");
        if (maxOctave == null) throw new IllegalArgumentException("Oitava máxima obrigatória.");

        if (minOctave.getOctaveInt() > maxOctave.getOctaveInt()) {
            throw new IllegalArgumentException("Intervalo de oitavas inválido.");
        }

        this.defaultVolume = defaultVolume;
        this.instrumentType = instrumentType;
        this.minOctave = minOctave;
        this.maxOctave = maxOctave;

        createVoicePresetContainer();
    }

    private void createVoicePresetContainer() {

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

        new BoxBuilder<>(this)
                                    .addClass("voice-preset-container")
                                    .add(instrumentContainer)
                                    .add(ControlFactory.createHorizontalGrower())
                                    .add(volumeContainer)
                                    .add(ControlFactory.createHorizontalGrower())
                                    .add(octaveContainer)
                                    .build();

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
        return getOctaveName(Math.max(minOctave.getOctaveInt(), Math.min(DEFAULT_OCTAVE, maxOctave.getOctaveInt())));
    }

    public String getDefaultInstrument() {
        return createInstrumentTypes().keySet().iterator().next();
    }

    public VolumeElement getDefaultVolume() {
        return defaultVolume;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    private Map<String, Octave> createOctaveTypes() {
        Map<String, Octave> octaves = new LinkedHashMap<>();
        int minimum = minOctave.getOctaveInt();
        int maximum = maxOctave.getOctaveInt();

        for (int number = minimum; number <= maximum; number++) {
            String name = getOctaveName(number);
            Octave octave = new Octave(number);

            octaves.put(name, octave);
        }

        return octaves;
    }

    private static String getOctaveName(int octave) {
        return switch (octave) {
            case 2 -> "Oitava 2 - Grave";
            case 3 -> "Oitava 3 - Tenor";
            case 4 -> "Oitava 4 - Central";
            case 5 -> "Oitava 5 - Soprano";
            default -> "Oitava " + octave;
        };
    }

    private Map<String, InstrumentElement> createInstrumentTypes() {
        Map<String, InstrumentElement> instruments = new LinkedHashMap<>();

        JsonInputReader<String, Integer> reader = new JsonInputReader<>(String.class, Integer.class);

        try {
            LinkedHashMap<String, Integer> data = reader.readFile(
                MusicPathUtils.getResourceFolderPath("instruments/instruments.json")
            );

            Map<String, Integer> filtered = filterInstrumentByType(
                data,
                getInstrumentType()
            );

            for (Map.Entry<String, Integer> entry : filtered.entrySet()) {
                instruments.put(
                    entry.getKey(),
                    new InstrumentElement(entry.getValue(), getInstrumentType())
                );
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao carregar instrumentos.", e);
        }

        return instruments;
    }

    private <K> LinkedHashMap<K, Integer> filterInstrumentByType(Map<K, Integer> data, InstrumentType instrumentType) {
        int inicio = instrumentType.getMinInstrument();
        int fim = instrumentType.getMaxInstrument();

        if (inicio > fim) {
            throw new IllegalArgumentException("Intervalo inválido.");
        }

        LinkedHashMap<K, Integer> result = new LinkedHashMap<>();

        for (Map.Entry<K, Integer> entry : data.entrySet()) {
            int valor = entry.getValue();

            if (valor >= inicio && valor <= fim) {
                result.put(entry.getKey(), valor);
            }
        }

        return result;
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
