package translators.TextToMusic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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

// Regras de uma tradução: as ações de cada grupo e o REGEX montado a partir do mapeamento.
// O mapeamento de caracteres fica no CharacterMapping e a montagem do REGEX no RegexBuilder.
// Cada instância tem o seu próprio estado; o tradutor cria uma nova a cada textToMusic().
public class TextToMusicRules {

    private final CharacterMapping mapping;

    //Dict das Actions de cada grupo do REGEX, sendo salva como: NomeGrupo , Action
    //A ordem importa: "startDelay" e "note" primeiro, "else" por último
    private final Map<String, TextToMusicActions> elementsActionMapping;

    private final String REGEXToken;

    public TextToMusicRules() {
        this(new CharacterMapping());
    }

    public TextToMusicRules(CharacterMapping mapping) {
        this(mapping, Map.of());
    }

    // customActions: ações extras (NomeGrupo -> Action), sem precisar editar esta classe.
    // Cada grupo extra precisa ter caracteres no mapeamento; ele entra antes do "else".
    public TextToMusicRules(CharacterMapping mapping, Map<String, TextToMusicActions> customActions) {
        if (mapping == null) throw new IllegalArgumentException("O mapeamento não pode ser nulo");
        if (customActions == null) throw new IllegalArgumentException("As ações extras não podem ser nulas");

        this.mapping = mapping;
        this.elementsActionMapping = createActionMapping(customActions);
        this.REGEXToken = RegexBuilder.build(mapping, elementsActionMapping.keySet());
    }

    private Map<String, TextToMusicActions> createActionMapping(Map<String, TextToMusicActions> customActions) {
        Map<String, TextToMusicActions> actions = new LinkedHashMap<>();
        actions.put("startDelay",   this::addStartDelay);
        actions.put("note",         this::addNote);
        actions.put("pause",        this::addPause);
        actions.put("octaveUp",     this::raiseOctave);
        actions.put("octaveDown",   this::lowerOctave);
        actions.put("bpmUp",        this::increaseBpm);
        actions.put("bpmDown",      this::decreaseBpm);
        actions.put("doubleVolume", this::doubleVolume);
        actions.put("newVoice",     this::startNewVoice);
        actions.put("instrument",   this::nextInstrument);

        for (Map.Entry<String, TextToMusicActions> custom : customActions.entrySet()) {
            if (actions.containsKey(custom.getKey()) || custom.getKey().equals("else"))
                throw new IllegalArgumentException("Já existe uma ação para o grupo " + custom.getKey());
            if (!mapping.hasGroup(custom.getKey()))
                throw new IllegalArgumentException("O grupo " + custom.getKey() + " não tem caracteres no mapeamento");
            actions.put(custom.getKey(), custom.getValue());
        }

        actions.put("else",         this::repeatLastNoteOrPause);
        return Collections.unmodifiableMap(actions);
    }

    private void addStartDelay(Matcher matcher, TextToMusicContext context) {
        // "[n]" no começo da linha: a voz espera n tempos
        int beats = Integer.parseInt(matcher.group("startDelay"));
        for (int i = 0; i < beats; i++) {
            context.appendToSheetMusic(new PauseElement(DurationTime.QUARTER));
        }
    }

    private void addNote(Matcher matcher, TextToMusicContext context) {
        //Dá um append de uma nova nota na sheetMusic atual
        NoteElement newNote = new NoteElement(
            getNote(matcher.group("note")),
            context.getCurrentOctave(),
            getDurationTime(matcher.group("duration")),
            getAccidental(matcher.group("accidental"))
        );
        context.appendToSheetMusic(newNote);
    }

    private void addPause(Matcher matcher, TextToMusicContext context) {
        context.appendToSheetMusic(new PauseElement(DurationTime.QUARTER));
    }

    private void raiseOctave(Matcher matcher, TextToMusicContext context) {
        // oitava atual + 1; se passar de 8, volta pra oitava base da voz
        int octave = context.getCurrentOctave().getOctaveInt() + 1;
        if (octave > Octave.MAX_OCTAVE) octave = context.getBaseOctave().getOctaveInt();
        context.setCurrentOctave(new Octave(octave));
    }

    private void lowerOctave(Matcher matcher, TextToMusicContext context) {
        // oitava atual - 1, sem passar da mínima
        int octave = context.getCurrentOctave().getOctaveInt() - 1;
        if (octave < Octave.MIN_OCTAVE) octave = Octave.MIN_OCTAVE;
        context.setCurrentOctave(new Octave(octave));
    }

    private void increaseBpm(Matcher matcher, TextToMusicContext context) {
        context.setBpm(Math.min(context.getBpm() + 10, 999));
        context.appendControlElement(new BPMElement(context.getBpm()));
    }

