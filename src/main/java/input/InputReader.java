package input;

import java.io.IOException;
import java.nio.file.Path;

public interface InputReader {
    
    String readFile(Path path) throws IOException;

}