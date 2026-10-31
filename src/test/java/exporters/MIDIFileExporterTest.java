
package exporters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.midi.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MIDIFileExporterTest {

    @TempDir
    Path tempDir;

    private MIDIFileExporter exporter() {
        return new MIDIFileExporter();
    }

    private Sequence sequenceWithOneNote()
            throws InvalidMidiDataException {

        Sequence sequence = new Sequence(Sequence.PPQ, 480);
        Track track = sequence.createTrack();

        ShortMessage noteOn = new ShortMessage(
            ShortMessage.NOTE_ON, 0, 60, 100
        );

        ShortMessage noteOff = new ShortMessage(
            ShortMessage.NOTE_OFF, 0, 60, 100
        );

        track.add(new MidiEvent(noteOn, 0));
        track.add(new MidiEvent(noteOff, 480));

        return sequence;
    }

    @Test
    void rejectsNullFilePath() throws Exception {
        Sequence sequence = sequenceWithOneNote();

        assertThrows(
            NullPointerException.class,
            () -> exporter().save(sequence, null, "music.mid")
        );
    }

    @Test
    void rejectsNullFileName() throws Exception {
        Sequence sequence = sequenceWithOneNote();

        assertThrows(
            NullPointerException.class,
            () -> exporter().save(sequence, tempDir, null)
        );
    }

    @Test
    void rejectsNullSequence() {
        assertThrows(
            NullPointerException.class,
            () -> exporter().save(null, tempDir, "music.mid")
        );
    }

    @Test
    void savesMidiFile() throws Exception {
        Path file = tempDir.resolve("music.mid");

        exporter().save(sequenceWithOneNote(), tempDir, "music.mid");

        assertThat(file).exists().isRegularFile();
        assertThat(Files.size(file)).isPositive();
    }

    @Test
    void savesValidMidiFile() throws Exception {
        Path file = tempDir.resolve("music.mid");

        exporter().save(sequenceWithOneNote(), tempDir, "music.mid");

        Sequence imported = MidiSystem.getSequence(file.toFile());

        assertThat(imported.getTracks()).hasSize(1);
        assertThat(imported.getResolution()).isEqualTo(480);
        assertThat(imported.getDivisionType()).isEqualTo(Sequence.PPQ);

        Track track = imported.getTracks()[0];

        ShortMessage noteOn =
            (ShortMessage) track.get(0).getMessage();

        ShortMessage noteOff =
            (ShortMessage) track.get(1).getMessage();

        assertThat(noteOn.getCommand()).isEqualTo(ShortMessage.NOTE_ON);
        assertThat(noteOn.getData1()).isEqualTo(60);
        assertThat(track.get(0).getTick()).isZero();

        assertThat(noteOff.getCommand()).isEqualTo(ShortMessage.NOTE_OFF);
        assertThat(noteOff.getData1()).isEqualTo(60);
        assertThat(track.get(1).getTick()).isEqualTo(480);
    }

    @Test
    void savesMidiFileAsTypeOne() throws Exception {
        Path file = tempDir.resolve("music.mid");

        exporter().save(sequenceWithOneNote(), tempDir, "music.mid");

        MidiFileFormat format =
            MidiSystem.getMidiFileFormat(file.toFile());

        assertThat(format.getType()).isEqualTo(1);
    }

    @Test
    void savesFilesWithDifferentNames() throws Exception {
        MIDIFileExporter exporter = exporter();
        Sequence sequence = sequenceWithOneNote();

        exporter.save(sequence, tempDir, "first.mid");
        exporter.save(sequence, tempDir, "second.mid");

        assertThat(tempDir.resolve("first.mid")).exists();
        assertThat(tempDir.resolve("second.mid")).exists();
    }

    @Test
    void propagatesIOExceptionWhenDirectoryDoesNotExist()
            throws Exception {

        Path nonexistentDirectory = tempDir.resolve("nonexistent");

        Sequence sequence = sequenceWithOneNote();

        assertThrows(
            IOException.class,
            () -> exporter().save(
                sequence,
                nonexistentDirectory,
                "music.mid"
            )
        );
    }
}
