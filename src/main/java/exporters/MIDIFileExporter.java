package exporters;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.nio.file.Path;

public class MIDIFileExporter implements FileExporter<Sequence> {

    @Override 
    public void save(Sequence data, Path filePath, String fileName) throws IOException {
        if (fileName == null) throw new NullPointerException("file name can't be null");
        if (data == null) throw new NullPointerException("sequence can't be null");
        if (filePath == null) throw new NullPointerException("file path can't be null");

        File file = filePath.resolve(fileName).toFile();

        MidiSystem.write(data, 1, file);
    }
}
