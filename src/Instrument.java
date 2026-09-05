public class Instrument implements SheetMusicBuilder {
     private final int newInstrument;
     private final boolean addMidiNumber;

    public Instrument (int instrument, boolean addNumberToInstrument){
        this.newInstrument= instrument;
        this.addMidiNumber = addNumberToInstrument;
    }

    @Override
    public void updateCreateMusicMoment (CreateMusic musicMoment){
    
        if (addMidiNumber){
            musicMoment.changeInstrument(musicMoment.getCurrentInstrument()+newInstrument);
        }
        else{
            musicMoment.changeInstrument(newInstrument);
        }
    }

    @Override
    public void joinMusicElement (Music music, CreateMusic musicMoment){
        if (addMidiNumber) {

            int addInstrumentValues = newInstrument+musicMoment.getCurrentInstrument();

            music.addToSheetMusic("I"+Integer.toString(addInstrumentValues));

        }
        else{
             music.addToSheetMusic("I"+newInstrument);
        }
       
    }
}
