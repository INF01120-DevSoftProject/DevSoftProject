package translators.TextToMusic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class RegexBuilderTest {

    private final CharacterMapping mapping = new CharacterMapping();

    @Test
    void charGroupCapturesWithTheGroupName() {
        Matcher matcher = Pattern.compile(RegexBuilder.charGroup("bpmUp", mapping)).matcher(">");

        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group("bpmUp")).isEqualTo(">");
    }

    @Test
    void specialRegexCharactersAreQuoted() {
        mapping.setElementChars("bpmUp", "]^");
        Pattern pattern = Pattern.compile(RegexBuilder.charGroup("bpmUp", mapping));

        assertThat(pattern.matcher("]").matches()).isTrue();
        assertThat(pattern.matcher("^").matches()).isTrue();
        assertThat(pattern.matcher("a").matches()).isFalse();
    }

    @Test
    void tokenFollowsTheCurrentMapping() {
        mapping.replaceChar("bpmUp", ">", "+");
        String token = new TextToMusicRules(mapping).getREGEXToken();
        Matcher matcher = Pattern.compile(token).matcher("+");

        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group("bpmUp")).isEqualTo("+");
    }

    @Test
    void startDelayOnlyMatchesAtTheStartOfALine() {
        Pattern pattern = Pattern.compile(RegexBuilder.startDelay(mapping));

        assertThat(pattern.matcher("[12] A").lookingAt()).isTrue();
        assertThat(pattern.matcher("A[12]").lookingAt()).isFalse();
    }
}
