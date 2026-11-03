package parsers;

import musicElements.*;
import music.*;
import builders.MidiSequenceBuilder;

import javax.sound.midi.*;


public class MusicToMidiParser implements MusicParser<Sequence> {

    private MidiSequenceBuilder midiBuilder;

    public Sequence convertMusicToFile(Music music) {
        if (music == null) throw new NullPointerException("music can't be null") ; 
        
        midiBuilder = new MidiSequenceBuilder();

        for (Voice voice : music.getVoices()) {
            midiBuilder.addTrack();
            addSheetMusicToMIDI(voice.getSheetMusic());
        }

        return midiBuilder.build();
    }

    private void addSheetMusicToMIDI(SheetMusic sheetMusic) {
        if (sheetMusic == null) throw new NullPointerException("sheetMusic can't be null") ; 

        for (MusicElement element : sheetMusic.getSheetMusic()) {
            addMusicElementToMIDI(element);
        }
    }

    private void addMusicElementToMIDI(MusicElement element) {  
        switch(element) {
            case NoteElement note             -> midiBuilder.addNote(note);
            case InstrumentElement instrument -> midiBuilder.setInstrument(instrument);
            case PauseElement pause           -> midiBuilder.addPause(pause);
            case BPMElement bpm               -> midiBuilder.setBPM(bpm);
            case VolumeElement volume         -> midiBuilder.setVolume(volume);

            default -> throw new IllegalArgumentException("Invalid element");
        }
    }
}
