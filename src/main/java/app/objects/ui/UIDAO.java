package app.objects.ui;

import app.objects.player.Player;
import app.objects.tournament.Tournament;
import app.objects.tournament.TournamentType;
import app.view.MainApp;
import database.DatabaseConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UIDAO {


    public List<TeamAndTournamentName> getTeamsAndTournamentsNames() {
        String selectSQL = """
        SELECT
            t.TeamID,
            t.name             AS team_name,
            tr.name            AS tournament_name,
            p.PlayerID         AS player_id,
            p.name             AS player_name
        FROM team t
        JOIN tournament tr        ON tr.TournamentID = t.TournamentID
        LEFT JOIN teammitglied tm ON tm.TeamID = t.TeamID
        LEFT JOIN player p        ON p.PlayerID = tm.PlayerID
        ORDER BY tr.name, t.name, p.name
        """;

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet rs = statement.executeQuery()) {

            // Aggregation pro Team
            Map<Integer, Agg> agg = new java.util.LinkedHashMap<>();

            while (rs.next()) {
                int teamID = rs.getInt("TeamID");

                Agg a = agg.get(teamID);
                if (a == null) {
                    String teamName = rs.getString("team_name");
                    String tournamentName = rs.getString("tournament_name");
                    a = new Agg(teamID, teamName, tournamentName);
                    agg.put(teamID, a);
                }

                // Spieler
                int playerId = rs.getInt("player_id");
                if (!rs.wasNull()) {
                    String playerName = rs.getString("player_name");
                    a.players.add(new Player(playerId, playerName)); // ggf. an deinen Player-Konstruktor anpassen
                }
            }

            // In unveränderliche DTOs umwandeln
            List<TeamAndTournamentName> result = new java.util.ArrayList<>(agg.size());
            for (Agg a : agg.values()) {
                result.add(new TeamAndTournamentName(
                        a.teamID,
                        a.teamName,
                        a.tournamentName,
                        java.util.List.copyOf(a.players) // immutable
                ));
            }
            return result;

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
            return java.util.List.of();
        }
    }

    // interner Helfer zum Gruppieren
    private static final class Agg {
        final int teamID;
        final String teamName;
        final String tournamentName;
        final java.util.List<Player> players = new java.util.ArrayList<>();
        Agg(int teamID, String teamName, String tournamentName) {
            this.teamID = teamID;
            this.teamName = teamName;
            this.tournamentName = tournamentName;
        }
    }
}
