package app.controller;

import app.objects.player.Player;
import app.objects.team.Team;
import app.objects.team.TeamService;
import app.objects.tournament.Tournament;
import app.util.SceneLoader;
import app.view.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class TournamentPageController {
    private final TeamService teamService = new TeamService();
    @FXML private Label tournamentLabel;
    @FXML private Label tournamentTypeLabel;
    @FXML private Label tournamentPlaydateLabel;
    @FXML private Label tournamentStatusLabel;
    @FXML private TextField teamNameField;
    @FXML private Button addTeamButton;
    @FXML private Button rmvTeamButton;
    @FXML private ListView<Team> teamListView;
    @FXML private VBox teamDetailBox;
    @FXML private Label selectedTeamLabel;
    @FXML private ListView<Player> playerListView;
    @FXML private TextField playerNameField;
    @FXML private Button addPlayerButton;
    @FXML private Button rmvPlayerButton;
    @FXML private Button backButton;
    @FXML private Button startButton;

    private final ObservableList<Player> playersModel = FXCollections.observableArrayList();
    private Tournament tournament;
    private Player selectedPlayer;
    private Team selectedTeam;

    // Übersicht Tournament methode wird beim öffnen der Seite einmal aufgerufen
    public void setTournament(Tournament t) {
        this.tournament = t;
        tournamentLabel.setText("Tournament: " + t.name());
        tournamentTypeLabel.setText("Type:   " + t.tournamentType().toString());
        if (t.playdate() != null) {
            tournamentPlaydateLabel.setText(t.playdate().toString());
        } else {
            tournamentPlaydateLabel.setText("--.--.--");
        }
        if (t.round() == 0) {
            tournamentStatusLabel.setText("noch nicht gestartet");
        } else {
            tournamentStatusLabel.setText(t.round() + " Runde");
        }

        refreshTeamList();
    }

    @FXML
    private void initialize() {
        // Player-ListView ans Modell binden
        playerListView.setItems(playersModel);

        teamDetailBox.setVisible(false);
        teamDetailBox.managedProperty().bind(teamDetailBox.visibleProperty());
        playerListView.managedProperty().bind(playerListView.visibleProperty());

        teamListView.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Team t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t.teamName());
            }
        });

        playerListView.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.playerName());
            }
        });

        addTeamButton.setOnAction(e -> {
            String name = teamNameField.getText().trim();
            teamService.addTeam(name, tournament).ifPresent(t -> refreshTeamList());
        });

        teamListView.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            selectedTeam = sel;
            if (sel != null) {
                refreshPlayerList();      // lädt playersModel
                teamDetailBox.setVisible(true);
                playerListView.setVisible(true);
            } else {
                playersModel.clear();
                selectedPlayer = null;
                teamDetailBox.setVisible(false);
                playerListView.setVisible(false);
            }
        });

        rmvTeamButton.setOnAction(e -> {teamService.rmvTeam(selectedTeam, tournament); refreshTeamList();});

        playerListView.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            selectedPlayer = sel;
        });

        addPlayerButton.setOnAction(e -> {
            String playerName = playerNameField.getText().trim();
            Team sel = teamListView.getSelectionModel().getSelectedItem();
            teamService.addTeamPlayerAndPlayer(sel, playerName).ifPresent(t -> refreshPlayerList());
        });

        rmvPlayerButton.setOnAction(e -> {
            if (selectedTeam != null && selectedPlayer != null) {
                if (teamService.rmvTeamPlayer(selectedTeam, selectedPlayer)) {


                }
                refreshPlayerList();
            }
        });

        backButton.setOnAction(e -> SceneLoader.loadMainMenu());
    }

    private void refreshTeamList() {
        List<Team> teams = teamService.getTeams(tournament);
        teamListView.getItems().setAll(teams);
        // WICHTIG: hier NICHT teamDetailBox verstecken
    }

    private void refreshPlayerList() {
        if (selectedTeam == null) return;
        selectedTeamLabel.setText("Players of " + selectedTeam.teamName());

        // Frisch aus DB/Service laden (nicht aus selectedTeam.players(), falls „stale“)
        List<Player> fresh = teamService.getTeamPlayers(selectedTeam); // falls nicht vorhanden: hinzufügen
        playersModel.setAll(fresh == null ? List.of() : fresh);
    }
}