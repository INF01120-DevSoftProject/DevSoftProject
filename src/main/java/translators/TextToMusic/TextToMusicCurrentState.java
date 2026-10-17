package translators.TextToMusic;

import music.VoicePreset;
import musicElements.InstrumentElement;
import musicElements.MusicElement;
import musicElements.OctaveElement;
import musicElements.VolumeElement;

public class TextToMusicCurrentState {
    private OctaveElement currentOctave = new OctaveElement(VoicePreset.DEFAULT_OCTAVE);
    private VolumeElement currentVolume;
    private InstrumentElement currentInstrument;
    private MusicElement lastMusicElement;

    public OctaveElement getCurrentOctave() {
        return currentOctave;
    }

    public void setCurrentOctave(OctaveElement currentOctave) {
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
