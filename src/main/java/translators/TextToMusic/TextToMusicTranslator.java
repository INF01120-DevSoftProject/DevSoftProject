package translators.TextToMusic;

import java.util.List;
import java.util.Map;
import java.util.regex.*;
import music.Music;
import music.Voice;
import musicElements.MusicElement;
import musicElements.NoteElement;

public class TextToMusicTranslator {
    public Music textToMusic(String name, String text){
        // regras novas a cada tradução, pra pegar o mapeamento atual
        TextToMusicRules musicRules = new TextToMusicRules();

        // a quebra de linha no fim do texto (TerminalInputReader, GUI e .txt terminam assim) só fecha a última voz
        text = removeTrailingLineBreak(text);

        // cada linha (separada por \n) é uma voz; a Music cria as vozes (no mínimo MIN_VOICES)
        int numberOfLines = text.split("\n", -1).length;
        if (numberOfLines > Music.MAX_VOICES) {
            throw new IllegalArgumentException(
                "O texto tem " + numberOfLines + " linhas, mas o máximo é " + Music.MAX_VOICES + " (uma voz por linha)");
        }
        Music music = new Music(name, Math.max(numberOfLines, Music.MIN_VOICES));

        Pattern token = Pattern.compile(musicRules.getREGEXToken());
        Matcher matcher = token.matcher(text);
        TextToMusicContext context = new TextToMusicContext(music.getVoices());

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
