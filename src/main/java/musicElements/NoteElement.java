package musicElements;

public class NoteElement implements MusicElement {

    private final Notes note;
    private final Octave octave;
    private final DurationTime duration;
    private final Accidentals accidental;

    public NoteElement(Notes newNote, Octave newOctave, DurationTime newDuration, Accidentals newAccidental){

        if (newNote == null) throw new IllegalArgumentException("Nota invalida");

        if (newOctave == null) throw new IllegalArgumentException("Oitava invalida");

        if (newDuration == null) throw new IllegalArgumentException("Duracao invalida");

        if (newAccidental == null) throw new IllegalArgumentException("Acidente invalido");

        this.note = newNote;
        this.octave = newOctave;
        this.duration = newDuration;
        this.accidental = newAccidental;

    }

    public Notes getNote(){
        return this.note;
    }

    public String getNoteString(){
        return  this.note.getString();
    }

    public Octave getOctave(){
        return this.octave;
    }

    public int getOctaveValue(){
        return this.octave.getOctaveInt();
    }

    public DurationTime getDuration(){
        return  this.duration;
    }

    public Accidentals getAccidental(){
        return this.accidental;
    }
}
