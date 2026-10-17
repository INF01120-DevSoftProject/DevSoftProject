package music;


import musicElements.InstrumentElement;
import musicElements.Octave;
import musicElements.VolumeElement;


public class VoicePreset {

    public static final int DEFAULT_INSTRUMENT = 0;
    public static final int DEFAULT_VOLUME = 100;
    public static final int DEFAULT_OCTAVE = 5;

    private final InstrumentElement instrument;
    private final VolumeElement volume;
    private final Octave octave;

    
    public VoicePreset(){
        this(new InstrumentElement(DEFAULT_INSTRUMENT),
             new VolumeElement(DEFAULT_VOLUME),
             new Octave(DEFAULT_OCTAVE));
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
