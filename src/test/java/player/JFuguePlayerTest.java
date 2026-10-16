package player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JFuguePlayerTest {

    @Test
    void isAPlayer() {
        assertThat(new JFuguePlayer()).isInstanceOf(Player.class);
    }

    @Test
    void throwsExceptionWhenPathIsNull() {
        JFuguePlayer player = new JFuguePlayer();
        assertThrows(IllegalArgumentException.class, () -> player.playFile(null));
    }

    @Test
    void throwsFileNotFoundExceptionWhenFileDoesNotExist(@TempDir Path tempDir) {
        JFuguePlayer player = new JFuguePlayer();
        Path nonExistent = tempDir.resolve("non_existent.mid");

        assertThrows(FileNotFoundException.class, () -> player.playFile(nonExistent));
    }

    @Test
    void throwsExceptionWhenJFuguePlayerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new JFuguePlayer(null));
    }

    @Test
    void loadsAndPlaysValidMidiFile() throws Exception {
        Path midiFile = Path.of("target", "test-play.mid");
        MidiFileManager.savePatternToMidi(new Pattern("C"), midiFile.toFile());

        JFuguePlayer player = new JFuguePlayer();
        player.playFile(midiFile);

        assertThat(midiFile).exists();
    }
}
