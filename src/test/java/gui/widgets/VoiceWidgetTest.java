package gui.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gui.JavaFXTestBase;
import gui.widgets.voicePresetWidget.VoicePresetKeysWidget;

class VoiceWidgetTest extends JavaFXTestBase {

    @BeforeEach
    void setUp() {
        VoiceWidget.restartCurrentVoiceNum();
    }

    @Test
    void incrementsVoiceNumberOnCreation() {
        assertThat(VoiceWidget.getCurrentVoiceNum()).isZero();

        new VoiceWidget(new VoicePresetKeysWidget());
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(1);

        new VoiceWidget(new VoicePresetKeysWidget());
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(2);
    }

    @Test
    void decrementsVoiceNumberOnlyAboveOne() {
        new VoiceWidget(new VoicePresetKeysWidget());
        new VoiceWidget(new VoicePresetKeysWidget());
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(2);

        VoiceWidget.decrementCurrentVoiceNum();
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(1);

        VoiceWidget.decrementCurrentVoiceNum();
        assertThat(VoiceWidget.getCurrentVoiceNum()).isEqualTo(1);
    }

    @Test
    void setsAndGetsVoiceText() {
        VoiceWidget widget = new VoiceWidget(new VoicePresetKeysWidget());

        widget.setVoiceText("C D E F G");
        assertThat(widget.getVoiceText()).isEqualTo("C D E F G");
    }

    @Test
    void delegatesPresetValuesCorrectly() {
        VoicePresetKeysWidget preset = new VoicePresetKeysWidget();
        VoiceWidget widget = new VoiceWidget(preset);

        assertThat(widget.getInstrument()).isEqualTo(0);
        assertThat(widget.getOctave()).isEqualTo(3);
        assertThat(widget.getVolume()).isEqualTo(66);
    }

    @Test
    void hasExpectedStyleClassAndLayout() {
        VoiceWidget widget = new VoiceWidget(new VoicePresetKeysWidget());

        assertThat(widget.getStyleClass()).contains("voice-node");
        assertThat(widget.getLeft()).isNotNull();
        assertThat(widget.getCenter()).isNotNull();
    }
}
