package me.A5H73Y.Parkour.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
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

	private static Map<String, Question> questions = new HashMap<String, Question>();

	private static List<String> courseList = new ArrayList<String>();
	private static List<String> quiet = new ArrayList<String>();
	private static List<String> hidden = new ArrayList<String>();
	private static List<Challenge> challenges = new ArrayList<Challenge>();
	private static Map<String, Long> delay = new HashMap<String, Long>();

	private static List<String> lobbyList;

	private static boolean economy = false;
	private static boolean bountifulAPI = false;
	private static boolean placeholderAPI = false;
	private static boolean devBuild = false;
	private static ParkourBlocks parkourBlocks;

	private static String parkourString;
	private static String parkourSignString;
	private static Double version;

	public final static String PATH = Parkour.getPlugin().getDataFolder() + File.separator + "playing.bin";

	public final static void initiate() {
		version = Double.parseDouble(Parkour.getPlugin().getDescription().getVersion());
		courseList = Parkour.getParkourConfig().getAllCourses();
		parkourBlocks = Utils.populateDefaultParkourBlocks();
		parkourString = Utils.getTranslation("Parkour.Prefix", false);
		parkourSignString = Utils.getTranslation("Parkour.SignHeading", false);
	}

	public final static void populateCourses(List<String> courses) {
		courseList = courses;
	}

	public final static String getParkourString() {
		return parkourString;
	}

	public final static String getParkourSignString() { return parkourSignString; }

	public final static Double getVersion() {
		return version;
	}

	public final static List<String> getCourses() {
		return courseList;
	}

	public final static void setCourses(List<String> courses) {
		courseList = courses;
		Collections.sort(courses);
	}

	public final static boolean containsQuestion(String playerName) {
		return questions.containsKey(playerName);
	}

	public final static void addQuestion(String playerName, Question question) {
		questions.put(playerName, question);
	}

	public final static Question getQuestion(String playerName) {
		return questions.get(playerName);
	}

	public final static void removeQuestion(String playerName) {
		questions.remove(playerName);
	}

	public final static void setEconomy(boolean value) {
		economy = value;
	}

	public final static boolean getEconomy() {
		return economy;
	}

	public final static boolean getDevBuild() {
		return devBuild;
	}

	public final static void addQuiet(Player player) {
		Utils.sendActionBar(player, Utils.colour("Quiet Mode: &bON"), true);
		quiet.add(player.getName());
	}

	public final static void removeQuiet(Player player) {
		quiet.remove(player.getName());
		Utils.sendActionBar(player, Utils.colour("Quiet Mode: &bOFF"), true);
	}

	public final static boolean containsQuiet(String playerName) {
		return quiet.contains(playerName);
	}

	public final static ParkourBlocks getParkourBlocks() {
		return parkourBlocks;
	}

	public final static void setBountifulAPI(boolean value) {
		bountifulAPI = value;
	}

	public final static boolean getBountifulAPI() {
		return bountifulAPI;
	}

	public static boolean isPlaceholderAPI() {
		return placeholderAPI;
	}

	public static void setPlaceholderAPI(boolean placeholderAPI) {
		Static.placeholderAPI = placeholderAPI;
	}

	public final static boolean containsHidden(String playerName) {
		return hidden.contains(playerName);
	}

	public final static void addHidden(String playerName) {
		hidden.add(playerName);
	}

	public final static void removeHidden(String playerName) {
		hidden.remove(playerName);
	}

	public final static void addChallenge(Challenge challenge){
		challenges.add(challenge);
	}

	public final static void removeChallenge(Challenge challenge){
		if (challenges.contains(challenge))
			challenges.remove(challenge);
	}

	/**
	 * Find the challenge the recipient player has recieved.
	 * @param targetPlayer
	 * @return
	 */
	public final static Challenge getChallenge(String targetPlayer){
		for (Challenge challenge : challenges){
			if (challenge.getTargetPlayer().equals(targetPlayer))
				return challenge;
		}
		return null;
	}

	/**
	 * Get the current delay map
	 * @return
	 */
	public final static Map<String, Long> getDelay(){
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
		lobbyList = new ArrayList<String>(lobbyListSet);

		return lobbyList;
	}

	public static List<String> getWhitelistedCommands() {
		return new ArrayList<String>(Parkour.getPlugin().getConfig().getStringList("OnCourse.EnforceParkourCommands.Whitelist"));
	}

	public static void addWhitelistedCommand(String command) {
		List<String> commands = getWhitelistedCommands();
		commands.add(command);
		Parkour.getPlugin().getConfig().set("OnCourse.EnforceParkourCommands.Whitelist", commands);
	}
}
