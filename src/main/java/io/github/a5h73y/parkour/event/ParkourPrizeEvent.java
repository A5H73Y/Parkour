package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourPrizeEvent extends ParkourEvent {

	protected ParkourPrizeEvent(Player player, String courseName) {
		super(player, courseName);
	}
}