    private void decreaseBpm(Matcher matcher, TextToMusicContext context) {
        context.setBpm(Math.max(context.getBpm() - 10, 20));
        context.appendControlElement(new BPMElement(context.getBpm()));
    }

    private void doubleVolume(Matcher matcher, TextToMusicContext context) {
        // volume * 2, no máximo 127 (VolumeElement.MAX_VOLUME)
        int volume = Math.min(context.getCurrentVolume().getVolume() * 2, VolumeElement.MAX_VOLUME);
        VolumeElement newVolume = new VolumeElement(volume);
        context.setCurrentVolume(newVolume);
        context.appendControlElement(newVolume);
    }

    private void startNewVoice(Matcher matcher, TextToMusicContext context) {
        context.closeVoice();
    }

    private void nextInstrument(Matcher matcher, TextToMusicContext context) {
        // dígito par: avança um instrumento dentro da família da voz, dando a volta
        InstrumentElement newInstrument = nextInstrumentInFamily(context.getCurrentInstrument());
        context.setCurrentInstrument(newInstrument);
        context.appendControlElement(newInstrument);
    }

    private void repeatLastNoteOrPause(Matcher matcher, TextToMusicContext context) {
        // se o último elemento é nota → repete; senão → pausa
        MusicElement last = context.getLastMusicElement();
        if (last instanceof NoteElement) {
            context.appendToSheetMusic(last);
        } else {
            context.appendToSheetMusic(new PauseElement(DurationTime.QUARTER));
        }
    }

    //Próximo instrumento da família (InstrumentType) do instrumento atual e loopa se necessário
    private InstrumentElement nextInstrumentInFamily(InstrumentElement current) {
        InstrumentType family = current.getType();
        int familySize = family.getMaxInstrument() - family.getMinInstrument() + 1;
        int position = current.getInstrument() - family.getMinInstrument();
        int next = family.getMinInstrument() + (position + 1) % familySize;
        return new InstrumentElement(next, family);
    }

    public CharacterMapping getMapping() {
        return mapping;
    }

    public String getElementChars(String elementType) {
        return RegexBuilder.quotedChars(elementType, mapping);
    }

    public String getREGEXToken() {
        return this.REGEXToken;
    }

    public String getStartDelayREGEX() {
        return RegexBuilder.startDelay(mapping);
    }

    public Map<String, TextToMusicActions> getElementsActionMapping() {
        return elementsActionMapping;
    }

    public Notes getNote(String input) {
        return mapping.getNote(input);
    }

    public DurationTime getDurationTime(String input) {
        return mapping.getDurationTime(input);
    }

    public Accidentals getAccidental(String input) {
        return mapping.getAccidental(input);
    }

    // ------------------------------------------------------------------------------------------
    // Compatibilidade com a GUI atual (MapperScreen e MappingWidget), que ainda chama os métodos
    // estáticos. Eles agem sobre um único mapeamento compartilhado, que é o usado pelo
    // TextToMusicTranslator sem argumentos. Código novo deve usar um CharacterMapping próprio.
    // ------------------------------------------------------------------------------------------

    private static final CharacterMapping SHARED_MAPPING = new CharacterMapping();

    // Mapeamento compartilhado usado pelo TextToMusicTranslator padrão
    static CharacterMapping sharedMapping() {
        return SHARED_MAPPING;
    }

    /** @deprecated use {@link CharacterMapping#reset()} */
    @Deprecated
    public static void resetMappings() {
        SHARED_MAPPING.reset();
    }

    /** @deprecated use {@link CharacterMapping#replaceChar(String, String, String)} */
    @Deprecated
    public static void replaceChar(String elementType, String oldChar, String newChar) {
        SHARED_MAPPING.replaceChar(elementType, oldChar, newChar);
    }

    /** @deprecated use {@link CharacterMapping#getMappingCharacters(String)} */
    @Deprecated
    public static List<String> getMappingCharacters(String type) {
        return SHARED_MAPPING.getMappingCharacters(type);
    }

    /** @deprecated use {@link CharacterMapping#setElementChars(String, String)} */
    @Deprecated
    public static void setElementChars(String elementType, String chars) {
        SHARED_MAPPING.setElementChars(elementType, chars);
    }

    /** @deprecated use {@link CharacterMapping#addElementChars(String, String)} */
    @Deprecated
    public static void addElementChars(String elementType, String chars) {
        SHARED_MAPPING.addElementChars(elementType, chars);
    }

    /** @deprecated use {@link CharacterMapping#removeElementChars(String, String)} */
    @Deprecated
    public static void removeElementChars(String elementType, String chars) {
        SHARED_MAPPING.removeElementChars(elementType, chars);
    }

    /** @deprecated use {@link CharacterMapping#mapNote(String, Notes)} */
    @Deprecated
    public static void mapNote(String input, Notes note) {
        SHARED_MAPPING.mapNote(input, note);
    }
}
