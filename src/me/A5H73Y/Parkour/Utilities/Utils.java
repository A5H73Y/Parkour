package me.A5H73Y.Parkour.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.ParkourBlocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.bg.derh4nnes.TitleActionBarAPI;

public final class Utils {

	public final static String getTranslation(String string){
		return getTranslation(string, true);
	}

	public final static String getTranslation(String string, boolean prefix){
		String translated = Parkour.getParkourConfig().getStringData().getString(string);
		translated = translated != null ? Colour(translated) : "String not found!";
		return prefix ? Static.getParkourString() + translated : translated;
	}

	public final static boolean hasPermission(Player player, String permission){
		if (player.hasPermission(permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
		return false;
	}

	public final static boolean hasPermission(Player player, String permissionBranch, String permission){
		if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
		return false;
	}

	public final static boolean hasPermissionOrOwnership(Player player, String permissionBranch, String permission, String courseName){
		if (!(player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*"))){
			player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
			return false;
		} else if (!(CourseMethods.exist(courseName))){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return false;
		} else if (player.getName().toString() != Parkour.getParkourConfig().getCourseData().getString(courseName + ".Creator")){
			player.sendMessage(Static.getParkourString() + courseName + " is not your course!");
			return false;
		}
		return true;
	}

	public final static boolean validateArgs(Player player, int args, int desired){
		if (args > desired) {
			player.sendMessage(Utils.getTranslation("Error.TooMany"));
			return false;
		} else if (args < desired) { // || args == (desired - 1))
			player.sendMessage(Utils.getTranslation("Error.TooLittle"));
			return false;
		}
		return true;
	}

	public String standardizeText(String text) {
	    if (text == null || text.length() == 0) {
	        return text;
	    }
	    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}
	
	public final static boolean isNumber(String args) {
		try {
			Integer.parseInt(args);
			return true;
		} catch (Exception e) {}
		return false;
	}

	public final static String Colour(String S) {
		return ChatColor.translateAlternateColorCodes('&', S);
	}

	public final static String calculateTime(long ms) {
		int hours = (int) (ms / 1000L);
		int minutes = hours / 60;
		int seconds = minutes / 60;
		hours %= 60;
		minutes %= 60;
		seconds %= 24;

		String hoursn = hours < 10 ? "0" + hours : String.valueOf(hours);
		String minutesn = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
		String secondsn = seconds < 10 ? "0" + seconds : String.valueOf(seconds);

		return seconds > 0 ? secondsn + ":" + minutesn + ":" + hoursn : minutesn + ":" + hoursn;
	}
	
	public final static void log(String message) {
		Parkour.getPlugin().getLogger().info(message);
	}

	public final static void logToFile(String message) {
		if (Settings.isLog()){
			try {
				File saveTo = new File(Parkour.getParkourConfig().getDataFolder(), "Parkour.log");
				if (!saveTo.exists()) {
					saveTo.createNewFile();
				}

				FileWriter fw = new FileWriter(saveTo, true);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(getDateTime() + " " + message);
				pw.flush();
				pw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public final static String getDateTime() {
		Date date = new Date();
		Format formatter = new SimpleDateFormat("[dd/MM/yyyy | HH:mm:ss]");
		String s = formatter.format(date);
		return s;
	}

	public final static String getDate() {
		Date date = new Date();
		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		String s = formatter.format(date);
		return s;
	}

	public final static void saveAllConfig() {
		Parkour.getParkourConfig().saveCheck();
		Parkour.getParkourConfig().saveCourses();
		Parkour.getParkourConfig().saveEcon();
		Parkour.getParkourConfig().saveInv();
		Parkour.getParkourConfig().saveLeaders();
		Parkour.getParkourConfig().saveStrings();
		Parkour.getParkourConfig().saveUpgrades();
		Parkour.getParkourConfig().saveUsers();
	}

	public final static Location getLocation(String courseName, String path) {
		/*
		String locData;
		if (fileLocation.equalsIgnoreCase("courses.yml")){
			locData = Parkour.getParkourConfig().getCourseData().getString(data);
		}else if(fileLocation.equalsIgnoreCase("checkpoints.yml")){
			locData = Parkour.getParkourConfig().getCheckData().getString(data);
		}else if(fileLocation.equalsIgnoreCase("config.yml")){
			locData = Parkour.getParkourConfig().getConfig().getString(data);
		}else{
			return null;
		}*/

		FileConfiguration config = Parkour.getParkourConfig().getCourseData();

		double x = config.getDouble(courseName + path + "X");
		double y = config.getDouble(courseName + path + "Y");
		double z = config.getDouble(courseName + path + "Z");
		float yaw = (float) config.getDouble(courseName + path + "Yaw");
		float pitch = (float) config.getDouble(courseName + path + "Pitch");
		World world = Bukkit.getWorld(config.getString(courseName + ".World"));


		return new Location(world, x, y, z, yaw, pitch);
	}

	public static void Confirm(int command, String argument, Player player) {
		switch(command){
		case 1:	
			List<String> courselist = Static.getCourses();
			courselist.remove(argument);
			Parkour.getParkourConfig().getCourseData().set(argument, null);
			Parkour.getParkourConfig().getCourseData().set("Courses", courselist);
			Parkour.getParkourConfig().saveCourses();

			player.sendMessage(Utils.getTranslation(("Parkour.Delete")).replace("%COURSE%", argument));
			/*
				if (pl.getConfig().getBoolean("Database.Use")) {
					pl.logger.info("[Parkour] [SQL] Deleting from database...");
					MySQL.deleteRecord(argument);
				}*/
		}
		/*
		if (command == "delete") {
			List<String> courselist = pl.courseData.getStringList("Courses");
			courselist.remove(argument);
			pl.courseData.set(argument, "");
			pl.courseData.set("Courses", courselist);
			pl.saveCourses();
			player.sendMessage(pl.ParkourString + pl.Colour(pl.stringData.getString("Parkour.Delete").replace("%COURSE%", argument)));
			if (pl.getConfig().getBoolean("Database.Use")) {
				pl.logger.info("[Parkour] [SQL] Deleting from database...");
				MySQL.deleteRecord(argument);
			}

		} else if (command.equalsIgnoreCase("resetp")) {
			pl.usersData.set("PlayerInfo." + argument, "");
			pl.saveUsers();
			player.sendMessage(pl.ParkourString + argument + " information has been reset!");

		} else if (command.equalsIgnoreCase("resetall")) {
			List<String> courselist = pl.courseData.getStringList("Courses");
			player.sendMessage(pl.ParkourString + "Resetting " + courselist.size() + " courses...");
			for (String course : courselist){
				pl.leaderData.set(course + ".1.time", 999999999);
				pl.leaderData.set(course + ".1.deaths", 999);
				pl.leaderData.set(course + ".1.player", "N/A");
				pl.leaderData.set(course + ".2.time", 999999999);
				pl.leaderData.set(course + ".2.deaths", 999);
				pl.leaderData.set(course + ".2.player", "N/A");
				pl.leaderData.set(course + ".3.time", 999999999);
				pl.leaderData.set(course + ".3.deaths", 999);
				pl.leaderData.set(course + ".3.player", "N/A");
			}
			player.sendMessage(pl.ParkourString + "Process complete!");
			pl.Log(player.getName() + " reset all the course information!");
			pl.saveLeaders();

		} else {

		}
		 */
	}

	public static String invalidSyntax(String command, String arguments){
		return getTranslation("Error.Syntax").replaceAll("%COMMAND%", command).replaceAll("%ARGUMENTS%", arguments);
	}

	public static GameMode getGamemode(int gamemode){
		if (gamemode == 0)
			return GameMode.SURVIVAL;
		if (gamemode == 1)
			return GameMode.CREATIVE;
		if (gamemode == 2)
			return GameMode.ADVENTURE;
		if (gamemode == 3)
			return GameMode.SPECTATOR;

		return GameMode.SURVIVAL;
	}

	public static ParkourBlocks populateParkourBlocks() {
		FileConfiguration config = Parkour.getParkourConfig().getConfig();

		ParkourBlocks pb = new ParkourBlocks(
				Material.getMaterial(config.getString("Block.Finish.ID")),
				Material.getMaterial(config.getString("Block.Climb.ID")),
				Material.getMaterial(config.getString("Block.Launch.ID")),
				Material.getMaterial(config.getString("Block.Speed.ID")),
				Material.getMaterial(config.getString("Block.Repulse.ID")),
				Material.getMaterial(config.getString("Block.NoRun.ID")),
				Material.getMaterial(config.getString("Block.NoPotion.ID")),
				Material.getMaterial(config.getString("Block.DoubleJump.ID"))
				);

		return pb;
	}
	/*
	public static void saveAllPlaying(){
		for (Entry<String, PPlayer> entry : PlayerMethods.getPlaying().entrySet()) {
			System.out.println("Saving " + entry.getKey());
		    Parkour.getParkourConfig().getUsersData().set(entry.getKey(), entry.getValue());
		    Parkour.getParkourConfig().saveUsers();
		    System.out.println("Saved");
		}
	}*/

	/**
	 * Used for saving the playing players
	 * Thanks to Tomsik68 for this code.
	 * @param obj
	 * @param path
	 * @throws Exception
	 */
	public static void saveAllPlaying(Object obj,String path){
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	public static Object loadAllPlaying(String path) throws Exception{
		Object result = null;
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			result = ois.readObject();
			ois.close();
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return result;
	}

	public static void sendTitle(Player player, String title){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerTitle(player, 5, 25, 5, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}
	public static void sendActionBar(Player player, String title){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerActionbar(player, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}
	public static void sendFullTitle(Player player, String title, String subTitle){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendFullTitle(player, 5, 25, 5, title, subTitle);
		else
			player.sendMessage(Static.getParkourString() + title + " " + subTitle);
	}
	public static void sendSubTitle(Player player, String subTitle){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerSubTitle(player, 5, 25, 5, subTitle);
		else
			player.sendMessage(Static.getParkourString() + subTitle);
	}

	public static void toggleVisibility(Player player, boolean enabled) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (enabled)
				player.hidePlayer(players);
			else
				player.showPlayer(players);
		}
		if (enabled){
			Static.addHidden(player.getName());
			player.sendMessage(Utils.getTranslation("Event.HideAll1"));
		}else{
			Static.removeHidden(player.getName());
			player.sendMessage(Utils.getTranslation("Event.HideAll2"));
		}
	}
}
