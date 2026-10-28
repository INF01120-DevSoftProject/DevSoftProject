package gui.screens;

import java.util.List;

import gui.nodes.BorderNode;
import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.MappingNode;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import translators.TextToMusic.TextToMusicRules;
import javafx.scene.Node;

public class MapperScreen extends Screen {

    public MapperScreen(Stage stage) {
        setScreenNode(createScreen(stage)); 
    }
    
    private BorderPane mainContent;
    private FlowPane pauseMappingContainer;
    private FlowPane noteMappingContainer;
    private FlowPane accidentalMappingContainer;
    private FlowPane musicControlsMappingContainer;

    public Pane createScreen(Stage stage) {

        restoreMappingContainers();

        HBox mappingCategories = new BoxNode<>(new HBox(20))
								.addClass("plus-minus-container") // Concertar nome
								.add(new ButtonNode("Notas")
										.onClick(e -> mainContent.setCenter(noteMappingContainer))
										.build())
								.add(new ButtonNode("Pausas")
										.onClick(e -> mainContent.setCenter(pauseMappingContainer))
										.build())
								.add(new ButtonNode("Acidentes")
										.onClick(e -> mainContent.setCenter(accidentalMappingContainer))
										.build())
								.add(new ButtonNode("Outros")
										.onClick(e -> mainContent.setCenter(musicControlsMappingContainer))
										.build())
								.build();

        VBox mappingHeader = new BoxNode<>(new VBox(25))
								.add(new Label("Mapeamento"))
								.add(mappingCategories)
								.build();



        HBox saveResetButtons = new BoxNode<>(new HBox(20))
								.addClass("save-import-container") // Concertar nome
								.add(new ButtonNode("Salvar Mapeamento")
										.onClick(e -> applyAllMappings())
                                        .build())
								.add(new ButtonNode("Restaurar ao Padrão")
                                        .onClick(e -> restoreMappingContainers())
                                        .build())
								.build();


        mainContent = new BorderNode()
								.setId("main")
								.apply(b -> b.setTop(mappingHeader))
								.apply(b -> b.setCenter(noteMappingContainer))
								.apply(b -> b.setBottom(saveResetButtons))
								.build();


        
        return mainContent;
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
            if (node instanceof MappingNode mappingNode) {
                mappingNode.saveChange();
            }
        }
    }


    private FlowPane createNoteMappingContainer() {
        String[] notes = {"Dó", "Ré", "Mi", "Fá", "Sol", "Lá", "Si"};

        BoxNode<FlowPane> noteMappingContainer = new BoxNode<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        List<String> characters = TextToMusicRules.getMappingCharacters("note");

        for (int i = 0; i < notes.length; i++) {
            noteMappingContainer.add(
                new MappingNode("note", notes[i], characters.get(i))
            );
        }

        return noteMappingContainer.build();
    }

    private FlowPane createPauseMappingContainer() {
        BoxNode<FlowPane> pauseMappingContainer = new BoxNode<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        for (String c : TextToMusicRules.getMappingCharacters("pause")) {
            pauseMappingContainer.add(
                new MappingNode("pause", "Pausa", c)
            );
        }

        return pauseMappingContainer.build();
    }

    private FlowPane createMusicControlsMappingContainer() {
        BoxNode<FlowPane> musicControlsMappingContainer = new BoxNode<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        for (String c : TextToMusicRules.getMappingCharacters("octaveUp")) {
            musicControlsMappingContainer.add(
                new MappingNode("octaveUp", "Aumentar Oitava", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("octaveDown")) {
            musicControlsMappingContainer.add(
                new MappingNode("octaveDown", "Diminuir Oitava", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("bpmUp")) {
            musicControlsMappingContainer.add(
                new MappingNode("bpmUp", "Aumentar BPM", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("bpmDown")) {
            musicControlsMappingContainer.add(
                new MappingNode("bpmDown", "Diminuir BPM", c)
            );
        }

        for (String c : TextToMusicRules.getMappingCharacters("doubleVolume")) {
            musicControlsMappingContainer.add(
                new MappingNode("doubleVolume", "Dobrar Volume", c)
            );
        }

        return musicControlsMappingContainer.build();
    }

    private FlowPane createAccidentalMappingContainer() {
        String[] accidentals = {"Sustenido", "Bemol"};

        BoxNode<FlowPane> accidentalMappingContainer = new BoxNode<>(new FlowPane())
            .addClass("mapping-container")
            .apply(b -> b.setHgap(20))
            .apply(b -> b.setVgap(20));

        List<String> characters = TextToMusicRules.getMappingCharacters("accidental");

        for (int i = 0; i < accidentals.length; i++) {
            accidentalMappingContainer.add(
                new MappingNode("accidental", accidentals[i], characters.get(i))
            );
        }

        return accidentalMappingContainer.build();
    }
}