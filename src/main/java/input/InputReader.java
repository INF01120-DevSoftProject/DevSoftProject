package input;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class InputReader {

    public Path getResourcesPath() {
        return Path.of("src", "main", "resources")
                .toAbsolutePath()
                .normalize();
    }

    public List<String> getFileNamesFromPaths(List<Path> paths) {
        return paths.stream()
                .map(path -> path.getFileName().toString())
                .toList();
    }

    public Path getResourcesFolderPath(String folderName) {
        if (folderName == null) throw new IllegalArgumentException("Folder name cannot be null.");
       
        return getResourcesPath()
                .resolve(folderName)
                .normalize();
    }

    public String getFileNameFromPath(Path path) {
        return path.getFileName().toString();
    }
}
