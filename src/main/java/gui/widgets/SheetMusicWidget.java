package gui.widgets;

import com.sheetmusic4j.core.io.ScoreFile;
import com.sheetmusic4j.core.model.Score;
import com.sheetmusic4j.fxviewer.StripSheetView;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.nio.file.Path;
import java.util.function.DoubleSupplier;

public class SheetMusicWidget extends StackPane {

    private StripSheetView sheetView;
    private AnimationTimer animation;

    private static final double SHEET_MUSIC_ZOOM = 1.0;
    private static final double STRIP_SPACING = 1.0;
    private static final double CURSOR_POSITION = 0.25;

    public SheetMusicWidget(Path midiPath, DoubleSupplier positionSupplier) {
        sheetView = createSheetView(midiPath);
        animation = createAnimation(positionSupplier);
        animation.start();
    }

    public void loadMusic(Path path) {
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

    private AnimationTimer createAnimation(DoubleSupplier positionSupplier) {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveSheetByTick(positionSupplier);
            }
        };
    }

    private void moveSheetByTick(DoubleSupplier positionSupplier) {
        double position = positionSupplier.getAsDouble();

        if (!Double.isFinite(position)) return;

        sheetView.cursorTimeProperty().set(position);
    }
}
