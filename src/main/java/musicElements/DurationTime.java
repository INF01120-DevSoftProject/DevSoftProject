package musicElements;

public enum DurationTime {
    WHOLE(1),
    HALF(2),
    QUARTER(4),
    EIGHTH(8),
    SIXTEENTH(16),
    THIRTY_SECOND(32),
    SIXTY_FOURTH(64);

    private  final int divisor;

    DurationTime(int divisor){
        this.divisor = divisor;
    }
    
    public int getDivisor(){
        return divisor;
    }

    public double getFraction(){
        return  1.0 / divisor;
    }
}