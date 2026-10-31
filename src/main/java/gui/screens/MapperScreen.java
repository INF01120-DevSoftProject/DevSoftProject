package gui.screens;

import java.util.List;

import gui.builders.BorderPaneBuilder;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.widgets.MappingWidget;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import translators.TextToMusic.TextToMusicRules;
import javafx.scene.Node;

public class MapperScreen extends Screen {

    private BorderPane mainContent;
    
    private FlowPane pauseMappingContainer;
    private FlowPane noteMappingContainer;
    private FlowPane accidentalMappingContainer;
    private FlowPane musicControlsMappingContainer;

    public MapperScreen() {

        restoreMappingContainers();

        HBox mappingCategories = new BoxBuilder<>(new HBox(20))
								.addClass("plus-minus-container") // Concertar nome
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
								.addClass("save-import-container") // Concertar nome
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
        TextToMusicRules.resetMappings();

        noteMappingContainer = createNoteMappingContainer();
        pauseMappingContainer = createPauseMappingContainer();
        accidentalMappingContainer = createAccidentalMappingContainer();
        musicControlsMappingContainer = createMusicControlsMappingContainer();

        if (mainContent != null) {
            mainContent.setCenter(noteMappingContainer);
        }
    }

    private void applyAllMappings() {
        applyMappings(noteMappingContainer);
        applyMappings(pauseMappingContainer);
        applyMappings(musicControlsMappingContainer);
        applyMappings(accidentalMappingContainer);
    }

    private void applyMappings(FlowPane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof MappingWidget mappingNode) {
                mappingNode.saveChange();
            }
        }
    }

    private FlowPane createNoteMappingContainer() {
        String[] notes = {"Dó", "Ré", "Mi", "Fá", "Sol", "Lá", "Si"};

        BoxBuilder<FlowPane> noteMappingContainer = new BoxBuilder<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        List<String> characters = TextToMusicRules.getMappingCharacters("note");

        for (int i = 0; i < notes.length; i++) {
            noteMappingContainer.add(
                new MappingWidget("note", notes[i], characters.get(i))
            );
        }

        return noteMappingContainer.build();
    }

    private FlowPane createPauseMappingContainer() {
        BoxBuilder<FlowPane> pauseMappingContainer = new BoxBuilder<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        for (String c : TextToMusicRules.getMappingCharacters("pause")) {
            pauseMappingContainer.add(
                new MappingWidget("pause", "Pausa", c)
            );
        }

        return pauseMappingContainer.build();
    }

    private FlowPane createMusicControlsMappingContainer() {
        BoxBuilder<FlowPane> musicControlsMappingContainer = new BoxBuilder<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        for (String c : TextToMusicRules.getMappingCharacters("octaveUp")) {
            musicControlsMappingContainer.add(
                new MappingWidget("octaveUp", "Aumentar Oitava", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("octaveDown")) {
            musicControlsMappingContainer.add(
                new MappingWidget("octaveDown", "Diminuir Oitava", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("bpmUp")) {
            musicControlsMappingContainer.add(
                new MappingWidget("bpmUp", "Aumentar BPM", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("bpmDown")) {
            musicControlsMappingContainer.add(
                new MappingWidget("bpmDown", "Diminuir BPM", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("doubleVolume")) {
            musicControlsMappingContainer.add(
                new MappingWidget("doubleVolume", "Dobrar Volume", c)
            );
        }

        return musicControlsMappingContainer.build();
    }

    private FlowPane createAccidentalMappingContainer() {
        String[] accidentals = {"Sustenido", "Bemol"};

        BoxBuilder<FlowPane> accidentalMappingContainer = new BoxBuilder<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        List<String> characters = TextToMusicRules.getMappingCharacters("accidental");

        for (int i = 0; i < accidentals.length; i++) {
            accidentalMappingContainer.add(
                new MappingWidget("accidental", accidentals[i], characters.get(i))
            );
        }

        return accidentalMappingContainer.build();
    }
}