package player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MidiPlayer implements Player {

    private final Sequencer sequencer;
    private boolean isLoaded;

    private Path currentMusicPath;
    private String currentMusicName;

    public MidiPlayer() {
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            this.isLoaded = false;
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("MIDI device unavailable.", e);
        }
    }

    public MidiPlayer(Sequencer sequencer) {
        if (sequencer == null) {
            throw new IllegalArgumentException("Sequencer cannot be null.");
        }
        this.sequencer = sequencer;
        if (!this.sequencer.isOpen()) {
            try {
                this.sequencer.open();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException("MIDI device unavailable.", e);
            }
        }
        this.isLoaded = false;
    }

    @Override
    public void load(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("File path cannot be null.");
        }
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        if (this.sequencer.isRunning()) {
            this.sequencer.stop();
        }
        try {
            Sequence sequence = MidiSystem.getSequence(path.toFile());
            this.sequencer.setSequence(sequence);
            this.sequencer.setMicrosecondPosition(0);
            this.isLoaded = true;
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException("Failed to load MIDI file: " + path, e);
        }

        setCurrentMusic(path);
    }

    @Override
    public void play() {
        ensureLoaded();

        if (this.sequencer.getMicrosecondPosition() >= this.sequencer.getMicrosecondLength()) {
            this.sequencer.setMicrosecondPosition(0);
        }

        this.sequencer.start();
    }

    @Override 
    public Sequencer getSequencer() {
        return this.sequencer;
    }

    @Override
    public void pause() {
        ensureLoaded();
        this.sequencer.stop();
    }

    @Override
    public void stop() {
        ensureLoaded();
        this.sequencer.stop();
        this.sequencer.setMicrosecondPosition(0);
    }

    @Override
    public void seek(long milliseconds) {
        ensureLoaded();
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Seek time cannot be negative: " + milliseconds);
        }
        this.sequencer.setMicrosecondPosition(milliseconds * 1000L);
    }

    @Override
    public long getCurrentTime() {
        ensureLoaded();
        return this.sequencer.getMicrosecondPosition() / 1000L;
    }


    @Override
    public long getDuration() {
        ensureLoaded();
        return this.sequencer.getMicrosecondLength() / 1000L;
    }

    @Override
    public boolean isPlaying() {
        return this.isLoaded && this.sequencer.isOpen() && this.sequencer.isRunning();
    }

    @Override
    public void close() {
        if (this.sequencer.isOpen()) {
            if (this.sequencer.isRunning()) {
                this.sequencer.stop();
            }
            this.sequencer.close();
        }
        this.isLoaded = false;
    }

    public Path getCurrentMusicPath() {
        return currentMusicPath;
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    private void ensureLoaded() {
        if (!this.sequencer.isOpen()) {
            throw new IllegalStateException("Player is closed.");
        }
        if (!this.isLoaded || this.sequencer.getSequence() == null) {
            throw new IllegalStateException("No MIDI sequence loaded.");
        }
    }

    private void setCurrentMusic(Path path) {
        currentMusicPath = path;
        currentMusicName = path.getFileName().toString();
    }
}