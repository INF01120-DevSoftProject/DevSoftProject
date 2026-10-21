package translators.TextToMusic;

import java.util.ArrayList;
import java.util.List;
import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.MusicElement;
import musicElements.Octave;
import musicElements.VolumeElement;

public class TextToMusicContext {
    public static final int DEFAULT_BPM = 120;

    //Pensar em como tirar essa informação repetida daqui
    private static final List<InstrumentType> VOICE_INSTRUMENT_CYCLE = List.of(
        InstrumentType.STRINGS,
        InstrumentType.KEYS,
        InstrumentType.PLUCKED,
        InstrumentType.WINDS);

    private Octave currentOctave;
    private VolumeElement currentVolume;
    private InstrumentElement currentInstrument;
    private final List<Voice> voices = new ArrayList<>();
    private Voice currentVoice;
    private SheetMusic sheetMusic;
    private MusicElement lastMusicElement;
    private int bpm = DEFAULT_BPM;


    public TextToMusicContext() {
        startVoice();
    }

    public Octave getBaseOctave() {
        return currentVoice.getVoicePreset().getDefaultOctave();
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
        InstrumentType type = VOICE_INSTRUMENT_CYCLE.get(voices.size() % VOICE_INSTRUMENT_CYCLE.size());
        currentVoice = new Voice(type);
        VoicePreset preset = currentVoice.getVoicePreset();
        sheetMusic = currentVoice.getSheetMusic();
        currentOctave = preset.getDefaultOctave();
        currentVolume = preset.getVolumeElement();
        currentInstrument = preset.getInstrumentElement();
        lastMusicElement = null;
        bpm = DEFAULT_BPM;
    }

    public List<Voice> finish() {
        closeVoice();
        return voices;
    }

    public void closeVoice() {
        voices.add(currentVoice);
        startVoice();
    }
}
