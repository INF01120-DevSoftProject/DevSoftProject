package exporters;

import java.io.IOException;
import java.nio.file.Path;
import music.VoicePreset;
import music.Music;
import java.util.List;

public interface DraftMusicCreator {

    default Path createDraft(String text, List<VoicePreset> presets) throws IOException {
        return createDraft(text, presets, Music.DEFAULT_BPM);
    }

    Path createDraft(String text, List<VoicePreset> presets, int initialBpm) throws IOException;
    
}
