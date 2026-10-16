package parsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.Test;

import builders.MusicBuilder;
import builders.VoiceBuilder;
import music.Music;
import music.SheetMusic;
import musicElements.*;

class MusicToMIDIParserTest {

    // Fixtures: todos os objetos abaixo são reais (sem mocks).
    private NoteElement note(Notes pitch, int octave,
                             DurationTime duration, Accidentals accidental) {
        return new NoteElement(pitch, new Octave(octave), duration, accidental);
    }

    private NoteElement cSharpQuarter() {
        return note(Notes.C, 4, DurationTime.QUARTER, Accidentals.SHARP);
    }

    private NoteElement dSharpQuarter() {
        return note(Notes.D, 4, DurationTime.QUARTER, Accidentals.SHARP);
    }

    private PauseElement quarterRest() {
        return new PauseElement(DurationTime.QUARTER);
    }

    private InstrumentElement instrument(int midiProgram) {
        return new InstrumentElement(midiProgram);
    }

    private VolumeElement volume(int midiVolume) {
        return new VolumeElement(midiVolume);
    }

    private SheetMusic sheetWith(MusicElement... elements) {
        SheetMusic sheet = new SheetMusic();
        for (MusicElement element : elements) {
            sheet.appendToSheetMusic(element);
        }
        return sheet;
    }

    private Music musicWith(SheetMusic... sheets) {
        MusicBuilder builder = new MusicBuilder().setName("Test music");
        for (SheetMusic sheet : sheets) {
            builder.addVoice(new VoiceBuilder().setSheetMusic(sheet));
        }
        return builder.buildMusic();
    }

    private Sequence convert(SheetMusic... sheets) {
        Music music = musicWith(sheets);
        return new MusicToMIDIParser().convertMusicToMIDI(music);
    }

