package app.controller;

import app.objects.tournament.TournamentService;
import app.objects.tournament.TournamentType;
import app.util.SceneLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class TournamentCreatePageController {
    @FXML
    private TextField tournamentNameField;
    @FXML
    private ComboBox<TournamentType> tournamentTypeComboBox;
    @FXML
    private Button createTournamentButton;
    @FXML
    private Button goToMainMenuButton;

    private final TournamentService tournamentService = new TournamentService();

    @FXML
    private void initialize() {

        // Combobox abfüllen und erste Option anwählen
        tournamentTypeComboBox.setItems(FXCollections.observableArrayList(TournamentType.values()));
        tournamentTypeComboBox.getSelectionModel().selectFirst();

        createTournamentButton.setOnAction(e -> {
            String name = tournamentNameField.getText().trim();
            // Load Tournament Page with tournament
            // utility you create
            TournamentType tournamtType = tournamentTypeComboBox.getValue();
            tournamentService.addTournament(name,tournamtType).ifPresent(SceneLoader::loadTournamentPage);
        });

        goToMainMenuButton.setOnAction(e -> SceneLoader.loadMainMenu());
    }

}
