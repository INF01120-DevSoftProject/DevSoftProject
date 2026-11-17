package translators.TextToMusic;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.regex.Pattern;

// Monta o REGEX da tradução a partir do mapeamento de caracteres atual.
// O REGEX segue a ordem dos grupos que têm ação: "startDelay" e "note" têm formato próprio,
// "else" pega qualquer caractere que sobrar e os outros grupos são "um caractere do grupo".
public final class RegexBuilder {

    private RegexBuilder() {}

    public static String build(CharacterMapping mapping, Collection<String> actionGroups) {
        StringJoiner joiner = new StringJoiner("|");
        for (String group : actionGroups) {
            joiner.add(groupRegex(group, mapping));
        }
        return joiner.toString();
    }

    public static String groupRegex(String group, CharacterMapping mapping) {
        return switch (group) {
            case "startDelay" -> startDelay(mapping);
            case "note"       -> note(mapping);
            case "else"       -> "(?<else>.)";
            default           -> charGroup(group, mapping);
        };
    }

    // Caracteres de um grupo prontos para ir dentro de [ ]
    // (o quote serve pra caracteres como ] e ^ não quebrarem o REGEX)
    public static String quotedChars(String elementType, CharacterMapping mapping) {
        return Pattern.quote(mapping.getChars(elementType));
    }

    // Um caractere qualquer do grupo, capturado com o nome do grupo
    public static String charGroup(String name, CharacterMapping mapping) {
        return "(?<" + name + ">[" + quotedChars(name, mapping) + "])";
    }

    // Nota, com acidente e ":duração" opcionais (ex.: "Bb:4")
    public static String note(CharacterMapping mapping) {
        return charGroup("note", mapping)
             + "(?<accidental>[" + quotedChars("accidental", mapping) + "])?"
             + "(?::(?<duration>[" + quotedChars("duration", mapping) + "]))?";
    }

    // "[n]" no começo da linha (até 3 dígitos), com um espaço opcional depois
    public static String startDelay(CharacterMapping mapping) {
        return "(?m:^)\\[(?<startDelay>[" + quotedChars("startDelay", mapping) + "]{1,3})\\] ?";
    }
}
