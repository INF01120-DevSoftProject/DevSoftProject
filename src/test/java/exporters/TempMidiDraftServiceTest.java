package exporters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import music.VoicePreset;
import musicElements.InstrumentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import parsers.MusicToMidiParser;
import translators.TextToMusic.TextToMusicTranslator;

class TempMidiDraftServiceTest {

    @TempDir
    Path tempDir;

    private TempMidiDraftService service;

    @AfterEach
    void tearDown() {
        if (service != null) {
            service.cleanUp();
        }
    }

    private TempMidiDraftService createService() {
        MusicExportPipeline<Sequence> pipeline = new MusicExportPipeline<>(
            new TextToMusicTranslator(),
            new MusicToMidiParser(),
            new MidiFileExporter()
        );
        service = new TempMidiDraftService(pipeline);
        return service;
    }

    private List<VoicePreset> defaultPresets() {
        return List.of(new VoicePreset(InstrumentType.KEYS));
    }

    @Test
    void rejectsNullExportPipeline() {
        assertThrows(IllegalArgumentException.class, () -> new TempMidiDraftService(null));
    }

    @Test
    void createsValidTemporaryMidiDraft() throws Exception {
        TempMidiDraftService draftService = createService();

        Path draft = draftService.createDraft("C D E", defaultPresets());

        assertThat(draft).exists().isRegularFile();
        assertThat(draft.getFileName().toString()).endsWith(".mid");
        assertThat(Files.size(draft)).isPositive();
        assertThat(draftService.isDraft(draft)).isTrue();

        Sequence sequence = MidiSystem.getSequence(draft.toFile());
        assertThat(sequence.getTracks()).isNotEmpty();
    }

    @Test
    void rejectsNullOrBlankTextAndNullPresets() {
        TempMidiDraftService draftService = createService();

        assertThrows(IllegalArgumentException.class, () -> draftService.createDraft(null, defaultPresets()));
        assertThrows(IllegalArgumentException.class, () -> draftService.createDraft("   ", defaultPresets()));
        assertThrows(IllegalArgumentException.class, () -> draftService.createDraft("C D E", null));
    }

    @Test
    void replacesPreviousDraftWhenCreatingNewOne() throws Exception {
        TempMidiDraftService draftService = createService();

        Path firstDraft = draftService.createDraft("C D E", defaultPresets());
        Path secondDraft = draftService.createDraft("F G A", defaultPresets());

        assertThat(firstDraft).doesNotExist();
        assertThat(draftService.isDraft(firstDraft)).isFalse();
        assertThat(secondDraft).exists().isRegularFile();
        assertThat(draftService.isDraft(secondDraft)).isTrue();
    }

    @Test
    void preservesExistingDraftIfNewDraftCreationFails() throws Exception {
        TempMidiDraftService draftService = createService();
        Path validDraft = draftService.createDraft("C D E", defaultPresets());

        assertThrows(IllegalArgumentException.class, () -> draftService.createDraft("", defaultPresets()));

        assertThat(validDraft).exists();
        assertThat(draftService.isDraft(validDraft)).isTrue();
    }

    @Test
    void savesDraftToTargetAndAllowsSavingAgain() throws Exception {
        TempMidiDraftService draftService = createService();
        Path draft = draftService.createDraft("C D E", defaultPresets());

        Path firstTarget = tempDir.resolve("primeira_versao.mid");
        Path secondTarget = tempDir.resolve("segunda_copia.mid");

        draftService.saveDraft(draft, firstTarget);
        draftService.saveDraft(draft, secondTarget);

        assertThat(firstTarget).exists().isRegularFile();
        assertThat(secondTarget).exists().isRegularFile();
        assertThat(Files.readAllBytes(firstTarget)).isEqualTo(Files.readAllBytes(draft));
        assertThat(Files.readAllBytes(secondTarget)).isEqualTo(Files.readAllBytes(draft));
        assertThat(draft).exists();
        assertThat(draftService.isDraft(draft)).isTrue();
    }

    @Test
    void rejectsSavingUnmanagedOrNullPaths() throws Exception {
        TempMidiDraftService draftService = createService();
        Path unmanagedFile = Files.writeString(tempDir.resolve("comum.mid"), "abc");
        Path target = tempDir.resolve("destino.mid");

        assertThrows(IllegalArgumentException.class, () -> draftService.saveDraft(unmanagedFile, target));
        assertThrows(IllegalArgumentException.class, () -> draftService.saveDraft(null, target));
        assertThrows(IllegalArgumentException.class, () -> draftService.saveDraft(unmanagedFile, null));
    }

    @Test
    void isDraftReturnsFalseForNullOrUnknownPath() {
        TempMidiDraftService draftService = createService();

        assertThat(draftService.isDraft(null)).isFalse();
        assertThat(draftService.isDraft(tempDir.resolve("desconhecido.mid"))).isFalse();
    }

    @Test
    void cleanUpRemovesActiveDraftFiles() throws Exception {
        TempMidiDraftService draftService = createService();
        Path draft = draftService.createDraft("C D E", defaultPresets());

        draftService.cleanUp();

        assertThat(draft).doesNotExist();
        assertThat(draftService.isDraft(draft)).isFalse();
    }
}
