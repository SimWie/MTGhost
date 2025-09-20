package app.objects.player;

import app.view.MainApp;
import database.DatabaseConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerDAO {

    public PlayerDAO() {
        // empty Constructor
    }

    /// Fügt einen Spieler auf der Tabelle Players hinzu. Name muss unique sein
    /// Playername max 50 Zeichen
    public Optional<Player> addPlayer(String playerName) {

        String insertSQL = "INSERT INTO player (name) VALUES (?)";

        //Statment an DB senden
        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();

            //Generierten Key herauslesen
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return Optional.of(new Player(generatedId, playerName));
                }
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return Optional.empty();
    }

    /// Löscht einen Player in der Tabelle Player, wenn nicht vorhanden gibt Funktion false zurück.
    /// Der zu Löschende Player muss mitgegeben werden
    public boolean rmvPlayer(Player player) {

        String deleteSQL = "DELETE FROM player WHERE name = VALUE (?)";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, player.playerName());
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return false;
    }

    /// Gibt eine Liste aller Spieler zurück
    public List<Player> getPlayers() {

        List<Player> players = new ArrayList<>();

        String selectSQL = "SELECT * FROM player";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("PlayerID");
                String name = resultSet.getString("name");
                players.add(new Player(id, name));
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return players;
    }

    /// Gibt eine Liste aller Spieler eines Teams anhand der TeamID zurück
    public List<Player> getPlayers(int teamID) {

        List<Player> players = new ArrayList<>();

        String selectSQL = "SELECT player.* FROM player" +
                " JOIN teammitglied ON player.playerID = teammitglied.playerID" +
                " WHERE teammitglied.teamID = ?";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setInt(1, teamID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("PlayerID");
                    String name = resultSet.getString("name");
                    players.add(new Player(id, name));
                }
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return players;
    }

}
