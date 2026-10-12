package musicElements;

public class PauseElement {
    
    private final DurationTime duration;

    public PauseElement(DurationTime newDuration){

        if (newDuration == null) throw new IllegalArgumentException("Duracao invalida");

        this.duration = newDuration;
    }

    public DurationTime getDuration(){
        return this.duration;
    }
}
