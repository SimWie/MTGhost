package app.objects.ui;

import app.objects.player.Player;
import java.util.List;

public record TeamAndTournamentName(int teamID, String teamName, String tournamentName, List<Player>  players) { }
