package integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import exporters.MIDIFileExporter;
import input.TXTInputReader;
import music.Music;
import musicElements.BPMElement;
import musicElements.InstrumentElement;
import musicElements.MusicElement;
import musicElements.NoteElement;
import musicElements.PauseElement;
import musicElements.VolumeElement;
import parsers.MusicToMIDIParser;
import player.MidiPlayer;
import translators.TextToMusic.TextToMusicTranslator;

/**
 * Simula uma execucao completa do programa, passando por todos os modulos na ordem real de uso:
 *
 *   arquivo .txt -> TXTInputReader -> TextToMusicTranslator -> Music
 *                -> MusicToMIDIParser -> Sequence -> MIDIFileExporter -> arquivo .mid -> MidiPlayer
 *
 * Substitui o metodo provisorio CompositionScreen.saveMIDI, sem depender da interface grafica.
 */
public class FullExecutionTest {

    // ---------- Valores MIDI esperados (PPQ = 480) ----------
    private static final long QUARTER = 480;
    private static final long HALF    = 960;
    private static final long EIGHTH  = 240;

    /**
     * Caminho feliz: o pipeline roda UMA vez no @BeforeAll e cada teste confere a passagem de dados
     * entre dois modulos. Se um teste falhar, ele aponta qual "fronteira" quebrou.
     */
    @Nested
    class CompletePipeline {

        /*
         * Toda voz com conteudo comeca com o preset: instrumento, volume e BPM 120 (tick 0).
         *
         * Voz 1 (STRINGS: oitava base 6, instrumento 40), token a token:
         *   C     -> nota C6, seminima                 MIDI 84,  tick 0
         *   D:2   -> nota D6, minima                   MIDI 86,  tick 480
         *   a     -> pausa de seminima                           tick 1440 -> 1920
         *   ?     -> oitava sobe para 7
         *   E#    -> nota E#7, seminima                MIDI 101, tick 1920
         *   ' '   -> volume dobra: 100 * 2 -> limitado a 127     tick 2400
         *   >     -> BPM 120 -> 130                              tick 2400
         *   2     -> proximo instrumento da familia: 40 -> 41    tick 2400
         *   V     -> oitava desce para 6
         *   Bb:4  -> nota Bb6, colcheia                MIDI 94,  tick 2400
         *   !     -> caractere sem regra: repete a ultima nota   MIDI 94,  tick 2640
         *   fim da voz 1 no tick 2880
         *
         * Voz 2 (KEYS: oitava base 4):
         *   [2]   -> atraso de 2 seminimas (duas pausas)
         *   G     -> nota G4, seminima                 MIDI 67,  tick 960
         *
         * Vozes 3 e 4: vazias (Music sempre tem no minimo 4 vozes).
         */
        private static final String VOICE_1 = "CD:2a?E# >2VBb:4!";
        private static final String VOICE_2 = "[2] G";

        @TempDir
        static Path tempDir;

        private static Music music;
        private static Sequence sequence;
        private static Path midiFile;

        @BeforeAll
        static void runPipeline() throws Exception {
            // 1. Usuário fornece um .txt com quebra de linha CRLF
            Path txtFile = tempDir.resolve("partitura.txt");
            Files.writeString(txtFile, VOICE_1 + "\r\n" + VOICE_2);

            // 2. Leitura
            String text = new TXTInputReader().readFile(txtFile);

            // 3. Tradução texto -> Music
            music = new TextToMusicTranslator()
                    .textToMusic("Musica de teste", text);

            // 4. Conversão Music -> Sequence MIDI
            sequence = new MusicToMIDIParser()
                    .convertMusicToMIDI(music);

            // 5. Exportação para arquivo .mid
            new MIDIFileExporter().save(sequence, tempDir, "musica.mid");

            midiFile = tempDir.resolve("musica.mid");
        }

        @Test
        void translatorBuildsTheExpectedMusic() {
            assertThat(music.getVoices()).hasSize(Music.MIN_VOICES);

            assertThat(types(music.getVoices().get(0).getSheetMusicList())).containsExactly(
                InstrumentElement.class, VolumeElement.class, BPMElement.class,
                NoteElement.class, NoteElement.class, PauseElement.class, NoteElement.class,
                VolumeElement.class, BPMElement.class, InstrumentElement.class,
                NoteElement.class, NoteElement.class);

            assertThat(types(music.getVoices().get(1).getSheetMusicList())).containsExactly(
                InstrumentElement.class, VolumeElement.class, BPMElement.class,
                PauseElement.class, PauseElement.class, NoteElement.class);

            assertThat(music.getVoices().get(2).getSheetMusicList()).isEmpty();
            assertThat(music.getVoices().get(3).getSheetMusicList()).isEmpty();
        }

