package io.github.a5h73y.event;

import org.bukkit.entity.Player;

public class PlayerLeaveCourseEvent extends ParkourEvent {

    public PlayerLeaveCourseEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
