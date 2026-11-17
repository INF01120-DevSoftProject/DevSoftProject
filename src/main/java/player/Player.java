package player;

import java.nio.file.Path;

public interface Player<T> extends AutoCloseable {

    void load(Path musicPath);

    void play();

    void pause();

    void stop();

    void seek(long milliseconds);

    long getCurrentTime();

    long getDuration();

    boolean isPlaying();

    double getMusicPosition();

    String getCurrentMusicName();

    Path getCurrentMusicPath();

    void addEndMusicEvent(Runnable action);

    @Override
    void close();
}