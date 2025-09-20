package app.controller;

import app.objects.player.Player;
import app.objects.tournament.TournamentService;
import app.objects.ui.TeamAndTournamentName;
import app.objects.ui.UIService;
import app.util.SceneLoader;
import app.view.MainApp;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.List;

public class TeamOverviewPageController {

    @FXML private Accordion teamAccordion;
    @FXML private Label emptyLabel;
    @FXML private Button backButton;

    private final UIService uiService = new UIService();
    private final TournamentService tournamentService = new TournamentService();

    @FXML
    private void initialize() {
        loadInitialTeamsAsync();
        backButton.setOnAction(e -> SceneLoader.loadMainMenu());
    }

    public void setTeams(List<TeamAndTournamentName> teams) {
        teamAccordion.getPanes().clear();
        if (teams == null || teams.isEmpty()) {
            emptyLabel.setVisible(true);
            emptyLabel.setManaged(true);
            return;
        }
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        for (var team : teams) {
            teamAccordion.getPanes().add(buildTeamPane(team));
        }
    }

    private void loadInitialTeamsAsync() {
        Task<List<TeamAndTournamentName>> task = new Task<>() {
            @Override protected List<TeamAndTournamentName> call() throws Exception {
                return uiService.getTeamsAndTournaments();
            }
        };
        task.setOnSucceeded(e -> setTeams(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) MainApp.errorBox(ex.getMessage());
            setTeams(List.of());
        });
        Thread th = new Thread(task, "load-teams");
        th.setDaemon(true);
        th.start();
    }

    private TitledPane buildTeamPane(TeamAndTournamentName team) {
        String header = team.teamName() + " • " + team.tournamentName();

        // --- Inhalt-Container
        VBox box = new VBox(8);
        box.setStyle("-fx-padding:12;");

        // Spieler-Liste vorbereiten
        List<Player> pl = team.players() == null ? java.util.List.of() : team.players();

        Label info = new Label("Spieler: " + pl.size());

        // Button rechts oben
        Button openBtn = new Button("Turnier öffnen");
        openBtn.setOnAction(e ->
                tournamentService.getTournament(team.tournamentName())
                        .ifPresentOrElse(
                                SceneLoader::loadTournamentPage,
                                () -> MainApp.errorBox("Turnier nicht gefunden: " + team.tournamentName())
                        )
        );

        // Top-Bar: Info links, Button rechts
        HBox topBar = new HBox(8);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(info, spacer, openBtn);

        // Liste
        ListView<Player> playerList = new ListView<>();
        playerList.setItems(FXCollections.observableArrayList(pl));
        playerList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setText(null); setGraphic(null); }
                else { setText(p.playerName());}
            }
        });

        // Höhe: nur so groß wie nötig (optional)
        VBox.setVgrow(playerList, Priority.NEVER);
        double cell = 28;
        playerList.setFixedCellSize(cell);
        playerList.prefHeightProperty().bind(
                javafx.beans.binding.Bindings.when(
                        javafx.beans.binding.Bindings.isEmpty(playerList.getItems())
                ).then(0.0).otherwise(
                        javafx.beans.binding.Bindings.size(playerList.getItems()).multiply(cell).add(2)
                )
        );

        box.getChildren().addAll(topBar, playerList);

        TitledPane pane = new TitledPane(header, box);
        pane.setExpanded(false);
        return pane;
    }


    private String playerName(Player p) {
        try { return (String) p.getClass().getMethod("getName").invoke(p); }
        catch (Exception e1) {
            try { return (String) p.getClass().getMethod("name").invoke(p); }
            catch (Exception e2) { return p.toString(); }
        }
    }
}
