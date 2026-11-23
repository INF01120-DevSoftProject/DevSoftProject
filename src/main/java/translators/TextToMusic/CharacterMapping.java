package translators.TextToMusic;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import musicElements.Accidentals;
import musicElements.DurationTime;
import musicElements.Notes;

// Mapeamento de caracteres do texto para os grupos da tradução (nota, pausa, bpmUp...).
// Cada instância tem o seu próprio estado: dois tradutores (ou dois testes) não interferem um no outro.
public class CharacterMapping {

    private final Map<MappingType, String> defaultCharacters;

    // Grupo -> caracteres que pertencem a ele
    private final Map<MappingType, String> elementsMapping = new LinkedHashMap<>();

    // Caractere -> valor musical, para os grupos que viram um valor (nota, duração e acidente)
    private final Map<String, Notes> notesRules = new LinkedHashMap<>();
    private final Map<String, DurationTime> durationTimeRules = new LinkedHashMap<>();
    private final Map<String, Accidentals> accidentalsRules = new LinkedHashMap<>();

    // Trocas pela metade: grupo -> (caractere tirado do lugar -> caractere temporário no lugar dele).
    // Ex.: em "A -> B" com o B já sendo outra nota, essa nota fica com um temporário até
    // chegar o pedido "B -> ...". Assim dá pra trocar dois caracteres de lugar, um de cada vez.
    private final Map<MappingType, Map<String, String>> pendingReplacements = new LinkedHashMap<>();

    // Temporários vêm da área de uso privado do Unicode, que não aparece em texto comum
    private static final char FIRST_TEMPORARY_CHAR = '';

    // Mapeamento padrão
    public CharacterMapping() {
        this(defaultCharacters());
    }

    // Caracteres padrão de cada grupo (grupo -> caracteres)
    private static Map<MappingType, String> defaultCharacters() {
        Map<MappingType, String> characters = new LinkedHashMap<>();
        characters.put(MappingType.NOTE,         "ABCDEFG");
        characters.put(MappingType.ACCIDENTAL,   "#b");
        characters.put(MappingType.DURATION,     "1234567");
        characters.put(MappingType.PAUSE,        "acdefgh");

        characters.put(MappingType.OCTAVE_UP,     "?.");
        characters.put(MappingType.OCTAVE_DOWN,   "V");

        characters.put(MappingType.BPM_UP,        ">");
        characters.put(MappingType.BPM_DOWN,      "<");

        characters.put(MappingType.DOUBLE_VOLUME, " ");

        characters.put(MappingType.NEW_VOICE,     "\n");

        characters.put(MappingType.INSTRUMENT,   "02468");
        characters.put(MappingType.START_DELAY,   "0123456789");
        return characters;
    }

    // Mapeamento com caracteres padrão escolhidos por quem cria (grupo -> caracteres)
    public CharacterMapping(Map<MappingType, String> defaultCharacters) {
        if (defaultCharacters == null) throw new IllegalArgumentException("O mapeamento padrão não pode ser nulo");
        this.defaultCharacters = new LinkedHashMap<>(defaultCharacters);
        reset();
    }

    // Volta o mapeamento para o padrão
    public void reset() {
        pendingReplacements.clear();
        elementsMapping.clear();
        elementsMapping.putAll(defaultCharacters);

        notesRules.clear();
        notesRules.put("A", Notes.A);
        notesRules.put("B", Notes.B);
        notesRules.put("C", Notes.C);
        notesRules.put("D", Notes.D);
        notesRules.put("E", Notes.E);
        notesRules.put("F", Notes.F);
        notesRules.put("G", Notes.G);

        durationTimeRules.clear();
        durationTimeRules.put("1", DurationTime.WHOLE);
        durationTimeRules.put("2", DurationTime.HALF);
        durationTimeRules.put("3", DurationTime.QUARTER);
        durationTimeRules.put("4", DurationTime.EIGHTH);
        durationTimeRules.put("5", DurationTime.SIXTEENTH);
        durationTimeRules.put("6", DurationTime.THIRTY_SECOND);
        durationTimeRules.put("7", DurationTime.SIXTY_FOURTH);

        accidentalsRules.clear();
        accidentalsRules.put("b", Accidentals.FLAT);
        accidentalsRules.put("#", Accidentals.SHARP);
        accidentalsRules.put("", Accidentals.NONE);
    }

    // Cópia independente do estado atual (usada para desfazer um salvamento que falhou)
    public CharacterMapping copy() {
        CharacterMapping copy = new CharacterMapping(defaultCharacters);
        copy.restore(this);
        return copy;
    }

