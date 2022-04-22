package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourPlayerNewLevelEvent extends ParkourEvent {

    private final Integer newLevel;

    public ParkourPlayerNewLevelEvent(Player player, String courseName, Integer newLevel) {
        super(player, courseName);
        this.newLevel = newLevel;
    }

    public Integer getNewLevel() {
        return newLevel;
    }

}
