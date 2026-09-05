public class Pause implements SheetMusicBuilder {

    @Override
    public void updateCreateMusicMoment (CreateMusic musicMoment){
        musicMoment.createPause();
    }

    @Override
    public void joinMusicElement (Music music, CreateMusic musicMoment){
        music.addToSheetMusic("Rw");
    }
}
    