    private List<MidiEvent> shortEvents(Track track, int command) {
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage message
                    && message.getCommand() == command) {
                events.add(event);
            }
        }
        return events;
    }

    private List<MidiEvent> metaEvents(Track track, int type) {
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof MetaMessage message
                    && message.getType() == type) {
                events.add(event);
            }
        }
        return events;
    }

    private ShortMessage messageOf(MidiEvent event) {
        return (ShortMessage) event.getMessage();
    }

    private int mpqOf(MidiEvent event) {
        byte[] data = ((MetaMessage) event.getMessage()).getData();
        return ((data[0] & 0xFF) << 16)
                | ((data[1] & 0xFF) << 8)
                | (data[2] & 0xFF);
    }

    @Test
    void convertsMusicWithOnlyOneNote() {
        Sequence sequence = convert(sheetWith(cSharpQuarter()));
        Track track = sequence.getTracks()[0];
        List<MidiEvent> on = shortEvents(track, ShortMessage.NOTE_ON);
        List<MidiEvent> off = shortEvents(track, ShortMessage.NOTE_OFF);

        assertThat(sequence.getTracks()).hasSize(1);
        assertThat(on).hasSize(1);
        assertThat(off).hasSize(1);
        assertThat(messageOf(on.get(0)).getData1()).isEqualTo(61);
        assertThat(messageOf(on.get(0)).getData2()).isEqualTo(100);
        assertThat(messageOf(on.get(0)).getChannel()).isZero();
        assertThat(on.get(0).getTick()).isZero();
        assertThat(messageOf(off.get(0)).getData1()).isEqualTo(61);
        assertThat(off.get(0).getTick()).isEqualTo(480);
    }

    @Test
    void convertsMusicWithOnlyOnePause() {
        Sequence sequence = convert(sheetWith(quarterRest()));
        Track track = sequence.getTracks()[0];
        List<MidiEvent> endOfTrack = metaEvents(track, 0x2F);

        assertThat(sequence.getTracks()).hasSize(1);
        assertThat(shortEvents(track, ShortMessage.NOTE_ON)).isEmpty();
        assertThat(shortEvents(track, ShortMessage.NOTE_OFF)).isEmpty();
        assertThat(endOfTrack).hasSize(1);
        assertThat(endOfTrack.get(0).getTick()).isEqualTo(480);
    }

    @Test
    void convertsMusicWithOnlyOneInstrument() {
        Track track = convert(sheetWith(instrument(40))).getTracks()[0];
        List<MidiEvent> changes = shortEvents(track, ShortMessage.PROGRAM_CHANGE);

        assertThat(changes).hasSize(1);
        assertThat(changes.get(0).getTick()).isZero();
        assertThat(messageOf(changes.get(0)).getData1()).isEqualTo(40);
        assertThat(messageOf(changes.get(0)).getChannel()).isZero();
    }

    @Test
    void convertsMusicWithOnlyOneBpm() {
        Track track = convert(sheetWith(new BPMElement(120))).getTracks()[0];
        List<MidiEvent> tempos = metaEvents(track, 0x51);

        assertThat(tempos).hasSize(1);
        assertThat(tempos.get(0).getTick()).isZero();
        assertThat(mpqOf(tempos.get(0))).isEqualTo(500_000);
    }

    @Test
    void convertsMusicWithOnlyOneVolume() {
        Track track = convert(sheetWith(volume(90))).getTracks()[0];
        List<MidiEvent> changes = shortEvents(track, ShortMessage.CONTROL_CHANGE);

        assertThat(changes).hasSize(1);
        assertThat(changes.get(0).getTick()).isZero();
        assertThat(messageOf(changes.get(0)).getData1()).isEqualTo(7);
        assertThat(messageOf(changes.get(0)).getData2()).isEqualTo(90);
        assertThat(messageOf(changes.get(0)).getChannel()).isZero();
    }

    @Test
    void convertsMultipleElementsInOneVoice() {
        Track track = convert(sheetWith(
            instrument(0), volume(90), new BPMElement(120),
            cSharpQuarter(), quarterRest(), dSharpQuarter(),
            volume(70), new BPMElement(90)
        )).getTracks()[0];

        List<MidiEvent> notesOn = shortEvents(track, ShortMessage.NOTE_ON);
        List<MidiEvent> notesOff = shortEvents(track, ShortMessage.NOTE_OFF);
        List<MidiEvent> volumes = shortEvents(track, ShortMessage.CONTROL_CHANGE);
        List<MidiEvent> tempos = metaEvents(track, 0x51);

        assertThat(shortEvents(track, ShortMessage.PROGRAM_CHANGE)).hasSize(1);
        assertThat(notesOn).hasSize(2);
        assertThat(notesOff).hasSize(2);
        assertThat(notesOn.get(0).getTick()).isZero();
        assertThat(notesOff.get(0).getTick()).isEqualTo(480);
        assertThat(notesOn.get(1).getTick()).isEqualTo(960);
        assertThat(notesOff.get(1).getTick()).isEqualTo(1440);
        assertThat(messageOf(notesOn.get(0)).getData1()).isEqualTo(61);
        assertThat(messageOf(notesOn.get(1)).getData1()).isEqualTo(63);
        assertThat(volumes).hasSize(2);
        assertThat(messageOf(volumes.get(0)).getData2()).isEqualTo(90);
        assertThat(messageOf(volumes.get(1)).getData2()).isEqualTo(70);
        assertThat(volumes.get(1).getTick()).isEqualTo(1440);
        assertThat(tempos).hasSize(2);
        assertThat(mpqOf(tempos.get(0))).isEqualTo(500_000);
        assertThat(mpqOf(tempos.get(1))).isEqualTo(666_666);
        assertThat(tempos.get(1).getTick()).isEqualTo(1440);
    }

    @Test
    void convertsMultipleVoicesWithMultipleElements() {
        Sequence sequence = convert(
            sheetWith(instrument(0), volume(90), new BPMElement(120),
                      cSharpQuarter(), quarterRest(), dSharpQuarter()),
            sheetWith(instrument(40), volume(70), dSharpQuarter(), cSharpQuarter()),
            sheetWith(instrument(10), cSharpQuarter(), quarterRest())
        );

        assertThat(sequence.getTracks()).hasSize(3);
        Track first = sequence.getTracks()[0];
        Track second = sequence.getTracks()[1];
        Track third = sequence.getTracks()[2];

        assertThat(shortEvents(first, ShortMessage.NOTE_ON)).hasSize(2);
        assertThat(shortEvents(second, ShortMessage.NOTE_ON)).hasSize(2);
        assertThat(shortEvents(third, ShortMessage.NOTE_ON)).hasSize(1);
        assertThat(messageOf(shortEvents(first, ShortMessage.NOTE_ON).get(0)).getChannel()).isZero();
        assertThat(messageOf(shortEvents(second, ShortMessage.NOTE_ON).get(0)).getChannel()).isEqualTo(1);
        assertThat(messageOf(shortEvents(third, ShortMessage.NOTE_ON).get(0)).getChannel()).isEqualTo(2);
        assertThat(messageOf(shortEvents(first, ShortMessage.PROGRAM_CHANGE).get(0)).getData1()).isZero();
        assertThat(messageOf(shortEvents(second, ShortMessage.PROGRAM_CHANGE).get(0)).getData1()).isEqualTo(40);
        assertThat(messageOf(shortEvents(third, ShortMessage.PROGRAM_CHANGE).get(0)).getData1()).isEqualTo(10);
        assertThat(messageOf(shortEvents(first, ShortMessage.CONTROL_CHANGE).get(0)).getData2()).isEqualTo(90);
        assertThat(messageOf(shortEvents(second, ShortMessage.CONTROL_CHANGE).get(0)).getData2()).isEqualTo(70);
        assertThat(shortEvents(third, ShortMessage.CONTROL_CHANGE)).isEmpty();
        assertThat(shortEvents(first, ShortMessage.NOTE_ON).get(1).getTick()).isEqualTo(960);
        assertThat(shortEvents(second, ShortMessage.NOTE_ON).get(0).getTick()).isZero();
        assertThat(metaEvents(third, 0x2F).get(0).getTick()).isEqualTo(960);
        assertThat(mpqOf(metaEvents(first, 0x51).get(0))).isEqualTo(500_000);
    }

    @Test
    void convertsEmptySheetMusic() {
        Sequence sequence = convert(sheetWith());
        Track track = sequence.getTracks()[0];

        assertThat(sequence.getTracks()).hasSize(1);
        assertThat(shortEvents(track, ShortMessage.NOTE_ON)).isEmpty();
        assertThat(shortEvents(track, ShortMessage.PROGRAM_CHANGE)).isEmpty();
        assertThat(metaEvents(track, 0x2F)).hasSize(1);
    }

    @Test
    void rejectsNullMusic() {
        assertThrows(NullPointerException.class,
            () -> new MusicToMIDIParser().convertMusicToMIDI(null));
    }

    @Test
    void startsEachConversionWithANewSequence() {
        MusicToMIDIParser parser = new MusicToMIDIParser();
        Sequence first = parser.convertMusicToMIDI(musicWith(
            sheetWith(cSharpQuarter()), sheetWith(dSharpQuarter())
        ));
        Sequence second = parser.convertMusicToMIDI(musicWith(
            sheetWith(volume(70))
        ));

        assertThat(first).isNotSameAs(second);
        assertThat(first.getTracks()).hasSize(2);
        assertThat(second.getTracks()).hasSize(1);
        assertThat(shortEvents(second.getTracks()[0], ShortMessage.NOTE_ON)).isEmpty();
        assertThat(shortEvents(second.getTracks()[0], ShortMessage.CONTROL_CHANGE)).hasSize(1);
    }
}
