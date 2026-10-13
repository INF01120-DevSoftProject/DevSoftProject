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

        TXTInputReader reader = new TXTInputReader(filePath);

        assertThat(reader.readInput()).isEqualTo(content);
    }

    @Test
    void throwsExceptionWhenPathIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new TXTInputReader(null));
    }

    @Test
    void throwsIOExceptionWhenFileNotFound(@TempDir Path tempDir) {
        Path nonExistentPath = tempDir.resolve("missing_file.txt");
        TXTInputReader reader = new TXTInputReader(nonExistentPath);

        assertThrows(IOException.class, reader::readInput);
    }

    @Test
    void isAnInputReader(@TempDir Path tempDir) {
        Path dummyPath = tempDir.resolve("dummy.txt");
        assertThat(new TXTInputReader(dummyPath)).isInstanceOf(InputReader.class);
    }
}
