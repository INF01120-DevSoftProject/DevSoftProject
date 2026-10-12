package musicElements;

public class DefaultElement implements MusicElement{

    private final MusicElement typeElement;

    public DefaultElement(MusicElement element){

        if (element == null) throw new IllegalArgumentException("Elemento musical invalido");

        this.typeElement = element;
    }

    public MusicElement getTypeElement(){
        return this.typeElement;
    }
}
