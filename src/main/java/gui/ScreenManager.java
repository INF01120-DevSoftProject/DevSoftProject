package gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import player.MidiPlayer;
import exporters.MIDIFileExporter;
import gui.builders.BorderPaneBuilder;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.screens.*;
import input.MidiMusicPathUtils;
import input.TXTInputReader;

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

        compositionScreen = new CompositionScreen(new TXTInputReader(), new MIDIFileExporter());
        mapperScreen = new MapperScreen();
        playerScreen = new PlayerScreen(stage, new MidiPlayer(), new MidiMusicPathUtils());

        VBox sideBar = new BoxBuilder<>(new VBox(10))
                            .addClass("sidebar")
                            .add(new ButtonBuilder("Composição")
                                    .onClick(e -> layout.setCenter(compositionScreen))
                                    .build())
                            .add(new ButtonBuilder("Mapeamento")
                                    .onClick(e -> layout.setCenter(mapperScreen))
                                    .build())
                            .add(new ButtonBuilder("Player")
                                    .onClick(e -> layout.setCenter(playerScreen))
                                    .build())
                            .build();

        layout = new BorderPaneBuilder()
                            .apply(b -> b.setLeft(sideBar))
                            .build();

        layout.setCenter(compositionScreen);

        Scene scene = createScene(layout);
        addAllCSS(scene);
    }

    private Scene createScene(Parent layout) { 
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.show();

        return scene;
    }

    private void addAllCSS(Scene scene) {
        // Base style
        addCSStoScene(scene, "/styles/base.css");

        // Screen style
        addCSStoScene(scene, "/styles/composition.css");
        addCSStoScene(scene, "/styles/mapper.css");
        addCSStoScene(scene, "/styles/player.css");

        // Components base style
        addCSStoScene(scene, "/styles/button.css");
        addCSStoScene(scene, "/styles/comboBox.css");
        addCSStoScene(scene, "/styles/textArea.css");
        addCSStoScene(scene, "/styles/scrollPane.css");
    }

    private void addCSStoScene(Scene scene, String path) {
        scene.getStylesheets().add(
            getClass().getResource(path).toExternalForm()
        );
    }
}
