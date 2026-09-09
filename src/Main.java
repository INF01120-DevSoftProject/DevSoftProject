public class Main {
    public static void main(String[] args) {

        Music orchestrate = new Music();
        CreateMusic musicMoment = new CreateMusic(0, 100, 6, "q", orchestrate);

        String text = "ABCDEFgh 4?";

        orchestrate.initiateTextTranslator(text, musicMoment, orchestrate);

        orchestrate.playMusic();
    }
}
