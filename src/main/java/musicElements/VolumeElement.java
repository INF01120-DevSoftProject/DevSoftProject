package musicElements;

public class VolumeElement implements MusicElement{
    
    private final int volume;

    public static final int MIN_VOLUME = 0;
    public static final int MAX_VOLUME = 127;

    public VolumeElement(int newVolume){

        if(newVolume < MIN_VOLUME || newVolume > MAX_VOLUME ){
            throw new IllegalArgumentException("Volume inválido");
        }

        this.volume  = newVolume;
    }

    public int getVolume(){
        return this.volume;
    }
}
