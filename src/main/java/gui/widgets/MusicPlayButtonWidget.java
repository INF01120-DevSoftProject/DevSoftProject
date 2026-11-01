package gui.widgets;

import gui.builders.ButtonBuilder;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import player.Player;

public class MusicPlayButtonWidget extends StackPane {

    private Button playButton;
    private Button pauseButton;

    public MusicPlayButtonWidget(Player player) {
        
        playButton = new ButtonBuilder("▶")
            .onClick(e -> {
                player.play();
                setCurrentButton(pauseButton);})
            .build();

        pauseButton = new ButtonBuilder("⏸")
            .onClick(e -> {
                player.pause();
                setCurrentButton(playButton);})
            .build();

        getChildren().setAll(playButton, pauseButton);

        setCurrentButton(playButton);
    }


    public void setCurrentButtonToPlay() {
        setCurrentButton(playButton);
    }

    public void setCurrentButtonToPause() {
        setCurrentButton(pauseButton);
    }

    private void setCurrentButton(Button button) {
        if (button == null) return;

        playButton.setVisible(button == playButton);
        pauseButton.setVisible(button == pauseButton);
    }
}
