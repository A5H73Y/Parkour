package io.github.a5h73y.parkour.type.lobby;

import org.bukkit.Location;

public class Lobby {

	private final Location location;

	private final int requiredLevel;

	public Lobby(Location location, int requiredLevel) {
		this.location = location;
		this.requiredLevel = requiredLevel;
	}

	public Location getLocation() {
		return location;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}
}
