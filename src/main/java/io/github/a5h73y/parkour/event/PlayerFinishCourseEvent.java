package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class PlayerFinishCourseEvent extends ParkourEvent {

    public PlayerFinishCourseEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}
