package input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TerminalInputReader implements InputReader {

    private final Scanner scanner;

    public TerminalInputReader() {
        this(System.in);
    }

    public TerminalInputReader(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("O InputStream não pode ser nulo.");
        }
        this.scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
    }

    @Override
    public String readInput() throws IOException {

        StringBuilder fullText = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            fullText.append(line).append("\n");
        }
        return fullText.toString();
    }

}
