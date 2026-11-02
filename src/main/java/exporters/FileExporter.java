package exporters;

import java.io.IOException;

import java.nio.file.Path;

public interface FileExporter<T> {

    public void save(T data, Path filePath, String fileName) throws IOException;
}
