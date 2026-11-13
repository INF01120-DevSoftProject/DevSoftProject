package gui.screens;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import gui.TestPlayer;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.MidiMusicPathUtils;

class PlayerScreenTest extends JavaFXTestBase {

    @Test
    void initializesPlayerScreenInsideFxThread() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<PlayerScreen> screenRef = new AtomicReference<>();
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                TestPlayer player = new TestPlayer();
                Path testMidi = Path.of("src", "main", "resources", "musics", "musica.mid");
                player.load(testMidi);

                MidiMusicPathUtils pathUtils = new MidiMusicPathUtils();
                PlayerScreen screen = new PlayerScreen(stage, player, pathUtils);
                screenRef.set(screen);
            } catch (Throwable t) {
                errorRef.set(t);
            } finally {
                latch.countDown();
            }
        });

        boolean finished = latch.await(5, TimeUnit.SECONDS);
        assertThat(finished).isTrue();
        assertThat(errorRef.get()).isNull();
        assertThat(screenRef.get()).isNotNull();
        assertThat(screenRef.get().getChildren()).isNotEmpty();
    }
}
