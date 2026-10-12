package musicElements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DurationTimeTest {

    @Test
    void hasAllSevenDurations() {
        assertThat(DurationTime.values()).containsExactly(
            DurationTime.WHOLE,
            DurationTime.HALF,
            DurationTime.QUARTER,
            DurationTime.EIGHTH,
            DurationTime.SIXTEENTH,
            DurationTime.THIRTY_SECOND,
            DurationTime.SIXTY_FOURTH
        );
    }

    @Test
    void eachDurationKeepsItsOwnDivisor() {
        assertThat(DurationTime.WHOLE.getDivisor()).isEqualTo(1);
        assertThat(DurationTime.HALF.getDivisor()).isEqualTo(2);
        assertThat(DurationTime.QUARTER.getDivisor()).isEqualTo(4);
        assertThat(DurationTime.EIGHTH.getDivisor()).isEqualTo(8);
        assertThat(DurationTime.SIXTEENTH.getDivisor()).isEqualTo(16);
        assertThat(DurationTime.THIRTY_SECOND.getDivisor()).isEqualTo(32);
        assertThat(DurationTime.SIXTY_FOURTH.getDivisor()).isEqualTo(64);
    }

    @Test
    void eachDurationKeepsItsOwnFraction() {
        assertThat(DurationTime.WHOLE.getFraction()).isEqualTo(1.0);
        assertThat(DurationTime.HALF.getFraction()).isEqualTo(0.5);
        assertThat(DurationTime.QUARTER.getFraction()).isEqualTo(0.25);
        assertThat(DurationTime.EIGHTH.getFraction()).isEqualTo(0.125);
        assertThat(DurationTime.SIXTEENTH.getFraction()).isEqualTo(0.0625);
        assertThat(DurationTime.THIRTY_SECOND.getFraction()).isEqualTo(0.03125);
        assertThat(DurationTime.SIXTY_FOURTH.getFraction()).isEqualTo(0.015625);
    }

    @Test
    void fractionIsTheInverseOfDivisor() {
        for (DurationTime duration : DurationTime.values()) {
            assertThat(duration.getFraction() * duration.getDivisor()).isEqualTo(1.0);
        }
    }

    @Test
    void eachDurationIsHalfOfThePreviousOne() {
        DurationTime[] durations = DurationTime.values();

        for (int i = 1; i < durations.length; i++) {
            assertThat(durations[i].getDivisor()).isEqualTo(durations[i - 1].getDivisor() * 2);
            assertThat(durations[i].getFraction()).isEqualTo(durations[i - 1].getFraction() / 2);
        }
    }
}
