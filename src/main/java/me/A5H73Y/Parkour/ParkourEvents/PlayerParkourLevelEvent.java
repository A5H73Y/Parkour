package me.A5H73Y.Parkour.ParkourEvents;

import org.bukkit.entity.Player;

public class PlayerParkourLevelEvent extends ParkourEvent {

    private Integer newLevel;

    public PlayerParkourLevelEvent(Player player, String courseName, Integer newLevel) {
        super(player, courseName);
        this.newLevel = newLevel;
    }

    public Integer getNewLevel() {
        return newLevel;
    }

}
