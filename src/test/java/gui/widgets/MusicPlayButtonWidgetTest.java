package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import gui.TestPlayer;
import javafx.scene.control.Button;

class MusicPlayButtonWidgetTest extends JavaFXTestBase {

    @Test
    void startsWithPlayButtonVisibleAndPauseHidden() {
        TestPlayer player = new TestPlayer();
        MusicPlayButtonWidget widget = new MusicPlayButtonWidget(player);

        Button playBtn = (Button) widget.getChildren().get(0);
        Button pauseBtn = (Button) widget.getChildren().get(1);

        assertThat(playBtn.isVisible()).isTrue();
        assertThat(pauseBtn.isVisible()).isFalse();
    }

    @Test
    void clickingPlayTriggersPlayerPlayAndSwitchesToPauseButton() {
        TestPlayer player = new TestPlayer();
        MusicPlayButtonWidget widget = new MusicPlayButtonWidget(player);

        Button playBtn = (Button) widget.getChildren().get(0);
        Button pauseBtn = (Button) widget.getChildren().get(1);

        playBtn.fire();

        assertThat(player.playCalled).isTrue();
        assertThat(playBtn.isVisible()).isFalse();
        assertThat(pauseBtn.isVisible()).isTrue();
    }

    @Test
    void clickingPauseTriggersPlayerPauseAndSwitchesToPlayButton() {
        TestPlayer player = new TestPlayer();
        MusicPlayButtonWidget widget = new MusicPlayButtonWidget(player);

        Button playBtn = (Button) widget.getChildren().get(0);
        Button pauseBtn = (Button) widget.getChildren().get(1);

        playBtn.fire();
        pauseBtn.fire();

        assertThat(player.pauseCalled).isTrue();
        assertThat(playBtn.isVisible()).isTrue();
        assertThat(pauseBtn.isVisible()).isFalse();
    }

    @Test
    void setsExplicitButtonStates() {
        TestPlayer player = new TestPlayer();
        MusicPlayButtonWidget widget = new MusicPlayButtonWidget(player);

        Button playBtn = (Button) widget.getChildren().get(0);
        Button pauseBtn = (Button) widget.getChildren().get(1);

        widget.setCurrentButtonToPause();
        assertThat(playBtn.isVisible()).isFalse();
        assertThat(pauseBtn.isVisible()).isTrue();

        widget.setCurrentButtonToPlay();
        assertThat(playBtn.isVisible()).isTrue();
        assertThat(pauseBtn.isVisible()).isFalse();
    }
}
