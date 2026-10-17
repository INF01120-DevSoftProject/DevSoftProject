package translators.TextToMusic;

import music.VoicePreset;
import musicElements.InstrumentElement;
import musicElements.MusicElement;
import musicElements.Octave;
import musicElements.VolumeElement;

public class TextToMusicCurrentState {
    private Octave currentOctave = new Octave(VoicePreset.DEFAULT_OCTAVE);
    private VolumeElement currentVolume;
    private InstrumentElement currentInstrument;
    private MusicElement lastMusicElement;

    public Octave getCurrentOctave() {
        return currentOctave;
    }

    public void setCurrentOctave(Octave currentOctave) {
        this.currentOctave = currentOctave;
    }

    public VolumeElement getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(VolumeElement currentVolume) {
        this.currentVolume = currentVolume;
    }

    public InstrumentElement getCurrentInstrument() {
        return currentInstrument;
    }

    public void setCurrentInstrument(InstrumentElement currentInstrument) {
        this.currentInstrument = currentInstrument;
    }

    public MusicElement getLastMusicElement() {
        return lastMusicElement;
    }

    public void setLastMusicElement(MusicElement lastMusicElement) {
        this.lastMusicElement = lastMusicElement;
    }
}
