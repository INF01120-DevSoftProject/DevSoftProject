package input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TXTInputReader implements InputReader {

    private final Path filePath;

    public TXTInputReader(Path filePath) {

        if (filePath == null) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser nulo.");
        }
        this.filePath = filePath;
    }

    @Override
    public String readInput() throws IOException {

        return Files.readString(this.filePath);
    }

}
