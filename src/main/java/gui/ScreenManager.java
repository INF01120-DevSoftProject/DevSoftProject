package gui;

import javax.sound.midi.Sequence;

import java.util.Map;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parsers.MusicToMidiParser;
import player.MidiPlayer;
import translators.TextToMusic.CharacterMapping;
import translators.TextToMusic.TextToMusicTranslator;
import utils.MidiMusicPathUtils;
import gui.builders.BorderPaneBuilder;
import gui.builders.BoxBuilder;
import gui.builders.ButtonBuilder;
import gui.screens.*;
import input.TXTInputReader;
import exporters.MusicExportPipeline;
import exporters.MidiFileExporter;
import exporters.TempMidiDraftService;

public class ScreenManager extends Application {
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

    // Tamanho com que a janela abre e o menor tamanho permitido, ambos limitados à tela do usuário
    private static final double SCENE_WIDTH = 1280;
    private static final double SCENE_HEIGHT = 800;
    private static final double MIN_WINDOW_WIDTH = 1100;
    private static final double MIN_WINDOW_HEIGHT = 700;

    private CompositionScreen compositionScreen;
    private MapperScreen mapperScreen;
    private PlayerScreen playerScreen;

    private BorderPane layout;

    @Override 
    public void start(Stage stage) {

        // o mesmo mapeamento é editado na MapperScreen e lido pelo tradutor
        CharacterMapping mapping = new CharacterMapping();

        MusicExportPipeline<Sequence> exportPipeline = new MusicExportPipeline<>(
            new TextToMusicTranslator(mapping),
            new MusicToMidiParser(),
            new MidiFileExporter()
        );
        TempMidiDraftService draftService = new TempMidiDraftService(exportPipeline);

        compositionScreen = new CompositionScreen(
            new TXTInputReader(),
            exportPipeline,
            draftService,
            draftPath -> {
                playerScreen.loadDraft(draftPath);
                layout.setCenter(playerScreen);
            },
            filePath -> {
                playerScreen.onMusicSaved(filePath);
            },
            mapping
        );

        mapperScreen = new MapperScreen(mapping);
        
        playerScreen = new PlayerScreen(
            stage,
            new MidiPlayer(),
            new MidiMusicPathUtils(),
            draftService
        );

        Button compositionButton = new ButtonBuilder("Composição")
                                    .onClick(e -> {
                                        compositionScreen.refreshMappingHelp();
                                        layout.setCenter(compositionScreen);
                                    })
                                    .build();
        Button mapperButton = new ButtonBuilder("Mapeamento")
                                    .onClick(e -> layout.setCenter(mapperScreen))
                                    .build();
        Button playerButton = new ButtonBuilder("Player")
                                    .onClick(e -> {
                                        playerScreen.refreshMusicList();
                                        layout.setCenter(playerScreen);
                                    })
                                    .build();

        VBox sideBar = new BoxBuilder<>(new VBox(10))
                            .addClass("sidebar")
                            .add(compositionButton)
                            .add(mapperButton)
                            .add(playerButton)
                            .build();

        layout = new BorderPaneBuilder()
                            .apply(b -> b.setLeft(sideBar))
                            .build();

        // o botão da tela aberta fica destacado (:selected em button.css)
        Map<Node, Button> screenButtons = Map.of(
            compositionScreen, compositionButton,
            mapperScreen, mapperButton,
            playerScreen, playerButton);
        layout.centerProperty().addListener((obs, oldScreen, newScreen) ->
            screenButtons.forEach((screen, button) -> button.pseudoClassStateChanged(SELECTED, screen == newScreen)));

        layout.setCenter(compositionScreen);

        Scene scene = createScene(stage, layout);
        addCssStyles(scene);
    }

    private Scene createScene(Stage stage, Parent layout) { 
        // javafx.stage.Screen escrito por extenso: gui.screens.Screen também está importada
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(layout,
            Math.min(SCENE_WIDTH, screen.getWidth()),
            Math.min(SCENE_HEIGHT, screen.getHeight()));

        stage.setTitle("Texto em Música");
        stage.setMinWidth(Math.min(MIN_WINDOW_WIDTH, screen.getWidth()));
        stage.setMinHeight(Math.min(MIN_WINDOW_HEIGHT, screen.getHeight()));
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        return scene;
    }

    private void addCssStyles(Scene scene) {
        // Base style
        addCSStoScene(scene, "/styles/base.css");

        // Screen style
        addCSStoScene(scene, "/styles/composition.css");
        addCSStoScene(scene, "/styles/mapper.css");
        addCSStoScene(scene, "/styles/player.css");

        // Components base style
        addCSStoScene(scene, "/styles/button.css");
        addCSStoScene(scene, "/styles/slider.css");
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
