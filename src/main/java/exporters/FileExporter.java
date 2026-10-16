package exporters;

import java.io.IOException;

public interface FileExporter<T> {

    void save(T data, String filePath) throws IOException;
    
}
