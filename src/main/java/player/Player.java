package player;

import java.nio.file.Path;

public interface Player extends AutoCloseable {

    void load(Path path);

    void play();

    void pause();

    void stop();

    void seek(long milliseconds);

    long getCurrentTime();

    long getDuration();

    boolean isPlaying();

    @Override
    void close();
}