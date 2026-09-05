public class Note implements SheetMusicBuilder {
    private final String newNote;

    public Note (String note){
        this. newNote = note;
    }

    @Override
    public void updateCreateMusicMoment (CreateMusic musicMoment){
        musicMoment.joinNote(newNote);
    }

    @Override
    public void joinMusicElement (Music music, CreateMusic musicMoment){
        music.addToSheetMusic(newNote + Integer.toString(musicMoment.getCurrentOctave()) + musicMoment.getDefaultBPM());
    }






}


