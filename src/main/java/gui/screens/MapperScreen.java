package gui.screens;

import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.nodes.ControlFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MapperScreen extends Screen {

    public MapperScreen(Stage stage) {
        setScreenNode(createScreen(stage)); 
    }

    public Pane createScreen(Stage stage) {
        HBox mappingCategories = new BoxNode<>(new HBox(20))
                                    .addClass("plus-minus-container")
                                    .add(new ButtonNode("Notas")
                                            .build())
                                    .add(new ButtonNode("Pausas")
                                            .build())
                                    .add(new ButtonNode("Acidentes")
                                            .build())
                                    .add(new ButtonNode("Outros")
                                            .build())
                                    .build();

        HBox saveResetButtons = new BoxNode<>(new HBox(20))
                                    .addClass("save-import-container")
                                    .add(new ButtonNode("Salvar Mapeamento")
                                            .build())
                                    .add(new ButtonNode("Restaurar ao Padrão").build())
                                    .build();

        VBox mainContent = new BoxNode<>(new VBox(25))
                                    .setId("main")
                                    .add(new Label("Mapeamento"))
                                    .add(mappingCategories)
                                    .add(ControlFactory.createVerticalGrower())
                                    .add(saveResetButtons)
                                    .build();
        
        return mainContent;
    }

}