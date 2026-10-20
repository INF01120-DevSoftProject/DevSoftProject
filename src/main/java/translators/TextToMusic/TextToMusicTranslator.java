package translators.TextToMusic;

import java.util.List;
import java.util.Map;
import java.util.regex.*;
import music.Voice;
import musicElements.MusicElement;
import musicElements.NoteElement;

public class TextToMusicTranslator {
    private TextToMusicRules musicRules = new TextToMusicRules();

    public List<Voice> textToVoices(String text){

        Pattern token = Pattern.compile(this.musicRules.getREGEXToken());
        Matcher matcher = token.matcher(text);
        TextToMusicContext context = new TextToMusicContext();

        while (matcher.find()) {
            // acha o grupo que casou e executa a action dele
            for (Map.Entry<String, TextToMusicActions> rule : this.musicRules.getElementsActionMapping().entrySet()) {
                if (matcher.group(rule.getKey()) != null) {
                    rule.getValue().apply(matcher, context);
                    break;
                }
            }
        }
        return context.finish();
    }

    public static void main(String[] args) {
        TextToMusicTranslator translator = new TextToMusicTranslator();
        List<Voice> voices = translator.textToVoices("CDE:2 F#G?A\nC:1 aE");

        for (int i = 0; i < voices.size(); i++) {
            System.out.println("Voz " + (i + 1) + ":");
            for (MusicElement element : voices.get(i).getSheetMusic().getSheetMusic()) {
                if (element instanceof NoteElement note) {
                    System.out.println("  " + note.getNote() + " " + note.getAccidental() + " " + note.getDuration());
                } else {
                    System.out.println("  " + element.getClass().getSimpleName());
                }
            }
        }
    }
}
