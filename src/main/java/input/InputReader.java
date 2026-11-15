package input;

import java.io.IOException;
import java.nio.file.Path;

public interface InputReader<T> {
    
    T readFile(Path path) throws IOException;

}