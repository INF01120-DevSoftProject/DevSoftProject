package input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class MIDIInputReader extends InputReader  {

    private static final String MidiFolderName = "musics";


    public Path getMidiFolderPath() {
        return getResourcesFolderPath(MidiFolderName);
    }

    public List<Path> getAllMidiInResourceFolder() {
        try (Stream<Path> files = Files.list(getMidiFolderPath())) {
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

    public Path getPreviousMidiFilePath(Path currentPath) {
        List<Path> musics = getAllMidiInResourceFolder();

        int index = musics.indexOf(currentPath);

        if (index <= 0) return null;

        return musics.get(index - 1);
    }

    public Path getNextMidiFilePath(Path currentPath) {
        List<Path> musics = getAllMidiInResourceFolder();

        int index = musics.indexOf(currentPath);

        if (index == -1 || index >= musics.size() - 1) return null;

        return musics.get(index + 1);
    }

    public Path getMidiFilePathFromName(String fileName) {
        Path path = getMidiFolderPath()
                    .resolve(fileName)
                    .normalize();

        if (!path.startsWith(getMidiFolderPath()) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException(
                    "Arquivo MIDI não encontrado: " + fileName
            );
        }

        return path;
    }

    private boolean isMidiFile(String name) {
        String lowerName = name.toLowerCase(Locale.ROOT);

        return lowerName.endsWith(".mid") || lowerName.endsWith(".midi");
    }
    
}
