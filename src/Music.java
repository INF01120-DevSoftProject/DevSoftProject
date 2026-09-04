public class Music {
    private String sheetMusic;

    public Music(){
        this.sheetMusic = "";
    }

    public String getSheetMusic() {
        return sheetMusic;
    }

    public String setSheetMusic(String newSheet){
        return this.sheetMusic;
    }

    public String addToSheetMusic(String element){
        return this.sheetMusic = this.sheetMusic + " " + element;
    }

    public void playMusic(){
        System.out.println(sheetMusic);
    }
    
    public void initiateTextTranslator(String text, CreateMusic musicMoment, Music orchestrate){

        MusicTextTranslator translator = new MusicTextTranslator();

        translator.translateTextToSheetMusic(text, musicMoment, orchestrate);


    }
}
