package me.A5H73Y.Parkour.ParkourEvents;

import org.bukkit.entity.Player;

public class PlayerJoinCourseEvent extends ParkourEvent {

    public PlayerJoinCourseEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
