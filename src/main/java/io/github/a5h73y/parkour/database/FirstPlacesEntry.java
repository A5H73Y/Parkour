package io.github.a5h73y.parkour.database;

import io.github.a5h73y.parkour.utility.PlayerUtils;

public class FirstPlacesEntry {

    private final String playerId;
    private final int total;

    private String playerName;

    public FirstPlacesEntry(String playerId, int total) {
        this.playerId = playerId;
        this.total = total;
    }

    public String getPlayerName() {
        if (playerName == null) {
            playerName = PlayerUtils.findPlayerName(this.playerId);
        }
        return playerName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getTotal() {
        return total;
    }
}
