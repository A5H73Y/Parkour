package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourResetPlayerEvent extends ParkourEvent {

	public ParkourResetPlayerEvent(final Player player, final String courseName) {
		   super(player, courseName);
	}

}
