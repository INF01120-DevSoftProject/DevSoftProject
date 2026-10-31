package gui.widgets;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import com.sheetmusic4j.core.io.ScoreFile;
import com.sheetmusic4j.core.model.Score;
import com.sheetmusic4j.fxviewer.StripSheetView;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.nio.file.Path;

import player.MidiPlayer;

public class SheetMusicWidget extends StackPane {

    private StripSheetView sheetView;
    private AnimationTimer animation;

    private static final double SHEET_MUSIC_ZOOM = 1.0;
    private static final double STRIP_SPACING = 1.0;
    private static final double CURSOR_POSITION = 0.25;

    public SheetMusicWidget(MidiPlayer midiPlayer) {
        sheetView = createSheetView(midiPlayer.getCurrentMusicPath());
        animation = createAnimation(midiPlayer.getSequencer());
        animation.start();
    }

    public void loadMusic(MidiPlayer midiPlayer) {
        Path path = midiPlayer.getCurrentMusicPath();

        sheetView.setScore(ScoreFile.load(path));
        sheetView.cursorTimeProperty().set(0);
    }

    public StripSheetView getSheetView() {
        return sheetView;
    }

    public AnimationTimer getSheetViewAnimation() {
        return animation;
    }

    private StripSheetView createSheetView(Path midiPath) {
        Score score = ScoreFile.load(midiPath);

        StripSheetView sheetView = new StripSheetView();
        sheetView.setScore(score);

        sheetView.setZoom(SHEET_MUSIC_ZOOM);
        sheetView.setStripSpacingFactor(STRIP_SPACING);
        sheetView.setCursorScreenPosition(CURSOR_POSITION);
        sheetView.setCursorVisible(true);
        sheetView.setCursorColor(Color.DODGERBLUE);

        getChildren().add(sheetView);

        return sheetView;
    }

    public void stopAnimation() {
        getSheetViewAnimation().stop();
    }

    private AnimationTimer createAnimation(Sequencer sequencer) {
        Sequence sequence = sequencer.getSequence();

        if (sequence == null || sequence.getDivisionType() != Sequence.PPQ) {
            throw new IllegalStateException("MIDI PPQ não carregado");
        }

        AnimationTimer animation = new AnimationTimer() {

            private long lastTick = -1;

            @Override
            public void handle(long now) {                 
                moveSheetByTick(sequencer, lastTick);
            }
        };
        return animation;
    }

    private void moveSheetByTick(Sequencer sequencer, long lastTick) {
        long tick = sequencer.getTickPosition();

        if (tick != lastTick) {
            lastTick = tick;

            double resolution = sequencer.getSequence().getResolution();
            double position = tick / resolution;

            sheetView.cursorTimeProperty().set(position);
        }
    }
}
