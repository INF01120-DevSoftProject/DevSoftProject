package gui;

import java.nio.file.Path;
import player.Player;

public class TestPlayer implements Player<Object> {

    public boolean playCalled = false;
    public boolean pauseCalled = false;
    public boolean stopCalled = false;
    public boolean closeCalled = false;
    public long seekPosition = -1;
    public boolean isPlaying = false;
    public long currentTime = 0;
    public long duration = 120_000;
    public double musicPosition = 0.0;
    public Path currentMusicPath = Path.of("test.mid");
    public String currentMusicName = "test.mid";

    @Override
    public void load(Path path) {
        this.currentMusicPath = path;
        this.currentMusicName = path != null && path.getFileName() != null ? path.getFileName().toString() : "test.mid";
    }

    @Override
    public void play() {
        this.playCalled = true;
        this.isPlaying = true;
    }

    @Override
    public void pause() {
        this.pauseCalled = true;
        this.isPlaying = false;
    }

    @Override
    public void stop() {
        this.stopCalled = true;
        this.isPlaying = false;
        this.currentTime = 0;
    }

    @Override
    public void seek(long milliseconds) {
        this.seekPosition = milliseconds;
        this.currentTime = milliseconds;
    }

    @Override
    public long getCurrentTime() {
        return this.currentTime;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public boolean isPlaying() {
        return this.isPlaying;
    }

    @Override
    public double getMusicPosition() {
        return this.musicPosition;
    }

    @Override
    public Object getMusicData() {
        return null;
    }

    @Override
    public String getCurrentMusicName() {
        return this.currentMusicName;
    }

    @Override
    public Path getCurrentMusicPath() {
        return this.currentMusicPath;
    }

    @Override
    public void close() {
        this.closeCalled = true;
    }
}
