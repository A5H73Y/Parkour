package me.A5H73Y.Parkour.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.entity.Player;

public final class Static {

	private static List<String> hidden = new ArrayList<>();
	private static Map<String, Long> delay = new HashMap<>();

	private static boolean economy = false;
	private static boolean bountifulAPI = false;
	private static boolean placeholderAPI = false;

    private static String parkourString;
	private static String parkourSignString;
	private static Double version;

	public final static String PLAYING_BIN_PATH = Parkour.getPlugin().getDataFolder() + File.separator + "playing.bin";

	public static void initiate() {
		version = Double.parseDouble(Parkour.getPlugin().getDescription().getVersion());
		parkourString = Utils.getTranslation("Parkour.Prefix", false);
		parkourSignString = Utils.getTranslation("Parkour.SignHeading", false);
	}

    public static String getParkourString() {
		return parkourString;
	}

	public static String getParkourSignString() { return parkourSignString; }

	public static Double getVersion() {
		return version;
	}

	public static void enableEconomy() {
		economy = true;
	}

	public static boolean getEconomy() {
		return economy;
	}

	public static void enableBountifulAPI() {
		bountifulAPI = true;
	}

	public static boolean getBountifulAPI() {
		return bountifulAPI;
	}

	public static boolean isPlaceholderAPI() {
		return placeholderAPI;
	}

	public static void enablePlaceholderAPI() {
		Static.placeholderAPI = true;
	}

	public static boolean containsHidden(String playerName) {
		return hidden.contains(playerName);
	}

	public static void addHidden(String playerName) {
		hidden.add(playerName);
	}

	public static void removeHidden(String playerName) {
		hidden.remove(playerName);
	}

	/**
	 * Get the current delay map
	 * @return
	 */
	public static Map<String, Long> getDelay() {
		return delay;
	}

	/**
	 * Get list of Lobbys
	 * I'm aware this looks like shit, but it's the best solution.
	 * @return List<String>
	 */
	public static Set<String> getLobbyList() {
		Set<String> lobbyListSet = Parkour.getPlugin().getConfig().getConfigurationSection("Lobby").getKeys(false);

		lobbyListSet.remove("Set");
		lobbyListSet.remove("World");
		lobbyListSet.remove("EnforceWorld");
		lobbyListSet.remove("X");
		lobbyListSet.remove("Y");
		lobbyListSet.remove("Z");
		lobbyListSet.remove("Pitch");
		lobbyListSet.remove("Yaw");

		return lobbyListSet;
	}

	public static void addWhitelistedCommand(String command) {
		List<String> commands = Parkour.getSettings().getWhitelistedCommands();
		commands.add(command);
		Parkour.getPlugin().getConfig().set("OnCourse.EnforceParkourCommands.Whitelist", commands);
		Parkour.getPlugin().saveConfig();
	}
}
