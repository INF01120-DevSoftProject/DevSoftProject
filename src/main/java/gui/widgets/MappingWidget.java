package gui.widgets;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;

import translators.TextToMusic.TextToMusicRules;

public class MappingWidget extends VBox {
    private final String mappingRuleType;
    private String oldCaracter;
    private final TextField textField;

    public MappingWidget(String mappingRuleType, String nodeName, String character) {
        super(10);

        this.mappingRuleType = mappingRuleType;
        this.oldCaracter = character;

        this.textField = createOneCaracterTextField();
        this.textField.setText(character);

        getStyleClass().add("mapping-node");
        getChildren().addAll(
            new Label(nodeName),
            textField
        );

        setMaxHeight(Region.USE_PREF_SIZE);
    }

    public void saveChange() {
        String newCaracter = textField.getText();

        if (oldCaracter.equals(newCaracter)) {
            return;
        }

        TextToMusicRules.replaceChar(
            mappingRuleType,
            oldCaracter,
            newCaracter
        );

        oldCaracter = newCaracter;
    }

    private TextField createOneCaracterTextField() {
        TextField field = new TextField();

        field.setPrefColumnCount(1);
        field.setMaxWidth(45);

        field.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().length() <= 1
                ? change
                : null
        ));

        return field;
    }
}

