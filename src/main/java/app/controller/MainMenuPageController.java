package app.controller;

import app.util.SceneLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainMenuPageController {

    @FXML
    private Button createTournamentButton;

    @FXML
    private Button loadTournamentButton;

    @FXML
    private Button viewTeamsButton;

    @FXML
    private Button endGameButton;



    @FXML

    private void initialize() {
        createTournamentButton.setOnAction(e -> SceneLoader.loadTournamentCreatePage());

        viewTeamsButton.setOnAction(e -> SceneLoader.loadTeamOverviewPage());

        loadTournamentButton.setOnAction(e -> SceneLoader.loadFindTournamentPage());

        endGameButton.setOnAction(e -> Platform.exit());

    }


}
