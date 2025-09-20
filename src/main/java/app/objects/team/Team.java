package app.objects.team;

import app.objects.player.Player;
import java.util.List;

public record Team(int teamID, String teamName, int tournamentID, List<Player>  players) { }
