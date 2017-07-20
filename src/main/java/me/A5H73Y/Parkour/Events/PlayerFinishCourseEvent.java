package me.A5H73Y.Parkour.Events;

import org.bukkit.entity.Player;

public class PlayerFinishCourseEvent extends ParkourEvent {

    public PlayerFinishCourseEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
