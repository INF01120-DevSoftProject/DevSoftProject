package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class MidiMusicPathUtils extends MusicPathUtils {

    private static final String MIDI_FOLDER_NAME = "musics";

    @Override
    public Path getMusicsFolderPath() {
        return getResourceFolderPath(MIDI_FOLDER_NAME);
    }

    @Override
    public List<Path> getAllMusicsPathInResources() {
        try (Stream<Path> files = Files.list(getMusicsFolderPath())) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedFile)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException( "Erro ao listar arquivos MIDI", e);
        }
    }

    @Override
    public Path getPreviousMusicPath(Path currentPath) {
        List<Path> musics = getAllMusicsPathInResources();

        int index = musics.indexOf(currentPath);

        if (index <= 0) return null;

        return musics.get(index - 1);
    }

    @Override
    public Path getNextMusicPath(Path currentPath) {
        List<Path> musics = getAllMusicsPathInResources();

        int index = musics.indexOf(currentPath);

        if (index == -1 || index >= musics.size() - 1) return null;

        return musics.get(index + 1);
    }

    @Override
    public Path getMusicPathFromName(String fileName) {
        Path path = getMusicsFolderPath()
                .resolve(fileName)
                .normalize();

        if (!path.startsWith(getMusicsFolderPath()) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Arquivo MIDI não encontrado: " + fileName);
        }

        return path;
    }

    @Override
    public boolean isSupportedFile(Path path) {
        String fileName = path.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        return fileName.endsWith(".mid") || fileName.endsWith(".midi");
    }

    @Override
    public String getDefaultExtension() {
        return ".mid";
    }
}
