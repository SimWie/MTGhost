package app.objects.player;

import java.util.List;
import java.util.Optional;

public class PlayerService {

    PlayerDAO playerDAO;

    public PlayerService() {
        playerDAO = new PlayerDAO();
    }

    public Optional<Player> addPlayer(String playerName) {
        return playerDAO.addPlayer(playerName);
    }

    public boolean rmvPlayer(Player player) {
        return playerDAO.rmvPlayer(player);
    }

    public List<Player> getPlayers() {
        return playerDAO.getPlayers();
    }
}
