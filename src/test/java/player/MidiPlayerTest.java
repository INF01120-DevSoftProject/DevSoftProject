package player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MidiPlayerTest {

    private Path createSampleMidiFile(Path dir) throws Exception {
        Path midiFile = dir.resolve("sample.mid");
        Sequence sequence = new Sequence(Sequence.PPQ, 24);
        Track track = sequence.createTrack();

        ShortMessage noteOn = new ShortMessage();
        noteOn.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
        track.add(new MidiEvent(noteOn, 0));

        ShortMessage noteOff = new ShortMessage();
        noteOff.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
        track.add(new MidiEvent(noteOff, 240));

        MidiSystem.write(sequence, 1, midiFile.toFile());
        return midiFile;
    }

    @Test
    void isAPlayer() {
        try (MidiPlayer player = new MidiPlayer()) {
            assertThat(player).isInstanceOf(Player.class);
        }
    }

    @Test
    void throwsExceptionWhenSequencerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new MidiPlayer(null));
    }

    @Test
    void throwsExceptionWhenPathIsNull() {
        try (MidiPlayer player = new MidiPlayer()) {
            assertThrows(IllegalArgumentException.class, () -> player.load(null));
        }
    }

    @Test
    void throwsExceptionWhenFileDoesNotExist(@TempDir Path tempDir) {
        try (MidiPlayer player = new MidiPlayer()) {
            Path nonExistent = tempDir.resolve("non_existent.mid");
            assertThrows(IllegalArgumentException.class, () -> player.load(nonExistent));
        }
    }

    @Test
    void throwsExceptionWhenOperatingBeforeLoading() {
        try (MidiPlayer player = new MidiPlayer()) {
            assertThrows(IllegalStateException.class, player::play);
            assertThrows(IllegalStateException.class, player::pause);
            assertThrows(IllegalStateException.class, player::stop);
            assertThrows(IllegalStateException.class, () -> player.seek(100));
            assertThrows(IllegalStateException.class, player::getCurrentTime);
            assertThrows(IllegalStateException.class, player::getDuration);
            assertThat(player.isPlaying()).isFalse();
        }
    }

    @Test
    void throwsExceptionWhenSeekTimeIsNegative(@TempDir Path tempDir) throws Exception {
        Path midiFile = createSampleMidiFile(tempDir);
        try (MidiPlayer player = new MidiPlayer()) {
            player.load(midiFile);
            assertThrows(IllegalArgumentException.class, () -> player.seek(-1));
        }
    }

    @Test
    void loadsAndControlsPlayback(@TempDir Path tempDir) throws Exception {
        Path midiFile = createSampleMidiFile(tempDir);
        try (MidiPlayer player = new MidiPlayer()) {
            player.load(midiFile);

            assertThat(player.getDuration()).isGreaterThanOrEqualTo(0);
            assertThat(player.getCurrentTime()).isZero();
            assertThat(player.isPlaying()).isFalse();

            player.play();
            assertThat(player.isPlaying()).isTrue();

            player.pause();
            assertThat(player.isPlaying()).isFalse();

            player.seek(100);
            assertThat(player.getCurrentTime()).isGreaterThanOrEqualTo(0);

            player.stop();
            assertThat(player.isPlaying()).isFalse();
            assertThat(player.getCurrentTime()).isZero();
        }
    }

    @Test
    void throwsExceptionWhenOperatingAfterClose(@TempDir Path tempDir) throws Exception {
        Path midiFile = createSampleMidiFile(tempDir);
        MidiPlayer player = new MidiPlayer();
        player.load(midiFile);
        player.close();

        assertThrows(IllegalStateException.class, player::play);
        assertThrows(IllegalStateException.class, player::pause);
        assertThrows(IllegalStateException.class, player::stop);
        assertThrows(IllegalStateException.class, () -> player.seek(100));
        assertThrows(IllegalStateException.class, player::getCurrentTime);
        assertThrows(IllegalStateException.class, player::getDuration);
        assertThat(player.isPlaying()).isFalse();
    }
}
