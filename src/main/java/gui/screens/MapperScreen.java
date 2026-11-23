package gui.screens;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import gui.builders.BorderPaneBuilder;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.widgets.MappingWidget;

import musicElements.Accidentals;
import musicElements.Notes;
import translators.TextToMusic.CharacterMapping;

public class MapperScreen extends Screen {

    // Nome de cada valor musical. O caractere de cada um vem do CharacterMapping (getNote/getAccidental),
    // então o rótulo continua certo depois de trocas e não depende da ordem dos caracteres no grupo.
    private static final Map<Notes, String> NOTE_NAMES = new EnumMap<>(Map.of(
        Notes.C, "Dó", Notes.D, "Ré", Notes.E, "Mi", Notes.F, "Fá",
        Notes.G, "Sol", Notes.A, "Lá", Notes.B, "Si"));

    private static final Map<Accidentals, String> ACCIDENTAL_NAMES = new EnumMap<>(Map.of(
        Accidentals.SHARP, "Sustenido", Accidentals.FLAT, "Bemol"));

    private final CharacterMapping mapping;

    private BorderPane mainContent;
    
    private FlowPane pauseMappingContainer;
    private FlowPane noteMappingContainer;
    private FlowPane accidentalMappingContainer;
    private FlowPane musicControlsMappingContainer;

    public MapperScreen(CharacterMapping mapping) {
        this.mapping = mapping;

        restoreMappingContainers();

        HBox mappingCategories = new BoxBuilder<>(new HBox(20))
								.addClass("fixed-button-container") // Concertar nome
								.add(new ButtonBuilder("Notas")
										.onClick(e -> showMapping(noteMappingContainer))
										.build())
								.add(new ButtonBuilder("Pausas")
										.onClick(e -> showMapping(pauseMappingContainer))
										.build())
								.add(new ButtonBuilder("Acidentes")
										.onClick(e -> showMapping(accidentalMappingContainer))
										.build())
								.add(new ButtonBuilder("Outros")
										.onClick(e -> showMapping(musicControlsMappingContainer))
										.build())
								.build();

        VBox mappingHeader = new BoxBuilder<>(new VBox(25))
								.add(new Label("Mapeamento"))
								.add(mappingCategories)
								.build();

        HBox saveResetButtons = new BoxBuilder<>(new HBox(20))
								.addClass("fill-button-container") // Concertar nome
								.add(new ButtonBuilder("Salvar Mapeamento")
										.onClick(e -> applyAllMappings())
                                        .build())
								.add(new ButtonBuilder("Restaurar ao Padrão")
                                        .onClick(e -> restoreDefaultMapping())
                                        .build())
								.build();

        mainContent = new BorderPaneBuilder()
								.setId("main")
								.apply(b -> b.setTop(mappingHeader))
								.apply(b -> b.setCenter(noteMappingContainer))
								.apply(b -> b.setBottom(saveResetButtons))
								.build();

        setScreenMainContent(mainContent);
    }

    private void showMapping(FlowPane container) {
        mainContent.setCenter(container);
    }

    private void restoreDefaultMapping() {
        if (!showWarningMessage("Deseja mesmo restaurar o mapeamento ao padrão? As alterações serão perdidas.")) {
            return;
        }

        restoreMappingContainers();
    }

    private void restoreMappingContainers() {
        mapping.reset();
        refreshMappingContainers();

        if (mainContent != null) {
            mainContent.setCenter(noteMappingContainer);
        }
    }

    // Recria os campos a partir do mapeamento atual, mantendo a aba que estava aberta
    private void refreshMappingContainers() {
        Node shown = mainContent == null ? null : mainContent.getCenter();
        boolean pauses = shown != null && shown == pauseMappingContainer;
        boolean accidentals = shown != null && shown == accidentalMappingContainer;
        boolean controls = shown != null && shown == musicControlsMappingContainer;

        noteMappingContainer = createNoteMappingContainer();
        pauseMappingContainer = createPauseMappingContainer();
        accidentalMappingContainer = createAccidentalMappingContainer();
        musicControlsMappingContainer = createMusicControlsMappingContainer();

        if (mainContent != null) {
            mainContent.setCenter(pauses ? pauseMappingContainer
                : accidentals ? accidentalMappingContainer
                : controls ? musicControlsMappingContainer
                : noteMappingContainer);
        }
    }

