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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.Checkpoint;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Enums.QuestionType;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Other.Question;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Other.Validation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.connorlinfoot.bountifulapi.BountifulAPI;

public final class Utils {

	/**
	 * The string parameter will be matched to an entry in the Strings.yml
	 * The boolean will decide to display the Parkour prefix
	 * 
	 * @param string to translate
	 * @param display Parkour prefix?
	 * @return String of appropriate translation 
	 */
	public final static String getTranslation(String string, boolean prefix) {
		String translated = Parkour.getParkourConfig().getStringData().getString(string);
		translated = translated != null ? colour(translated) : "String not found: " + string;
		return prefix ? Static.getParkourString().concat(translated) : translated;
	}

	/**
	 * Override method, but with a default of an enabled Parkour prefix.
	 * 
	 * @param string
	 * @return String of appropriate translation 
	 */
	public final static String getTranslation(String string) {
		return getTranslation(string, true);
	}

	/**
	 * Return whether or not the player has a specific permission.
	 * If they don't, a message will be sent alerting them.
	 * 
	 * @param player
	 * @param permission
	 * @return boolean
	 */
	public final static boolean hasPermission(Player player, String permission) {
		if (player.hasPermission(permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
		return false;
	}

	/**
	 * Return whether the player has a specific permission OR has the branch permission. 
	 * Example "parkour.basic.join" OR "parkour.basic.*"
	 * 
	 * @param player
	 * @param permissionBranch
	 * @param permission
	 * @return boolean
	 */
	public final static boolean hasPermission(Player player, String permissionBranch, String permission) {
		if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*"))
			return true;

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
		return false;
	}

	/**
	 * Check if the player has the permission, if not were they the creator the course in question. 
	 * Example, they may not be an admin but they can change the start location of their own course.
	 * 
	 * @param player
	 * @param permissionBranch
	 * @param permission
	 * @param courseName
	 * @return boolean
	 */
	public final static boolean hasPermissionOrCourseOwnership(Player player, String permissionBranch, String permission, String courseName) {
		if (!(CourseMethods.exist(courseName))) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return false;

		} else if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*")) {
			return true;

		} else if (player.getName().equals(Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Creator"))) {
			return true;
		}

		player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
		return false;
	}
	
	/**
	 * Return whether or not the player has a specific sign permission.
	 * The sign will be broken if they don't have permission.
	 * 
	 * @param player
	 * @param permission
	 * @return boolean
	 */
	public static boolean hasSignPermission(Player player, SignChangeEvent sign, String permission){
		if (!Utils.hasPermission(player, "Parkour.Sign", permission)){
			sign.setCancelled(true);
			sign.getBlock().breakNaturally();
			return false;
		}
		return true;
	}

	/**
	 * Validate the length of the arguments before allowing it to be processed further.
	 * 
	 * @param sender
	 * @param args
	 * @param desired
	 * @return boolean
	 */
	public final static boolean validateArgs(CommandSender sender, String[] args, int desired) {
		if (args.length > desired) {
			sender.sendMessage(Utils.getTranslation("Error.TooMany") + " (" + desired + ")");
			sender.sendMessage(Utils.getTranslation("Help.Command").replace("%COMMAND%", Utils.standardizeText(args[0])));
			return false;

		} else if (args.length < desired) {
			sender.sendMessage(Utils.getTranslation("Error.TooLittle") + " (" + desired + ")");
			sender.sendMessage(Utils.getTranslation("Help.Command").replace("%COMMAND%", Utils.standardizeText(args[0])));
			return false;
		}
		return true;
	}

	/**
	 * Format and standardize text to a constant case.
	 * Will turn "hElLO" into "Hello"
	 * 
	 * @param text
	 * @return String
	 */
	public final static String standardizeText(String text) {
		if (text == null || text.length() == 0) {
			return text;
		}
		return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
	}

	/**
	 * Returns if the argument is numeric
	 * "1" - true, "Hi" - false
	 * 
	 * @param args
	 * @return boolean
	 */
	public final static boolean isNumber(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Converts text to the appropriate colours
	 * "&4Hello" is the same as ChatColor.RED + "Hello"
	 * 
	 * @param text
	 * @return
	 */
	public final static String colour(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	/**
	 * The input will be broken down into seconds, minutes and hours. If the
	 * time is not greater than an hour, hours will not be displayed.
	 * 
	 * @param milliseconds
	 * @return formatted time: HH:MM:SS
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
	 * Used for logging plugin events, varying in severity. 
	 * 0 - Info; 1 - Warn; 2 - Severe.
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

	/**
	 * Default level of logging (INFO)
	 * 
	 * @param message
	 */
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
	 * If the incorrect command usage is entered, an error message defined in the strings.yml
	 * will be displayed with the appropriate parameters.
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
	 * @return GameMode
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
	 * @return ParkourBlocks
	 */
	public final static ParkourBlocks populateDefaultParkourBlocks() {
		ParkourBlocks parkourBlocks = new ParkourBlocks(
				getParkourMaterial("DefaultBlocks.Death.Material"), 
				getParkourMaterial("DefaultBlocks.Finish.Material"), 
				getParkourMaterial("DefaultBlocks.Climb.Material"), 
				getParkourMaterial("DefaultBlocks.Launch.Material"), 
				getParkourMaterial("DefaultBlocks.Speed.Material"),
				getParkourMaterial("DefaultBlocks.Repulse.Material"), 
				getParkourMaterial("DefaultBlocks.NoRun.Material"), 
				getParkourMaterial("DefaultBlocks.NoPotion.Material"), 
				getParkourMaterial("DefaultBlocks.Bounce.Material"));

		return parkourBlocks;
	}

	/**
	 * Retrieve a custom ParkourBlocks set from the config. 
	 * Some of the items will be set to the default ParkourBlocks.
	 * 
	 * @param name
	 * @return
	 */
	public final static ParkourBlocks populateParkourBlocks(String name) {
		if (!Parkour.getParkourConfig().getConfig().contains(name + ".Death.Material"))
			return null;

		ParkourBlocks parkourBlocks = new ParkourBlocks(
				getParkourMaterial(name + ".Death.Material"), 
				getParkourMaterial(name + ".Finish.Material"), 
				getParkourMaterial(name + ".Climb.Material"), 
				getParkourMaterial(name + ".Launch.Material"), 
				getParkourMaterial(name + ".Speed.Material"),
				getParkourMaterial(name + ".Repulse.Material"), 
				Static.getParkourBlocks().getNorun(), 
				Static.getParkourBlocks().getNopotion(), 
				Static.getParkourBlocks().getBounce());

		return parkourBlocks;
	}

	/**
	 * Used for identifying what the problem with the ParkourBlocks is.
	 * 
	 * @param name
	 * @param player
	 */
	public final static void validateParkourBlocks(String[] args, Player player){
		String name = (args.length == 2 ? "ParkourBlocks." + args[1].toLowerCase() : "DefaultBlocks");

		if (!Parkour.getParkourConfig().getConfig().contains(name + ".Death.Material")) {
			player.sendMessage("ParkourBlocks " + name + " doesn't exist!");
			return;
		}

		List<String> invalidTypes = new ArrayList<String>();
		String[] types = {"Death", "Finish", "Climb", "Launch", "Speed", "Repulse", "NoRun", "NoPotion", "Bounce"};

		for (String type : types){
			if (getParkourMaterial(name + "." + type + ".Material") == null)
				invalidTypes.add(type);
		}

		player.sendMessage(Static.getParkourString() + invalidTypes.size() + " problems with " + ChatColor.AQUA + name + ChatColor.WHITE + " found.");
		if (invalidTypes.size() > 0){
			String message = ChatColor.RED + "";
			for (String type : invalidTypes){
				message = message.concat(type + " ");
			}
			player.sendMessage(message);
		}
	}

	public static Material getParkourMaterial(String path){
		String materialName = Parkour.getParkourConfig().getConfig().getString(path);

		if (materialName == null || materialName.equals("DEFAULT"))
			return null;

		return Material.getMaterial(materialName.toUpperCase());
	}

	/**
	 * Used for saving the playing players Thanks to Tomsik68 for this code.
	 * 
	 * @param obj
	 * @param path
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
		if (Static.getBountifulAPI())
			BountifulAPI.sendTitle(player, 5, 20, 5, title, "");
		else
			player.sendMessage(Static.getParkourString() + title);
	}

	public final static void sendActionBar(Player player, String title) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBountifulAPI())
			BountifulAPI.sendActionBar(player, title);
		else
			player.sendMessage(Static.getParkourString() + title);
	}

	public final static void sendFullTitle(Player player, String title, String subTitle) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBountifulAPI())
			BountifulAPI.sendTitle(player, 5, 20, 5, title, subTitle);
		else
			player.sendMessage(Static.getParkourString() + title + " " + subTitle);
	}

	public final static void sendSubTitle(Player player, String subTitle) {
		if (Static.containsQuiet(player.getName()))
			return;
		if (Static.getBountifulAPI())
			BountifulAPI.sendTitle(player, 5, 20, 5, "", subTitle);
		else
			player.sendMessage(Static.getParkourString() + subTitle);
	}

	/**
	 * Delete method, possible arguments are course, checkpoint and lobby
	 * This will only add a Question object with the relevant data until the player confirms the action later on.
	 * 
	 * @param args
	 * @param player
	 */
	public final static void deleteCommand(String[] args, Player player) {
		if (args[1].equalsIgnoreCase("course")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}
			
			if (!Validation.deleteCourse(args[2], player))
				return;

			player.sendMessage(Static.getParkourString() + "You are about to delete course " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "This will remove all information about the course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_COURSE, args[2].toLowerCase()));

		} else if (args[1].equalsIgnoreCase("checkpoint")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}
			
			int checkpoints = Parkour.getParkourConfig().getCourseData().getInt(args[2].toLowerCase() + ".Points");
			// if it has no checkpoints
			if (checkpoints <= 0) {
				player.sendMessage(Static.getParkourString() + args[2] + " has no checkpoints!");
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to delete checkpoint " + ChatColor.AQUA + checkpoints + ChatColor.WHITE + " for course " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Deleting a checkpoint will impact everybody that is currently playing on " + args[2] + ". You should not set a course to finished and then continue to make changes.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_CHECKPOINT, args[2].toLowerCase()));

		} else if (args[1].equalsIgnoreCase("lobby")) {
			if (!Parkour.getParkourConfig().getConfig().contains("Lobby." + args[2].toLowerCase() + ".World")) {
				player.sendMessage(Static.getParkourString() + "This lobby does not exist!");
				return;
			}
			
			if (!Validation.deleteLobby(args[2], player))
				return;

			player.sendMessage(Static.getParkourString() + "You are about to delete lobby " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Deleting a lobby will remove all information about it from the server. If any courses are linked to this lobby, they will be broken.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_LOBBY, args[2].toLowerCase()));

		} else {
			invalidSyntax("delete", "(course / checkpoint / lobby) (name)");
		}
	}

	/**
	 * Reset method, possible arguments are course and player
	 * This will only add a Question object with the relevant data until the player confirms the action later on.
	 * 
	 * @param args
	 * @param player
	 */
	public final static void resetCommand(String[] args, Player player) {
		if (args[1].equalsIgnoreCase("course")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}

			player.sendMessage(Static.getParkourString() + "You are about to reset " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
			player.sendMessage(ChatColor.GRAY + "Resetting a course will delete all the statistics stored, which includes leaderboards and various parkour attributes. This will NOT affect the spawn / checkpoints.");
			player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
			Static.addQuestion(player.getName(), new Question(QuestionType.RESET_COURSE, args[2].toLowerCase()));

		} else if (args[1].equalsIgnoreCase("player")) {
			if (Bukkit.getPlayer(args[2]) == null && Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + args[2])) {
				player.sendMessage(Utils.getTranslation("Error.UnknownPlayer"));
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

	/**
	 * Return the standardised heading for Parkour
	 * @param headingText
	 * @return String
	 */
	public static String getStandardHeading(String headingText){
		return "-- " + ChatColor.BLUE + ChatColor.BOLD + headingText + ChatColor.RESET + " --";
	}

	/**
	 * Validate amount of Material
	 * Must be between 1 and 64.
	 * @param amountString
	 * @return int
	 */
	public static int parseMaterialAmount(String amountString) {
		int amount = Integer.parseInt(amountString);
		return amount < 1 ? 1 : amount > 64 ? 64 : amount;
	}

	/**
	 * Display Leaderboards
	 * Present the course times to the player.
	 * @param times
	 * @param player
	 */
	public static void displayLeaderboard(Player player, List<TimeObject> times) {
		if (times.size() == 0) {
			player.sendMessage(Static.getParkourString() + "No results were found!");
			return;
		}

		player.sendMessage(Utils.getStandardHeading(times.size() + " results"));
		for (int i = 0; i < times.size(); i++) {
			player.sendMessage(Utils.colour((i + 1) + ") &b" + times.get(i).getPlayer() + "&f in &3" + Utils.calculateTime(times.get(i).getTime()) + "&f, dying &7" + times.get(i).getDeaths() + " &ftimes"));
		}
	}

	/**
	 * Get a list of possible ParkourKits
	 * @return
	 */
	public static List<String> getParkourBlockList() {
		return new ArrayList<String>(Parkour.getParkourConfig().getConfig().getConfigurationSection("ParkourBlocks").getKeys(false));
	}

	/**
	 * Create a new checkpoint based on the players current location.
	 * @param player
	 * @return
	 */
	public static Checkpoint getCheckpointOfCurrentPosition(Player player) {
		return new Checkpoint(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), player.getLocation().getWorld().getName(), 0, 0, 0);
	}

	/**
	 * Get an ItemStack for the material with a display name of a translated message
	 * @param translation
	 * @param material
	 * @return ItemStack
	 */
	public static ItemStack getItemStack(String translation, Material material) {
		ItemStack item = new ItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.getTranslation(translation, false));
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Delay certain actions 
	 * @param player
	 * @param secondsToWait
	 * @param displayMessage
	 * @return boolean (wait expired)
	 */
	public static boolean delayPlayer(Player player, int secondsToWait, boolean displayMessage) {
		if (player.isOp())
			return true;

		if (!Static.getDelay().containsKey(player.getName())) {
			Static.getDelay().put(player.getName(), System.currentTimeMillis());
			return true;
		} 

		long lastAction = Static.getDelay().get(player.getName());
		int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

		if (secondsElapsed >= secondsToWait) {
			Static.getDelay().put(player.getName(), System.currentTimeMillis());
			return true;
		}

		if (displayMessage && !Static.containsQuiet(player.getName()))
			player.sendMessage(Utils.getTranslation("Error.Cooldown").replace("%AMOUNT%", String.valueOf(secondsToWait - secondsElapsed)));

		return false;
	}

	/**
	 * Add a whitelisted command
	 * @param args
	 * @param player
	 */
	public static void addWhitelistedCommand(String[] args, Player player) {
		if (Static.getWhitelistedCommands().contains(args[1].toLowerCase())) {
			player.sendMessage(Static.getParkourString() + "This command is already whitelisted!");
			return;
		}
		
		Static.addWhitelistedCommand(args[1].toLowerCase());
		player.sendMessage(Static.getParkourString() + "Command " + ChatColor.AQUA + args[1] + ChatColor.WHITE + " added to the whitelisted commands!");
	}
}
