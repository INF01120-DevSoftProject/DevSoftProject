import java.util.HashMap;
import java.util.Map;

public class MusicTextTranslator {
    private final Map < Character, SheetMusicBuilder > musicDictionary;
    private boolean constructingSheetMusic; 

    public MusicTextTranslator(){
        this.constructingSheetMusic = false;
        this.musicDictionary = new HashMap<>();

        initMusicDictionary();

    }

    private void initMusicDictionary() {
        initNotesDict();
        initPausesDict();
        initInstrumentsDict();
        initVolumeDict();
        initOctavesDict();
    }

    private void initNotesDict(){
        musicDictionary.put('A', new Note("A"));  // Lá
        musicDictionary.put('B', new Note("B"));  // Si
        musicDictionary.put('C', new Note("C"));  // Dó
        musicDictionary.put('D', new Note("D"));  // Ré
        musicDictionary.put('E', new Note("E"));  // Mi
        musicDictionary.put('F', new Note("F"));  // Fá
        musicDictionary.put('G', new Note("G"));  // Sol
        musicDictionary.put('H', new Note("Bb")); // H é Si Bemol (Bb na notação padrão)
    }

    private void initPausesDict(){
        char[] pauseLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        for (char letter : pauseLetters) {
            musicDictionary.put(letter, new Pause());
        }
    }

    private void initInstrumentsDict(){ 
        musicDictionary.put('!', new Instrument(24, false)); // -> Bandoneon 

         
        char[] vowelsGaita = {'O', 'o', 'I', 'i', 'U', 'u'};
        for (char letter : vowelsGaita) {
            musicDictionary.put(letter, new Instrument(110, false)); // -> BagPipes
        }

        musicDictionary.put('\n', new Instrument(123,false)); // -> waves 

        musicDictionary.put(';', new Instrument(15, false)); // -> Tubular Bells 
        char[] oddDigits = {'1', '3', '5', '7', '9'};
        for (char num: oddDigits) {
            musicDictionary.put(num, new Instrument(15, false));
        }

        musicDictionary.put(',', new Instrument(114, false)); // -> Agogô

        char[] evenDigits = {'0', '2', '4', '6', '8'};
        for (char num : evenDigits) {
            musicDictionary.put(num, new Instrument(Character.getNumericValue(num), true)); 
        }

    }

    private void initVolumeDict(){ 
        musicDictionary.put(' ', new Volume()); // 2xVolume
    }

    private void initOctavesDict(){
        musicDictionary.put('?', new Octave()); // -> +1Octave
    }

    public boolean getConstructingSheetMusic(){
        return constructingSheetMusic;
    }

    private boolean setConstructingSheetMusic(boolean constructing){
        return this.constructingSheetMusic = constructing; 
    }

    private void translateCharToSheetMusicElem (char currentChar, CreateMusic musicMoment, Music music){

        SheetMusicBuilder build = musicDictionary.getOrDefault(currentChar, new DefaultElseAction());

        build.updateCreateMusicMoment(musicMoment);
        build.joinMusicElement(music, musicMoment);

    }

    public void translateTextToSheetMusic (String text, CreateMusic musicMoment, Music music){

        setConstructingSheetMusic(true);

        for (char currentChar : text.toCharArray()){
            translateCharToSheetMusicElem(currentChar, musicMoment, music);
        }

        setConstructingSheetMusic(false);

    }

   



}