public class Octave implements SheetMusicBuilder {

    private static final int MAX_OCTAVE = 10;

    @Override
    public void updateCreateMusicMoment (CreateMusic musicMoment){

        musicMoment.changeOctave(selectNewOctave(musicMoment));

    }

    @Override
    public void joinMusicElement (Music music, CreateMusic musicMoment){

        music.addToSheetMusic("K"+Integer.toString(selectNewOctave(musicMoment)));
    
    }

    private int selectNewOctave (CreateMusic musicMoment){

        int newOctave = musicMoment.getCurrentOctave()+1;

        if (newOctave>MAX_OCTAVE){
            newOctave = musicMoment.getDefaultOctave();
        }
        
        return newOctave;
    }
}
