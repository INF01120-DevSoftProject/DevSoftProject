package translators.TextToMusic;

import java.util.regex.Matcher;

@FunctionalInterface
interface TextToMusicActions {
    void apply(Matcher matcher, TextToMusicContext context);
}
