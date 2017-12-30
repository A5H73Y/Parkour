package me.A5H73Y.Parkour.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.Question;

import org.bukkit.entity.Player;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public final class Static {

	private static Map<String, Question> questions = new HashMap<>();

	private static List<String> courseList = new ArrayList<>();
	private static List<String> quiet = new ArrayList<>();
	private static List<String> hidden = new ArrayList<>();
	private static Map<String, Long> delay = new HashMap<>();

	private static List<String> lobbyList;

	private static boolean economy = false;
	private static boolean bountifulAPI = false;
	private static boolean placeholderAPI = false;

    private static String parkourString;
	private static String parkourSignString;
	private static Double version;

	public final static String PATH = Parkour.getPlugin().getDataFolder() + File.separator + "playing.bin";

	public static void initiate() {
		version = Double.parseDouble(Parkour.getPlugin().getDescription().getVersion());
		courseList = Parkour.getParkourConfig().getAllCourses();
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

	public static List<String> getCourses() {
		return courseList;
	}

    public static boolean containsQuestion(String playerName) {
		return questions.containsKey(playerName);
	}

	public static void addQuestion(String playerName, Question question) {
		questions.put(playerName, question);
	}

	public static Question getQuestion(String playerName) {
		return questions.get(playerName);
	}

	public static void removeQuestion(String playerName) {
		questions.remove(playerName);
	}

	public static void enableEconomy() {
		economy = true;
	}

	public static boolean getEconomy() {
		return economy;
	}

	public static boolean getDevBuild() {
        return false;
	}

	public static void addQuiet(Player player) {
		Utils.sendActionBar(player, Utils.colour("Quiet Mode: &bON"), true);
		quiet.add(player.getName());
	}

	public static void removeQuiet(Player player) {
		quiet.remove(player.getName());
		Utils.sendActionBar(player, Utils.colour("Quiet Mode: &bOFF"), true);
	}

	public static boolean containsQuiet(String playerName) {
		return quiet.contains(playerName);
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
	public static Map<String, Long> getDelay(){
		return delay;
	}

	/**
	 * Get list of Lobbys
	 * I'm aware this looks like shit, but it's the best solution.
	 * @return List<String>
	 */
	public static List<String> getLobbyList() {
		if (lobbyList != null)
			return lobbyList;

		Set<String> lobbyListSet = Parkour.getPlugin().getConfig().getConfigurationSection("Lobby").getKeys(false);

		lobbyListSet.remove("Set");
		lobbyListSet.remove("World");
		lobbyListSet.remove("X");
		lobbyListSet.remove("Y");
		lobbyListSet.remove("Z");
		lobbyListSet.remove("Pitch");
		lobbyListSet.remove("Yaw");
		lobbyList = new ArrayList<>(lobbyListSet);

		return lobbyList;
	}

	public static List<String> getWhitelistedCommands() {
		return new ArrayList<>(Parkour.getPlugin().getConfig().getStringList("OnCourse.EnforceParkourCommands.Whitelist"));
	}

	public static void addWhitelistedCommand(String command) {
		List<String> commands = getWhitelistedCommands();
		commands.add(command);
		Parkour.getPlugin().getConfig().set("OnCourse.EnforceParkourCommands.Whitelist", commands);
		Parkour.getPlugin().saveConfig();
	}
}
