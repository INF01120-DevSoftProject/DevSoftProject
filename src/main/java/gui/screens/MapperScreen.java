package gui.screens;

import gui.nodes.BorderNode;
import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.VoiceNode;
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

public class MapperScreen extends Screen {

    public MapperScreen(Stage stage) {
        setScreenNode(createScreen(stage)); 
    }
    
    private BorderPane mainContent;
    private FlowPane pauseMappingContainer;

    public Pane createScreen(Stage stage) {

        FlowPane noteMappingContainer = new BoxNode<>(new FlowPane())
                                .addClass("mapping-container")
                                .apply(b -> b.setHgap(20))
                                .apply(b -> b.setVgap(20))
                                .add(createMappingNode("Note A"))
                                .add(createMappingNode("Note B"))
                                .add(createMappingNode("Note C"))
                                .build();

        pauseMappingContainer = new BoxNode<>(new FlowPane())
                                .addClass("mapping-container")
                                .add(createMappingNode("Pausa"))
                                .apply(b -> b.setHgap(20))
                                .apply(b -> b.setVgap(20))
                                .build();

        HBox plusMinusButtons = new BoxNode<>(new HBox(20))
								.addClass("plus-minus-container")
								.add(new ButtonNode("+")
										.onClickAddToContainer(() -> createMappingNode("Pausa"), pauseMappingContainer)
										.build())
								.add(new ButtonNode("-")
										.onClickRemoveLastFromContainer(pauseMappingContainer)
										.onClick(e -> VoiceNode.decrementCurrentVoiceNum())
										.build())
								.build();

        VBox pauseMappingMain = new BoxNode<>(new VBox(25))
								.add(pauseMappingContainer)
								.add(plusMinusButtons)
								.build();


        HBox mappingCategories = new BoxNode<>(new HBox(20))
								.addClass("plus-minus-container") // Concertar nome
								.add(new ButtonNode("Notas")
										.onClick(e -> mainContent.setCenter(noteMappingContainer))
										.build())
								.add(new ButtonNode("Pausas")
										.onClick(e -> mainContent.setCenter(pauseMappingMain))
										.build())
								.add(new ButtonNode("Acidentes")
										.build())
								.add(new ButtonNode("Outros")
										.build())
								.build();

        VBox mappingHeader = new BoxNode<>(new VBox(25))
								.add(new Label("Mapeamento"))
								.add(mappingCategories)
								.build();



        HBox saveResetButtons = new BoxNode<>(new HBox(20))
								.addClass("save-import-container") // Concertar nome
								.add(new ButtonNode("Salvar Mapeamento")
										.build())
								.add(new ButtonNode("Restaurar ao Padrão").build())
								.build();


        mainContent = new BorderNode()
								.setId("main")
								.apply(b -> b.setTop(mappingHeader))
								.apply(b -> b.setCenter(noteMappingContainer))
								.apply(b -> b.setBottom(saveResetButtons))
								.build();


        
        return mainContent;
    }

    private VBox createMappingNode(String text) {

        VBox mappingNode = new BoxNode<>(new VBox(10))
                                .addClass("mapping-node")
                                .add(new Label(text))
                                .add(createOneCaracterTextField())
                                .build();
        mappingNode.setMaxHeight(Region.USE_PREF_SIZE);

        return mappingNode;
    }


    private TextField createOneCaracterTextField() {
        TextField field = new TextField();

        field.setPrefColumnCount(1);
        field.setMaxWidth(45);

        field.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 1
                        ? change
                        : null
        ));


        return field;
    }

}