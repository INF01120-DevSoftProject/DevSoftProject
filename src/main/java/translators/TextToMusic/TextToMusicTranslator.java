package translators.TextToMusic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import music.Music;
import music.VoicePreset;
import musicElements.BPMElement;

public class TextToMusicTranslator {

    private final CharacterMapping mapping;
    private final Map<String, TextToMusicActions> customActions;

    // Usa um mapeamento novo, com os caracteres padrão
    public TextToMusicTranslator() {
        this(new CharacterMapping());
    }

    public TextToMusicTranslator(CharacterMapping mapping) {
        this(mapping, Map.of());
    }

    // customActions: comandos extras, repassados para o TextToMusicRules
    public TextToMusicTranslator(CharacterMapping mapping, Map<String, TextToMusicActions> customActions) {
        if (mapping == null) throw new IllegalArgumentException("O mapeamento não pode ser nulo");
        if (customActions == null) throw new IllegalArgumentException("As ações extras não podem ser nulas");
        this.mapping = mapping;
        // mantém a ordem: ela define a prioridade dos comandos extras no REGEX
        this.customActions = Collections.unmodifiableMap(new LinkedHashMap<>(customActions));
    }

    public Music textToMusic(String name, String text){
        return textToMusic(name, text, null);
    }

    // voz sem preset na lista usa o preset padrão dela
    public Music textToMusic(String name, String text, List<VoicePreset> presets){
        return textToMusic(name, text, presets, Music.DEFAULT_BPM);
    }

    // initialBpm: BPM com que a música (e cada voz) começa
    public Music textToMusic(String name, String text, List<VoicePreset> presets, int initialBpm){
        // regras novas a cada tradução, com o mapeamento como está agora
        TextToMusicRules musicRules = new TextToMusicRules(mapping, customActions);

        // a quebra de linha no fim do texto (TerminalInputReader, GUI e .txt terminam assim) só fecha a última voz
        text = removeTrailingLineBreak(text);

        // cada linha (separada por \n) é uma voz; a Music cria as vozes (no mínimo MIN_VOICES)
        int numberOfLines = text.split("\n", -1).length;
        if (numberOfLines > Music.MAX_VOICES) {
            throw new IllegalArgumentException(
                "O texto tem " + numberOfLines + " linhas, mas o máximo é " + Music.MAX_VOICES + " (uma voz por linha)");
        }
        Music music = new Music(name, new BPMElement(initialBpm), Math.max(numberOfLines, Music.MIN_VOICES));

        Pattern token = Pattern.compile(musicRules.getREGEXToken());
        Matcher matcher = token.matcher(text);
        TextToMusicContext context = new TextToMusicContext(
            music.getVoices(), presets, music.getDefaultBPMElement().getBPM());

        while (matcher.find()) {
            // acha o grupo que casou e executa a action dele
            for (Map.Entry<String, TextToMusicActions> rule : musicRules.getElementsActionMapping().entrySet()) {
                if (matcher.group(rule.getKey()) != null) {
                    rule.getValue().apply(matcher, context);
                    break;
                }
            }
        }
        return music;
    }

    // remove uma única quebra de linha final ("\n" ou "\r\n"); linhas vazias antes dela continuam sendo vozes
    private static String removeTrailingLineBreak(String text) {
        if (text.endsWith("\r\n")) return text.substring(0, text.length() - 2);
        if (text.endsWith("\n")) return text.substring(0, text.length() - 1);
        return text;
    }
}
