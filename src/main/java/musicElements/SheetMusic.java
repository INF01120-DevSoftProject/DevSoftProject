package musicElements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Partitura: a sequência de elementos musicais (notas, pausas, volume...) na ordem em que serão tocados.
 *
 * O TextToSheetMusicTranslator preenche a partitura chamando appendToSheetMusic a cada caractere,
 * e o SheetMusicToMIDITranslator percorre getSheetMusic() para gerar a String do JFugue.
 */
public class SheetMusic {

    private final List<MusicElement> sheetMusic;

    public SheetMusic(){
        this.sheetMusic = new ArrayList<>();
    }

    public void appendToSheetMusic(MusicElement musicElement){

        if (musicElement == null) throw new IllegalArgumentException("Elemento musical invalido");

        this.sheetMusic.add(musicElement);
    }

    // Somente leitura: elementos só entram pelo appendToSheetMusic
    public List<MusicElement> getSheetMusic(){
        return Collections.unmodifiableList(this.sheetMusic);
    }
}
