package exporters;

import java.nio.file.Path;
import java.io.IOException;

public interface DraftMusicManager {
    
    boolean isDraft(Path path);

    void saveDraft(Path draft_path, Path targetPath) throws IOException;

    void cleanUp();

}