        @Test
        void parserCreatesOneTrackPerVoiceWithTheExpectedNotes() {
            Track[] tracks = sequence.getTracks();

            assertThat(tracks).hasSize(music.getVoices().size());
            assertThat(noteOns(tracks[0])).containsExactly(
                new NoteOn(84, 0),
                new NoteOn(86, QUARTER),
                new NoteOn(101, QUARTER + HALF + QUARTER),
                new NoteOn(94, 2400),
                new NoteOn(94, 2400 + EIGHTH));
            assertThat(noteOns(tracks[1])).containsExactly(new NoteOn(67, 2 * QUARTER));
            assertThat(noteOns(tracks[2])).isEmpty();
            assertThat(noteOns(tracks[3])).isEmpty();

            // cada voz toca no seu proprio canal
            assertThat(channelOfFirstNote(tracks[0])).isNotEqualTo(channelOfFirstNote(tracks[1]));
            assertThat(sequence.getTickLength()).isEqualTo(2880);
        }

        @Test
        void controlElementsBecomeMidiMessagesAtTheRightTick() {
            Track voice1 = sequence.getTracks()[0];

            assertThat(controlValueAt(voice1, 2400, 7)).isEqualTo(127);       // volume
            assertThat(programAt(voice1, 2400)).isEqualTo(41);                // instrumento
            assertThat(tempoMpqAt(voice1, 2400)).isEqualTo(60_000_000 / 130); // BPM 130
        }

        @Test
        void exportedFileKeepsTheSequenceIntact() throws Exception {
            Sequence imported = MidiSystem.getSequence(midiFile.toFile());

            assertThat(MidiSystem.getMidiFileFormat(midiFile.toFile()).getType()).isEqualTo(1);
            assertThat(imported.getResolution()).isEqualTo(sequence.getResolution());
            assertThat(imported.getTracks()).hasSameSizeAs(sequence.getTracks());
            for (int i = 0; i < sequence.getTracks().length; i++) {
                assertThat(noteOns(imported.getTracks()[i])).isEqualTo(noteOns(sequence.getTracks()[i]));
            }
        }

        @Test
        void playerLoadsAndPlaysTheExportedFile() {
            // 2400 ticks a 120 BPM (2500 ms) + 480 ticks a 130 BPM (~461 ms)
            long expectedDurationMs = 2500 + 60_000 / 130;

            try (MidiPlayer player = new MidiPlayer()) {
                player.load(midiFile);
                assertThat(player.getDuration()).isCloseTo(expectedDurationMs, within(1L));

                player.play();
                assertThat(player.isPlaying()).isTrue();

                player.stop();
                assertThat(player.isPlaying()).isFalse();
                assertThat(player.getCurrentTime()).isZero();
            }
        }
    }

    /**
     * Casos de fronteira entre modulos corrigidos na integracao:
     * aplicacao do VoicePreset/BPM inicial no MIDI e tratamento da quebra de linha final.
     */
    @Nested
    class CommunicationBoundaries {

        private final TextToMusicTranslator translator = new TextToMusicTranslator();

        // Translator -> MusicToMIDIParser: o VoicePreset (instrumento/volume da voz) e o BPM padrao
        // vem no inicio da partitura e devem chegar no inicio da track (tick 0).
        @Test
        void voicePresetIsAppliedToTheMidiTrack() {
            Music music = translator.textToMusic("Preset", "C");
            Track voice1 = new MusicToMIDIParser().convertMusicToMIDI(music).getTracks()[0];

            assertThat(tempoMpqAt(voice1, 0)).as("BPM padrao (120) no tick 0").isEqualTo(500_000);
            assertThat(programAt(voice1, 0)).as("instrumento do preset STRINGS no tick 0").isEqualTo(40);
            assertThat(controlValueAt(voice1, 0, 7)).as("volume do preset STRINGS no tick 0").isEqualTo(100);
        }
    }

    // ---------- Auxiliares de inspecao MIDI ----------

    private record NoteOn(int pitch, long tick) {}

    private static List<Class<?>> types(List<MusicElement> elements) {
        return elements.stream().<Class<?>>map(Object::getClass).toList();
    }

    private static List<NoteOn> noteOns(Track track) {
        List<NoteOn> notes = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage msg
                    && msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() > 0) {
                notes.add(new NoteOn(msg.getData1(), event.getTick()));
            }
        }
        return notes;
    }

    private static Integer channelOfFirstNote(Track track) {
        for (int i = 0; i < track.size(); i++) {
            if (track.get(i).getMessage() instanceof ShortMessage msg && msg.getCommand() == ShortMessage.NOTE_ON) {
                return msg.getChannel();
            }
        }
        return null;
    }

    private static Integer programAt(Track track, long tick) {
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getTick() == tick && event.getMessage() instanceof ShortMessage msg
                    && msg.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                return msg.getData1();
            }
        }
        return null;
    }

    private static Integer controlValueAt(Track track, long tick, int controller) {
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getTick() == tick && event.getMessage() instanceof ShortMessage msg
                    && msg.getCommand() == ShortMessage.CONTROL_CHANGE && msg.getData1() == controller) {
                return msg.getData2();
            }
        }
        return null;
    }

    private static Integer tempoMpqAt(Track track, long tick) {
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getTick() == tick && event.getMessage() instanceof MetaMessage meta && meta.getType() == 0x51) {
                byte[] d = meta.getData();
                return ((d[0] & 0xFF) << 16) | ((d[1] & 0xFF) << 8) | (d[2] & 0xFF);
            }
        }
        return null;
    }
}
