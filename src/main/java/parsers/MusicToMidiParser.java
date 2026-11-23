package parsers;

import musicElements.*;
import music.*;
import builders.MidiSequenceBuilder;

import javax.sound.midi.*;


public class MusicToMidiParser implements MusicParser<Sequence> {

    private MidiSequenceBuilder midiSequenceBuilder;
    private boolean initialBpmWritten;

    public Sequence convertMusicToFile(Music music) {
        if (music == null) throw new NullPointerException("music can't be null") ; 
        
        midiSequenceBuilder = new MidiSequenceBuilder();
        initialBpmWritten = false;

        for (Voice voice : music.getVoices()) {
            midiSequenceBuilder.addTrack();
            addSheetMusicToMIDI(voice.getSheetMusic(), music.getDefaultBPMElement().getBPM());
        }

        return midiSequenceBuilder.build();
    }

    private void addSheetMusicToMIDI(SheetMusic sheetMusic, int initialBpm) {
        if (sheetMusic == null) throw new NullPointerException("sheetMusic can't be null") ; 

        boolean firstBpm = true;
        boolean voiceStarted = false;

        for (MusicElement element : sheetMusic.getSheetMusic()) {
            if (element instanceof BPMElement bpm) {
                // Cada voz recebe o BPM inicial no preset, mas o MIDI precisa dele só uma vez.
                boolean isInitialBpm = firstBpm && !voiceStarted && bpm.getBPM() == initialBpm;
                firstBpm = false;
                if (isInitialBpm) {
                    if (initialBpmWritten) continue;
                    initialBpmWritten = true;
                }
            }
            if (element instanceof NoteElement || element instanceof PauseElement) {
                voiceStarted = true;
            }
            element.addMusicElementToMIDI(midiSequenceBuilder);
        }
    }


}
