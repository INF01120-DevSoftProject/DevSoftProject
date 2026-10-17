package translators.TextToMusic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.Accidentals;
import musicElements.BPMElement;
import musicElements.DurationTime;
import musicElements.MusicElement;
import musicElements.NoteElement;
import musicElements.Notes;
import musicElements.OctaveElement;
import musicElements.PauseElement;
import musicElements.VolumeElement;

public class TextToMusicTranslator {
    private TextToMusicCurrentState currentState = new TextToMusicCurrentState();
    private TextToMusicRules musicRules = new TextToMusicRules();

    public List<Voice> textToVoices(String text){

        Pattern token = Pattern.compile(this.musicRules.getREGEXToken());
        Matcher matcher = token.matcher(text);

        List<Voice> voices = new ArrayList<>();
        SheetMusic sheetMusic = new SheetMusic();
        int currentBPM = 120;

        while (matcher.find()) {
            if (matcher.group("note") != null) {
                String durationTimeString = matcher.group("duration");
                String noteString = matcher.group("note");
                String accidentalString = matcher.group("accidental");

                OctaveElement currentOctave = this.currentState.getCurrentOctave();
                DurationTime durationTime = this.musicRules.getDurationTime(durationTimeString);
                Notes note = this.musicRules.getNote(noteString);
                Accidentals accidental = this.musicRules.getAccidental(accidentalString);

                NoteElement newNote = new NoteElement(
                    note,
                    currentOctave,
                    durationTime,
                    accidental
                );
                sheetMusic.appendToSheetMusic(newNote);
                this.currentState.setLastMusicElement(newNote);

            }else if (matcher.group("pause") != null) {
                PauseElement pause = new PauseElement(DurationTime.QUARTER);
                sheetMusic.appendToSheetMusic(pause);
                this.currentState.setLastMusicElement(pause);
            } else if (matcher.group("octaveUp") != null) {
                // oitava atual + 1; se passar de 8, volta pra oitava base da voz
                int octave = this.currentState.getCurrentOctave().getOctave() + 1;
                if (octave > OctaveElement.MAX_OCTAVE) octave = VoicePreset.DEFAULT_OCTAVE;
                this.currentState.setCurrentOctave(new OctaveElement(octave));
            } else if (matcher.group("octaveDown") != null) {
                // oitava atual - 1, sem passar da mínima
                int octave = this.currentState.getCurrentOctave().getOctave() - 1;
                if (octave < OctaveElement.MIN_OCTAVE) octave = OctaveElement.MIN_OCTAVE;
                this.currentState.setCurrentOctave(new OctaveElement(octave));
            } else if (matcher.group("bpmUp") != null) {
                // BPM global + 10
                currentBPM = Math.min(currentBPM + 10, 999);
                sheetMusic.appendToSheetMusic(new BPMElement(currentBPM));
            } else if (matcher.group("bpmDown") != null) {
                // BPM global - 10
                currentBPM = Math.max(currentBPM - 10, 20);
                sheetMusic.appendToSheetMusic(new BPMElement(currentBPM));
            } else if (matcher.group("doubleVolume") != null) {
                // volume * 2, no máximo 127 (VolumeElement.MAX_VOLUME)
                VolumeElement current = this.currentState.getCurrentVolume();
                int volume = (current == null) ? VoicePreset.DEFAULT_VOLUME : current.getVolume();
                volume = Math.min(volume * 2, VolumeElement.MAX_VOLUME);
                VolumeElement newVolume = new VolumeElement(volume);
                this.currentState.setCurrentVolume(newVolume);
                sheetMusic.appendToSheetMusic(newVolume);

            } else if (matcher.group("else") != null) {
                // se lastMusicElement é NoteElement → repete; senão → pausa
                MusicElement last = this.currentState.getLastMusicElement();
                if (last instanceof NoteElement) {
                    sheetMusic.appendToSheetMusic(last);
                } else {
                    PauseElement pause = new PauseElement(DurationTime.QUARTER);
                    sheetMusic.appendToSheetMusic(pause);
                    this.currentState.setLastMusicElement(pause);
                }
            } else if (matcher.group("newVoice") != null) {
                // fecha a voz atual e começa outra do zero (BPM é global, continua)
                voices.add(new Voice(sheetMusic, new VoicePreset()));
                sheetMusic = new SheetMusic();
                this.currentState = new TextToMusicCurrentState();
            }

        }
        voices.add(new Voice(sheetMusic, new VoicePreset()));
        return voices;
    }

    public static void main(String[] args) {
        TextToMusicTranslator translator = new TextToMusicTranslator();
        List<Voice> voices = translator.textToVoices("CDE:2 F#G?A\nC:1 aE");

        for (int i = 0; i < voices.size(); i++) {
            System.out.println("Voz " + (i + 1) + ":");
            for (MusicElement element : voices.get(i).getSheetMusic().getSheetMusic()) {
                if (element instanceof NoteElement note) {
                    System.out.println("  " + note.getNote() + " " + note.getAccidental() + " " + note.getDuration());
                } else {
                    System.out.println("  " + element.getClass().getSimpleName());
                }
            }
        }
    }
}
