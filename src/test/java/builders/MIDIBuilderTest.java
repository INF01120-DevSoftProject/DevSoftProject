package builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.Test;

import musicElements.*;

public class MIDIBuilderTest {


    private NoteElement cSharp4Quarter() {
        return note(Notes.C, 4, DurationTime.QUARTER, Accidentals.SHARP);
    }

    private NoteElement dSharp4Quarter() {
        return note(Notes.D, 4, DurationTime.QUARTER, Accidentals.SHARP);
    }

    private NoteElement note(Notes pitch, int octave, DurationTime duration, Accidentals accidental) {
        return new NoteElement(pitch, new Octave(octave), duration, accidental);
    }

    private PauseElement pause(DurationTime duration) {
        return new PauseElement(duration);
    }

    private InstrumentElement instrument(int midiProgram) {
        for (InstrumentType type : InstrumentType.values()) {
            if (type.contains(midiProgram)) {
                return new InstrumentElement(midiProgram, type);
            }
        }
        throw new IllegalArgumentException("Invalid MIDI program: " + midiProgram);
    }

    private VolumeElement volume(int midiVolume) {
        return new VolumeElement(midiVolume);
    }

    private List<MidiEvent> shortEvents(Track track, int command) {
        return IntStream.range(0, track.size())
            .mapToObj(track::get)
            .filter(event -> event.getMessage() instanceof ShortMessage message
                && message.getCommand() == command)
            .toList();
    }

    private MidiEvent firstShortEvent(Track track, int command) {
        return shortEvents(track, command).get(0);
    }

    private ShortMessage firstShortMessage(Track track, int command) {
        return (ShortMessage) firstShortEvent(track, command).getMessage();
    }

    private List<MidiEvent> tempoEvents(Track track) {
        return IntStream.range(0, track.size())
            .mapToObj(track::get)
            .filter(event -> event.getMessage() instanceof MetaMessage message
                && message.getType() == 0x51)
            .toList();
    }

    private int mpq(MidiEvent event) {
        byte[] data = ((MetaMessage) event.getMessage()).getData();
        assertThat(data).hasSize(3);
        return (Byte.toUnsignedInt(data[0]) << 16)
             | (Byte.toUnsignedInt(data[1]) << 8)
             | Byte.toUnsignedInt(data[2]);
    }

    @Test
    void buildsSequenceWithCorrectResolution() {
        Sequence sequence = new MidiSequenceBuilder().build();

        assertThat(sequence.getDivisionType()).isEqualTo(Sequence.PPQ);
        assertThat(sequence.getResolution()).isEqualTo(480);
    }

    @Test
    void createsSingleTrack() {
        Sequence sequence = new MidiSequenceBuilder().addTrack().build();

        assertThat(sequence.getTracks()).hasSize(1);
    }

    @Test
    void createsMultipleTracks() {
        Sequence sequence = new MidiSequenceBuilder()
            .addTrack()
            .addTrack()
            .addTrack()
            .build();

        assertThat(sequence.getTracks()).hasSize(3);
        assertThat(sequence.getTracks()[0]).isNotSameAs(sequence.getTracks()[1]);
        assertThat(sequence.getTracks()[1]).isNotSameAs(sequence.getTracks()[2]);
    }

    @Test
    void addsNoteWithCorrectMidiValue() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .addNote(cSharp4Quarter())
            .build().getTracks()[0];

        ShortMessage noteOn = firstShortMessage(track, ShortMessage.NOTE_ON);
        ShortMessage noteOff = firstShortMessage(track, ShortMessage.NOTE_OFF);

