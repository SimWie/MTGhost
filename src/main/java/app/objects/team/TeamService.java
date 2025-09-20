package app.objects.team;

import app.objects.player.Player;
import app.objects.player.PlayerDAO;
import app.objects.player.PlayerService;
import app.objects.tournament.Tournament;
import app.objects.tournament.TournamentService;
import app.view.MainApp;

import java.util.List;
import java.util.Optional;

public class TeamService {
    TeamDAO teamDAO;
    PlayerService playerService;

    public TeamService() {
        teamDAO = new TeamDAO();
        playerService = new PlayerService();
    }

    public Optional<Team> addTeam(String teamName, Tournament tournament) {
        return teamDAO.addTeam(teamName, tournament);
    }

    public Boolean rmvTeam (Team team, Tournament tournament) {

        // Nur löschen wenn Tournier noch nicht gestartet
        if (tournament.round() == 0 ) {
            return teamDAO.deleteTeam(team);
        } else {
            MainApp.errorBox("Tournier schon gestartet");
            return false;
        }
    };

    public List<Team> getTeams(Tournament tournament) {
        return teamDAO.getTeams(tournament);
    }

    public List<Player> getTeamPlayers(Team team) {
        return teamDAO.getTeamPlayers(team);
    }

    public Boolean rmvTeamPlayer(Team team, Player player) {
        return teamDAO.rmvTeamPlayer(team, player);
    }

    /// Player einem Team zuweisen.
    /// Hierbei wird kontroliert ob der Spieler nicht schon im Team ist und ob er nicht schon an Tournament teillnimt
    public Optional<Team> addTeamPlayer(Team team, Player player) {
        if (team.players().stream().anyMatch(p -> p.equals(player))) {
            MainApp.messageBox(player.playerName() + " ist schon in diesem Team");
            return Optional.of(team);
        }
        return teamDAO.addTeamPlayer(team, player);
    }

    /// Teamplayer zu Team hinzufügen, wenn Spieler noch nicht existiert wird dieser angelegt
    public Optional<Team> addTeamPlayerAndPlayer(Team team, String playerName) {
        Optional<Player> player;
        List<Player> players;
        players = playerService.getPlayers();

        //Wenn der Player schon existiert wird kein neuer angelegt es wird nur der existierende zum team hinzugefügt
        for (Player p : players) {
            if (p.playerName().equals(playerName)) {
                return addTeamPlayer(team, p);
            }
        }

        //Wenn der Player nicht gefunden wurde wird ein neuer angelegt
        player = playerService.addPlayer(playerName);

        MainApp.messageBox("Neuer Spieler wurde angelegt");

        if (player.isPresent()) {
            return addTeamPlayer(team, player.get());
        }

        return Optional.empty();

    }



    ;
}