    // Volta para o estado de outro mapeamento (normalmente uma cópia feita antes)
    public void restore(CharacterMapping saved) {
        if (saved == null) throw new IllegalArgumentException("O mapeamento salvo não pode ser nulo");
        elementsMapping.clear();
        elementsMapping.putAll(saved.elementsMapping);
        notesRules.clear();
        notesRules.putAll(saved.notesRules);
        durationTimeRules.clear();
        durationTimeRules.putAll(saved.durationTimeRules);
        accidentalsRules.clear();
        accidentalsRules.putAll(saved.accidentalsRules);
        pendingReplacements.clear();
        saved.pendingReplacements.forEach((group, pending) -> pendingReplacements.put(group, new LinkedHashMap<>(pending)));
    }

    // Registra um grupo novo (ex.: de um comando extra) sem mexer nos que já existem
    public void addGroupIfAbsent(MappingType elementType, String defaultChars) {
        if (defaultChars == null || defaultCharacters.containsKey(elementType)) return;
        defaultCharacters.put(elementType, defaultChars);
        elementsMapping.putIfAbsent(elementType, defaultChars);
    }

    public boolean hasGroup(MappingType elementType) {
        return elementsMapping.containsKey(elementType);
    }

    // Caracteres de um grupo, sem tratamento (ex.: "ABCDEFG")
    public String getChars(MappingType elementType) {
        String chars = elementsMapping.get(elementType);
        if (chars == null) throw new IllegalArgumentException("O grupo " + elementType + " não existe");
        return chars;
    }

    public List<String> getMappingCharacters(MappingType elementType) {
        String mapping = elementsMapping.get(elementType);
        if (mapping == null) return List.of();
        return Arrays.asList(mapping.split(""));
    }

    // Substitui um caractere por outro, levando junto o valor musical dele (nota, duração ou acidente).
    // Se o novo caractere já é de outro elemento do MESMO grupo, esse elemento fica com um caractere
    // temporário até ser trocado também (ver pendingReplacements). Isso permite trocar dois de lugar.
    public void replaceChar(MappingType elementType, String oldChar, String newChar) {
        String chars = elementsMapping.get(elementType);
        if (chars == null)
            throw new IllegalArgumentException("O grupo " + elementType + " não tem o caractere '" + oldChar + "'");

        // o caractere antigo pode ter sido tirado do lugar por uma troca anterior
        Map<String, String> pending = pendingReplacements.getOrDefault(elementType, Map.of());
        boolean wasDisplaced = pending.containsKey(oldChar);
        String currentChar = wasDisplaced ? pending.get(oldChar) : oldChar;

        if (!chars.contains(currentChar))
            throw new IllegalArgumentException("O grupo " + elementType + " não tem o caractere '" + oldChar + "'");
        if (currentChar.equals(newChar)) return;
        checkNotUsed(newChar, elementType);

        if (wasDisplaced) removePending(elementType, oldChar);

        if (chars.contains(newChar)) {
            // o novo caractere é de outro elemento do grupo: esse elemento vai pra um temporário
            String temporary = nextTemporaryChar();
            moveChar(elementType, newChar, temporary);
            pendingReplacements.computeIfAbsent(elementType, k -> new LinkedHashMap<>()).put(newChar, temporary);
        }
        moveChar(elementType, currentChar, newChar);
    }

    // Troca o caractere dentro do grupo, levando junto o valor musical dele
    private void moveChar(MappingType elementType, String from, String to) {
        elementsMapping.put(elementType, elementsMapping.get(elementType).replace(from, to));
        if (elementType == MappingType.NOTE)
            notesRules.put(to, notesRules.remove(from));
        if (elementType == MappingType.DURATION)
            durationTimeRules.put(to, durationTimeRules.remove(from));
        if (elementType == MappingType.ACCIDENTAL)
            accidentalsRules.put(to, accidentalsRules.remove(from));
    }

    private void removePending(MappingType elementType, String displacedChar) {
        Map<String, String> pending = pendingReplacements.get(elementType);
        pending.remove(displacedChar);
        if (pending.isEmpty()) pendingReplacements.remove(elementType);
    }

    private String nextTemporaryChar() {
        for (char c = FIRST_TEMPORARY_CHAR; c < ''; c++) {
            String candidate = String.valueOf(c);
            boolean used = elementsMapping.values().stream().anyMatch(chars -> chars.contains(candidate));
            if (!used) return candidate;
        }
        throw new IllegalStateException("Não há caractere temporário disponível");
    }

    public boolean isComplete() {
        return pendingReplacements.isEmpty();
    }

    // Garante que nenhum elemento ficou com caractere temporário (troca feita pela metade)
    public void checkComplete() {
        for (Map.Entry<MappingType, Map<String, String>> group : pendingReplacements.entrySet()) {
            String displaced = group.getValue().keySet().iterator().next();
            throw new IllegalArgumentException("Mapeamento incompleto: no grupo " + group.getKey()
                + ", o elemento que usava '" + displaced + "' ficou sem caractere. Termine a troca no Mapeamento.");
        }
    }

