package player;

import java.nio.file.Path;

public interface Player {
    void playFile(Path midiFilePath) throws Exception;
}