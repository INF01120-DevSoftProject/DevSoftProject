package input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TXTInputReader implements InputReader { 

    @Override 
    public String readFile(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException(
                "O caminho do arquivo não pode ser nulo."
            );
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

}
