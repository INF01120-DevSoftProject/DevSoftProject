package translators.TextToMusic;

import java.util.regex.Matcher;

@FunctionalInterface
public interface TextToMusicActions {
    void apply(Matcher matcher, TextToMusicContext context);
}
