package utils;

import java.nio.file.Path;
import java.util.List;

public abstract class MusicPathUtils {

    private static final String MIDI_FOLDER_NAME = "musics";

    public static Path getMusicsFolderPath() {
        return getResourceFolderPath(MIDI_FOLDER_NAME);
    }

    public static Path getResourceFolderPath(String folderName) {
        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException("Folder name cannot be null or blank.");
        }

        Path resourcesPath = Path.of("src", "main", "resources")
                .toAbsolutePath()
                .normalize();

        Path folderPath = resourcesPath.resolve(folderName).normalize();

        if (!folderPath.startsWith(resourcesPath)) {
            throw new IllegalArgumentException("Invalid resource folder.");
        }

        return folderPath;
    }

    public List<String> getMusicNamesFromPaths(List<Path> paths) {
        return paths.stream()
                .map(path -> path.getFileName().toString())
                .toList();
    }

    public String getMusicNameFromPath(Path path) {
        return path.getFileName().toString();
    }


    public abstract String getDefaultExtension();

    public abstract List<Path> getAllMusicsPathInResources();

    public abstract Path getPreviousMusicPath(Path currentPath);

    public abstract Path getNextMusicPath(Path currentPath);

    public abstract Path getMusicPathFromName(String fileName);

    public abstract boolean isSupportedFile(Path path);

    public abstract List<String> getSupportedExtensions();
}