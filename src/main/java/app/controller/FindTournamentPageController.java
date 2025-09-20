package app.controller;

import app.objects.tournament.TournamentService;
import app.objects.tournament.Tournament;
import app.util.SceneLoader;
import app.view.MainApp;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.List;

public class FindTournamentPageController {

    @FXML private TextField tournamentNameField;
    @FXML private ListView<Tournament> tournamentListView;
    @FXML private Button backButton;

    private final TournamentService tournamentService = new TournamentService();
    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));
    private Task<List<Tournament>> currentTask;
    private long searchSeq = 0; // Guard gegen veraltete Task-Ergebnisse

    @FXML
    private void initialize() {
        tournamentListView.setPlaceholder(new Label("Keine Daten"));
        tournamentListView.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Tournament t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) setText(null);
                else {
                    // Passe an dein Modell an (getName() oder name())
                    try { setText((String) t.getClass().getMethod("getName").invoke(t)); }
                    catch (Exception e1) {
                        try { setText((String) t.getClass().getMethod("name").invoke(t)); }
                        catch (Exception e2) { setText(t.toString()); }
                    }
                }
            }
        });

        tournamentListView.setOnMouseClicked(e -> { if (e.getClickCount() == 2) loadSelectedTournament(); });
        tournamentListView.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) loadSelectedTournament(); });

        backButton.setOnAction(e -> SceneLoader.loadMainMenu());

        debounce.setOnFinished(e -> runSearch(tournamentNameField.getText()));
        tournamentNameField.textProperty().addListener((obs, ov, nv) -> debounce.playFromStart());

        // initial (leer = alle)
        runSearch("");

    }

    private void runSearch(String rawQuery) {
        final long seq = ++searchSeq;
        final String query = rawQuery == null ? "" : rawQuery.trim();

        if (currentTask != null) currentTask.cancel();

        tournamentListView.setDisable(true);
        tournamentListView.setPlaceholder(new Label("Suche…"));

        currentTask = new Task<>() {
            @Override protected List<Tournament> call() throws Exception {
                List<Tournament> data = tournamentService.getTournaments(query);
                if (isCancelled()) return List.of(); // sauber abbrechen
                return data;
            }
        };

        currentTask.setOnSucceeded(e -> {
            if (seq != searchSeq) return; // veraltetes Ergebnis ignorieren
            List<Tournament> data = currentTask.getValue();
            tournamentListView.setItems(FXCollections.observableArrayList(data));
            tournamentListView.setDisable(false);
            if (data.isEmpty()) tournamentListView.setPlaceholder(new Label("Keine Treffer"));
        });

        currentTask.setOnFailed(e -> {
            if (seq != searchSeq) return;
            tournamentListView.setDisable(false);
            tournamentListView.setPlaceholder(new Label("Fehler bei der Suche"));
            Throwable ex = currentTask.getException();
            if (ex != null) MainApp.errorBox(ex.getMessage());
        });

        currentTask.setOnCancelled(e -> {
            if (seq != searchSeq) return;
            tournamentListView.setDisable(false);
            // optional: Placeholder zurücksetzen
        });

        Thread th = new Thread(currentTask, "tournament-search");
        th.setDaemon(true);
        th.start();
    }

    private void loadSelectedTournament() {
        Tournament t = tournamentListView.getSelectionModel().getSelectedItem();
        if (t != null) SceneLoader.loadTournamentPage(t);
    }
}
