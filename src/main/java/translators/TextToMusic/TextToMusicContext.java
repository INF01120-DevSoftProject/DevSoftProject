package translators.TextToMusic;

import java.util.ArrayList;
import java.util.List;
import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.InstrumentElement;
import musicElements.MusicElement;
import musicElements.Octave;
import musicElements.VolumeElement;

public class TextToMusicContext {
    public static final int DEFAULT_BPM = 120;

    private Octave currentOctave;
    private VolumeElement currentVolume;
    private InstrumentElement currentInstrument;
    private final List<Voice> voices = new ArrayList<>();
    private SheetMusic sheetMusic;
    private MusicElement lastMusicElement;
    private int bpm = DEFAULT_BPM;


    public TextToMusicContext() {
        startVoice();
    }

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

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void appendToSheetMusic(MusicElement musicElement) {
        this.sheetMusic.appendToSheetMusic(musicElement);
        lastMusicElement = musicElement;
    }

    public void appendControlElement(MusicElement musicElement) {
        this.sheetMusic.appendToSheetMusic(musicElement);
    }

    private void startVoice() {
        sheetMusic = new SheetMusic();
        currentOctave = new Octave(VoicePreset.DEFAULT_OCTAVE);
        currentVolume = new VolumeElement(VoicePreset.DEFAULT_VOLUME);
        currentInstrument = null;
        lastMusicElement = null;
        bpm = DEFAULT_BPM;
    }

    public List<Voice> finish() {
        closeVoice();
        return voices;
    }

    public void closeVoice() {
        voices.add(new Voice(sheetMusic, new VoicePreset()));
        startVoice();
    }
}
