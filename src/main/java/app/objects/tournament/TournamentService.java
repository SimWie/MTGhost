package app.objects.tournament;
import java.util.List;
import java.util.Optional;

public class TournamentService {
    TournamentDAO tournamentDAO;

    public TournamentService() {
        tournamentDAO = new TournamentDAO();
    }

    public Optional<Tournament> addTournament(String tournamentName, TournamentType tournamentType) {
        return tournamentDAO.addTournament(tournamentName, tournamentType);
    }

    public Optional<Tournament> getTournament(String tournamentName) {
        return  tournamentDAO.getTournament(tournamentName);
    }

    public List<Tournament> getTournaments(String tournamentName) {
        return  tournamentDAO.getTournaments(tournamentName);
    }

}

