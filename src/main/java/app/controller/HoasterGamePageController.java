package app.controller;

import app.objects.team.Team;
import app.objects.tournament.Tournament;
import app.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.Map;

public class HoasterGamePageController {

    @FXML Label tournamentNameLabel;
    @FXML ListView<Game> tournamentListView;
    @FXML Button backButton;
    @FXML Button nextButton;

    // initialize Variables
    Tournament tournament;
    Map<Team, Team>

    // Get the actual Tournament
    public void setTournament(Tournament t) {
        tournament = t;
    }

    private void initialize() {

        // Buttons actions setzen
        backButton.setOnAction(event -> {SceneLoader.loadMainMenu();});
        nextButton.setOnAction(event -> {nextMatchups();});

        // List View der Matchups
        to
    }

    // Help Functions

    // Opens the next Matchups
    private void nextMatchups() {

    }
}
