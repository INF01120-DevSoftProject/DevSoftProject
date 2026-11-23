package translators.TextToMusic;

public enum MappingType {
    NOTE("note"),
    ACCIDENTAL("accidental"),
    DURATION("duration"),
    PAUSE("pause"),
    OCTAVE_UP("octaveUp"),
    OCTAVE_DOWN("octaveDown"),
    BPM_UP("bpmUp"),
    BPM_DOWN("bpmDown"),
    DOUBLE_VOLUME("doubleVolume"),
    NEW_VOICE("newVoice"),
    INSTRUMENT("instrument"),
    START_DELAY("startDelay"),
    TEMPO("tempo"),
    ELSE("else");

    private final String groupName;

    MappingType(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public String toString() {
        return groupName;
    }
}
