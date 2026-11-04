package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import gui.TestPlayer;

class MusicSliderWidgetTest extends JavaFXTestBase {

    @Test
    void initializesSliderWithPlayerDuration() {
        TestPlayer player = new TestPlayer();
        player.duration = 60_000;

        MusicSliderWidget slider = new MusicSliderWidget(player);
        try {
            assertThat(slider.getMin()).isZero();
            assertThat(slider.getMax()).isEqualTo(60_000);
            assertThat(slider.getValue()).isZero();
        } finally {
            slider.stopUpdater();
        }
    }

    @Test
    void updatesSeekPositionOnKeyReleased() {
        TestPlayer player = new TestPlayer();
        player.duration = 50_000;

        MusicSliderWidget slider = new MusicSliderWidget(player);
        try {
            slider.setValue(25_000);
            slider.getOnKeyReleased().handle(null);

            assertThat(player.seekPosition).isEqualTo(25_000);
        } finally {
            slider.stopUpdater();
        }
    }

    @Test
    void updatesSeekPositionOnMouseReleased() {
        TestPlayer player = new TestPlayer();
        player.duration = 40_000;

        MusicSliderWidget slider = new MusicSliderWidget(player);
        try {
            slider.setValue(15_000);
            slider.getOnMouseReleased().handle(null);

            assertThat(player.seekPosition).isEqualTo(15_000);
        } finally {
            slider.stopUpdater();
        }
    }
}
