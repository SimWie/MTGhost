package app.objects.team;

import app.objects.player.Player;
import app.objects.player.PlayerDAO;
import app.objects.tournament.Tournament;
import app.objects.tournament.TournamentType;
import app.view.MainApp;
import database.DatabaseConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamDAO {

    public Optional<Team> addTeam(String teamName, Tournament tournament) {

        String insertSQL = "INSERT INTO team (name, tournamentID) VALUES (?, ?)";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, teamName);
            preparedStatement.setInt(2, tournament.tournamentID());
            preparedStatement.executeUpdate();
            //Generierten Key herauslesen
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    Team team = new Team(generatedId, teamName, tournament.tournamentID(), new ArrayList<>());
                    return Optional.of(team);
                }
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }
        return Optional.empty();
    }

    public Boolean deleteTeam(Team team) {

        String insertSQL =
        """
        DELETE FROM team WHERE TeamID = ?
        """;

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, team.teamID());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
            return false;
        }
        return true;
    }

    /// Gibt alle Teams eines Turniers zurück
    public List<Team> getTeams(Tournament tournament) {

        List<Team> teams = new ArrayList<>();
        PlayerDAO playerDAO = new PlayerDAO();

        String selectSQL = "SELECT * FROM team WHERE tournamentID = ?";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setInt(1, tournament.tournamentID());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("teamID");
                    String name = resultSet.getString("name");
                    Team team = new Team(id, name, tournament.tournamentID(), playerDAO.getPlayers(id));
                    teams.add(team);
                }
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return teams;
    }




    public Optional<Team> addTeamPlayer(Team team, Player player) {
        String insertSQL = "INSERT INTO teammitglied (teamID, playerID) VALUES (?, ?)";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setInt(1, team.teamID());
            preparedStatement.setInt(2, player.playerID());

            preparedStatement.executeUpdate();

            team.players().add(player);  // Spieler dem Team hinzufügen
            return Optional.of(team);


        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return Optional.empty();
    }

    public boolean rmvTeamPlayer(Team team, Player player) {

        String deleteSQL = "DELETE FROM teammitglied WHERE teamID = (?) AND PlayerID = (?)";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(2, player.playerID());
            preparedStatement.setInt(1, team.teamID());
            preparedStatement.executeUpdate();

            if (preparedStatement.executeUpdate() == 1) {
                MainApp.messageBox("Spieler erfolgreich entfernt");
                return true;
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        return false;
    }

    public List<Player> getTeamPlayers(Team team) {
        String sql =
                """
                SELECT p.PlayerID, p.Name 
                FROM Player p
                JOIN teammitglied tm ON tm.PlayerID = p.PlayerID
                WHERE teamID = (?);
                """;

        try (Connection c = DatabaseConnectionProvider.createConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, team.teamID());
            try (ResultSet rs = ps.executeQuery()) {
                List<Player> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    int playerID = rs.getInt("PlayerID");
                    String name = rs.getString("Name");
                    out.add(new Player(playerID, name));
                }
                return out;
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
            return java.util.List.of();
        }
    }


}
