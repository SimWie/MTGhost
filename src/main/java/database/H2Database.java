package database;

import app.view.MainApp;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Database {

    public static void createTables() throws SQLException {

        /// Tabelle Player
        String createTableSQL = "CREATE TABLE IF NOT EXISTS player (" +
                                "PlayerID INT AUTO_INCREMENT PRIMARY KEY, " +
                                "name VARCHAR(50) NOT NULL UNIQUE)";

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        /// Tabelle Tournament
        createTableSQL =    """
                            CREATE TABLE IF NOT EXISTS tournament (
                            TournamentID   INT AUTO_INCREMENT PRIMARY KEY,
                            name           VARCHAR(50) NOT NULL,
                            Playdate       DATE,
                            Round          INT NOT NULL,
                            TournamentType VARCHAR(50) NOT NULL,
                            UNIQUE (name, Playdate)
                            )
                            """;

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        /// Tabelle Team
        createTableSQL =    """
                            CREATE TABLE IF NOT EXISTS team (
                            TeamID       INT AUTO_INCREMENT PRIMARY KEY,
                            name         VARCHAR(50) NOT NULL,
                            TournamentID INT NOT NULL,
                            CONSTRAINT fk_team_tournament
                            FOREIGN KEY (TournamentID)
                            REFERENCES tournament(TournamentID)
                            ON DELETE CASCADE,
                            UNIQUE (TournamentID, name)
                            )
                            """;
        try (Connection connection = DatabaseConnectionProvider.createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        /// Tabelle Teammitglied
        createTableSQL =    """
                            CREATE TABLE IF NOT EXISTS teammitglied (
                            TeamID   INT NOT NULL,
                            PlayerID INT NOT NULL,
                            PRIMARY KEY (TeamID, PlayerID),
                            CONSTRAINT fk_tm_team
                            FOREIGN KEY (TeamID)   REFERENCES team(TeamID)     ON DELETE CASCADE,
                            CONSTRAINT fk_tm_player
                            FOREIGN KEY (PlayerID) REFERENCES player(PlayerID) ON DELETE CASCADE
                            )
                            """;

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

        /// Tabelle Game
        createTableSQL =    """
                            CREATE TABLE IF NOT EXISTS game (
                            GameID       INT AUTO_INCREMENT PRIMARY KEY,
                            TournamentID INT NOT NULL,
                            TeamID1      INT NOT NULL,
                            TeamID2      INT NOT NULL,
                            WinsTeam1    INT DEFAULT 0,
                            WinsTeam2    INT DEFAULT 0,
                            Round        INT NOT NULL,
                            CONSTRAINT fk_game_tourn FOREIGN KEY (TournamentID) REFERENCES tournament(TournamentID) ON DELETE CASCADE,
                            CONSTRAINT fk_game_t1    FOREIGN KEY (TeamID1)      REFERENCES team(TeamID)             ON DELETE CASCADE,
                            CONSTRAINT fk_game_t2    FOREIGN KEY (TeamID2)      REFERENCES team(TeamID)             ON DELETE CASCADE,
                            CONSTRAINT chk_game_teams CHECK (TeamID1 <> TeamID2)
                            )
            """;

        try (Connection connection = DatabaseConnectionProvider.createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            MainApp.errorBox(e.getMessage());
        }

    }
}
