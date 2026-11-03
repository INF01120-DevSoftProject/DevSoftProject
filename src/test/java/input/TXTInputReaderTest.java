package input;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TXTInputReaderTest {

    @Test
    void readsContentFromTextFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("song.txt");
        String content = "Line 1: A B C\nLine 2: D E F\n";
        Files.writeString(filePath, content);

        TXTInputReader reader = new TXTInputReader();

        assertThat(reader.readFile(filePath)).isEqualTo(content);
    }

    @Test
    void throwsExceptionWhenPathIsNull() {
        TXTInputReader reader = new TXTInputReader();

        assertThrows(
            IllegalArgumentException.class,
            () -> reader.readFile(null)
        );
    }

    @Test
    void throwsIOExceptionWhenFileNotFound(@TempDir Path tempDir) {
        Path nonExistentPath = tempDir.resolve("missing_file.txt");
        TXTInputReader reader = new TXTInputReader();

        assertThrows(
            IOException.class,
            () -> reader.readFile(nonExistentPath)
        );
    }

}
