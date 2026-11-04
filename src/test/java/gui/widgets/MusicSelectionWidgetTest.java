package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import utils.MusicPathUtils;

class MusicSelectionWidgetTest extends JavaFXTestBase {

    static class DummyMusicPathUtils extends MusicPathUtils {
        @Override
        public Path getMusicsFolderPath() {
            return Path.of("fake");
        }

        @Override
        public List<Path> getAllMusicsPathInResources() {
            return List.of(Path.of("songA.mid"), Path.of("songB.mid"));
        }

        @Override
        public Path getPreviousMusicPath(Path currentPath) {
            return null;
        }

        @Override
        public Path getNextMusicPath(Path currentPath) {
            return null;
        }

        @Override
        public Path getMusicPathFromName(String fileName) {
            return Path.of(fileName);
        }
    }

    @Test
    void initializesWithMusicListAndSelectsFirst() {
        DummyMusicPathUtils utils = new DummyMusicPathUtils();
        MusicSelectionWidget widget = new MusicSelectionWidget(utils);

        try {
            assertThat(widget.getItems()).containsExactly("songA.mid", "songB.mid");
            assertThat(widget.getValue()).isEqualTo("songA.mid");
        } finally {
            widget.stopUpdater();
        }
    }
}
