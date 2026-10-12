package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    void anyClassCanBeAPlayer() {
        List<Music> played = new ArrayList<>();
        Player player = played::add;

        Music music = new MusicBuilder().setName("Minha musica").addVoice(new VoiceBuilder().setSheetMusic(new SheetMusic())).buildMusic();
        player.playMusic(music);

        assertThat(played).containsExactly(music);
    }
}
