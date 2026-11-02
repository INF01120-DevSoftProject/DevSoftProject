package input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class MidiMusicPathUtils extends MusicPathUtils  {

    private static final String MidiFolderName = "musics";

    @Override 
    public Path getMusicsFolderPath() {
        return getResourcesFolderPath(MidiFolderName);
    }

    @Override 
    public List<Path> getAllMusicsPathInResources() {
        try (Stream<Path> files = Files.list(getMusicsFolderPath())) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> isMidiFile(path.getFileName().toString()))
                    .sorted()
                    .toList();
        }
        catch (IOException e) {
            throw new IllegalStateException("Erro ao listar arquivos MIDI", e);
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

    private boolean isMidiFile(String name) {
        String lowerName = name.toLowerCase(Locale.ROOT);

        return lowerName.endsWith(".mid") || lowerName.endsWith(".midi");
    }
}
