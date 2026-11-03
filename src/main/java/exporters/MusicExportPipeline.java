package exporters;

import java.util.List;
import java.io.File;
import java.io.IOException;

import music.Music;
import music.VoicePreset;
import parsers.MusicParser;
import translators.TextToMusic.TextToMusicTranslator;

public class MusicExportPipeline<T> {
    
    private final MusicParser<T> parser;
    private final FileExporter<T> exporter;
    private final TextToMusicTranslator translator;

    public MusicExportPipeline(TextToMusicTranslator translator, MusicParser<T> parser, FileExporter<T> exporter) {
        this.parser = parser;
        this.exporter = exporter;
        this.translator = translator;
    }

    public void exportMusic(File file, String text, List<VoicePreset> presets) throws IOException  {
        if (text.isBlank()) throw new IllegalArgumentException("A composição está vazia.");

        Music music = translator.textToMusic(
            file.getName(),
            text,
            presets
        );

        T data = parser.convertMusicToFile(music);
        exporter.save(data, file.toPath().getParent(), file.getName());
    }
}