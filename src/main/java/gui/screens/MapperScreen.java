package gui.screens;

import java.util.List;

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

import translators.TextToMusic.CharacterMapping;

public class MapperScreen extends Screen {

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
                                        .onClick(e -> restoreMappingContainers())
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

    private void restoreMappingContainers() {
        mapping.reset();

        noteMappingContainer = createNoteMappingContainer();
        pauseMappingContainer = createPauseMappingContainer();
        accidentalMappingContainer = createAccidentalMappingContainer();
        musicControlsMappingContainer = createMusicControlsMappingContainer();

        if (mainContent != null) {
            mainContent.setCenter(noteMappingContainer);
        }
    }

    private void applyAllMappings() {
        try {
            applyMappings(noteMappingContainer);
            applyMappings(pauseMappingContainer);
            applyMappings(musicControlsMappingContainer);
            applyMappings(accidentalMappingContainer);
            
        } catch(IllegalArgumentException e) {
            showErrorMessage("Erro ao salvar mapeamento: " + e.getMessage());
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

    private void assingMappingWidgetToContainer(BoxBuilder<FlowPane> container, String type, String[] texts) {
        List<String> characters = mapping.getMappingCharacters(type);

        if (characters.size() != texts.length) {
            throw new IllegalStateException("Quantidade de caracteres incompatível com o tipo de mapeamento: " + type);
        }

        for (int i = 0; i < texts.length; i++) {
            container.add(new MappingWidget(mapping, type, texts[i], characters.get(i)));
        }
    }

    private FlowPane createNoteMappingContainer() {
        String[] notes = {"Dó", "Ré", "Mi", "Fá", "Sol", "Lá", "Si"};

        BoxBuilder<FlowPane> noteMappingContainer = createMappingContainer();
        assingMappingWidgetToContainer(noteMappingContainer, "note", notes);

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
        String[] accidentals = {"Sustenido", "Bemol"};

        BoxBuilder<FlowPane> accidentalMappingContainer = createMappingContainer();
        assingMappingWidgetToContainer(accidentalMappingContainer, "accidental", accidentals);

        return accidentalMappingContainer.build();
    }
}