package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourResetLeaderboardEvent extends ParkourEvent {

	public ParkourResetLeaderboardEvent(final Player player, final String courseName) {
	    super(player, courseName);
	}

}
