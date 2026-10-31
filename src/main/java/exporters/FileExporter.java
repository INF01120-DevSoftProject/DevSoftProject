package exporters;

import java.io.IOException;

import javax.sound.midi.Sequence;
import java.nio.file.Path;

public interface FileExporter<T> {

    public void save(Sequence data, Path filePath, String fileName) throws IOException;
    
}
