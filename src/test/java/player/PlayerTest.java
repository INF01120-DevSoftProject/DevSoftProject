package player;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import javax.sound.midi.Sequencer;

public class PlayerTest {

    static class DummyPlayer implements Player {
        private boolean played = false;
        private boolean closed = false;
        private Sequencer sequencer;

        private Path currentMusicPath;
        private String currentMusicName;

        @Override
        public void load(Path path) {
        }

        @Override
        public void play() {
            this.played = true;
        }

        @Override
        public void pause() {
            this.played = false;
        }

        @Override
        public void stop() {
            this.played = false;
        }

        @Override
        public void seek(long milliseconds) {
        }

        @Override
        public Sequencer getMusicData() {
            return this.sequencer;
        }

        @Override 
        public double getMusicPosition() {
            return 0;
        }


        @Override
        public long getCurrentTime() {
            return 0;
        }

        @Override
        public long getDuration() {
            return 1000;
        }

        @Override
        public boolean isPlaying() {
            return this.played;
        }

        @Override
        public void close() {
            this.closed = true;
        }

        @Override
        public String getCurrentMusicName() {
            return currentMusicName;
        }

        public Path getCurrentMusicPath() {
            return currentMusicPath;
        }

        public boolean isClosed() {
            return this.closed;
        }
    }

    @Test
    void anyClassCanImplementPlayerContract() {
        try (DummyPlayer player = new DummyPlayer()) {
            player.play();
            assertThat(player.isPlaying()).isTrue();
            player.pause();
            assertThat(player.isPlaying()).isFalse();
            assertThat(player.getDuration()).isEqualTo(1000);
        }
    }
}
