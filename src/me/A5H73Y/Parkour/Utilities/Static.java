package me.A5H73Y.Parkour.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.ParkourBlocks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Static {

	private static List<String> courseList = new ArrayList<String>();
	private static Map<String, String> questions = new HashMap<String, String>();
	private static List<String> quiet = new ArrayList<String>();
	private static boolean economy = false;
	private static boolean barAPI = false;
	private static boolean devBuild = true;
	private static ParkourBlocks pblocks;

	private final static String ParkourString = Utils.Colour("&0[&bParkour&0] &f");
	public final static String PATH = Parkour.getPlugin().getDataFolder() + File.separator + "playing.bin";
	private static String version;

	public final static ChatColor Aqua = ChatColor.AQUA;
	public final static ChatColor Daqua = ChatColor.DARK_AQUA;
	public final static ChatColor Gray = ChatColor.GRAY;
	public final static ChatColor White = ChatColor.WHITE;
	
	public static void initiate(){
		version = Parkour.getPlugin().getDescription().getVersion();
		courseList = Parkour.getParkourConfig().getAllCourses();
		pblocks = Utils.populateParkourBlocks();
	}

	public static void populateCourses(List<String> courses){
		courseList = courses;
	}
	
	public static String getParkourString() {
		return ParkourString;
	}

	public static String getVersion(){
		return version;
	}
	
	public static List<String> getCourses(){
		return courseList;
	}
	
	public static void setCourses(List<String> courses){
		courseList = courses;
	}
	
	public static boolean containsQuestion(String playerName){
		return questions.containsKey(playerName);
	}
	
	public static void addQuestion(String playerName, int command, String argument){
		questions.put(playerName, command + "," + argument);
	}

	public static String getQuestion(String playerName){
		return questions.get(playerName);
	}
	
	public static void removeQuestion(String playerName){
		questions.remove(playerName);
	}
	
	public static void setEconomy(boolean value){
		economy = value;
	}
	
	public static boolean getEconomy(){
		return economy;
	}
	
	public static boolean getDevBuild(){
		return devBuild;
	}
	
	public static void addQuiet(Player player){
		quiet.add(player.getName());
		player.sendMessage(getParkourString() + Utils.Colour("Quiet Mode: &bON"));
	}
	
	public static void removeQuiet(Player player){
		quiet.remove(player.getName());
		player.sendMessage(getParkourString() + Utils.Colour("Quiet Mode: &bOFF"));
	}
	
	public static boolean containsQuiet(String playerName){
		return quiet.contains(playerName);
	}
	
	public static ParkourBlocks getParkourBlocks(){
		return pblocks;
	}
	
	public static void setBarAPI(boolean value){
		barAPI = value;
	}
	
	public static boolean getBarAPI(){
		return barAPI;
	}
}
