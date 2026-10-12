package musicElements;

/**
 * Tempo da nota: muda a duração de uma nota (semínima, colcheia...), mantendo a mesma nota.
 *
 * Exemplo: com DurationTime.EIGHTH, um C de semínima vira um C de colcheia.
 */
public class NoteTempoElement implements MusicElement{

    private final DurationTime duration;

    public NoteTempoElement(DurationTime newDuration){

        if (newDuration == null) throw new IllegalArgumentException("Duracao invalida");

        this.duration = newDuration;
    }

    public DurationTime getDuration(){
        return this.duration;
    }

    public NoteElement applyTo(NoteElement note){

        if (note == null) throw new IllegalArgumentException("Nota invalida");

        return new NoteElement(note.getNote(), this.duration);
    }
}
