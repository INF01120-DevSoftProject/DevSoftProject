package gui.widgets;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;

import translators.TextToMusic.CharacterMapping;

public class MappingWidget extends VBox {
    private final CharacterMapping mapping;
    private final String mappingRuleType;
    private String oldCaracter;
    private final TextField textField;

    public MappingWidget(CharacterMapping mapping, String mappingRuleType, String nodeName, String character) {
        this.mapping = mapping;
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
        if (newCaracter.equals("SPACE")) {
            newCaracter = " ";
        }

        if (oldCaracter.equals(newCaracter)) {
            return;
        }

        mapping.replaceChar(mappingRuleType, oldCaracter, newCaracter);

        oldCaracter = newCaracter;
    }

    private TextField createOneCaracterTextField() {
        TextField field = new TextField();

        field.setPrefColumnCount(1);
        field.setMaxWidth(45);

        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (change.isContentChange() && change.getControlText().equals("SPACE")
                    && change.getText().length() <= 1) {
                change.setRange(0, change.getControlText().length());
                change.selectRange(change.getText().length(), change.getText().length());
                newText = change.getControlNewText();
            }
            if (newText.equals(" ")) {
                change.setRange(0, change.getControlText().length());
                change.setText("SPACE");
                change.selectRange(5, 5);
                return change;
            }
            if (newText.equals("SPACE")) return change;
            return newText.length() <= 1 ? change : null;
        }));

        field.textProperty().addListener((observable, oldText, newText) -> {
            boolean isSpace = newText.equals("SPACE");
            field.setPrefColumnCount(isSpace ? 5 : 1);
            field.setMaxWidth(isSpace ? 85 : 45);
        });

        return field;
    }
}

