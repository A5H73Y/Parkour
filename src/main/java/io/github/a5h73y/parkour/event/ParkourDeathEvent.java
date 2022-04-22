package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourDeathEvent extends ParkourEvent {

    public ParkourDeathEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
