package musicElements;

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
