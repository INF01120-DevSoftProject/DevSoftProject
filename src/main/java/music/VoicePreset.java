package music;


import musicElements.InstrumentElement;
import musicElements.InstrumentType;
import musicElements.Octave;
import musicElements.VolumeElement;


public class VoicePreset {

    private final InstrumentElement instrument;
    private final VolumeElement volume;
    private final Octave octave;

    
    public VoicePreset(InstrumentType type){

        if (type == null) throw new IllegalArgumentException("Tipo de instrumento invalido");

        int defaultVolume = 100;
        int defaultOctave = 5;

        switch (type) {
            case STRINGS:
                defaultVolume = 100;
                defaultOctave = 6;
                break;
            
            case KEYS:
                defaultVolume = 90;
                defaultOctave = 4;
                break;
            
            case PLUCKED:
                defaultVolume = 90;
                defaultOctave = 3;
                break;

            case WINDS:
                defaultVolume = 80;
                defaultOctave = 5;
                break;
            
            case PERCUSSION_AND_SYNTH:
                defaultVolume = 80;
                defaultOctave = 5;
                break;
        
            default:
                break;
        }

        this.instrument = new InstrumentElement(type);
        this.volume = new VolumeElement(defaultVolume);
        this.octave = new Octave(defaultOctave);
    }

    public VoicePreset(InstrumentElement newInstrument, VolumeElement newVolume, Octave newOctave){

        if (newInstrument == null) throw new IllegalArgumentException("Instrumento invalido");
        if (newVolume == null) throw new IllegalArgumentException("Volume invalido");
        if (newOctave == null) throw new IllegalArgumentException("Oitava invalida");


        this.instrument = newInstrument;
        this.volume = newVolume;
        this.octave = newOctave;
    }

    public InstrumentElement getInstrumentElement(){
        return this.instrument;
    }

    public VolumeElement getVolumeElement(){
        return this.volume;
    }

    public Octave getDefaultOctave(){
        return this.octave;
    }
}
