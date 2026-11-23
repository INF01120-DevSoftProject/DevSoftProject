package gui.widgets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import gui.builders.BoxBuilder;
import gui.factory.ControlFactory;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import musicElements.Accidentals;
import musicElements.DurationTime;
import musicElements.Notes;
import translators.TextToMusic.CharacterMapping;

public class MappingHelpWidget extends TitledPane {

    private static final Map<Notes, String> NOTE_NAMES = Map.of(
        Notes.C, "Dó", Notes.D, "Ré", Notes.E, "Mi", Notes.F, "Fá",
        Notes.G, "Sol", Notes.A, "Lá", Notes.B, "Si");

    private static final Map<DurationTime, String> DURATION_NAMES = Map.of(
        DurationTime.WHOLE, "Semibreve: 4 tempos.",
        DurationTime.HALF, "Mínima: 2 tempos.",
        DurationTime.QUARTER, "Semínima: 1 tempo (duração padrão).",
        DurationTime.EIGHTH, "Colcheia: 1/2 tempo.",
        DurationTime.SIXTEENTH, "Semicolcheia: 1/4 de tempo.",
        DurationTime.THIRTY_SECOND, "Fusa: 1/8 de tempo.",
        DurationTime.SIXTY_FOURTH, "Semifusa: 1/16 de tempo.");

    private final CharacterMapping mapping;
    private final VBox explanations;
    private VBox currentSection;

    // Tópicos que o usuário fechou; continuam fechados quando o guia é recriado após salvar o mapeamento
    private final Set<String> collapsedSections = new HashSet<>();

    public MappingHelpWidget(CharacterMapping mapping) {
        if (mapping == null) throw new IllegalArgumentException("Mapping cannot be null.");
        this.mapping = mapping;

        explanations = new BoxBuilder<>(new VBox(16))
                                    .addClass("mapping-help-details")
                                    .build();

        ScrollPane scrollPane = ControlFactory.createScrollPane(explanations);

        setText("Como escrever sua música");
        setContent(scrollPane);
        setExpanded(true);
        setCollapsible(false);
        setAnimated(false);
        setMinHeight(0);
        getStyleClass().add("mapping-help");

        refreshMapping();
    }

    public void refreshMapping() {
        explanations.getChildren().clear();

        addHeading("Notas");
        for (String character : mapping.getMappingCharacters("note")) {
            addExplanation(displayCharacter(character),
                "Toca " + NOTE_NAMES.get(mapping.getNote(character)) + " na oitava atual.");
        }

        addHeading("Pausas e acidentes");
        addExplanation(characters("pause"), "Insere uma pausa de um tempo.");
        for (String character : mapping.getMappingCharacters("accidental")) {
            String description = mapping.getAccidental(character) == Accidentals.SHARP
                ? "Sustenido: sobe meio tom. Use logo após a nota."
                : "Bemol: desce meio tom. Use logo após a nota.";
            addExplanation(displayCharacter(character), description);
        }

        addHeading("Duração da nota");
        for (String character : mapping.getMappingCharacters("duration")) {
            addExplanation(":" + displayCharacter(character),
                DURATION_NAMES.get(mapping.getDurationTime(character)) + " Use após a nota ou acidente.");
        }

        addHeading("Comandos");
        addExplanation(characters("octaveUp"), "Sobe uma oitava. Após a oitava 8, volta à oitava inicial da voz.");
        addExplanation(characters("octaveDown"), "Desce uma oitava, até a oitava 0.");
        addExplanation(characters("bpmUp"), "Aumenta o andamento em 10 BPM, até 999.");
        addExplanation(characters("bpmDown"), "Diminui o andamento em 10 BPM, até 20.");
        addExplanation(characters("doubleVolume"), "Dobra o volume da voz, até 127.");
    }

    // Cada tópico é retrátil: clicar no título abre ou fecha as explicações dele
    private void addHeading(String text) {
        currentSection = new BoxBuilder<>(new VBox(8))
                                    .addClass("mapping-help-rows")
                                    .build();

        TitledPane section = new TitledPane(text, currentSection);
        section.getStyleClass().add("mapping-help-section");
        section.setAnimated(false);
        section.setExpanded(!collapsedSections.contains(text));
        section.expandedProperty().addListener((obs, wasExpanded, expanded) -> {
            if (expanded) collapsedSections.remove(text);
            else collapsedSections.add(text);
        });
        explanations.getChildren().add(section);
    }

    private void addExplanation(String character, String description) {
        Label characterLabel = new Label(character);
        characterLabel.setWrapText(true);
        characterLabel.getStyleClass().add("mapping-help-character");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("mapping-help-description");

        HBox row = new BoxBuilder<>(new HBox(10))
                                    .addClass("mapping-help-row")
                                    .add(characterLabel)
                                    .add(descriptionLabel)
                                    .apply(b -> HBox.setHgrow(descriptionLabel, Priority.ALWAYS))
                                    .build();
        currentSection.getChildren().add(row);
    }

    private String characters(String group) {
        StringJoiner characters = new StringJoiner(", ");
        for (String character : mapping.getMappingCharacters(group)) {
            characters.add(displayCharacter(character));
        }
        return characters.toString();
    }

    private String displayCharacter(String character) {
        return switch (character) {
            case " " -> "SPACE";
            case "\n" -> "ENTER";
            case "\t" -> "TAB";
            default -> character;
        };
    }
}
