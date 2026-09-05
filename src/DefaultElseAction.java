public class DefaultElseAction implements SheetMusicBuilder {

    @Override
    public void updateCreateMusicMoment (CreateMusic musicMoment){

        if (musicMoment.getLastCharacterWasNote()){

            musicMoment.joinNote(musicMoment.getLastNotePlayed());

        }
        else{

            musicMoment.createPause();

        }
    }

    @Override
    public void joinMusicElement (Music music, CreateMusic musicMoment){

        if (musicMoment.getLastCharacterWasNote()){
            music.addToSheetMusic(musicMoment.getLastNotePlayed() + musicMoment.getDefaultBPM());
        }
        else{
             music.addToSheetMusic("Rw");
        }
    }
}