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

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Conversation.Conversation;
import me.A5H73Y.Parkour.Conversation.Conversation.ConversationType;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Other.Question;
import me.A5H73Y.Parkour.Other.Question.QuestionType;

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
	 * The string parameter will be matched to an entry in the Strings.yml. The
	 * boolean will determine whether or not the String that is returned has the
	 * Parkour prefix before it.
	 * 
	 * @param string
	 *            to translate
	 * @param display
	 *            prefix?
	 * @return
	 */
	public final static String getTranslation(String string, boolean prefix) {
		String translated = Parkour.getParkourConfig().getStringData().getString(string);
		translated = translated != null ? colour(translated) : "String not found!";
		return prefix ? Static.getParkourString().concat(translated) : translated;
	}

	/**
	 * Override above method, but with a default of enabled prefix.
	 * 
	 * @param string
	 * @return
	 */
	public final static String getTranslation(String string) {
		return getTranslation(string, true);
	}

	/**
	 * Check whether or not the player has a specific permission.
	 * 
	 * @param player
	 * @param permission
	 * @return
	 */
	public final static boolean hasPermission(Player player, String permission) {
		if (player.hasPermission(permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
		return false;
	}

	/**
	 * Check whether the player has a specific permission OR has the branch
	 * permission. Example "parkour.basic.join" OR "parkour.basic.*"
	 * 
	 * @param player
	 * @param permissionBranch
	 * @param permission
	 * @return
	 */
	public final static boolean hasPermission(Player player, String permissionBranch, String permission) {
		if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
		return false;
	}

	/**
	 * Same as above, except it will check if the player has the permission for
	 * the course in question. e.g. Did the player create the course in the
	 * first place.
	 * 
	 * @param player
	 * @param permissionBranch
	 * @param permission
	 * @param courseName
	 * @return
	 */
	public final static boolean hasPermissionOrOwnership(Player player, String permissionBranch, String permission, String courseName) {
		if (!(CourseMethods.exist(courseName))) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return false;

		} else if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*")) {
			return true;

		} else if (player.getName().equals(Parkour.getParkourConfig().getCourseData().getString(courseName + ".Creator"))) {
			return true;
		}

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
		return false;
	}

	/**
	 * Commands that require a certain length of arguments before it becomes
	 * processable.
	 * 
	 * @param player
	 * @param args
	 * @param desired
	 * @return
	 */
	public final static boolean validateArgs(Player player, String[] args, int desired) {
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
	 * 
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
	 * Checks whether or not a value is numeric "test" will fail for example.
	 * 
	 * @param args
	 * @return
	 */
	public final static boolean isNumber(String args) {
		try {
			Integer.parseInt(args);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public final static String colour(String S) {
		return ChatColor.translateAlternateColorCodes('&', S);
	}

	/**
	 * The input will be broken down into seconds, minutes and hours. If the
	 * time is not greater than an hour, don't display hours.
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

		return hours > 0 ? hoursn.concat(":").concat(minutesn).concat(":").concat(secondsn) : minutesn.concat(":").concat(secondsn);
	}

	/**
	 * Used for logging plugin events, varying in severity. 0 - Info; 1 - Warn;
	 * 2 - Severe.
	 * 
	 * @param message
	 * @param severity
	 */
	public final static void log(String message, int severity) {
		switch (severity) {
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
	 * This will write 'incriminating' events to a seperate file that can't be
	 * erased. Examples: playerA deleted courseB - This means any griefers can
	 * easily be caught etc.
	 * 
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
	 * 
	 * @param message
	 * @param permission
	 */
	public final static void broadcastMessage(String message, String permission) {
		Bukkit.broadcast(message, permission);
		log(message);
	}

	/**
	 * This is currently only used for logToFile method.
	 * 
	 * @return
	 */
	public final static String getDateTime() {
		Format formatter = new SimpleDateFormat("[dd/MM/yyyy | HH:mm:ss]");
		return formatter.format(new Date());
	}

	/**
	 * This is currently only used for the backup file names. Might make these
	 * configurable.
	 * 
	 * @return
	 */
	public final static String getDate() {
		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		return formatter.format(new Date());
	}

	/**
	 * Retrieve the Location saved in the course config file for a course.
	 * 
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
	 * This needs to be used for all commands, just trying to think of a good
	 * way to implement it.
	 * 
	 * @param command
	 * @param arguments
	 * @return
	 */
	public final static String invalidSyntax(String command, String arguments) {
		return getTranslation("Error.Syntax").replace("%COMMAND%", command).replace("%ARGUMENTS%", arguments);
	}

	/**
	 * Retrieve the GameMode for the integer, survival being default.
	 * 
	 * @param gamemode
	 * @return
	 */
	public final static GameMode getGamemode(int gamemode) {
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
	 * This will be executed once during start up to populate the default
	 * Parkour blocks. The strength and duration will be configurable and stored
	 * against the appropriate blocks.
	 * 
	 * @return
	 */
	public final static ParkourBlocks populateParkourBlocks() {
		return populateParkourBlocks("DefaultBlocks");
	}

	public final static ParkourBlocks populateParkourBlocks(String path) {
		if (!Parkour.getParkourConfig().getConfig().contains(path)) {
			return null;
		}

		FileConfiguration config = Parkour.getParkourConfig().getConfig();
		ParkourBlocks pb = new ParkourBlocks(Material.getMaterial(config.getString(path + ".Death.Material")), Material.getMaterial(config.getString(path + ".Finish.Material")), Material.getMaterial(config.getString(path + ".Climb.Material")), Material.getMaterial(config.getString(path + ".Launch.Material")), Material.getMaterial(config.getString(path + ".Speed.Material")),
				Material.getMaterial(config.getString(path + ".Repulse.Material")), Material.getMaterial(config.getString("DefaultBlocks.NoRun.Material")), Material.getMaterial(config.getString("DefaultBlocks.NoPotion.Material")), Material.getMaterial(config.getString("DefaultBlocks.Bounce.Material")));

		return pb;
	}

	/**
	 * Used for saving the playing players Thanks to Tomsik68 for this code.
	 * 
	 * @param obj
	 * @param path
	 * @throws Exception
	 */
	public final static void saveAllPlaying(Object obj, String path) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final static Object loadAllPlaying(String path) throws Exception {
		Object result = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			result = ois.readObject();
			ois.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Different methods for displaying titles, the fallback option is to just
	 * message the player
	 * 
	 * @param player
	 * @param title
	 */
	public final static void sendTitle(Player player, String title) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerTitle(player, 5, 20, 5, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}

	public final static void sendActionBar(Player player, String title) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerActionbar(player, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}

	public final static void sendFullTitle(Player player, String title, String subTitle) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendFullTitle(player, 5, 20, 5, title, subTitle);
		else
			player.sendMessage(Static.getParkourString() + title + " " + subTitle);
	}

	public final static void sendSubTitle(Player player, String subTitle) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBarAPI())
			TitleActionBarAPI.sendPlayerSubTitle(player, 5, 20, 5, subTitle);
		else
			player.sendMessage(Static.getParkourString() + subTitle);
	}

	public final static void deleteCommand(String[] args, Player player) {
		if (args[1].equalsIgnoreCase("course")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to delete course " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "This will remove all information about the course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_COURSE, args[2].toLowerCase()));

		} else if (args[1].equalsIgnoreCase("checkpoint")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}

			Course course = CourseMethods.findByName(args[2]);
			// if it has no checkpoints
			if ((course.getCheckpoints() - 1) <= 0) {
				player.sendMessage(Static.getParkourString() + course.getName() + " has no checkpoints!");
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to delete checkpoint " + ChatColor.AQUA + (course.getCheckpoints() - 1) + ChatColor.WHITE + " for course " + ChatColor.AQUA + course.getName() + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Deleting a checkpoint will impact everybody that is currently playing on " + course.getName() + ". You should not set a course to finished and then continue to make changes.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_CHECKPOINT, args[2].toLowerCase()));

		} else if (args[1].equalsIgnoreCase("lobby")) {
			if (!Parkour.getParkourConfig().getConfig().contains(args[2].toLowerCase() + ".World")) {
				player.sendMessage(Static.getParkourString() + "This lobby does not exist!");
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to delete lobby " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Deleting a lobby will remove all information about it from the server. If any courses are linked to this lobby, they will be broken."); // TODO
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_LOBBY, args[2].toLowerCase()));

		} else {
			invalidSyntax("delete", "(course / checkpoint / lobby) (name)");
		}
	}

	public final static void resetCommand(String[] args, Player player) {
		if (args[1].equalsIgnoreCase("course")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to reset " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Resetting a course will delete all the statistics stored, which includes leaderboards and various parkour attributes. This will NOT affect the spawn / checkpoints.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.RESET_COURSE, args[2]));

		} else if (args[1].equalsIgnoreCase("player")) {
			if (Bukkit.getPlayer(args[2]) == null && Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + args[2])) {
				player.sendMessage("This player does not exist.");
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to reset player " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Resetting a player will delete all their times across all courses and delete all various parkour attributes.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.RESET_PLAYER, args[2]));

		} else {
			invalidSyntax("reset", "(course / player) (courseName / playerName)");
		}
	}

	public static String getStandardHeading(String headingText){
		return "-- " + ChatColor.BLUE + ChatColor.BOLD + headingText + ChatColor.RESET + " --";
	}
	
	
	/**
	 * Initiate a conversation using the provided API. 
	 * @param player
	 * @param type
	 */
	public static void startConversation(Player player, ConversationType type) {
		new Conversation(player, type);
	}
}