    // Substitui TODOS os caracteres de um grupo, apagando os antigos
    public void setElementChars(MappingType elementType, String chars) {
        checkNotUsed(chars, elementType);
        checkHasValue(elementType, chars);
        if (chars.isEmpty())
            throw new IllegalArgumentException("O grupo " + elementType + " não pode ficar sem caractere");
        elementsMapping.put(elementType, chars);
        pendingReplacements.remove(elementType);
    }

    // Acrescenta caracteres a um grupo
    public void addElementChars(MappingType elementType, String chars) {
        checkNotUsed(chars, elementType);
        checkHasValue(elementType, chars);
        StringBuilder current = new StringBuilder(getChars(elementType));
        for (char c : chars.toCharArray()) {
            if (current.indexOf(String.valueOf(c)) < 0) current.append(c);
        }
        elementsMapping.put(elementType, current.toString());
    }

    // Tira caracteres de um grupo
    public void removeElementChars(MappingType elementType, String chars) {
        StringBuilder current = new StringBuilder();
        for (char c : getChars(elementType).toCharArray()) {
            if (chars.indexOf(c) < 0) current.append(c);
        }
        if (current.length() == 0)
            throw new IllegalArgumentException("O grupo " + elementType + " não pode ficar sem caractere");
        elementsMapping.put(elementType, current.toString());
    }

    // Associa um caractere a uma nota, tirando o caractere antigo.
    // Um caractere que já é de outra nota é recusado (para trocar duas notas, use replaceChar).
    public void mapNote(String input, Notes note) {
        if (note == null) throw new IllegalArgumentException("A nota não pode ser nula");
        checkNotUsed(input, MappingType.NOTE);
        Notes current = notesRules.get(input);
        if (current == note) return;
        if (current != null)
            throw new IllegalArgumentException("O caractere '" + input + "' já é usado pela nota " + current
                + " (use replaceChar para trocar as duas)");
        String oldChar = null;
        for (Map.Entry<String, Notes> rule : notesRules.entrySet()) {
            if (rule.getValue() == note) oldChar = rule.getKey();
        }
        if (oldChar != null) {
            notesRules.remove(oldChar);
            removeElementChars(MappingType.NOTE, oldChar);
        }
        notesRules.put(input, note);
        addElementChars(MappingType.NOTE, input);
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

    // Verifica se o char já está associado a outro grupo (números ficam de fora)
    // Nos grupos que viram um valor musical (nota, duração e acidente), todo caractere precisa ter
    // esse valor; senão o REGEX reconhece o caractere e a tradução recebe um valor nulo.
    // Para associar um caractere novo a uma nota, use mapNote.
    private void checkHasValue(MappingType elementType, String chars) {
        Map<String, ?> values = valuesOf(elementType);
        if (values == null) return;
        for (char c : chars.toCharArray()) {
            if (!values.containsKey(String.valueOf(c)))
                throw new IllegalArgumentException("O caractere '" + c + "' não tem valor no grupo " + elementType
                    + (elementType == MappingType.NOTE ? " (use mapNote para associá-lo a uma nota)" : ""));
        }
    }

    // Mapa caractere -> valor musical do grupo, ou null se o grupo não tem valor
    private Map<String, ?> valuesOf(MappingType elementType) {
        return switch (elementType) {
            case NOTE       -> notesRules;
            case DURATION   -> durationTimeRules;
            case ACCIDENTAL -> accidentalsRules;
            default           -> null;
        };
    }

    // Verifica se o char já está associado a outro grupo.
    // Um dígito pode ser compartilhado só quando um dos grupos é contextual (ver CONTEXTUAL_GROUPS):
    // ex.: "2" é duração depois de ":" e troca de instrumento sozinho, sem um atrapalhar o outro.
    private void checkNotUsed(String chars, MappingType elementType) {
        for (char c : chars.toCharArray()) {
            for (Map.Entry<MappingType, String> element : elementsMapping.entrySet()) {
                MappingType otherType = element.getKey();
                if (otherType == elementType || element.getValue().indexOf(c) < 0) continue;
                if (Character.isDigit(c) && (isContextual(elementType) || isContextual(otherType))) continue;
                throw new IllegalArgumentException("O caractere '" + c + "' já é usado em " + otherType);
            }
        }
    }

    // Grupos que só valem em um contexto: "[n]" no começo da linha, ":duração" e o acidente logo
    // depois da nota. O mesmo dígito sozinho no texto continua livre para outro grupo.
    private static final Set<MappingType> CONTEXTUAL_GROUPS = Set.of(MappingType.START_DELAY, MappingType.DURATION, MappingType.ACCIDENTAL);

    private static boolean isContextual(MappingType elementType) {
        return elementType != null && CONTEXTUAL_GROUPS.contains(elementType);
    }
}
