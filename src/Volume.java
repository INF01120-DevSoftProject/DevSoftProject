public class Volume implements SheetMusicBuilder {

    private static final int MAX_VOLUME = 127;

    @Override
    public void updateCreateMusicMoment (CreateMusic musicMoment){
        
        musicMoment.increaseVolume(selectNewVolume(musicMoment));
        
    }

    @Override
    public void joinMusicElement (Music music, CreateMusic musicMoment){
        
        music.addToSheetMusic(":C(7," + Integer.toString(selectNewVolume(musicMoment)) + ")");
        
    }

    private int selectNewVolume (CreateMusic musicMoment){

        int newVolume = musicMoment.getCurrentVolume()*2;

        if (newVolume>MAX_VOLUME){
            newVolume = MAX_VOLUME;
        }

        return  newVolume;
            
    }
}

