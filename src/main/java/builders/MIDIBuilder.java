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

    public MIDIBuilder() {

        try {
            sequence = new Sequence(Sequence.PPQ, DurationTime.QUARTER.getDivisor());
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
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

            if (currentTrack == null) throw new IllegalStateException("Nenhuma track foi criada");

            ShortMessage messageOn = new ShortMessage();
            messageOn.setMessage(ShortMessage.NOTE_ON, currentChannel, getMIDINoteNumber(note), DEFAULT_VELOCITY);
            currentTrack.add(new MidiEvent(messageOn, currentTick));

            currentTick += note.getDuration().getDivisor();

            ShortMessage messageOff = new ShortMessage();
            messageOff.setMessage(ShortMessage.NOTE_OFF, currentChannel, getMIDINoteNumber(note), DEFAULT_VELOCITY);
            currentTrack.add(new MidiEvent(messageOff, currentTick));

            return this;

        } catch(InvalidMidiDataException e) {
            throw new RuntimeException("Não foi possível criar a sequência MIDI", e);
        }
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

        MIDINoteNumber += (note.getOctaveValue() * NOTES_PER_OCTAVE);

        if (MIDINoteNumber < MIN_NOTE_VALUE || MIDINoteNumber > MAX_NOTE_VALUE) {
            throw new IllegalArgumentException("Número de nota musical MIDI invalida");
        }


        return MIDINoteNumber;
    }

}
