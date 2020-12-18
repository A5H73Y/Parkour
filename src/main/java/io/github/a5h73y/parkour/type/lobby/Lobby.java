package io.github.a5h73y.parkour.type.lobby;

import org.bukkit.Location;

/**
 * Parkour Lobby.
 * A {@link Location} marking the ability to join other Courses.
 * A possible destination when leaving or finishing a Course.
 * May have a required ParkourLevel to join.
 */
public class Lobby {

	private final Location location;

	private final int requiredLevel;

	/**
	 * Construct a Lobby from the details.
	 *
	 * @param location lobby {@link Location}
	 * @param requiredLevel required ParkourLevel
	 */
	public Lobby(Location location, int requiredLevel) {
		this.location = location;
		this.requiredLevel = requiredLevel;
	}

	/**
	 * Get the {@link Location} of the Lobby.
	 * @return location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get the required ParkourLevel to join.
	 * @return required ParkourLevel
	 */
	public int getRequiredLevel() {
		return requiredLevel;
	}
}
