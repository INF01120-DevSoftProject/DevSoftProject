package music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import musicElements.MusicElement;

public class SheetMusic {

    private final List<MusicElement> sheetMusic;

    public SheetMusic(){
        this.sheetMusic = new ArrayList<>();
    }

    public void appendToSheetMusic(MusicElement musicElement){

        if (musicElement == null) throw new IllegalArgumentException("Elemento musical invalido");

        this.sheetMusic.add(musicElement);
    }

    public List<MusicElement> getSheetMusic(){
        return Collections.unmodifiableList(this.sheetMusic); // legal usar unmodifiableList, pois assim protege a lista
    }
}
