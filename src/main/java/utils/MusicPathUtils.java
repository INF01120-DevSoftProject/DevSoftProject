package utils;

import java.nio.file.Path;
import java.util.List;

public abstract class MusicPathUtils {

    public Path getResourcesPath() {
        return Path.of("src", "main", "resources")
                .toAbsolutePath()
                .normalize();
    }

    public Path getResourcesFolderPath(String folderName) {
        if (folderName == null) throw new IllegalArgumentException("Folder name cannot be null.");
       
        return getResourcesPath()
                .resolve(folderName)
                .normalize();
    }

    public List<String> getMusicNamesFromPaths(List<Path> paths) {
        return paths.stream()
                .map(path -> path.getFileName().toString())
                .toList();
    }

    public String getMusicNameFromPath(Path path) {
        return path.getFileName().toString();
    }

    public abstract Path getMusicsFolderPath();

    public abstract List<Path> getAllMusicsPathInResources();

    public abstract Path getPreviousMusicPath(Path currentPath);

    public abstract Path getNextMusicPath(Path currentPath);

    public abstract Path getMusicPathFromName(String fileName);


}
