package musicElements;

/**
 * Elemento "padrão": embrulha outro elemento musical.
 *
 * Usado quando um caractere não tem regra própria e a ação depende do contexto
 * (por exemplo, repetir a última nota ou fazer uma pausa).
 */
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
