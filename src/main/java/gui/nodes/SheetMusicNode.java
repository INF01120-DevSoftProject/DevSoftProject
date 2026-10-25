package gui.nodes;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import com.sheetmusic4j.core.io.ScoreFile;
import com.sheetmusic4j.core.model.Score;
import com.sheetmusic4j.fxviewer.StripSheetView;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import java.nio.file.Path;
import gui.screens.PlayerScreen;

public class SheetMusicNode {

    private StripSheetView sheetView;
    private AnimationTimer animation;
    private Sequencer sequencer;

    public SheetMusicNode(Path midiPath, Sequencer sequencer) {
        setSequencer(sequencer);
        sheetView = createSheetView(midiPath);
        animation = createAnimation(sequencer);
        animation.start();
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public StripSheetView getSheetView() {
        return sheetView;
    }

    public AnimationTimer getAnimation() {
        return animation;
    }

    private StripSheetView createSheetView(Path midiPath) {
        Score score = ScoreFile.load(midiPath);

        StripSheetView sheetView = new StripSheetView();
        sheetView.setScore(score);

        sheetView.setZoom(1.0);
        sheetView.setStripSpacingFactor(1.0);
        sheetView.setCursorScreenPosition(0.25);

        sheetView.setCursorVisible(true);
        sheetView.setCursorColor(Color.DODGERBLUE);

        return sheetView;
    }

    private void moveSheetByTick(long lastTick) {
        long tick = sequencer.getTickPosition();

        if (tick != lastTick) {
            lastTick = tick;

            double resolution = sequencer.getSequence().getResolution();
            double position = tick / resolution;

            sheetView.cursorTimeProperty().set(position);
        }
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
                moveSheetByTick(lastTick);
            }
        };
        return animation;
    }


    
}
