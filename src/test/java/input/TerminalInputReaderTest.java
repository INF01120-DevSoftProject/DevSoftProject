package input;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class TerminalInputReaderTest {

    @Test
    void readsLinesUntilEmptyLine() throws IOException {
        String input = "Line 1: A B C\nLine 2: D E F\n\nIgnored after empty line\n";
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        TerminalInputReader reader = new TerminalInputReader(in);

        assertThat(reader.readInput()).isEqualTo("Line 1: A B C\nLine 2: D E F\n");
    }

    @Test
    void readsSingleLineUntilEmptyLine() throws IOException {
        String input = "A:3 B:2 C:1\n\n";
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        TerminalInputReader reader = new TerminalInputReader(in);

        assertThat(reader.readInput()).isEqualTo("A:3 B:2 C:1\n");
    }

    @Test
    void returnsEmptyStringWhenFirstLineIsEmpty() throws IOException {
        String input = "\n";
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        TerminalInputReader reader = new TerminalInputReader(in);

        assertThat(reader.readInput()).isEmpty();
    }

    @Test
    void throwsExceptionWhenInputStreamIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new TerminalInputReader(null));
    }

    @Test
    void isAnInputReader() {
        InputStream in = new ByteArrayInputStream(new byte[0]);
        assertThat(new TerminalInputReader(in)).isInstanceOf(InputReader.class);
    }
}
