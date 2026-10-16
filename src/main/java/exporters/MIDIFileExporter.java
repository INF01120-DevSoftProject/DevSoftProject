package exporters;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

public class MIDIFileExporter implements FileExporter<Sequence> {

    private String filePath;
    
    public MIDIFileExporter(String filePath) {
        if (filePath == null) throw new NullPointerException("file name can't be null");

        this.filePath = filePath;
    }

    @Override 
    public void save(Sequence data, String fileName) throws IOException {
        if (fileName == null) throw new NullPointerException("file name can't be null");
        if (filePath == null) throw new NullPointerException("file name can't be null");

        File file = new File(filePath + fileName);
        MidiSystem.write(data, 1, file);
    }

}
