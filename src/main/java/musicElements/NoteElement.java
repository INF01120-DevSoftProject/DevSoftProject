package musicElements;



public class NoteElement implements MusicElement {

    private final String note;
    private final DurationTime duration;
    private final boolean flat;
    private final boolean sharp;

    public NoteElement ( String newNote, DurationTime newDuration){

        char baseNote = newNote.charAt(0);

        if (baseNote < 'A' || baseNote > 'G'){
            throw new IllegalArgumentException("Nota inválida");
        }


        if (newDuration == null) throw new IllegalArgumentException("Duração inválida");


        if (newNote.length()==3){
             char extensionNote = newNote.charAt(1);
            
            if (extensionNote != 'b' & extensionNote != '#' ){
                throw new IllegalArgumentException("Extensão da nota inválida");
            }
            
            if (extensionNote == 'b'){
                this.flat = true;
                this.sharp = false;
            }
            else {
                this.flat = false;
                this.sharp = true;
            }
        }
        else {
            this.flat = false;
            this.sharp = false;
        }

        this.note = newNote;
        this.duration = newDuration;
    
    }

    public String getNote(){
        return this.note;
    }

     public char getNoteBase(){
        return this.note.charAt(0);
    }

    public boolean isFlat(){
        return this.flat;
    }

    public boolean isSharp(){
        return this.sharp;
    }
}
