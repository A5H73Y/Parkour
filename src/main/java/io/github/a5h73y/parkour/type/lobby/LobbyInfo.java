package io.github.a5h73y.parkour.type.lobby;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.other.ParkourConstants;
import java.util.Collections;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Lobby Information Utility class.
 * Convenience methods for accessing lobby configuration.
 */
public class LobbyInfo {

	/**
	 * Get all available Lobby names.
	 * @return lobby names
	 */
	public static Set<String> getAllLobbyNames() {
		return Parkour.getDefaultConfig().isConfigurationSection("Lobby")
					? Parkour.getDefaultConfig().getConfigurationSection("Lobby").getKeys(false)
					: Collections.emptySet();
	}

	/**
	 * Check if 'default' Lobby exists.
	 * @return default parkour kit exists
	 */
	public static boolean doesLobbyExist() {
		return doesLobbyExist(ParkourConstants.DEFAULT);
	}

	/**
	 * Check if Lobby exists.
	 * @param lobbyName lobby name
	 * @return lobby exists
	 */
	public static boolean doesLobbyExist(String lobbyName) {
		return Parkour.getDefaultConfig().contains("Lobby." + lobbyName.toLowerCase());
	}

	/**
	 * Get {@link Location} from Lobby coordinates.
	 * @param lobbyName lobby name
	 * @return lobby Location
	 */
	public static Location getLobbyLocation(String lobbyName) {
		String path = "Lobby." + lobbyName.toLowerCase();
		DefaultConfig config = Parkour.getDefaultConfig();
		World world = Bukkit.getWorld(config.getString(path + ".World"));
		double x = config.getDouble(path + ".X");
		double y = config.getDouble(path + ".Y");
		double z = config.getDouble(path + ".Z");
		float yaw = config.getInt(path + ".Yaw");
		float pitch = config.getInt(path + ".Pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * Get the World name for the Lobby.
	 * @param lobbyName lobby name
	 * @return world name
	 */
	public static String getLobbyWorld(String lobbyName) {
		return Parkour.getDefaultConfig().getString("Lobby." + lobbyName + ".World");
	}

	/**
	 * Set the lobby's Location.
	 * The location will stored against the lobby name provided.
	 * @param lobbyName lobby name
	 * @param location location
	 */
	public static void setLobby(String lobbyName, Location location) {
		DefaultConfig config = Parkour.getDefaultConfig();
		String path = "Lobby." + lobbyName.toLowerCase();
		config.set(path + ".World", location.getWorld().getName());
		config.set(path + ".X", location.getX());
		config.set(path + ".Y", location.getY());
		config.set(path + ".Z", location.getZ());
		config.set(path + ".Pitch", location.getPitch());
		config.set(path + ".Yaw", location.getYaw());
		config.save();
	}

	/**
	 * Delete the Lobby.
	 * The Lobby with the matching name will be erased from the config.
	 * @param lobbyName lobby name
	 */
	public static void deleteLobby(String lobbyName) {
		Parkour.getDefaultConfig().set("Lobby." + lobbyName.toLowerCase(), null);
		Parkour.getDefaultConfig().save();
	}

	/**
	 * Get the Required ParkourLevel for Lobby.
	 * @param lobbyName lobby name
	 * @return required ParkourLevel
	 */
	public static Integer getRequiredLevel(String lobbyName) {
		return Parkour.getDefaultConfig().getInt("Lobby." + lobbyName + ".RequiredLevel", 0);
	}

	/**
	 * Check if Lobby has Required ParkourLevel.
	 * @param lobbyName lobby name
	 * @return has ParkourLevel requirement
	 */
	public static boolean hasRequiredLevel(String lobbyName) {
		return getRequiredLevel(lobbyName) > 0;
	}

	/**
	 * Set the Required ParkourLevel for Lobby.
	 * @param lobbyName lobby name
	 * @param requiredLevel required ParkourLevel
	 */
	public static void setRequiredLevel(String lobbyName, Integer requiredLevel) {
		Parkour.getDefaultConfig().set("Lobby." + lobbyName.toLowerCase() + ".RequiredLevel", requiredLevel);
		Parkour.getDefaultConfig().save();
	}

	private LobbyInfo() {
		throw new IllegalStateException("Utility class");
	}
}
