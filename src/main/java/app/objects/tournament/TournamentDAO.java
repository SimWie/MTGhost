package app.objects.tournament;

import app.objects.player.Player;
import app.view.MainApp;
import database.DatabaseConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TournamentDAO {

    public Optional<Tournament> addTournament(String tournamentName, TournamentType tournamentType) {

        String insertSQL = "INSERT INTO tournament (name,round,tournamentType) VALUES (?,?,?)";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, tournamentName);
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, tournamentType.toString());
            preparedStatement.executeUpdate();
            //Generierten Key herauslesen
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    //Initialwerte von Tournament
                    Date playdate = null;
                    int round = 0;
                    return Optional.of(new Tournament(generatedId, tournamentName, round, tournamentType ,playdate));
                }
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Tournament> getTournament(String tournamentName) {

        String selectSQL = "SELECT * FROM tournament WHERE name = ?";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setString(1, tournamentName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    TournamentType type = TournamentType.valueOf(resultSet.getString("TournamentType"));

                    return Optional.of(new Tournament(  resultSet.getInt("tournamentID"),
                                                        tournamentName,
                                                        resultSet.getInt("Round"),
                                                        type,
                                                        resultSet.getDate("Playdate")));
                }
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }
        return Optional.empty();
    }

    public List<Tournament> getTournaments(String query) {
        String sql =
        """
        SELECT *
        FROM tournament
        WHERE UPPER(name) LIKE UPPER(?) ESCAPE '\\'
        ORDER BY name
        """;

        String pattern = "%" + escapeLike(query == null ? "" : query.trim()) + "%";

        try (Connection c = DatabaseConnectionProvider.createConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                List<Tournament> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    int tournamentID = rs.getInt("tournamentID");
                    String tournamentName = rs.getString("name");
                    int round = rs.getInt("round");
                    TournamentType type = TournamentType.valueOf(rs.getString("tournamentType"));
                    Date playdate = rs.getDate("Playdate");
                    out.add(new Tournament(tournamentID, tournamentName, round, type, playdate));
                }
                return out;
            }
        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
            return java.util.List.of();
        }
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }



}
