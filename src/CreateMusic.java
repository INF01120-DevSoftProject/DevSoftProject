public class CreateMusic {
    private  String lastNotePlayed; 
    private  int currentInstrument;
    private  int currentVolume;
    private  int currentOcateve;
    private final String defaultBPM;
    private final int defaultOctave;
    private boolean lastCharacterWasNote;

    

    public CreateMusic (int initialInstrument, int initialVolume, int initialOctave, String bpm, Music orchestrate){
        this.currentInstrument = initialInstrument;
        this.currentVolume = initialVolume;
        this.currentOcateve = initialOctave;
        this.defaultOctave = initialOctave;
        this.defaultBPM = bpm;
        this.lastCharacterWasNote = false;

        orchestrate.addToSheetMusic(":CON(7,"+ Integer.toString(currentVolume) + ") I" +Integer.toString(initialInstrument) );
        
    }

    public String getLastNotePlayed () {
        return lastNotePlayed;
    }

    public String setLastNotePlayed (String note) {
        return this.lastNotePlayed = note;
    }

    public int getCurrentInstrument () {
        return currentInstrument;
    }

    public int setCurrentInstrument (int instrument) {
        return this.currentInstrument = instrument;
    }

    public int getCurrentVolume () {
        return currentVolume;
    }

    public int setCurrentVolume (int volume) {
        return this.currentVolume = volume;
    }

    public int getCurrentOctave () {
        return currentOcateve;
    }

    public int setCurrentOctave (int octave) {
        return this.currentOcateve = octave;
    }

    public int getDefaultOctave () {
        return defaultOctave;
    }

    public String getDefaultBPM () {
        return this.defaultBPM;
    }

    public boolean getLastCharacterWasNote (){
        return lastCharacterWasNote;
    }

    private boolean setLasCharacterWasNote (boolean wasNote){
        return this.lastCharacterWasNote = wasNote;
    }

    public void joinNote(String note){
        this.lastNotePlayed = note;
        setLasCharacterWasNote(true);
    }

    public void createPause(){
        setLasCharacterWasNote(false);
    }

    public void changeInstrument(int newInstrument){
        setLasCharacterWasNote(false);
        setCurrentInstrument(newInstrument);
    }

    public void increaseVolume(int newVolume){
        setLasCharacterWasNote(false);
        setCurrentVolume(newVolume);
    }

    public void changeOctave(int newOctave){
        setLasCharacterWasNote(false);
        setCurrentOctave(newOctave);
    }

}
