package app.util;

import app.controller.*;
import app.view.MainApp;
import app.objects.tournament.Tournament;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneLoader {

    private static final String APP_CSS = "/app/css/style.css";

    private static Scene buildScene(Parent root) {
        Scene scene = new Scene(root);
        applyAppCss(scene);
        return scene;
    }

    private static void applyAppCss(Scene scene) {
        URL css = MainApp.class.getResource(APP_CSS);
        if (css != null) {
            String href = css.toExternalForm();
            if (!scene.getStylesheets().contains(href)) {
                scene.getStylesheets().add(href);
            }
        } else {
            System.err.println("WARN: Stylesheet nicht gefunden: " + APP_CSS);
        }
    }

    public static void loadTournamentPage(Tournament tournament) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("TournamentPage.fxml"));
            Parent root = loader.load();
            TournamentPageController controller = loader.getController();
            controller.setTournament(tournament);
            Stage stage = (Stage) MainApp.getPrimaryStage();
            stage.setScene(buildScene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadTournamentCreatePage() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("TournamentCreatePage.fxml"));
            Parent root = loader.load();
            TournamentCreatePageController controller = loader.getController();
            Stage stage = (Stage) MainApp.getPrimaryStage();
            stage.setScene(buildScene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("MainMenuPage.fxml"));
            Parent root = loader.load();
            MainMenuPageController controller = loader.getController();
            Stage stage = (Stage) MainApp.getPrimaryStage();
            stage.setScene(buildScene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadTeamOverviewPage() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("TeamOverviewPage.fxml"));
            Parent root = loader.load();
            TeamOverviewPageController controller = loader.getController();
            Stage stage = (Stage) MainApp.getPrimaryStage();
            stage.setScene(buildScene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFindTournamentPage() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("FindTournamentPage.fxml"));
            Parent root = loader.load();
            FindTournamentPageController controller = loader.getController();
            Stage stage = (Stage) MainApp.getPrimaryStage();
            stage.setScene(buildScene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGamePage(Tournament tournament) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("HosterGamePage.fxml"));
            Parent root = loader.load();
            HoasterGamePageController controller = loader.getController();
            controller.setTournament(tournament);
            Stage stage = (Stage) MainApp.getPrimaryStage();
            stage.setScene(buildScene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void endApplication() { }
}
