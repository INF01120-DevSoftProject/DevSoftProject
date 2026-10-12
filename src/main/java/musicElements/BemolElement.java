package musicElements;

/**
 * Bemol: abaixa uma nota em meio tom.
 *
 * Não guarda nenhum valor; o que importa é o que ele faz com a nota (applyTo).
 * Exemplos: C vira Cb, C# vira C, e uma nota que já é bemol (Bb) continua igual,
 * porque o NoteElement não aceita bemol duplo.
 */
public class BemolElement implements MusicElement{

    public NoteElement applyTo(NoteElement note){

        if (note == null) throw new IllegalArgumentException("Nota invalida");

        if (note.isFlat()){
            return note;
        }

        String baseNote = String.valueOf(note.getNoteBase());

        if (note.isSharp()){
            return new NoteElement(baseNote, note.getDuration());
        }

        return new NoteElement(baseNote + "b", note.getDuration());
    }
}
