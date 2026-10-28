package gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import gui.nodes.BorderNode;
import gui.nodes.BoxNode;
import gui.nodes.ButtonNode;
import gui.screens.*;

public class ScreenManager extends Application {


    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT= 400;

    private CompositionScreen compositionScreen;
    private MapperScreen mapperScreen;
    private PlayerScreen playerScreen;

    private Stage stage;
    private BorderPane layout;

    @Override 
    public void start(Stage stage) {
        this.stage = stage;

        compositionScreen = new CompositionScreen(stage);
        mapperScreen = new MapperScreen(stage);
        playerScreen = new PlayerScreen(stage);

        VBox sideBar = new BoxNode<>(new VBox(10))
                            .addClass("sidebar")
                            .add(new ButtonNode("Composição")
                                    .onClick(e -> setScreen(compositionScreen))
                                    .build())
                            .add(new ButtonNode("Mapeamento")
                                    .onClick(e -> setScreen(mapperScreen))
                                    .build())
                            .add(new ButtonNode("Player")
                                    .onClick(e -> setScreen(playerScreen))
                                    .build())
                            .build();

        layout = new BorderNode()
                            .apply(b -> b.setLeft(sideBar))
                            .build();

        setScreen(compositionScreen);

        Scene scene = createScene(layout);
        addCSStoScene(scene, "/styles/style.css");
        addCSStoScene(scene, "/styles/comboBox.css");
        addCSStoScene(scene, "/styles/slider.css");
        addCSStoScene(scene, "/styles/textArea.css");
        addCSStoScene(scene, "/styles/scrollPane.css");
    }

    private void setScreen(Screen screen) {
        layout.setCenter(screen.getScreenNode());
    }

    private Scene createScene(Parent layout) { 
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.show();

        return scene;
    }

    private void addCSStoScene(Scene scene, String path) {
        scene.getStylesheets().add(
            getClass().getResource(path).toExternalForm()
        );
    }
}
