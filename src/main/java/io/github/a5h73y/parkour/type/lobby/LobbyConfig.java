package io.github.a5h73y.parkour.type.lobby;

import io.github.a5h73y.parkour.other.ParkourConstants;
import java.io.File;
import java.util.List;
import java.util.Set;
import de.leonhard.storage.Yaml;
import org.bukkit.Location;

/**
 * Lobby Information Utility class.
 * Convenience methods for accessing lobby configuration.
 */
public class LobbyConfig extends Yaml {

	public LobbyConfig(File file) {
		super(file);
	}

	/**
	 * Get all available Lobby names.
	 * @return lobby names
	 */
	public Set<String> getAllLobbyNames() {
		return this.singleLayerKeySet();
	}

	/**
	 * Check if 'default' Lobby exists.
	 * @return default parkour kit exists
	 */
	public boolean doesDefaultLobbyExist() {
		return doesLobbyExist(ParkourConstants.DEFAULT);
	}

	/**
	 * Check if Lobby exists.
	 * @param lobbyName lobby name
	 * @return lobby exists
	 */
	public boolean doesLobbyExist(String lobbyName) {
		return lobbyName != null && this.contains(lobbyName.toLowerCase());
	}

	/**
	 * Get {@link Location} from Lobby coordinates.
	 * @param lobbyName lobby name
	 * @return lobby Location
	 */
	public Location getLobbyLocation(String lobbyName) {
		return getSerializable(lobbyName + ".Location", Location.class);
	}

	/**
	 * Get the World name for the Lobby.
	 * @param lobbyName lobby name
	 * @return world name
	 */
	public String getLobbyWorld(String lobbyName) {
		return this.getString(lobbyName.toLowerCase() + ".Location.world");
	}

	/**
	 * Set the lobby's Location.
	 * The location will stored against the lobby name provided.
	 * @param lobbyName lobby name
	 * @param location location
	 */
	public void setLobbyLocation(String lobbyName, Location location) {
		this.setSerializable(lobbyName.toLowerCase() + ".Location", location);
	}

	/**
	 * Delete the Lobby.
	 * The Lobby with the matching name will be erased from the config.
	 * @param lobbyName lobby name
	 */
	public void deleteLobby(String lobbyName) {
		this.remove(lobbyName.toLowerCase());
	}

	/**
	 * Check if Lobby has Required ParkourLevel.
	 * @param lobbyName lobby name
	 * @return has ParkourLevel requirement
	 */
	public boolean hasRequiredLevel(String lobbyName) {
		return getRequiredLevel(lobbyName) > 0;
	}

	/**
	 * Get the Required ParkourLevel for Lobby.
	 * @param lobbyName lobby name
	 * @return required ParkourLevel
	 */
	public Integer getRequiredLevel(String lobbyName) {
		return this.getInt(lobbyName.toLowerCase() + ".RequiredLevel");
	}

	/**
	 * Set the Required ParkourLevel for Lobby.
	 * @param lobbyName lobby name
	 * @param requiredLevel required ParkourLevel
	 */
	public void setRequiredLevel(String lobbyName, Integer requiredLevel) {
		this.set(lobbyName.toLowerCase() + ".RequiredLevel", requiredLevel);
	}

	/**
	 * Check if the Lobby has Commands set.
	 * @param lobbyName lobby
	 * @return lobby has commands
	 */
	public boolean hasLobbyCommand(String lobbyName) {
		return this.contains(lobbyName.toLowerCase() + ".Commands");
	}

	/**
	 * Get a List of Commands for the Lobby.
	 * @param lobbyName lobby
	 * @return commands
	 */
	public List<String> getLobbyCommands(String lobbyName) {
		return this.getStringList(lobbyName.toLowerCase() + ".Commands");
	}

	/**
	 * Add a Command to the specified Lobby.
	 * @param lobbyName lobby
	 * @param command command
	 */
	public void addLobbyCommand(String lobbyName, String command) {
		List<String> commands = getLobbyCommands(lobbyName);
		commands.add(command);

		this.set(lobbyName.toLowerCase() + ".Commands", commands);
	}
}
