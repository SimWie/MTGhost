package app.objects.tournament;

import java.util.Date;

public record Tournament(int tournamentID, String name, int round, TournamentType tournamentType, Date playdate) { }