package io.github.a5h73y.parkour.type.lobby;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.other.Constants;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LobbyInfo {

	public static Set<String> getAllLobbyNames() {
		return Parkour.getDefaultConfig().getConfigurationSection("Lobby").getKeys(false);
	}

	public static boolean doesLobbyExist() {
		return doesLobbyExist(Constants.DEFAULT);
	}

	public static boolean doesLobbyExist(String lobbyName) {
		return Parkour.getDefaultConfig().contains("Lobby." + lobbyName.toLowerCase());
	}

	public static Lobby getLobby(String lobbyName) {
		return new Lobby(getLobbyLocation(lobbyName), getRequiredLevel(lobbyName));
	}

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

	public static String getLobbyWorld(String lobbyName) {
		return Parkour.getDefaultConfig().getString("Lobby." + lobbyName + ".World");
	}

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

	public static void deleteLobby(String lobbyName) {
		Parkour.getDefaultConfig().set("Lobby." + lobbyName.toLowerCase(), null);
		Parkour.getDefaultConfig().save();
	}

	public static Integer getRequiredLevel(String lobbyName) {
		return Parkour.getDefaultConfig().getInt("Lobby." + lobbyName + ".RequiredLevel", 0);
	}

	public static boolean hasRequiredLevel(String lobbyName) {
		return getRequiredLevel(lobbyName) > 0;
	}

	public static void setRequiredLevel(String lobbyName, Integer requiredLevel) {
		Parkour.getDefaultConfig().set("Lobby." + lobbyName.toLowerCase() + ".RequiredLevel", requiredLevel);
		Parkour.getDefaultConfig().save();
	}
}
