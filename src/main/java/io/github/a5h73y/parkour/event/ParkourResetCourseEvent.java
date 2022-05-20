package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourResetCourseEvent extends ParkourEvent {

	public ParkourResetCourseEvent(final Player player, final String courseName) {
	    super(player, courseName);
	}

}
