package musicElements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class MusicBuilderTest {

    private VoiceBuilder voiceWith(SheetMusic sheet){
        return new VoiceBuilder().setSheetMusic(sheet);
    }

    @Test
    void buildsMusicWithAllValues() {
        SheetMusic sheet1 = new SheetMusic();
        SheetMusic sheet2 = new SheetMusic();
        BPMElement bpm = new BPMElement(90);

        Music music = new MusicBuilder()
            .setName("Minha musica")
            .setDefaultBPM(bpm)
            .addVoice(voiceWith(sheet1))
            .addVoice(voiceWith(sheet2))
            .buildMusic();

        assertThat(music.getName()).isEqualTo("Minha musica");
        assertThat(music.getDefaultBPM()).isSameAs(bpm);
        assertThat(music.getVoices()).hasSize(2);
        assertThat(music.getVoices().get(0).getSheetMusic()).isSameAs(sheet1);
        assertThat(music.getVoices().get(1).getSheetMusic()).isSameAs(sheet2);
    }

    @Test
    void usesDefaultBpmWhenNoneIsSet() {
        Music music = new MusicBuilder().setName("Minha musica").addVoice(voiceWith(new SheetMusic())).buildMusic();

        assertThat(music.getDefaultBPM().getBPM()).isEqualTo(MusicBuilder.DEFAULT_BPM);
    }

    @Test
    void voicesAddedAfterBuildDoNotChangeBuiltMusic() {
        MusicBuilder builder = new MusicBuilder().setName("Minha musica").addVoice(voiceWith(new SheetMusic()));

        Music music = builder.buildMusic();
        builder.addVoice(voiceWith(new SheetMusic()));

        assertThat(music.getVoices()).hasSize(1);
        assertThat(builder.buildMusic().getVoices()).hasSize(2);
    }

    @Test
    void missingValuesAreRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().addVoice(voiceWith(new SheetMusic())).buildMusic());
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().setName("Minha musica").buildMusic());
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().setName("Minha musica").setDefaultBPM(null).addVoice(voiceWith(new SheetMusic())).buildMusic());
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().addVoice(null));
    }

    @Test
    void invalidVoiceIsRejectedWhenAdded() {
        assertThrows(IllegalArgumentException.class, () -> new MusicBuilder().addVoice(new VoiceBuilder()));
    }
}
