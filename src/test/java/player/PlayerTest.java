package player;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    void anyClassCanBeAPlayer() throws Exception {
        List<Path> played = new ArrayList<>();
        Player player = played::add;

        Path midiPath = Path.of("sample.mid");
        player.playFile(midiPath);

        assertThat(played).containsExactly(midiPath);
    }
}
