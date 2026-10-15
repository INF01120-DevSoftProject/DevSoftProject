package player;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sound.midi.Sequence;
import org.jfugue.midi.MidiFileManager;

public class JFuguePlayer implements Player {

    private final org.jfugue.player.Player jfuguePlayer;

    public JFuguePlayer() {
        this(new org.jfugue.player.Player());
    }

    public JFuguePlayer(org.jfugue.player.Player jfuguePlayer) {
        if (jfuguePlayer == null) {
            throw new IllegalArgumentException("O tocador JFugue não pode ser nulo.");
        }
        this.jfuguePlayer = jfuguePlayer;
    }

    @Override
    public void playFile(Path midiFilePath) throws Exception {

        if (midiFilePath == null) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser nulo.");
        }

        if (!Files.exists(midiFilePath)) {
            throw new FileNotFoundException("O arquivo não foi encontrado: " + midiFilePath);
        }

        Sequence sequence = MidiFileManager.load(midiFilePath.toFile());
        this.jfuguePlayer.play(sequence);
    }
}