package app.objects.ui;

import java.util.List;

public class UIService {
    UIDAO uiDAO;

    public UIService() {
        uiDAO = new UIDAO();
    }

    // Gibt eine Liste mit allen Teams zurück zusammengesetzt mit Tournament und Player namen
    public List<TeamAndTournamentName> getTeamsAndTournaments() {
        return uiDAO.getTeamsAndTournamentsNames();
    }
}
