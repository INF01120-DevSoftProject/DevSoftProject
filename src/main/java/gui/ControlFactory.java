package gui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

import java.util.List;

public class ControlFactory {

    public static Slider createSlider(int minValue, int maxValue) {
        Slider slider = new Slider();
        slider.setMin(minValue);      
        slider.setMax(maxValue);  
        slider.setValue(minValue + (maxValue - minValue) / 2.0);
        slider.setShowTickLabels(true); // Show numbers

        return slider;
    }

    public static TextArea createTextArea(int rows, String promptText) {
        TextArea inputBox = new TextArea();
        inputBox.setPrefRowCount(3);
        inputBox.setPromptText("Digite sua partitura");
        inputBox.setWrapText(true);

        return inputBox;
    }

    public static <T> ComboBox<T> createComboBox(String defaultText, List<T> values) {
        ComboBox<T> comboBox = new ComboBox<>();

        comboBox.setPromptText(defaultText);
        comboBox.getItems().addAll(values);

        return comboBox;
    }

    public static Region createVerticalGrower() {
        Region grower = new Region();
        VBox.setVgrow(grower, Priority.ALWAYS);

        return grower;
    }

    public static Region createHorizontalGrower() {
        Region grower = new Region();
        HBox.setHgrow(grower, Priority.ALWAYS);

        return grower;
    }
}
