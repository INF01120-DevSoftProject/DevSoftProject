package parsers;

import musicElements.*;
import music.*;
import builders.MidiSequenceBuilder;

import javax.sound.midi.*;


public class MusicToMidiParser implements MusicParser<Sequence> {

    private MidiSequenceBuilder midiSequenceBuilder;

    public Sequence convertMusicToFile(Music music) {
        if (music == null) throw new NullPointerException("music can't be null") ; 
        
        midiSequenceBuilder = new MidiSequenceBuilder();

        for (Voice voice : music.getVoices()) {
            midiSequenceBuilder.addTrack();
            addSheetMusicToMIDI(voice.getSheetMusic());
        }

        return midiSequenceBuilder.build();
    }

    private void addSheetMusicToMIDI(SheetMusic sheetMusic) {
        if (sheetMusic == null) throw new NullPointerException("sheetMusic can't be null") ; 

        for (MusicElement element : sheetMusic.getSheetMusic()) {
            element.addMusicElementToMIDI(midiSequenceBuilder);
        }
    }


}
