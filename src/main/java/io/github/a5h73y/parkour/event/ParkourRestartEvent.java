package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourRestartEvent extends ParkourEvent {

    public ParkourRestartEvent(final Player player, final String courseName) {
        super(player, courseName);
    }
}
