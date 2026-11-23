package translators.TextToMusic;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.regex.Pattern;

// Monta o REGEX da tradução a partir do mapeamento de caracteres atual.
// O REGEX segue a ordem dos grupos que têm ação: MappingType.START_DELAY e MappingType.NOTE têm formato próprio,
// MappingType.ELSE pega qualquer caractere que sobrar e os outros grupos são "um caractere do grupo".
public final class RegexBuilder {

    private RegexBuilder() {}

    public static String build(CharacterMapping mapping, Collection<MappingType> actionGroups) {
        StringJoiner joiner = new StringJoiner("|");
        for (MappingType group : actionGroups) {
            joiner.add(groupRegex(group, mapping));
        }
        return joiner.toString();
    }

    public static String groupRegex(MappingType group, CharacterMapping mapping) {
        return switch (group) {
            case START_DELAY -> startDelay(mapping);
            case NOTE       -> note(mapping);
            case ELSE       -> "(?<" + MappingType.ELSE.getGroupName() + ">.)";
            default           -> charGroup(group, mapping);
        };
    }

    // Caracteres de um grupo prontos para ir dentro de [ ]
    // (o quote serve pra caracteres como ] e ^ não quebrarem o REGEX)
    public static String quotedChars(MappingType elementType, CharacterMapping mapping) {
        return Pattern.quote(mapping.getChars(elementType));
    }

    // Um caractere qualquer do grupo, capturado com o nome do grupo
    public static String charGroup(MappingType name, CharacterMapping mapping) {
        return "(?<" + name.getGroupName() + ">[" + quotedChars(name, mapping) + "])";
    }

    // Nota, com acidente e ":duração" opcionais (ex.: "Bb:4")
    public static String note(CharacterMapping mapping) {
        String accidental = charGroup(MappingType.ACCIDENTAL, mapping) + "?";
        String duration = "(?::" + charGroup(MappingType.DURATION, mapping) + ")?";
        return charGroup(MappingType.NOTE, mapping) + accidental + duration;
    }

    // "[n]" no começo da linha (até 3 dígitos), com um espaço opcional depois
    public static String startDelay(CharacterMapping mapping) {
        return "(?m:^)\\[(?<" + MappingType.START_DELAY.getGroupName() + ">[" + quotedChars(MappingType.START_DELAY, mapping) + "]{1,3})\\] ?";
    }
}
