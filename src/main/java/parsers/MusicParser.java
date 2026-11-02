package parsers;

import music.Music;

public interface MusicParser<T> {

    public T convertMusicToFile(Music music) ;

}