        assertThat(noteOn.getData1()).isEqualTo(61);
        assertThat(noteOn.getData2()).isEqualTo(100);
        assertThat(noteOff.getData1()).isEqualTo(61);
        assertThat(firstShortEvent(track, ShortMessage.NOTE_ON).getTick()).isZero();
        assertThat(firstShortEvent(track, ShortMessage.NOTE_OFF).getTick()).isEqualTo(480);
    }

    @Test
    void matchesNoteOnAndNoteOff() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .addNote(cSharp4Quarter())
            .build().getTracks()[0];

        ShortMessage noteOn = firstShortMessage(track, ShortMessage.NOTE_ON);
        ShortMessage noteOff = firstShortMessage(track, ShortMessage.NOTE_OFF);

        assertThat(noteOff.getData1()).isEqualTo(noteOn.getData1());
        assertThat(noteOff.getChannel()).isEqualTo(noteOn.getChannel());
        assertThat(firstShortEvent(track, ShortMessage.NOTE_OFF).getTick())
            .isGreaterThan(firstShortEvent(track, ShortMessage.NOTE_ON).getTick());
    }

    @Test
    void calculatesCorrectNoteDuration() {
        for (int divisor : new int[] {1, 2, 4, 8, 16}) {
            DurationTime duration = Arrays.stream(DurationTime.values())
                .filter(value -> value.getDivisor() == divisor)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Duração ausente: 1/" + divisor));

            Track track = new MidiSequenceBuilder()
                .addTrack()
                .addNote(note(Notes.C, 4, duration, Accidentals.SHARP))
                .build().getTracks()[0];

            assertThat(firstShortEvent(track, ShortMessage.NOTE_OFF).getTick())
                .as("Duração com divisor %s", divisor)
                .isEqualTo(1920L / divisor);
        }
    }

    @Test
    void addsPauseBetweenNotes() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .addNote(cSharp4Quarter())
            .addPause(pause(DurationTime.QUARTER))
            .addNote(dSharp4Quarter())
            .build().getTracks()[0];

        List<MidiEvent> noteOns = shortEvents(track, ShortMessage.NOTE_ON);

        assertThat(noteOns).hasSize(2);
        assertThat(noteOns.get(0).getTick()).isZero();
        assertThat(noteOns.get(1).getTick()).isEqualTo(960);
    }

    @Test
    void preservesTrailingPause() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .addNote(cSharp4Quarter())
            .addPause(pause(DurationTime.QUARTER))
            .build().getTracks()[0];

        assertThat(track.ticks()).isEqualTo(960);
    }

    @Test
    void setsInstrumentCorrectly() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .setInstrument(instrument(40))
            .build().getTracks()[0];

        ShortMessage message = firstShortMessage(track, ShortMessage.PROGRAM_CHANGE);
        assertThat(message.getChannel()).isZero();
        assertThat(message.getData1()).isEqualTo(40);
        assertThat(firstShortEvent(track, ShortMessage.PROGRAM_CHANGE).getTick()).isZero();
    }

    @Test
    void setsVolumeCorrectly() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .setVolume(volume(80))
            .build().getTracks()[0];

        ShortMessage message = firstShortMessage(track, ShortMessage.CONTROL_CHANGE);
        assertThat(message.getChannel()).isZero();
        assertThat(message.getData1()).isEqualTo(7);
        assertThat(message.getData2()).isEqualTo(80);
    }

    @Test
    void setsInitialBpmCorrectly() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .setBPM(new BPMElement(120))
            .build().getTracks()[0];

        assertThat(tempoEvents(track)).hasSize(1);
        assertThat(tempoEvents(track).get(0).getTick()).isZero();
        assertThat(mpq(tempoEvents(track).get(0))).isEqualTo(500_000);
    }

    @Test
    void changesBpmDuringMusic() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .setBPM(new BPMElement(120))
            .addNote(cSharp4Quarter())
            .setBPM(new BPMElement(60))
            .addNote(dSharp4Quarter())
            .build().getTracks()[0];

        List<MidiEvent> tempos = tempoEvents(track);
        List<MidiEvent> noteOns = shortEvents(track, ShortMessage.NOTE_ON);

        assertThat(tempos).hasSize(2);
        assertThat(tempos.get(0).getTick()).isZero();
        assertThat(mpq(tempos.get(0))).isEqualTo(500_000);
        assertThat(tempos.get(1).getTick()).isEqualTo(480);
        assertThat(mpq(tempos.get(1))).isEqualTo(1_000_000);
        assertThat(noteOns.get(1).getTick()).isEqualTo(480);
    }

    @Test
    void addsTempoChangesFromDifferentVoicesToTheFirstTrack() {
        Sequence sequence = new MidiSequenceBuilder()
            .addTrack()
            .setBPM(new BPMElement(120))
            .addTrack()
            .setBPM(new BPMElement(90))
            .build();

        assertThat(tempoEvents(sequence.getTracks()[0])).hasSize(2);
        assertThat(tempoEvents(sequence.getTracks()[1])).isEmpty();
        assertThat(mpq(tempoEvents(sequence.getTracks()[0]).get(0))).isEqualTo(500_000);
        assertThat(mpq(tempoEvents(sequence.getTracks()[0]).get(1))).isEqualTo(666_666);
    }

    @Test
    void addsControlEventsAtCorrectTick() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .addNote(cSharp4Quarter())
            .setInstrument(instrument(40))
            .setVolume(volume(80))
            .setBPM(new BPMElement(120))
            .build().getTracks()[0];

        assertThat(firstShortEvent(track, ShortMessage.PROGRAM_CHANGE).getTick()).isEqualTo(480);
        assertThat(firstShortEvent(track, ShortMessage.CONTROL_CHANGE).getTick()).isEqualTo(480);
        assertThat(tempoEvents(track).get(0).getTick()).isEqualTo(480);
    }

    @Test
    void rejectsElementsWithoutTrack() {
        assertThrows(NullPointerException.class,
            () -> new MidiSequenceBuilder().addNote(cSharp4Quarter()));
        assertThrows(NullPointerException.class,
            () -> new MidiSequenceBuilder().addPause(pause(DurationTime.QUARTER)));
        assertThrows(NullPointerException.class,
            () -> new MidiSequenceBuilder().setInstrument(instrument(40)));
        assertThrows(NullPointerException.class,
            () -> new MidiSequenceBuilder().setVolume(volume(80)));
        assertThrows(NullPointerException.class,
            () -> new MidiSequenceBuilder().setBPM(new BPMElement(120)));
    }

    @Test
    void rejectsNullElements() {
        MidiSequenceBuilder builder = new MidiSequenceBuilder().addTrack();

        assertThrows(NullPointerException.class, () -> builder.addNote(null));
        assertThrows(NullPointerException.class, () -> builder.addPause(null));
        assertThrows(NullPointerException.class, () -> builder.setInstrument(null));
        assertThrows(NullPointerException.class, () -> builder.setVolume(null));
        assertThrows(NullPointerException.class, () -> builder.setBPM(null));
    }

    @Test
    void rejectsInvalidBpm() {
        for (int bpm : new int[] {0, -1, 3, 60_000_001}) {
            assertThrows(IllegalArgumentException.class,
                () -> new MidiSequenceBuilder().addTrack().setBPM(new BPMElement(bpm)));
        }
    }

    @Test
    void rejectsInvalidMidiNote() {
        assertThrows(IllegalArgumentException.class,
            () -> new MidiSequenceBuilder().addTrack()
                .addNote(note(Notes.C, 10, DurationTime.QUARTER, Accidentals.SHARP)));
        assertThrows(IllegalArgumentException.class,
            () -> new MidiSequenceBuilder().addTrack()
                .addNote(note(Notes.C, -3, DurationTime.QUARTER, Accidentals.SHARP)));
    }

    @Test
    void rejectsInvalidMidiChannel() {
        MidiSequenceBuilder builder = new MidiSequenceBuilder();

        assertThrows(IllegalArgumentException.class, () -> builder.setCurrentChannel(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.setCurrentChannel(16));
    }

    @Test
    void skipsPercussionChannel() {
        MidiSequenceBuilder builder = new MidiSequenceBuilder();
        for (int i = 0; i < 10; i++) {
            builder.addTrack().addNote(cSharp4Quarter());
        }

        Sequence sequence = builder.build();
        assertThat(sequence.getTracks()).hasSize(10);
        assertThat(firstShortMessage(sequence.getTracks()[8], ShortMessage.NOTE_ON).getChannel())
            .isEqualTo(8);
        assertThat(firstShortMessage(sequence.getTracks()[9], ShortMessage.NOTE_ON).getChannel())
            .isEqualTo(10);
    }

    @Test
    void redirectsPercussionChannel() {
        Track track = new MidiSequenceBuilder()
            .addTrack()
            .setCurrentChannel(9)
            .addNote(cSharp4Quarter())
            .build().getTracks()[0];

        assertThat(firstShortMessage(track, ShortMessage.NOTE_ON).getChannel()).isEqualTo(10);
    }

    @Test
    void assignsDifferentChannelToEachTrack() {
        Sequence sequence = new MidiSequenceBuilder()
            .addTrack().addNote(cSharp4Quarter())
            .addTrack().addNote(cSharp4Quarter())
            .addTrack().addNote(cSharp4Quarter())
            .build();

        assertThat(firstShortMessage(sequence.getTracks()[0], ShortMessage.NOTE_ON).getChannel())
            .isEqualTo(0);
        assertThat(firstShortMessage(sequence.getTracks()[1], ShortMessage.NOTE_ON).getChannel())
            .isEqualTo(1);
        assertThat(firstShortMessage(sequence.getTracks()[2], ShortMessage.NOTE_ON).getChannel())
            .isEqualTo(2);
    }

    @Test
    void rejectsTrackWhenChannelLimitIsReached() {
        MidiSequenceBuilder builder = new MidiSequenceBuilder();
        for (int i = 0; i < 15; i++) {
            builder.addTrack();
        }

        assertThrows(IllegalArgumentException.class, builder::addTrack);
        assertThat(builder.build().getTracks()).hasSize(15);
    }

    @Test
    void addsNotesToDifferentTracks() {
        Sequence sequence = new MidiSequenceBuilder()
            .addTrack().addNote(cSharp4Quarter())
            .addTrack().addNote(dSharp4Quarter())
            .build();

        Track firstTrack = sequence.getTracks()[0];
        Track secondTrack = sequence.getTracks()[1];

        assertThat(shortEvents(firstTrack, ShortMessage.NOTE_ON)).hasSize(1);
        assertThat(shortEvents(secondTrack, ShortMessage.NOTE_ON)).hasSize(1);
        assertThat(firstShortMessage(firstTrack, ShortMessage.NOTE_ON).getData1()).isEqualTo(61);
        assertThat(firstShortMessage(secondTrack, ShortMessage.NOTE_ON).getData1()).isEqualTo(63);
        assertThat(firstShortMessage(firstTrack, ShortMessage.NOTE_ON).getChannel()).isZero();
        assertThat(firstShortMessage(secondTrack, ShortMessage.NOTE_ON).getChannel()).isEqualTo(1);
    }

    @Test
    void resetsTickForEachNewTrack() {
        Sequence sequence = new MidiSequenceBuilder()
            .addTrack()
            .addNote(cSharp4Quarter())
            .addNote(cSharp4Quarter())
            .addTrack()
            .addNote(dSharp4Quarter())
            .build();

        assertThat(shortEvents(sequence.getTracks()[0], ShortMessage.NOTE_ON).get(1).getTick())
            .isEqualTo(480);
        assertThat(firstShortEvent(sequence.getTracks()[1], ShortMessage.NOTE_ON).getTick())
            .isZero();
    }

    @Test
    void buildsSequenceWithMultipleTracksAndEvents() {
        Sequence sequence = new MidiSequenceBuilder()
            .addTrack()
            .setInstrument(instrument(0))
            .setVolume(volume(90))
            .setBPM(new BPMElement(120))
            .addNote(cSharp4Quarter())
            .addPause(pause(DurationTime.QUARTER))
            .addNote(dSharp4Quarter())
            .addTrack()
            .setInstrument(instrument(40))
            .setVolume(volume(70))
            .addNote(cSharp4Quarter())
            .setBPM(new BPMElement(90))
            .build();

        Track first = sequence.getTracks()[0];
        Track second = sequence.getTracks()[1];

        assertThat(sequence.getTracks()).hasSize(2);
        assertThat(firstShortMessage(first, ShortMessage.PROGRAM_CHANGE).getData1()).isZero();
        assertThat(firstShortMessage(second, ShortMessage.PROGRAM_CHANGE).getData1()).isEqualTo(40);
        assertThat(firstShortMessage(first, ShortMessage.CONTROL_CHANGE).getData2()).isEqualTo(90);
        assertThat(firstShortMessage(second, ShortMessage.CONTROL_CHANGE).getData2()).isEqualTo(70);
        assertThat(shortEvents(first, ShortMessage.NOTE_ON)).hasSize(2);
        assertThat(shortEvents(second, ShortMessage.NOTE_ON)).hasSize(1);
        assertThat(shortEvents(first, ShortMessage.NOTE_ON).get(1).getTick()).isEqualTo(960);
        assertThat(firstShortEvent(second, ShortMessage.NOTE_ON).getTick()).isZero();
        assertThat(firstShortMessage(first, ShortMessage.NOTE_ON).getChannel()).isZero();
        assertThat(firstShortMessage(second, ShortMessage.NOTE_ON).getChannel()).isEqualTo(1);
        assertThat(mpq(tempoEvents(first).get(0))).isEqualTo(500_000);
        assertThat(mpq(tempoEvents(first).get(1))).isEqualTo(666_666);
        assertThat(tempoEvents(first).get(1).getTick()).isEqualTo(480);
        assertThat(tempoEvents(second)).isEmpty();
    }

    @Test
    void pauseAdvancesCurrentTick() {
        PauseElement quarterPause = pause(DurationTime.QUARTER);

        assertThat(quarterPause.getDuration().getDivisor())
            .isEqualTo(4);

        Track track = new MidiSequenceBuilder()
            .addTrack()
            .addPause(quarterPause)
            .addNote(cSharp4Quarter())
            .build()
            .getTracks()[0];

        assertThat(firstShortEvent(track, ShortMessage.NOTE_ON).getTick())
            .isEqualTo(480);
    }
}
