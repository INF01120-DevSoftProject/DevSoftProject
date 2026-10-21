package translators.TextToMusic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import musicElements.Accidentals;
import musicElements.BPMElement;
import musicElements.DurationTime;
import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.MusicElement;
import musicElements.NoteElement;
import musicElements.Notes;
import musicElements.Octave;
import musicElements.PauseElement;
import musicElements.VolumeElement;

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

        elementsMapping.put("newVoice",     "\n");

        elementsMapping.put("instrument",   "02468");
        elementsMapping.put("startDelay",   "0123456789");

    }

    //Dict das Actions de cada grupo do REGEX, sendo salva como: NomeGrupo , Action
    //A ordem importa: "note" primeiro e "else" por último
    private Map<String, TextToMusicActions> elementsActionMapping = new LinkedHashMap<>();
    {
        elementsActionMapping.put("note", (matcher, context) -> {
            NoteElement newNote = new NoteElement(
                getNote(matcher.group("note")),
                context.getCurrentOctave(),
                getDurationTime(matcher.group("duration")),
                getAccidental(matcher.group("accidental"))
            );
            context.appendToSheetMusic(newNote);
        });

        elementsActionMapping.put("pause", (matcher, context) ->
            context.appendToSheetMusic(new PauseElement(DurationTime.QUARTER))
        );

        elementsActionMapping.put("octaveUp", (matcher, context) -> {
            // oitava atual + 1; se passar de 8, volta pra oitava base da voz
            int octave = context.getCurrentOctave().getOctaveInt() + 1;
            if (octave > Octave.MAX_OCTAVE) octave = context.getBaseOctave().getOctaveInt();
            context.setCurrentOctave(new Octave(octave));
        });

        elementsActionMapping.put("octaveDown", (matcher, context) -> {
            // oitava atual - 1, sem passar da mínima
            int octave = context.getCurrentOctave().getOctaveInt() - 1;
            if (octave < Octave.MIN_OCTAVE) octave = Octave.MIN_OCTAVE;
            context.setCurrentOctave(new Octave(octave));
        });

        elementsActionMapping.put("bpmUp", (matcher, context) -> {
            context.setBpm(Math.min(context.getBpm() + 10, 999));
            context.appendControlElement(new BPMElement(context.getBpm()));
        });

        elementsActionMapping.put("bpmDown", (matcher, context) -> {
            context.setBpm(Math.max(context.getBpm() - 10, 20));
            context.appendControlElement(new BPMElement(context.getBpm()));
        });

        elementsActionMapping.put("doubleVolume", (matcher, context) -> {
            // volume * 2, no máximo 127 (VolumeElement.MAX_VOLUME)
            int volume = Math.min(context.getCurrentVolume().getVolume() * 2, VolumeElement.MAX_VOLUME);
            VolumeElement newVolume = new VolumeElement(volume);
            context.setCurrentVolume(newVolume);
            context.appendControlElement(newVolume);
        });

        elementsActionMapping.put("newVoice", (matcher, context) -> context.closeVoice());

        elementsActionMapping.put("instrument", (matcher, context) -> {
            // dígito par (qualquer um): avança um instrumento dentro da família da voz, dando a volta
            InstrumentElement newInstrument = nextInstrumentInFamily(context.getCurrentInstrument());
            context.setCurrentInstrument(newInstrument);
            context.appendControlElement(newInstrument);
        });

        elementsActionMapping.put("else", (matcher, context) -> {
            // se o último elemento é nota → repete; senão → pausa
            MusicElement last = context.getLastMusicElement();
            if (last instanceof NoteElement) {
                context.appendToSheetMusic(last);
            } else {
                context.appendToSheetMusic(new PauseElement(DurationTime.QUARTER));
            }
        });
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
        REGEXRules.put("instrument",   "(?<instrument>["   + getElementChars("instrument")   + "])");
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

    public Map<String, TextToMusicActions> getElementsActionMapping() {
        return elementsActionMapping;
    }

    public Accidentals getAccidental(String input) {
        return accidentalsRules.getOrDefault(input, Accidentals.NONE);
    }

    { REGEXToken = createREGEXToken(); }

    //Próximo instrumento da família (InstrumentType) do instrumento atual; passando do último, volta pro primeiro
    private InstrumentElement nextInstrumentInFamily(InstrumentElement current) {
        InstrumentType family = current.getType();
        int familySize = family.getMaxInstrument() - family.getMinInstrument() + 1;
        int position = current.getInstrument() - family.getMinInstrument();
        int next = family.getMinInstrument() + (position + 1) % familySize;
        return new InstrumentElement(next, family);
    }
}
