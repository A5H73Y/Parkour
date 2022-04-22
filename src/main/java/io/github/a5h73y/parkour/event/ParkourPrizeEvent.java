package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

// TODO
// have a boolean with 'prizeGiven' to match the NoPrize event?
public class ParkourPrizeEvent extends ParkourEvent {

	protected ParkourPrizeEvent(Player player, String courseName) {
		super(player, courseName);
	}
}
