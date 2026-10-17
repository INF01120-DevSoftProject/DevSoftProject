package translators.TextToMusic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import musicElements.Accidentals;
import musicElements.DurationTime;
import musicElements.Notes;

public class TextToMusicRules {

    private String REGEXToken;

    private Map<String, String> elementsMapping = new LinkedHashMap<>();
    {
        elementsMapping.put("note",         "ABCDEFG");
        elementsMapping.put("accidental",   "#b");
        elementsMapping.put("duration",     "1234567");
        elementsMapping.put("pause",        "acdefgh");

        elementsMapping.put("octaveUp",     "?.");
        elementsMapping.put("octaveDown",   "V");

        elementsMapping.put("bpmUp",        ">");
        elementsMapping.put("bpmDown",      "<");

        elementsMapping.put("doubleVolume", " ");

        elementsMapping.put("newVoice", "\n");
        elementsMapping.put("startDelay",   "0123456789");

    }
    private String semNull(String s) {
        return s == null ? "" : s;
    }

    public String getElementChars(String elementType) {
        String chars = elementsMapping.get(elementType);
        return chars;
    }

    //Dict das Rules do REGEX usadas no TextToMusicTranslator, sendo salva como: NomeGrupo , REGEX
    private Map<String, String> REGEXRules = new LinkedHashMap<>();
    {
        REGEXRules.put("note",         "(?<note>["         + getElementChars("note")         + "])");
        REGEXRules.put("accidental",   "(?<accidental>["   + getElementChars("accidental")   + "])?");
        REGEXRules.put("duration",     "(?::(?<duration>[" + getElementChars("duration")     + "]))?");
        REGEXRules.put("pause",        "(?<pause>["        + getElementChars("pause")        + "])");

        REGEXRules.put("octaveUp",     "(?<octaveUp>["     + getElementChars("octaveUp")     + "])");
        REGEXRules.put("octaveDown",   "(?<octaveDown>["   + getElementChars("octaveDown")   + "])");

        REGEXRules.put("bpmUp",        "(?<bpmUp>["        + getElementChars("bpmUp")        + "])");
        REGEXRules.put("bpmDown",      "(?<bpmDown>["      + getElementChars("bpmDown")      + "])");

        REGEXRules.put("doubleVolume", "(?<doubleVolume>[" + getElementChars("doubleVolume") + "])");

        REGEXRules.put("newVoice",     "(?<newVoice>["     + getElementChars("newVoice")     + "])");
        REGEXRules.put("startDelay",   "^\\[(?<startDelay>[" + getElementChars("startDelay") + "]+)\\] ?");

        REGEXRules.put("else",         "(?<else>.)");
    }

    //Dict das Rules do DurationTime usadas no TextToMusicTranslator, sendo salva como: InputRecebido , DurationTime
    private Map<String, DurationTime> durationTimeRules = new LinkedHashMap<>();
    {
        durationTimeRules.put("1", DurationTime.WHOLE);
        durationTimeRules.put("2", DurationTime.HALF);
        durationTimeRules.put("3", DurationTime.QUARTER);
        durationTimeRules.put("4", DurationTime.EIGHTH);
        durationTimeRules.put("5", DurationTime.SIXTEENTH);
        durationTimeRules.put("6", DurationTime.THIRTY_SECOND);
        durationTimeRules.put("7", DurationTime.SIXTY_FOURTH);
    }

    //Dict das Rules dos accidentals usadas no TextToMusicTranslator, sendo salva como: InputRecebido , Accidentals
    private Map<String, Accidentals> accidentalsRules = new LinkedHashMap<>();
    {
        accidentalsRules.put("b", Accidentals.FLAT);
        accidentalsRules.put("#", Accidentals.SHARP);
        accidentalsRules.put("", Accidentals.NONE);
    }

    //Dict das Rules dos Notes usadas no TextToMusicTranslator, sendo salva como: InputRecebido , Notes
    private Map<String, Notes> notesRules = new LinkedHashMap<>();
    {
        notesRules.put("A", Notes.A);
        notesRules.put("B", Notes.B);
        notesRules.put("C", Notes.C);
        notesRules.put("D", Notes.D);
        notesRules.put("E", Notes.E);
        notesRules.put("F", Notes.F);
        notesRules.put("G", Notes.G);
    }

    //Cria um Token para o REGEX, é a unção de todas as regras em uma String
    private String createREGEXToken() {
        StringJoiner joiner = new StringJoiner("|");
        //Token das NOTAS
        joiner.add(REGEXRules.get("note") + REGEXRules.get("accidental") + REGEXRules.get("duration"));

        //Token de OUTRAS regras
        for (Map.Entry<String, String> rule : REGEXRules.entrySet()) {
            if (!List.of("note", "accidental", "duration", "startDelay").contains(rule.getKey()))
                joiner.add(rule.getValue());
        }
        return joiner.toString();
    }

    public String getREGEXToken() {
        return this.REGEXToken;
    }

    public String getStartDelayREGEX() {
        return REGEXRules.get("startDelay");
    }

    public Notes getNote(String input) {
        return notesRules.get(input);
    }

    public DurationTime getDurationTime(String input) {
        return durationTimeRules.getOrDefault(input, DurationTime.QUARTER);
    }

    public Accidentals getAccidental(String input) {
        return accidentalsRules.getOrDefault(input, Accidentals.NONE);
    }

    { REGEXToken = createREGEXToken(); }
}
