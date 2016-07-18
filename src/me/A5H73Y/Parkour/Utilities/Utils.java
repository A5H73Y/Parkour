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

	/**
	 * The string parameter will be matched to an entry in the Strings.yml.
	 * The boolean will determine whether or not the String that is returned has the Parkour prefix before it.
	 * 
	 * @param string to translate
	 * @param display prefix?
	 * @return
	 */
	public final static String getTranslation(String string, boolean prefix){
		String translated = Parkour.getParkourConfig().getStringData().getString(string);
		translated = translated != null ? colour(translated) : "String not found!";
		return prefix ? Static.getParkourString().concat(translated) : translated;
	}

	/**
	 * Override above method, but with a default of enabled prefix.
	 * @param string
	 * @return
	 */
	public final static String getTranslation(String string){
		return getTranslation(string, true);
	}

	/**
	 * Check whether or not the player has a specific permission.
	 * @param player
	 * @param permission
	 * @return
	 */
	public final static boolean hasPermission(Player player, String permission){
		if (player.hasPermission(permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
		return false;
	}

	/**
	 * Check whether the player has a specific permission OR has the branch permission.
	 * Example "parkour.basic.join" OR "parkour.basic.*"
	 * @param player
	 * @param permissionBranch
	 * @param permission
	 * @return
	 */
	public final static boolean hasPermission(Player player, String permissionBranch, String permission){
		if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
		return false;
	}

	/**
	 * Same as above, except it will check if the player has the permission for the course in question. 
	 * e.g. Did the player create the course in the first place.
	 * 
	 * @param player
	 * @param permissionBranch
	 * @param permission
	 * @param courseName
	 * @return
	 */
	public final static boolean hasPermissionOrOwnership(Player player, String permissionBranch, String permission, String courseName){
		if (!(CourseMethods.exist(courseName))){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return false;

		} else if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*")){
			return true;

		} else if (player.getName().equals(Parkour.getParkourConfig().getCourseData().getString(courseName + ".Creator"))){
			return true;
		}

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
		return false;
	}

	/**
	 * Commands that require a certain length of arguments before it becomes processable.
	 * @param player
	 * @param args
	 * @param desired
	 * @return
	 */
	public final static boolean validateArgs(Player player, String[] args, int desired){
		if (args.length > desired) {
			player.sendMessage(Utils.getTranslation("Error.TooMany") + " (" + desired + ")");
			player.sendMessage(Utils.getTranslation("Help.Command").replace("%COMMAND%", Utils.standardizeText(args[0])));
			return false;
		} else if (args.length < desired) {
			player.sendMessage(Utils.getTranslation("Error.TooLittle") + " (" + desired + ")");
			player.sendMessage(Utils.getTranslation("Help.Command").replace("%COMMAND%", Utils.standardizeText(args[0])));
			return false;
		}
		return true;
	}

	/**
	 * Used for displaying text nicely, will turn "hElLO" into "Hello"
	 * @param text
	 * @return
	 */
	public final static String standardizeText(String text) {
		if (text == null || text.length() == 0) {
			return text;
		}
		return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
	}

	/**
	 * Checks whether or not a value is numberic "test" will fail for example.
	 * @param args
	 * @return
	 */
	public final static boolean isNumber(String args) {
		try {
			Integer.parseInt(args);
			return true;
		} catch (Exception e) {}
		return false;
	}

	public final static String colour(String S) {
		return ChatColor.translateAlternateColorCodes('&', S);
	}

	/**
	 * The input will be broken down into seconds, minutes and hours.
	 * If the time is not greater than an hour, don't  display hours.
	 * 
	 * @param milliseconds
	 * @return the total time
	 */
	public final static String calculateTime(long ms) {
		int seconds = (int) (ms / 1000L);
		int minutes = seconds / 60;
		int hours = minutes / 60;
		seconds %= 60;
		minutes %= 60;
		hours %= 24;

		String secondsn = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
		String minutesn = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
		String hoursn = hours < 10 ? "0" + hours : String.valueOf(hours);

		return hours > 0 ? hoursn.concat(":").concat(minutesn).concat(":").concat(secondsn) : 
			minutesn.concat(":").concat(secondsn);
	}

	/**
	 * Used for logging plugin events, varying in severity. 0 - Info; 1 - Warn; 2 - Severe.
	 * @param message
	 * @param severity
	 */
	public final static void log(String message, int severity){
		switch (severity){
		case 0:
			Parkour.getPlugin().getLogger().info(message);
			break;
		case 1:
			Parkour.getPlugin().getLogger().warning(message);
			break;
		case 2:
			Parkour.getPlugin().getLogger().severe("! " + message);
			break;
		default:
			Parkour.getPlugin().getLogger().info(message);
			break;
		}
	}

	public final static void log(String message) {
		log(message, 0);
	}

	/**
	 * This will write 'incriminating' events to a seperate file that can't be erased.
	 * Examples: playerA deleted courseB - This means any griefers can easily be caught etc.
	 * @param message
	 */
	public final static void logToFile(String message) {
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.LogToFile"))
			return;

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

	/**
	 * Broadcasts messages to everybody and the server (for logging).
	 * @param message
	 * @param permission
	 */
	public final static void broadcastMessage(String message, String permission){
		Bukkit.broadcast(message, permission);
		log(message);
	}

	/**
	 * This is currently only used for logToFile method.
	 * @return
	 */
	public final static String getDateTime() {
		Format formatter = new SimpleDateFormat("[dd/MM/yyyy | HH:mm:ss]");
		return formatter.format(new Date());
	}

	/**
	 * This is currently only used for the backup file names.
	 * Might make these configurable.
	 * @return
	 */
	public final static String getDate() {
		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		return formatter.format(new Date());
	}

	/**
	 * Retrieve the Location saved in the course config file for a course.
	 * @param courseName
	 * @param path
	 * @return
	 */
	public final static Location getLocation(String courseName, String path) {
		FileConfiguration config = Parkour.getParkourConfig().getCourseData();

		double x = config.getDouble(courseName + path + "X");
		double y = config.getDouble(courseName + path + "Y");
		double z = config.getDouble(courseName + path + "Z");
		float yaw = (float) config.getDouble(courseName + path + "Yaw");
		float pitch = (float) config.getDouble(courseName + path + "Pitch");
		World world = Bukkit.getWorld(config.getString(courseName + ".World"));

		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * This still needs a lot of work. The purpose is to confirm that player was meant to do certain commands.
	 * For example if "/pa delete (course)" was used, they would have to enter "/pa yes" to confirm.
	 * @param command
	 * @param argument
	 * @param player
	 */
	public final static void questionConfirm(int command, String argument, Player player) {
		/*
		 * 1: Delete course
		 * 2: Delete checkpoint
		 * 3: Reset course
		 * 4: Reset player
		 */
		switch(command){
		case 1:	
			List<String> courselist = Static.getCourses();
			courselist.remove(argument);
			Static.setCourses(courselist); //TODO check if this is necessary
			Parkour.getParkourConfig().getCourseData().set(argument, null);
			Parkour.getParkourConfig().getCourseData().set("Courses", courselist);
			Parkour.getParkourConfig().saveCourses();
			DatabaseMethods.deleteCourseAndReferences(argument);

			player.sendMessage(Utils.getTranslation("Parkour.Delete").replace("%COURSE%", argument));
			Utils.logToFile(argument + " was deleted by " + player.getName());
			return;

		case 2:
			String[] data = argument.split(";");
			String checkpoint = data[0];
			String courseName = data[1];
			int points = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Points");

			Parkour.getParkourConfig().getCourseData().set(courseName + "." + checkpoint, null);
			Parkour.getParkourConfig().getCourseData().set(courseName + ".Points", points - 1);
			Parkour.getParkourConfig().getCheckData().set(courseName + "." + checkpoint, null);

			player.sendMessage(Utils.getTranslation("Parkour.DeleteCheckpoint")
					.replace("%CHECKPOINT%", checkpoint)
					.replace("%COURSE%", courseName));
			Utils.logToFile("Checkpoint " + checkpoint + " was deleted on " + courseName + " by " + player.getName());
			return;
		
		case 3:
			return;
			
		case 4:
			return;
		}
		
	}

	/**
	 * This needs to be used for all commands, just trying to think of a good way to implement it.
	 * @param command
	 * @param arguments
	 * @return
	 */
	public final static String invalidSyntax(String command, String arguments){
		return getTranslation("Error.Syntax").replace("%COMMAND%", command).replace("%ARGUMENTS%", arguments);
	}

	/**
	 * Retrieve the GameMode for the integer, survival being default.
	 * @param gamemode
	 * @return
	 */
	public final static GameMode getGamemode(int gamemode){
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

	/**
	 * This will be executed once during start up to populate the default Parkour blocks.
	 * The strength and duration will be configurable and stored against the appropriate blocks.
	 * @return
	 */
	public final static ParkourBlocks populateParkourBlocks() {
		FileConfiguration config = Parkour.getParkourConfig().getConfig();

		//TODO duration and strength

		ParkourBlocks pb = new ParkourBlocks(
				Material.getMaterial(config.getString("DefaultBlocks.Death.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.Finish.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.Climb.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.Launch.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.Speed.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.Repulse.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.NoRun.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.NoPotion.Material")),
				Material.getMaterial(config.getString("DefaultBlocks.Bounce.Material"))
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
	public final static void saveAllPlaying(Object obj,String path){
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	public final static Object loadAllPlaying(String path) throws Exception{
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

	/**
	 * Different methods for displaying titles, the fallback option is to just message the player
	 * @param player
	 * @param title
	 */
	public final static void sendTitle(Player player, String title){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerTitle(player, 5, 20, 5, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}
	public final static void sendActionBar(Player player, String title){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerActionbar(player, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}
	public final static void sendFullTitle(Player player, String title, String subTitle){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendFullTitle(player, 5, 20, 5, title, subTitle);
		else
			player.sendMessage(Static.getParkourString() + title + " " + subTitle);
	}
	public final static void sendSubTitle(Player player, String subTitle){
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerSubTitle(player, 5, 20, 5, subTitle);
		else
			player.sendMessage(Static.getParkourString() + subTitle);
	}

	//TODO sort this out.
	public final static void toggleVisibility(Player player, boolean enabled) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (enabled)
				player.hidePlayer(players);
			else
				player.showPlayer(players);
		}
		if (enabled){
			Static.removeHidden(player.getName());
			player.sendMessage(Utils.getTranslation("Event.HideAll2"));
		}else{
			Static.addHidden(player.getName());
			player.sendMessage(Utils.getTranslation("Event.HideAll1"));
		}
	}

	public final static void resetCommand(String[] args, Player player) {
		if (args[1].equalsIgnoreCase("course")){
			if (!CourseMethods.exist(args[2])){
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}
			//TODO move to confirmation
			resetCourse(args[2]);
			player.sendMessage(Utils.getTranslation("Parkour.Reset").replace("%COURSE%", args[2]));
			logToFile(args[2] + " was reset by " + player.getName());

		} else if (args[1].equalsIgnoreCase("player")){
			if (!Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + args[2])){
				player.sendMessage(Static.getParkourString() + "Player does not exist.");
				return;
			}
			resetPlayer(args[2]);
			player.sendMessage(Utils.getTranslation("Parkour.Reset").replace("%COURSE%", args[2]));
			logToFile("player " + args[2] + " was reset by " + player.getName());

		} else {
			invalidSyntax("Prize", "(course / player) (courseName / playerName)");
		}
	}

	/**
	 * Resets all the statistics to 0 as if the course was just created.
	 * None of the structure of the course changes, all checkpoints will remain untouched.
	 * All times will deleted from the database.
	 * @param courseName
	 */
	public final static void resetCourse(String courseName){
		courseName = courseName.toLowerCase();
		Parkour.getParkourConfig().getCourseData().set(courseName + ".Views", 0);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".Completed", 0);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".Finished", false);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".XP", 0);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".Level", 0);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".MinimumLevel", 0);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".MaxDeaths", 0);
		Parkour.getParkourConfig().saveCourses();
		DatabaseMethods.deleteCourseTimes(courseName);
	}

	/**
	 * Remove the player from the various config files.
	 * Delete their times from all courses. (if they cheated all the times etc).
	 * @param playerName
	 */
	public final static void resetPlayer(String playerName){
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName, null);
		Parkour.getParkourConfig().saveUsers();
		DatabaseMethods.deleteAllTimesForPlayer(playerName);
	}

	
	public final static void createParkourBlocks(String[] args, Player player) {
		//TODO finish this.
		if (Parkour.getParkourConfig().getUpgData().contains("ParkourBlocks." + args[1])){
			player.sendMessage(Static.getParkourString() + "This ParkourBlocks already exists!");
			return;
		}

		player.sendMessage(colour("Creating &b" + args[1] + "&f ParkourBlocks..."));
		Static.addCreatePB(player.getName());
	}
}
