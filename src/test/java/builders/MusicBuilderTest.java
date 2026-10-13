package builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import music.Music;
import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.BPMElement;
import musicElements.VolumeElement;

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
        assertThat(music.getDefaultBPMElement()).isSameAs(bpm);
        assertThat(music.getVoices()).hasSize(2);
        assertThat(music.getVoices().get(0).getSheetMusic()).isSameAs(sheet1);
        assertThat(music.getVoices().get(1).getSheetMusic()).isSameAs(sheet2);
    }

    @Test
    void usesDefaultBpmWhenNoneIsSet() {
        Music music = new MusicBuilder().setName("Minha musica").addVoice(voiceWith(new SheetMusic())).buildMusic();

        assertThat(music.getDefaultBPMElement().getBPM()).isEqualTo(MusicBuilder.DEFAULT_BPM);
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
            () -> new MusicBuilder().setName("Minha musica").setDefaultBPM(null).addVoice(voiceWith(new SheetMusic())).buildMusic());
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().addVoice(null));
    }

    @Test
    void invalidVoiceIsRejectedWhenAdded() {
        assertThrows(IllegalArgumentException.class, () -> new MusicBuilder().addVoice(new VoiceBuilder()));
    }

    @Test
    void hasThreeVoicesByDefault() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();

        assertThat(music.getVoices()).hasSize(MusicBuilder.DEFAULT_NUMBER_OF_VOICES);
        assertThat(MusicBuilder.DEFAULT_NUMBER_OF_VOICES).isEqualTo(3);
    }

    @Test
    void defaultVoicesAreIndependent() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();
        Voice first = music.getVoices().get(0);
        Voice second = music.getVoices().get(1);
        Voice third = music.getVoices().get(2);

        assertThat(first.getSheetMusic()).isNotSameAs(second.getSheetMusic());
        assertThat(second.getSheetMusic()).isNotSameAs(third.getSheetMusic());
        assertThat(first.getSheetMusic()).isNotSameAs(third.getSheetMusic());

        first.getSheetMusic().appendToSheetMusic(new VolumeElement(80));

        assertThat(first.getSheetMusic().getSheetMusic()).hasSize(1);
        assertThat(second.getSheetMusic().getSheetMusic()).isEmpty();
        assertThat(third.getSheetMusic().getSheetMusic()).isEmpty();
    }

    @Test
    void defaultVoicesUseDefaultPreset() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();

        for (Voice voice : music.getVoices()){
            assertThat(voice.getSheetMusic().getSheetMusic()).isEmpty();
            assertThat(voice.getVoicePreset().getInstrumentElement().getInstrument()).isEqualTo(VoicePreset.DEFAULT_INSTRUMENT);
            assertThat(voice.getVoicePreset().getVolumeElement().getVolume()).isEqualTo(VoicePreset.DEFAULT_VOLUME);
            assertThat(voice.getVoicePreset().getOctaveElement().getOctave()).isEqualTo(VoicePreset.DEFAULT_OCTAVE);
        }
    }

    @Test
    void canHaveAnyNumberOfVoicesUpToTheMaximum() {
        for (int count = 1; count <= Music.MAX_VOICES; count++){
            MusicBuilder builder = new MusicBuilder().setName("Minha musica");
            for (int i = 0; i < count; i++){
                builder.addVoice(voiceWith(new SheetMusic()));
            }

            assertThat(builder.buildMusic().getVoices()).hasSize(count);
        }
    }

    @Test
    void canHaveMoreThanThreeVoices() {
        MusicBuilder builder = new MusicBuilder().setName("Minha musica");
        for (int i = 0; i < 4; i++){
            builder.addVoice(voiceWith(new SheetMusic()));
        }

        assertThat(builder.buildMusic().getVoices()).hasSize(4);
        assertThat(Music.MAX_VOICES).isEqualTo(16);
    }

    @Test
    void canHaveFewerThanThreeVoices() {
        Music music = new MusicBuilder().setName("Minha musica").addVoice(voiceWith(new SheetMusic())).buildMusic();

        assertThat(music.getVoices()).hasSize(1);
    }

    @Test
    void moreVoicesThanTheMaximumAreRejected() {
        MusicBuilder builder = new MusicBuilder().setName("Minha musica");
        for (int i = 0; i <= Music.MAX_VOICES; i++){
            builder.addVoice(voiceWith(new SheetMusic()));
        }

        assertThrows(IllegalArgumentException.class, builder::buildMusic);
    }
}
