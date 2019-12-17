package io.github.a5h73y.event;

import org.bukkit.entity.Player;

public class PlayerDeathEvent extends ParkourEvent {

    public PlayerDeathEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
