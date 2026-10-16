package builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import music.Music;
import music.SheetMusic;
import music.Voice;
import music.VoicePreset;
import musicElements.BPMElement;
import musicElements.InstrumentType;
import musicElements.VolumeElement;

public class MusicBuilderTest {

    private VoiceBuilder voiceWith(SheetMusic sheet){
        return new VoiceBuilder().setSheetMusic(sheet).setInstrumentType(InstrumentType.PIANO);
    }

    private MusicBuilder builderWithVoices(int count){
        MusicBuilder builder = new MusicBuilder().setName("Minha musica");
        for (int i = 0; i < count; i++){
            builder.addVoice(voiceWith(new SheetMusic()));
        }
        return builder;
    }

    @Test
    void buildsMusicWithAllValues() {
        List<SheetMusic> sheets = new ArrayList<>();
        BPMElement bpm = new BPMElement(90);
        MusicBuilder builder = new MusicBuilder().setName("Minha musica").setDefaultBPM(bpm);
        for (int i = 0; i < 4; i++){
            SheetMusic sheet = new SheetMusic();
            sheets.add(sheet);
            builder.addVoice(voiceWith(sheet));
        }

        Music music = builder.buildMusic();

        assertThat(music.getName()).isEqualTo("Minha musica");
        assertThat(music.getDefaultBPMElement()).isSameAs(bpm);
        assertThat(music.getVoices()).hasSize(4);
        for (int i = 0; i < 4; i++){
            assertThat(music.getVoices().get(i).getSheetMusic()).isSameAs(sheets.get(i));
        }
    }

    @Test
    void usesDefaultBpmWhenNoneIsSet() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();

        assertThat(music.getDefaultBPMElement().getBPM()).isEqualTo(MusicBuilder.DEFAULT_BPM);
    }

    @Test
    void voicesAddedAfterBuildDoNotChangeBuiltMusic() {
        MusicBuilder builder = builderWithVoices(4);

        Music music = builder.buildMusic();
        builder.addVoice(voiceWith(new SheetMusic()));

        assertThat(music.getVoices()).hasSize(4);
        assertThat(builder.buildMusic().getVoices()).hasSize(5);
    }

    @Test
    void missingValuesAreRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().buildMusic());
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().setName("Minha musica").setDefaultBPM(null).buildMusic());
        assertThrows(IllegalArgumentException.class,
            () -> new MusicBuilder().addVoice(null));
    }

    @Test
    void invalidVoiceIsRejectedWhenAdded() {
        assertThrows(IllegalArgumentException.class, () -> new MusicBuilder().addVoice(new VoiceBuilder()));
    }

    @Test
    void hasFourVoicesByDefault() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();

        assertThat(MusicBuilder.DEFAULT_NUMBER_OF_VOICES).isEqualTo(4);
        assertThat(music.getVoices()).hasSize(4);
    }

    @Test
    void defaultVoicesAreIndependent() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();
        List<Voice> voices = music.getVoices();

        for (int i = 0; i < voices.size(); i++){
            for (int j = i + 1; j < voices.size(); j++){
                assertThat(voices.get(i).getSheetMusic()).isNotSameAs(voices.get(j).getSheetMusic());
            }
        }

        voices.get(0).getSheetMusic().appendToSheetMusic(new VolumeElement(80));

        assertThat(voices.get(0).getSheetMusic().getSheetMusic()).hasSize(1);
        for (int i = 1; i < voices.size(); i++){
            assertThat(voices.get(i).getSheetMusic().getSheetMusic()).isEmpty();
        }
    }

    @Test
    void defaultVoicesUseDefaultPreset() {
        Music music = new MusicBuilder().setName("Minha musica").buildMusic();

        for (Voice voice : music.getVoices()){
            assertThat(voice.getSheetMusic().getSheetMusic()).isEmpty();
            assertThat(voice.getVoicePreset().getInstrumentElement().getInstrument()).isEqualTo(VoicePreset.DEFAULT_INSTRUMENT);
            assertThat(voice.getVoicePreset().getVolumeElement().getVolume()).isEqualTo(VoicePreset.DEFAULT_VOLUME);
            assertThat(voice.getVoicePreset().getDefaultOctave().getOctaveInt()).isEqualTo(VoicePreset.DEFAULT_OCTAVE);
        }
    }

    @Test
    void defaultVoicesUseTheVoiceMapping() {
        List<Voice> voices = new MusicBuilder().setName("Minha musica").buildMusic().getVoices();

        assertThat(voices.get(0).getInstrumentType()).isEqualTo(InstrumentType.STRINGS);
        for (int i = 0; i < voices.size(); i++){
            assertThat(voices.get(i).getInstrumentType()).isEqualTo(MusicBuilder.getInstrumentTypeForVoice(i));
        }
    }

    @Test
    void voiceZeroIsStringsAndVoiceFourIsPercussion() {
        assertThat(MusicBuilder.getInstrumentTypeForVoice(0)).isEqualTo(InstrumentType.STRINGS);
        assertThat(MusicBuilder.getInstrumentTypeForVoice(4)).isEqualTo(InstrumentType.PERCUSSION);
    }

    @Test
    void undefinedVoicesUseThePlaceholderType() {
        for (int voice : new int[]{1, 2, 3, 5, 15}){
            assertThat(MusicBuilder.getInstrumentTypeForVoice(voice)).isEqualTo(InstrumentType.PIANO);
        }
    }

    @Test
    void voiceIndexOutsideTheLimitsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> MusicBuilder.getInstrumentTypeForVoice(-1));
        assertThrows(IllegalArgumentException.class, () -> MusicBuilder.getInstrumentTypeForVoice(16));
    }

    @Test
    void canHaveFromFourToSixteenVoices() {
        for (int count = 4; count <= 16; count++){
            assertThat(builderWithVoices(count).buildMusic().getVoices()).hasSize(count);
        }
    }

    @Test
    void fewerThanFourVoicesAreRejected() {
        for (int count = 1; count <= 3; count++){
            MusicBuilder builder = builderWithVoices(count);
            assertThrows(IllegalArgumentException.class, builder::buildMusic);
        }
    }

    @Test
    void moreThanSixteenVoicesAreRejected() {
        MusicBuilder builder = builderWithVoices(17);

        assertThrows(IllegalArgumentException.class, builder::buildMusic);
    }
}
