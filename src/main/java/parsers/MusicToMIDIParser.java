package parsers;

import musicElements.*;
import music.*;
import builders.MIDIBuilder;

import javax.sound.midi.*;


public class MusicToMIDIParser {

    private MIDIBuilder midiBuilder;

    public Sequence convertMusicToMIDI(Music music) {
        if (music == null) throw new NullPointerException("music can't be null") ; 
        
        midiBuilder = new MIDIBuilder();

        boolean isFirstTrack = true;
        for (Voice voice : music.getVoices()) {
            midiBuilder.addTrack();

            // No MIDI o andamento e global: o BPM padrao da musica vai uma unica vez, no inicio da primeira track
            if (isFirstTrack) {
                midiBuilder.setBPM(music.getDefaultBPMElement());
                isFirstTrack = false;
            }

            addVoicePresetToMIDI(voice.getVoicePreset());
            addSheetMusicToMIDI(voice.getSheetMusic());
        }

        return midiBuilder.build();
    }

    // Sem isso a track usa os padroes do MIDI (instrumento 0 = piano, volume 100) e ignora o preset da voz
    private void addVoicePresetToMIDI(VoicePreset voicePreset) {
        if (voicePreset == null) throw new NullPointerException("voicePreset can't be null") ; 

        midiBuilder.setInstrument(voicePreset.getInstrumentElement());
        midiBuilder.setVolume(voicePreset.getVolumeElement());
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