    private void applyAllMappings() {
        try {
            saveMappings();
        } catch(IllegalArgumentException e) {
            showErrorMessage("Erro ao salvar mapeamento: " + e.getMessage());
        }
    }

    // Aplica todos os campos e confere se nenhuma troca ficou pela metade
    // (ex.: Dó recebeu "D", mas Ré continuou com "D"). Assim o erro aparece ao salvar, não só ao tocar.
    // Se algo falhar, o salvamento inteiro é desfeito: o mapeamento e os campos voltam a como estavam.
    void saveMappings() {
        CharacterMapping before = mapping.copy();
        try {
            applyMappings(noteMappingContainer);
            applyMappings(pauseMappingContainer);
            applyMappings(musicControlsMappingContainer);
            applyMappings(accidentalMappingContainer);

            mapping.checkComplete();
        } catch (IllegalArgumentException e) {
            mapping.restore(before);
            refreshMappingContainers();
            throw e;
        }
    }

    private void applyMappings(FlowPane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof MappingWidget mappingNode) {
                mappingNode.saveChange();
            }
        }
    }

    private BoxBuilder<FlowPane> createMappingContainer() {
        BoxBuilder<FlowPane> noteMappingContainer = new BoxBuilder<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        return noteMappingContainer;
    }

    private void assingMappingWidgetToContainer(BoxBuilder<FlowPane> container, String type, String text) {
        for (String c : mapping.getMappingCharacters(type)) {
            container.add(new MappingWidget(mapping, type, text, c));
        }
    }

    // Um widget por caractere do grupo, com o rótulo do valor musical que o mapeamento dá a ele.
    // Os widgets ficam na ordem dos valores (Dó, Ré, ..., Si), não na ordem dos caracteres.
    private <T extends Enum<T>> void assingMappingWidgetToContainer(BoxBuilder<FlowPane> container, String type,
                                                                   Function<String, T> valueOf, Map<T, String> names) {
        List<String> characters = new ArrayList<>(mapping.getMappingCharacters(type));
        characters.sort(Comparator.comparing(valueOf));

        for (String c : characters) {
            container.add(new MappingWidget(mapping, type, names.get(valueOf.apply(c)), c));
        }
    }

    private FlowPane createNoteMappingContainer() {
        BoxBuilder<FlowPane> noteMappingContainer = createMappingContainer();
        assingMappingWidgetToContainer(noteMappingContainer, "note", mapping::getNote, NOTE_NAMES);

        return noteMappingContainer.build();
    }

    private FlowPane createPauseMappingContainer() {
        BoxBuilder<FlowPane> pauseMappingContainer = createMappingContainer();
        assingMappingWidgetToContainer(pauseMappingContainer, "pause",  "Pausa");

        return pauseMappingContainer.build();
    }

    private FlowPane createMusicControlsMappingContainer() {
        BoxBuilder<FlowPane> musicControlsMappingContainer = createMappingContainer();

        assingMappingWidgetToContainer(musicControlsMappingContainer, "octaveUp",  "Aumentar Oitava");
        assingMappingWidgetToContainer(musicControlsMappingContainer, "octaveDown",  "Diminuir Oitava");
        assingMappingWidgetToContainer(musicControlsMappingContainer, "bpmUp",  "Aumentar BPM");
        assingMappingWidgetToContainer(musicControlsMappingContainer, "bpmDown",  "Diminuir BPM");
        assingMappingWidgetToContainer(musicControlsMappingContainer, "doubleVolume",  "Dobrar Volume");

        return musicControlsMappingContainer.build();
    }

    private FlowPane createAccidentalMappingContainer() {
        BoxBuilder<FlowPane> accidentalMappingContainer = createMappingContainer();
        assingMappingWidgetToContainer(accidentalMappingContainer, "accidental", mapping::getAccidental, ACCIDENTAL_NAMES);

        return accidentalMappingContainer.build();
    }
}