package gui;

import javax.sound.midi.Sequence;

import javafx.application.Application;
import javafx.scene.Parent;
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
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT= 400;

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
            }
        );

        mapperScreen = new MapperScreen(mapping);
        
        playerScreen = new PlayerScreen(
            stage,
            new MidiPlayer(),
            new MidiMusicPathUtils(),
            draftService
        );

        VBox sideBar = new BoxBuilder<>(new VBox(10))
                            .addClass("sidebar")
                            .add(new ButtonBuilder("Composição")
                                    .onClick(e -> layout.setCenter(compositionScreen))
                                    .build())
                            .add(new ButtonBuilder("Mapeamento")
                                    .onClick(e -> layout.setCenter(mapperScreen))
                                    .build())
                            .add(new ButtonBuilder("Player")
                                    .onClick(e -> {
                                        playerScreen.refreshMusicList();
                                        layout.setCenter(playerScreen);
                                    })
                                    .build())
                            .build();

        layout = new BorderPaneBuilder()
                            .apply(b -> b.setLeft(sideBar))
                            .build();

        layout.setCenter(compositionScreen);

        Scene scene = createScene(stage, layout);
        addCssStyles(scene);
    }

    private Scene createScene(Stage stage, Parent layout) { 
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
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
