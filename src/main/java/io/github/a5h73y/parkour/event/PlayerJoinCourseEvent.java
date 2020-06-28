package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class PlayerJoinCourseEvent extends ParkourEvent {

    public PlayerJoinCourseEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
