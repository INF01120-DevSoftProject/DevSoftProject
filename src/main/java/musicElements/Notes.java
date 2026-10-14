package musicElements;

public enum Notes {
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    A("A"),
    B("B");

    private final String note;

    Notes (String newNote) {
        this.note = newNote;
    }

    public String getString(){
        return note;
    }
}
