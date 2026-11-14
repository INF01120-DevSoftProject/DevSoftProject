package exporters;

import java.io.IOException;
import java.nio.file.Path;
import music.VoicePreset;
import java.util.List;

public interface DraftMusicCreator {

    Path createDraft(String text, List<VoicePreset> presets) throws IOException;
    
}