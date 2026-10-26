package translators.TextToMusic;

import java.util.List;
import music.Music;
import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.BPMElement;
import musicElements.InstrumentElement;
import musicElements.MusicElement;
import musicElements.Octave;
import musicElements.VolumeElement;

public class TextToMusicContext {
    public static final int DEFAULT_BPM = Music.DEFAULT_BPM;

    private Octave currentOctave;
    private VolumeElement currentVolume;
    private InstrumentElement currentInstrument;
    private final List<Voice> voices;
    private final List<VoicePreset> chosenPresets;
    private int voiceIndex = 0;
    private Voice currentVoice;
    private SheetMusic sheetMusic;
    private MusicElement lastMusicElement;
    private int bpm = DEFAULT_BPM;
    private boolean presetWritten;


    public TextToMusicContext(List<Voice> voices) {
        this(voices, null);
    }

    // chosenPresets.get(i) é o preset da voz i; o que não tiver usa o preset padrão da voz
    public TextToMusicContext(List<Voice> voices, List<VoicePreset> chosenPresets) {
        this.voices = voices;
        this.chosenPresets = chosenPresets;
        startVoice();
    }

    // o preset recebido para a voz atual, ou o padrão dela se não recebeu nenhum
    private VoicePreset currentPreset() {
        if (chosenPresets != null && voiceIndex < chosenPresets.size() && chosenPresets.get(voiceIndex) != null)
            return chosenPresets.get(voiceIndex);
        return currentVoice.getVoicePreset();
    }

    public Octave getBaseOctave() {
        return currentPreset().getDefaultOctave();
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
        writePresetBeforeFirstElement();
        this.sheetMusic.appendToSheetMusic(musicElement);
        lastMusicElement = musicElement;
    }

    public void appendControlElement(MusicElement musicElement) {
        writePresetBeforeFirstElement();
        this.sheetMusic.appendToSheetMusic(musicElement);
    }

    private void startVoice() {
        currentVoice = voices.get(voiceIndex);
        VoicePreset preset = currentPreset();
        sheetMusic = currentVoice.getSheetMusic();
        currentOctave = preset.getDefaultOctave();
        currentVolume = preset.getVolumeElement();
        currentInstrument = preset.getInstrumentElement();
        lastMusicElement = null;
        bpm = DEFAULT_BPM;
        presetWritten = false;
    }

    // a sheet de cada voz começa com os valores do preset (instrumento, volume e BPM padrão);
    // nas vozes vazias não aplica o preset;
    private void writePresetBeforeFirstElement() {
        if (presetWritten) return;
        presetWritten = true;
        VoicePreset preset = currentPreset();
        sheetMusic.appendToSheetMusic(preset.getInstrumentElement());
        sheetMusic.appendToSheetMusic(preset.getVolumeElement());
        sheetMusic.appendToSheetMusic(new BPMElement(DEFAULT_BPM));
    }

    public void closeVoice() {
        voiceIndex++;
        startVoice();
    }
}
