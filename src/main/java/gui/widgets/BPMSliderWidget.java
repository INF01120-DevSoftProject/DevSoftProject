package gui.widgets;

import javafx.scene.control.Slider;
import music.Music;

public class BPMSliderWidget extends Slider {

    private static final int MIN_BPM = 20;
    private static final int MAX_BPM = 999;

    public BPMSliderWidget() {
        setMin(MIN_BPM);
        setMax(MAX_BPM);
        setValue(Music.DEFAULT_BPM);
        setBlockIncrement(1);
        getStyleClass().add("bpm-slider");
    }

    public int getBPM() {
        return (int) Math.round(getValue());
    }
}
