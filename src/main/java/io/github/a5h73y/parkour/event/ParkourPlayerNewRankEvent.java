package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourPlayerNewRankEvent extends ParkourEvent {

    private final String newParkourRank;

    public ParkourPlayerNewRankEvent(Player player, String courseName, String newParkourRank) {
        super(player, courseName);
        this.newParkourRank = newParkourRank;
    }

    public String getNewParkourRank() {
        return newParkourRank;
    }

}
