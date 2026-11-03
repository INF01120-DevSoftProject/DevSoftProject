package player;

import java.nio.file.Path;
public interface Player<T> extends AutoCloseable {

    void load(Path path);

    void play();

    void pause();

    void stop();

    void seek(long milliseconds);

    long getCurrentTime();

    long getDuration();

    boolean isPlaying();

    double getMusicPosition();

    T getMusicData();

    String getCurrentMusicName();

    Path getCurrentMusicPath();


    @Override
    void close();
}