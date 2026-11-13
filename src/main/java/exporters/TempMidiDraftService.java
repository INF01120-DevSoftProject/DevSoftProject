package exporters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import music.VoicePreset;


public class TempMidiDraftService implements DraftMusicCreator, DraftMusicManager {

    private final MusicExportPipeline<?> exportPipeline;
    private final Set<Path> activeDrafts = new HashSet<>();

    public TempMidiDraftService(MusicExportPipeline<?> exportPipeline) {
        if (exportPipeline == null) {
            throw new IllegalArgumentException("Export pipeline cannot be null.");
        }
        this.exportPipeline = exportPipeline;
    }

    @Override
    public Path createDraft(String text, List<VoicePreset> presets) throws IOException {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("The composition is empty.");
        }
        if (presets == null) {
            throw new IllegalArgumentException("The list of presets cannot be null.");
        }

        Path tempPath = normalize(Files.createTempFile("rascunho-", ".mid"));
        tempPath.toFile().deleteOnExit();

        try {
            exportPipeline.exportMusic(tempPath.toFile(), text, presets);
        } catch (IOException | RuntimeException e) {
            deleteQuietly(tempPath);
            throw e;
        }

        cleanUp();
        activeDrafts.add(tempPath);
        return tempPath;
    }

    @Override
    public boolean isDraft(Path path) {
        if (path == null) {
            return false;
        }
        return activeDrafts.contains(normalize(path));
    }

    @Override
    public void saveDraft(Path draftPath, Path targetPath) throws IOException {
        if (draftPath == null || targetPath == null) {
            throw new IllegalArgumentException("The draft path and the destination path cannot be null.");
        }

        Path normalizedDraft = normalize(draftPath);
        if (!isDraft(normalizedDraft) || !Files.isRegularFile(normalizedDraft)) {
            throw new IllegalArgumentException("Draft invalid or not found.");
        }

        Files.copy(normalizedDraft, normalize(targetPath), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void cleanUp() {
        for (Path draft : activeDrafts) {
            deleteQuietly(draft);
        }
        activeDrafts.clear();
    }

    private Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Melhor esforço na limpeza de arquivos temporários
        }
    }
}
