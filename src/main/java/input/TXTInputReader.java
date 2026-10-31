package input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TXTInputReader extends InputReader {

    public List<String> readLines(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser nulo.");
        }

        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public String readInput(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException(
                "O caminho do arquivo não pode ser nulo."
            );
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
