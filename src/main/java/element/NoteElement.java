package element;



public class NoteElement implements MusicElement {

    private final String note;

    public NoteElement ( String newNote){
        this.note = newNote;
    }

    public String getNote(){
        return this.note;
    }
}
