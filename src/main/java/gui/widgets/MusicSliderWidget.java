package gui.widgets;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Slider;
import player.Player;
import javafx.util.Duration;

public class MusicSliderWidget extends Slider {

    private Timeline sliderUpdater;
    private Player player;

    public MusicSliderWidget(Player player) {
        this.player = player;
        
        long duration = player.getDuration();

        setMin(0);
        setMax(duration);
        setValue(0);

        setOnMouseReleased(e ->
            player.seek(Math.round(getValue()))
        );

        setOnKeyReleased(e ->
            player.seek(Math.round(getValue()))
        );

        startSliderUpdater();
    }

    private void startSliderUpdater() {
        sliderUpdater = new Timeline(
            new KeyFrame(
                Duration.millis(100),
                e -> updateTimeSlider()
            )
        );

        sliderUpdater.setCycleCount(Animation.INDEFINITE);
        sliderUpdater.play();
    }


    public void stopUpdater() {
        sliderUpdater.stop();
    }

    private void updateTimeSlider() {

        if (this.isValueChanging() || this.isPressed()) {
            return;
        }

        setValue(player.getCurrentTime());
    }

}
