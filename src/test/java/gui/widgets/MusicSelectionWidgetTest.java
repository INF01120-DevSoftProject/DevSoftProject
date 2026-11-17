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
        public List<String> getSupportedExtensions() {
            return List.of("*.mid", "*.midi");
        }

        @Override 
        public String getDefaultExtension() {
            return "fake";
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


        @Override
        public boolean isSupportedFile(Path file) {
            return false;
        }

    }

    @Test
    void initializesWithMusicListAndSelectsFirst() {
        DummyMusicPathUtils utils = new DummyMusicPathUtils();
        MusicSelectionWidget widget = new MusicSelectionWidget(utils);

        try {
            assertThat(widget.getItems()).containsExactly("songA.mid", "songB.mid");
            assertThat(widget.getValue()).isEqualTo("songA.mid");
            assertThat(widget.getVisibleRowCount()).isEqualTo(5);
        } finally {
            widget.stopUpdater();
        }
    }

    @Test
    void refreshMusicListPreservesClearedSelectionWhenNewMusicAppears() {
        List<Path> dynamicPaths = new java.util.ArrayList<>(List.of(Path.of("songA.mid"), Path.of("songB.mid")));
        MusicPathUtils utils = new DummyMusicPathUtils() {
            @Override
            public List<Path> getAllMusicsPathInResources() {
                return List.copyOf(dynamicPaths);
            }
        };

        MusicSelectionWidget widget = new MusicSelectionWidget(utils);
        try {
            widget.getSelectionModel().clearSelection();
            dynamicPaths.add(Path.of("songC.mid"));

            widget.refreshMusicList();

            assertThat(widget.getItems()).containsExactly("songA.mid", "songB.mid", "songC.mid");
            assertThat(widget.getValue()).isNull();
        } finally {
            widget.stopUpdater();
        }
    }
}
