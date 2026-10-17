package builders;

import javax.sound.midi.*;
import musicElements.*;

public class MIDIBuilder {

    private final Sequence sequence;
    private Track currentTrack;

    private int currentChannel = -1;
    private long currentTick = 0;

    private static final int MIN_CHANNEL = 0;
    private static final int MAX_CHANNEL = 15;
    private static final int MIN_NOTE_VALUE = 0;
    private static final int MAX_NOTE_VALUE = 127;
    private static final int NOTES_PER_OCTAVE = 12;
    private static final int DEFAULT_VELOCITY = 100;

    private static final int VOLUME_CONTROLLER = 7;
    private static final int MPQ_META_MESSAGE_TYPE = 0x51;
    private static final int END_TRACK_MESSAGE_TYPE = 0x2F;
    private static final int MICROSECONDS_IN_MINUTE = 60000000;
    private static final int PPQ_RESOLUTION = 480;
    private static final int PERCUSSION_CHANNEL = 9;

    public MIDIBuilder() {

        try {
            sequence = new Sequence(Sequence.PPQ, PPQ_RESOLUTION);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequencia MIDI", e);
        }
    }

    public MIDIBuilder addTrack() {

        if (sequence.getTracks().length > (MAX_CHANNEL - 1)) // The -1 is because of the percussion channel not being used
            throw new IllegalArgumentException("Numero maximo de tracks excedidos");

        setCurrentChannel(currentChannel + 1);

        currentTrack = sequence.createTrack();
        currentTick = 0;
        return this;
    }


    public MIDIBuilder addNote(NoteElement note) {
        if (note == null) throw new NullPointerException("Nota nao pode ser nula");

        addShortMessageToTrack(ShortMessage.NOTE_ON, currentChannel, getMIDINoteNumber(note), DEFAULT_VELOCITY);

        currentTick += convertDurationToTicks(note.getDuration());

        addShortMessageToTrack(ShortMessage.NOTE_OFF, currentChannel, getMIDINoteNumber(note), DEFAULT_VELOCITY);

        return this;
    }

    public MIDIBuilder addPause(PauseElement pause) {
        if (pause == null) throw new NullPointerException("Pause nao pode ser nulo");
        if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");

        currentTick += convertDurationToTicks(pause.getDuration());
        addMetaMessageToTrack(END_TRACK_MESSAGE_TYPE, new byte[0], 0);

        return this;
    }

    public MIDIBuilder setInstrument(InstrumentElement instrument) {
        if (instrument == null) throw new NullPointerException("Instrumento nao pode ser nulo");

        addShortMessageToTrack(ShortMessage.PROGRAM_CHANGE, currentChannel, instrument.getInstrument(), 0);

        return this;
    }

    public MIDIBuilder setVolume(VolumeElement volume) {
        if (volume == null) throw new NullPointerException("Volume nao pode ser nulo");

        addShortMessageToTrack(ShortMessage.CONTROL_CHANGE, currentChannel, VOLUME_CONTROLLER, volume.getVolume());

        return this;
    }

    public MIDIBuilder setBPM(BPMElement bpm) {
        if (bpm == null) throw new NullPointerException("BPM nao pode ser nulo");

        int mpq; // Microseconds Per Quarter Note
        mpq = MICROSECONDS_IN_MINUTE / bpm.getBPM(); // Transform BPM in MPQ

        final int THIRD_BTYE_SHIFT = 16;
        final int SECOND_BTYE_SHIFT = 8;

        // In MIDI meta messages MPQ can only be read throw a 3-byte array
        byte[] mpqData = new byte[] {
            (byte) (mpq >> THIRD_BTYE_SHIFT),
            (byte) (mpq >> SECOND_BTYE_SHIFT),
            (byte) (mpq)
        };

        addMetaMessageToTrack(MPQ_META_MESSAGE_TYPE, mpqData, mpqData.length);

        return this;
    }

    public MIDIBuilder setCurrentChannel(int channel) {

        if (channel < MIN_CHANNEL || channel > MAX_CHANNEL) {
            throw new IllegalArgumentException("Canal invalido");
        }

        if (channel == PERCUSSION_CHANNEL) { // MIDI channel 10 (index 9) is reserved for percussion in MIDI.
            channel++;
        }


        this.currentChannel = channel;
        return this;
    }

    public Sequence build() {
        return sequence;
    }

    private long convertDurationToTicks(DurationTime duration) {
        return (4L * PPQ_RESOLUTION) / duration.getDivisor();
    }

    private static int getMIDINoteNumber(NoteElement note) {

        int MidiNoteNumber = switch(note.getNote()) {
            case Notes.C -> 0;
            case Notes.D -> 2;
            case Notes.E -> 4;
            case Notes.F -> 5;
            case Notes.G -> 7;
            case Notes.A -> 9;
            case Notes.B -> 11;
        };

        if (note.getAccidental() == Accidentals.FLAT)
            MidiNoteNumber--;
        else if (note.getAccidental() == Accidentals.SHARP)
            MidiNoteNumber++;

        MidiNoteNumber += ((note.getOctaveValue() + 1) * NOTES_PER_OCTAVE);

        if (MidiNoteNumber < MIN_NOTE_VALUE || MidiNoteNumber > MAX_NOTE_VALUE) {
            throw new IllegalArgumentException("Número de nota musical MIDI invalida");
        }

        return MidiNoteNumber;
    }


    private void addShortMessageToTrack(int messageType, int currentChannel, int value1, int value2) {
        if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(messageType, currentChannel, value1, value2);
            currentTrack.add(new MidiEvent(message, currentTick));

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }
    }

    private void addMetaMessageToTrack(int messageType, byte[] data, int dataLength) {
        if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");

        try {
            MetaMessage message = new MetaMessage();
            message.setMessage(messageType, data, dataLength);
            currentTrack.add(new MidiEvent(message, currentTick));

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }        
    }
}
