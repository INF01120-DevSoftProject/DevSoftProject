package musicElements;

import builders.MidiSequenceBuilder;

public interface MusicElement {
    void addMusicElementToMIDI(MidiSequenceBuilder builder);
}
