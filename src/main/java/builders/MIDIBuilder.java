package builders;

import javax.sound.midi.*;

import musicElements.*;


public class MIDIBuilder {

    private Sequence sequence;
    private Track currentTrack;

    private int currentChannel = 0;
    private int currentTick = 0;

    private final static int MIN_CHANNEL = 0;
    private final static int MAX_CHANNEL = 15;
    private final static int NOTES_PER_OCTAVE = 12;
    private final static int DEFAULT_VELOCITY = 100;
    private final static int MIN_NOTE_VALUE = 0;
    private final static int MAX_NOTE_VALUE = 127;
    private final static int VOLUME_CONTROL_CHANNEL = 7;

    public MIDIBuilder() {

        try {
            sequence = new Sequence(Sequence.PPQ, DurationTime.QUARTER.getDivisor());
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequencia MIDI", e);
        }

    }

    public MIDIBuilder addTrack() {
        currentTrack = sequence.createTrack();
        currentTick = 0;
        currentChannel = 0;
        return this;
    }

    public MIDIBuilder addNote(NoteElement note) {
        try {

            if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");
            if (note == null) throw new NullPointerException("Nota nao pode ser nula");

            ShortMessage messageOn = new ShortMessage();
            messageOn.setMessage(ShortMessage.NOTE_ON, currentChannel, getMIDINoteNumber(note), DEFAULT_VELOCITY);
            currentTrack.add(new MidiEvent(messageOn, currentTick));

            currentTick += note.getDuration().getDivisor();

            // Bad smell get duplo
            // Codigo precisa suportar resoluções diferentes de Quarter

            ShortMessage messageOff = new ShortMessage();
            messageOff.setMessage(ShortMessage.NOTE_OFF, currentChannel, getMIDINoteNumber(note), DEFAULT_VELOCITY);
            currentTrack.add(new MidiEvent(messageOff, currentTick));

            return this;

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }
    }

    public MIDIBuilder addPause(PauseElement pause) {
        if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");
        if (pause == null) throw new NullPointerException("Pause nao pode ser nulo");

        currentTick += pause.getDuration().getDivisor();
        // Bad smell get duplo

        return this;

    }

    public MIDIBuilder setInstrument(InstrumentElement instrument) {
        try {

            if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");
            if (instrument == null) throw new NullPointerException("Instrumento nao pode ser nulo");

            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.PROGRAM_CHANGE, currentChannel, instrument.getInstrument(), 0);
            currentTrack.add(new MidiEvent(message, currentTick));

            // Alterar getInstrument para um nome que diga que é o valor MIDI ou só valor

            return this;

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }
    }

    public MIDIBuilder setVolume(VolumeElement volume) {
        try {

            if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");
            if (volume == null) throw new NullPointerException("Instrument nao pode ser nulo");

            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, currentChannel, VOLUME_CONTROL_CHANNEL, volume.getVolume());
            currentTrack.add(new MidiEvent(message, currentTick));

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }

        return this;
    }

    public MIDIBuilder setBPM(BPMElement bpm) {
        try {
            if (currentTrack == null) throw new NullPointerException("Nenhuma track foi criada");
            if (bpm == null) throw new NullPointerException("BPM nao pode ser nulo");

            int mpq; // Microseconds Per Quarter Note
            mpq = 60000000 / bpm.getBPM();

            byte[] mpqData = new byte[] {
                (byte) (mpq >> 16),
                (byte) (mpq >> 8),
                (byte) (mpq)
            };

            MetaMessage bpmMessage = new MetaMessage();
            bpmMessage.setMessage(0x51, mpqData, 3);
            currentTrack.add(new MidiEvent(bpmMessage, currentTick));

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }

        return this;
    }

    public MIDIBuilder setCurrentChannel(int channel) {

        if (channel < MIN_CHANNEL || channel > MAX_CHANNEL) {
            throw new IllegalArgumentException("Canal invalido");
        }

        this.currentChannel = channel;
        return this;
    }

    public Sequence buildSequence() {
        return sequence;
    }

    private static int getMIDINoteNumber(NoteElement note) {

        int MIDINoteNumber = switch(note.getNote()) {
            case Notes.C -> 0;
            case Notes.D -> 2;
            case Notes.E -> 4;
            case Notes.F -> 5;
            case Notes.G -> 7;
            case Notes.A -> 9;
            case Notes.B -> 11;
        };

        if (note.getAccidental() == Accidentals.FLAT)
            MIDINoteNumber--;
        else if (note.getAccidental() == Accidentals.SHARP)
            MIDINoteNumber++;

        MIDINoteNumber += ((note.getOctaveValue() + 1) * NOTES_PER_OCTAVE);

        if (MIDINoteNumber < MIN_NOTE_VALUE || MIDINoteNumber > MAX_NOTE_VALUE) {
            throw new IllegalArgumentException("Número de nota musical MIDI invalida");
        }

        return MIDINoteNumber;
    }

}
