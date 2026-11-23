package translators.TextToMusic;

import java.util.Collections;
import java.util.LinkedHashMap;
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
    //A ordem importa: MappingType.START_DELAY e MappingType.NOTE primeiro, MappingType.ELSE por último
    private final Map<MappingType, TextToMusicActions> elementsActionMapping;

    private final String REGEXToken;

    public TextToMusicRules() {
        this(new CharacterMapping());
    }

    public TextToMusicRules(CharacterMapping mapping) {
        this(mapping, Map.of());
    }

    // customActions: ações extras (NomeGrupo -> Action), sem precisar editar esta classe.
    // Cada grupo extra precisa ter caracteres no mapeamento; ele entra antes do MappingType.ELSE.
    public TextToMusicRules(CharacterMapping mapping, Map<MappingType, TextToMusicActions> customActions) {
        if (mapping == null) throw new IllegalArgumentException("O mapeamento não pode ser nulo");
        if (customActions == null) throw new IllegalArgumentException("As ações extras não podem ser nulas");
        // troca de caracteres feita pela metade: melhor avisar do que traduzir errado
        mapping.checkComplete();

        this.mapping = mapping;
        this.elementsActionMapping = createActionMapping(customActions);
        this.REGEXToken = RegexBuilder.build(mapping, elementsActionMapping.keySet());
    }

    private Map<MappingType, TextToMusicActions> createActionMapping(Map<MappingType, TextToMusicActions> customActions) {
        Map<MappingType, TextToMusicActions> actions = new LinkedHashMap<>();
        actions.put(MappingType.START_DELAY,   this::addStartDelay);
        actions.put(MappingType.NOTE,         this::addNote);
        actions.put(MappingType.PAUSE,        this::addPause);
        actions.put(MappingType.OCTAVE_UP,     this::raiseOctave);
        actions.put(MappingType.OCTAVE_DOWN,   this::lowerOctave);
        actions.put(MappingType.BPM_UP,        this::increaseBpm);
        actions.put(MappingType.BPM_DOWN,      this::decreaseBpm);
        actions.put(MappingType.DOUBLE_VOLUME, this::doubleVolume);
        actions.put(MappingType.NEW_VOICE,     this::startNewVoice);
        actions.put(MappingType.INSTRUMENT,   this::nextInstrument);

        for (Map.Entry<MappingType, TextToMusicActions> custom : customActions.entrySet()) {
            if (actions.containsKey(custom.getKey()) || custom.getKey() == MappingType.ELSE)
                throw new IllegalArgumentException("Já existe uma ação para o grupo " + custom.getKey());
            if (!mapping.hasGroup(custom.getKey()))
                throw new IllegalArgumentException("O grupo " + custom.getKey() + " não tem caracteres no mapeamento");
            actions.put(custom.getKey(), custom.getValue());
        }

        actions.put(MappingType.ELSE,         this::repeatLastNoteOrPause);
        return Collections.unmodifiableMap(actions);
    }

    private void addStartDelay(Matcher matcher, TextToMusicContext context) {
        // "[n]" no começo da linha: a voz espera n tempos
        int beats = Integer.parseInt(matcher.group(MappingType.START_DELAY.getGroupName()));
        for (int i = 0; i < beats; i++) {
            context.appendToSheetMusic(new PauseElement(DurationTime.QUARTER));
        }
    }

    private void addNote(Matcher matcher, TextToMusicContext context) {
        //Dá um append de uma nova nota na sheetMusic atual
        NoteElement newNote = new NoteElement(
            getNote(matcher.group(MappingType.NOTE.getGroupName())),
            context.getCurrentOctave(),
            getDurationTime(matcher.group(MappingType.DURATION.getGroupName())),
            getAccidental(matcher.group(MappingType.ACCIDENTAL.getGroupName()))
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

    public String getElementChars(MappingType elementType) {
        return RegexBuilder.quotedChars(elementType, mapping);
    }

    public String getREGEXToken() {
        return this.REGEXToken;
    }

    public String getStartDelayREGEX() {
        return RegexBuilder.startDelay(mapping);
    }

    public Map<MappingType, TextToMusicActions> getElementsActionMapping() {
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
}
