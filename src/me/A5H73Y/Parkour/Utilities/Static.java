package me.A5H73Y.Parkour.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Other.Question;

import org.bukkit.entity.Player;

public final class Static {

	private static Map<String, Question> questions = new HashMap<String, Question>();

	private static List<String> courseList = new ArrayList<String>();
	private static List<String> quiet = new ArrayList<String>();
	private static List<String> hidden = new ArrayList<String>();
	private static List<Challenge> challenges = new ArrayList<Challenge>();

	private static boolean economy = false;
	private static boolean barAPI = false;
	private static boolean devBuild = true;
	private static ParkourBlocks pblocks;

	private final static String ParkourString = Utils.colour("&0[&bParkour&0] &f");
	private static Double version;

	public final static String PATH = Parkour.getPlugin().getDataFolder() + File.separator + "playing.bin";

	public final static void initiate() {
		version = Double.parseDouble(Parkour.getPlugin().getDescription().getVersion());
		courseList = Parkour.getParkourConfig().getAllCourses();
		pblocks = Utils.populateParkourBlocks();
	}

	public final static void populateCourses(List<String> courses) {
		courseList = courses;
	}

	public final static String getParkourString() {
		return ParkourString;
	}

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
		Utils.sendActionBar(player, Utils.colour("Quiet Mode: &bON"));
		quiet.add(player.getName());
	}

	public final static void removeQuiet(Player player) {
		quiet.remove(player.getName());
		Utils.sendActionBar(player, Utils.colour("Quiet Mode: &bOFF"));
	}

	public final static boolean containsQuiet(String playerName) {
		return quiet.contains(playerName);
	}

	public final static ParkourBlocks getParkourBlocks() {
		return pblocks;
	}

	public final static void setBarAPI(boolean value) {
		barAPI = value;
	}

	public final static boolean getBarAPI() {
		return barAPI;
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
	public final static Challenge getChallenge(String targetPlayer){
		for (Challenge challenge : challenges){
			if (challenge.getTargetPlayer().equals(targetPlayer))
				return challenge;
		}
		return null;
	}
}
