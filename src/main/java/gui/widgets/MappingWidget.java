package gui.widgets;

import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;

import translators.TextToMusic.CharacterMapping;
import translators.TextToMusic.MappingType;

public class MappingWidget extends VBox {
    // Campo mostrando "SPACE": o CSS deixa ele mais largo
    private static final PseudoClass SPACE = PseudoClass.getPseudoClass("space");

    private final CharacterMapping mapping;
    private final MappingType mappingRuleType;
    private String oldCaracter;
    private final TextField textField;

    public MappingWidget(CharacterMapping mapping, MappingType mappingRuleType, String nodeName, String character) {
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
            // a largura do campo vem do CSS (.text-field e .text-field:space em mapper.css)
            boolean isSpace = newText.equals("SPACE");
            field.setPrefColumnCount(isSpace ? 5 : 1);
            field.pseudoClassStateChanged(SPACE, isSpace);
        });

        return field;
    }
}

