package me.A5H73Y.Parkour.Course;

import java.util.List;
import java.util.Map;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Conversation.ParkourConversation.ConversationType;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.ParkourMode;
import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CourseMethods {

	/**
	 * This is used very often, if this could be optimized it would be awesome.
	 * I can see this becoming a problem when there are 100+ courses.
	 * 
	 * @param courseName
	 * @return
	 */
	public static boolean exist(String courseName) {
		if (courseName == null || courseName.length() == 0)
			return false;

		courseName = courseName.trim().toLowerCase();

		for (String course : Static.getCourses()) {
			if (courseName.equals(course))
				return true;
		}

		return false;
	}

	/**
	 * The main method of course retrieval, everything is populated from this
	 * method (all checkpoints & information). So with this in mind, this should
	 * only be used when necessary.
	 * 
	 * @param courseName
	 * @return
	 */
	public static Course findByName(String courseName) {
		if (!exist(courseName))
			return null;

		courseName = courseName.toLowerCase();

		Checkpoint checkpoint = CheckpointMethods.getNextCheckpoint(courseName, 0);
		Course course = new Course(courseName, checkpoint);

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".ParkourBlocks"))
			course.setParkourBlocks(Utils.populateParkourBlocks("ParkourBlocks." + Parkour.getParkourConfig().getCourseData().getString(courseName + ".ParkourBlocks")));

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".MaxDeaths"))
			course.setMaxDeaths(Parkour.getParkourConfig().getCourseData().getInt(courseName + ".MaxDeaths"));

		return course;
	}

	/**
	 * Same as the method above but uses the list index which is displayed in
	 * "/pa list courses"
	 * 
	 * @param courseNumber
	 * @return
	 */
	public static Course findByNumber(int courseNumber) {
		if (courseNumber <= 0 || courseNumber > Static.getCourses().size())
			return null;

		String courseName = Static.getCourses().get(courseNumber - 1);
		return findByName(courseName);
	}

	/**
	 * This is used several times to find out what / how the player is currently
	 * doing.
	 * 
	 * @param playerName
	 * @return
	 */
	public static Course findByPlayer(String playerName) {
		if (!PlayerMethods.isPlaying(playerName))
			return null;

		return PlayerMethods.getPlaying().get(playerName).getCourse();
	}

	/**
	 * This is the course creation. The spawn of the course is written in the
	 * courses.yml as checkpoint '0'. The world is assumed once the course is
	 * joined, so having 2 different checkpoints in 2 different worlds is not an
	 * option. We don't assume or default any options, so there is less to
	 * process on completion / join etc. (SetMinLevel, rewards etc.) All courses
	 * names are stored in a string list, which is held in memory then added /
	 * removed from.
	 * 
	 * @param args
	 * @param player
	 */
	public static void createCourse(String[] args, Player player) {
		if (!Validation.courseCreation(args, player))
			return;

		String name = args[1].toLowerCase();
		Location location = player.getLocation();
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();

		courseData.set(name + ".Creator", player.getName());
		courseData.set(name + ".Views", 0);
		courseData.set(name + ".Completed", 0);
		courseData.set(name + ".XP", 0);
		courseData.set(name + ".Points", 0);
		courseData.set(name + ".World", location.getWorld().getName());
		courseData.set(name + ".0.X", location.getBlockX() + 0.5);
		courseData.set(name + ".0.Y", location.getBlockY() + 0.5);
		courseData.set(name + ".0.Z", location.getBlockZ() + 0.5);
		courseData.set(name + ".0.Yaw", location.getYaw());
		courseData.set(name + ".0.Pitch", location.getPitch());

		List<String> courseList = Static.getCourses();
		courseList.add(name);
		Static.setCourses(courseList);
		courseData.set("Courses", courseList);
		Parkour.getParkourConfig().saveCourses();

		PlayerMethods.setSelected(player.getName(), name);

		player.sendMessage(Utils.getTranslation("Parkour.Created").replace("%COURSE%", args[1]));
		DatabaseMethods.insertCourse(name, player.getName());
	}

	/**
	 * This method only serves as a way of validating the course before joining
	 * the course as a player. This will be the only way of joining a course;
	 * The processing will be done via this message.
	 * 
	 * @param player
	 * @param courseName
	 */
	public static void joinCourse(Player player, String courseName) {
		Course course;
		if (Utils.isNumber(courseName))
			course = findByNumber(Integer.parseInt(courseName));
		else
			course = findByName(courseName);

		if (course == null) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return;
		}

		if (!Validation.courseJoining(player, course))
			return;

		PlayerMethods.playerJoin(player, course);
	}

	/**
	 * Will return a boolean whether the course is ready or not
	 * 
	 * @param courseName
	 * @return
	 */
	public static boolean isReady(String courseName) {
		return Parkour.getParkourConfig().getCourseData().getBoolean(courseName + ".Finished");
	}

	/**
	 * Displays all the information there is to know about a course. Accessed
	 * via "/pa course (course)" Will only display applicable information.
	 * 
	 * @param args
	 * @param player
	 */
	public static void displayCourseInfo(String courseName, Player player) {
		if (!exist(courseName)) {
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			return;
		}

		courseName = courseName.toLowerCase();
		ChatColor aqua = ChatColor.AQUA;

		int views = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Views");
		int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Completed");
		int checkpoints = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Points");
		int maxDeaths = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".MaxDeaths");
		int minLevel = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".MinimumLevel");
		int rewardLevel = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Level");
		int XP = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".XP");
		int parkoins = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Parkoins");
		String lobby = Parkour.getParkourConfig().getCourseData().getString(courseName + ".Lobby");
		String nextCourse = Parkour.getParkourConfig().getCourseData().getString(courseName + ".Course");
		String creator = Parkour.getParkourConfig().getCourseData().getString(courseName + ".Creator");

		double completePercent = Math.round(((completed * 1.0 / views) * 100));

		player.sendMessage(Utils.getStandardHeading(Utils.standardizeText(courseName) + " statistics"));

		player.sendMessage("Views: " + aqua + views);
		player.sendMessage("Completed: " + aqua + completed + " times (" + completePercent + "%)");
		player.sendMessage("Checkpoints: " + aqua + checkpoints);
		player.sendMessage("Creator: " + aqua + creator);

		if (minLevel > 0)
			player.sendMessage("Required level: " + aqua + maxDeaths);

		if (maxDeaths > 0)
			player.sendMessage("Max Deaths: " + aqua + maxDeaths);

		if (lobby != null && lobby.length() > 0)
			player.sendMessage("Lobby: " + aqua + lobby);

		if (nextCourse != null && nextCourse.length() > 0)
			player.sendMessage("Next course: " + aqua + nextCourse);

		if (rewardLevel > 0)
			player.sendMessage("Level Reward: " + aqua + rewardLevel);

		if (XP > 0)
			player.sendMessage("XP Reward: " + aqua + XP);

		if (parkoins > 0)
			player.sendMessage("Parkoins Reward: " + aqua + parkoins);
	}

	/**
	 * This is creating the lobby, everything is calculated based on the
	 * arguments.
	 * 
	 * @param args
	 * @param player
	 */
	public static void createLobby(String[] args, Player player) {
		String created = "Lobby ";
		setLobby(args, player);

		if (args.length > 1) {
			if (args.length > 2 && Utils.isNumber(args[2])) {
				created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created, with a required rank of " + ChatColor.DARK_AQUA + Integer.parseInt(args[2]));
				Parkour.getParkourConfig().getConfig().set("Lobby." + args[1] + ".Level", Integer.parseInt(args[2]));
			} else {
				created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created");
			}
		} else {
			Parkour.getParkourConfig().getConfig().set("Lobby.Set", true);
			created = created.concat("was successfully created!");
		}
		Parkour.getPlugin().saveConfig();
		player.sendMessage(Static.getParkourString() + created);
	}

	/**
	 * Joining the lobby. Can be accessed from the commands, and from the
	 * framework. The args will be null if it's from the framework, if this is
	 * the case we don't send them a message. (Finishing a course etc) If they
	 * join from a sign, we will have fake arguments, only [1] containing the
	 * courseName
	 * 
	 * @param args
	 * @param player
	 */
	public static void joinLobby(String[] args, Player player) {
		FileConfiguration getConfig = Parkour.getParkourConfig().getConfig();

		if (!getConfig.getBoolean("Lobby.Set")) {
			if (Utils.hasPermission(player, "Parkour.Admin")) {
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set!");
				player.sendMessage(Utils.colour("Type &b'/pa setlobby'&f where you want the lobby to be set."));
			} else {
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set! Please tell the Owner!");
			}
			return;
		}

		// variables
		Location lobby;
		boolean custom = false;
		int level = 0;

		if (args != null && args.length > 1) {
			custom = true;
			if (!getConfig.contains("Lobby." + args[1] + ".World")) {
				player.sendMessage(Static.getParkourString() + "Lobby does not exist!");
				return;
			}

			level = getConfig.getInt("Lobby." + args[1] + ".Level");

			if (level > 0 && !player.hasPermission("Parkour.Admin.MinBypass")) {
				if (Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level") < level) {
					player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replace("%LEVEL%", String.valueOf(level)));
					return;
				}

			}
			lobby = getLobby("Lobby." + args[1]);
		} else {
			// Default lobby
			lobby = getLobby("Lobby");
		}

		if (lobby == null) {
			player.sendMessage(Static.getParkourString() + "Lobby is corrupt, please investigate.");
			return;
		}
		if (PlayerMethods.isPlaying(player.getName())) {
			PlayerMethods.playerLeave(player);
		}

		player.teleport(lobby);

		// Only continue if player intentionally joined the lobby e.g /pa lobby
		if (args == null || args[0] == null)
			return;

		if (custom) {
			player.sendMessage(Utils.getTranslation("Parkour.LobbyOther").replace("%LOBBY%", args[1]));
		} else {
			player.sendMessage(Utils.getTranslation("Parkour.Lobby"));
		}
	}

	/**
	 * Based on the path passed in, will return a lobby location object from the
	 * config.
	 * 
	 * @param path
	 * @return
	 */
	private final static Location getLobby(String path) {
		World world = Bukkit.getWorld(Parkour.getParkourConfig().getConfig().getString(path + ".World"));
		double x = Parkour.getParkourConfig().getConfig().getDouble(path + ".X");
		double y = Parkour.getParkourConfig().getConfig().getDouble(path + ".Y");
		double z = Parkour.getParkourConfig().getConfig().getDouble(path + ".Z");
		float yaw = Parkour.getParkourConfig().getConfig().getInt(path + ".Yaw");
		float pitch = Parkour.getParkourConfig().getConfig().getInt(path + ".Pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * Based on the args, it will calculate whether or not it is setting a
	 * custom lobby.
	 * 
	 * @param args
	 * @param player
	 */
	private final static void setLobby(String[] args, Player player) {
		Location loc = player.getLocation();
		String path = args.length > 1 ? "Lobby." + args[1] : "Lobby";
		Parkour.getParkourConfig().getConfig().set(path + ".World", loc.getWorld().getName());
		Parkour.getParkourConfig().getConfig().set(path + ".X", loc.getX());
		Parkour.getParkourConfig().getConfig().set(path + ".Y", loc.getY());
		Parkour.getParkourConfig().getConfig().set(path + ".Z", loc.getZ());
		Parkour.getParkourConfig().getConfig().set(path + ".Pitch", loc.getPitch());
		Parkour.getParkourConfig().getConfig().set(path + ".Yaw", loc.getYaw());
		Utils.logToFile(path + " was set by " + player.getName());
	}

	/**
	 * Adds a question against the player for them to confirm. This is so they
	 * don't enter the command by accident, or something?
	 * 
	 * @param args
	 * @param player
	 */
	public static void deleteCourse(String courseName, Player player) {
		if (!exist(courseName)) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return;
		}

		courseName = courseName.toLowerCase();
		Static.getCourses().remove(courseName);
		Parkour.getParkourConfig().getCourseData().set(courseName, null);
		Parkour.getParkourConfig().getCourseData().set("Courses", Static.getCourses());
		Parkour.getParkourConfig().saveCourses();
		DatabaseMethods.deleteCourseAndReferences(courseName);

		player.sendMessage(Utils.getTranslation("Parkour.Delete").replace("%COURSE%", courseName));
	}

	public static void deleteLobby(String lobby, Player player) {
		if (!Parkour.getParkourConfig().getConfig().contains(lobby + ".World")) {
			player.sendMessage(Static.getParkourString() + "This lobby does not exist!");
			return;
		}

		Parkour.getParkourConfig().getConfig().set(lobby, null);
		Parkour.getPlugin().saveConfig();

		player.sendMessage(Static.getParkourString() + "Lobby " + lobby + " was deleted successfully.");
	}

	/**
	 * Displays all the Parkour courses or all the players using the plugin.
	 * 
	 * @param args
	 * @param player
	 */
	public static void displayList(String[] args, Player player) {
		if (args.length < 2) {
			player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
			return;
		}

		if (args[1].equalsIgnoreCase("players")) {
			if (PlayerMethods.getPlaying().size() == 0) {
				player.sendMessage(Static.getParkourString() + "Nobody is playing Parkour!");
				return;
			}

			displayPlaying(player);

		} else if (args[1].equalsIgnoreCase("courses")) {
			if (Static.getCourses().size() == 0) {
				player.sendMessage(Static.getParkourString() + "There are no Parkour courses!");
				return;
			}

			int page = (args.length == 3 && args[2] != null ? Integer.parseInt(args[2]) : 1);

			displayCourses(player, page);

		} else {
			player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
		}
	}

	private static void displayPlaying(Player player) {
		player.sendMessage(Static.getParkourString() + PlayerMethods.getPlaying().size() + " players using Parkour: ");

		// TODO pages - reuse courses page 
		for (Map.Entry<String, ParkourSession> entry : PlayerMethods.getPlaying().entrySet()) {
			player.sendMessage(Utils.getTranslation("Parkour.Playing")
					.replace("%PLAYER%", entry.getKey())
					.replace("%COURSE%", entry.getValue().getCourse().getName())
					.replace("%DEATHS%", entry.getValue().getDeaths() + "")
					.replace("%TIME%", entry.getValue().displayTime()));
		}
	}

	private static void displayCourses(Player player, int page) {
		List<String> courseList = Static.getCourses();
		if (page <= 0) {
			player.sendMessage(Static.getParkourString() + "Please enter a valid page number.");
			return;
		}

		int fromIndex = (page - 1) * 8;
		if (courseList.size() < fromIndex) {
			player.sendMessage(Static.getParkourString() + "This page doesn't exist.");
			return;
		}

		player.sendMessage(Static.getParkourString() + courseList.size() + " courses available:");
		List<String> limited = courseList.subList(fromIndex, Math.min(fromIndex + 8, courseList.size()));

		for (int i = 0; i < limited.size(); i++) {
			player.sendMessage(((fromIndex) + (i + 1)) + ") " + ChatColor.AQUA + limited.get(i));
		}

		player.sendMessage("== " + page + " / " + ((courseList.size() / 8) + 1) + " ==");
	}

	/**
	 * This is for functions that don't require a course parameter, for example
	 * creating a checkpoint. It will essentially store a 'selected' course
	 * against a player, so it will remember which course you are editing.
	 * 
	 * @param args
	 * @param player
	 */
	public static void selectCourse(String[] args, Player player) {
		if (!exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		String courseName = args[1].trim().toLowerCase();
		player.sendMessage(Static.getParkourString() + "Now Editing: " + ChatColor.AQUA + args[1]);
		Integer pointcount = Parkour.getParkourConfig().getCourseData().getInt((courseName + "." + "Points"));
		player.sendMessage(Static.getParkourString() + "Checkpoints: " + ChatColor.AQUA + pointcount);
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", courseName);
		Parkour.getParkourConfig().saveUsers();
	}

	/**
	 * This is now the main way of modifying a course now. Most things will no
	 * longer require a course argument, but instead use the course you are
	 * editing.
	 * 
	 * @param args
	 * @param player
	 */
	public static void deselectCourse(String[] args, Player player) {
		if (Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + player.getName() + ".Selected")) {
			Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", null);
			Parkour.getParkourConfig().saveUsers();
			player.sendMessage(Static.getParkourString() + "Finished editing.");
		} else {
			player.sendMessage(Utils.getTranslation("Error.Selected"));
		}
	}

	/**
	 * Set the prize that the player gets awarded with when the complete the
	 * course. Can be an item with any amount and/or a command.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setPrize(String[] args, Player player) {
		if (!exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		PlayerMethods.setSelected(player.getName(), args[1]);
		Utils.startConversation(player, ConversationType.COURSEPRIZE);
	}

	/**
	 * Below methods are self explanatory, they will increase the view and
	 * completion count when appropriate.
	 * 
	 * @param courseName
	 */
	public static void increaseComplete(String courseName) {
		int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Completed");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Completed", completed += 1);
		Parkour.getParkourConfig().saveCourses();
	}

	public static void increaseView(String courseName) {
		int views = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Views");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Views", views += 1);
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Overwrite the original start of a course.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setStart(String[] args, Player player) {
		String selected = PlayerMethods.getSelected(player.getName());

		if (!CourseMethods.exist(selected)) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
			return;
		}

		selected = selected.toLowerCase();
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.X", player.getLocation().getX());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Y", player.getLocation().getY());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Z", player.getLocation().getZ());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Yaw", player.getLocation().getYaw());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Pitch", player.getLocation().getPitch());
		Utils.logToFile(selected + " spawn was reset by " + player.getName());
		player.sendMessage(Static.getParkourString() + "Spawn for " + ChatColor.AQUA + selected + ChatColor.WHITE + " has been set to your position");
	}

	/**
	 * Overwrite the original Author of the course.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setCreator(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".Creator", args[2]);
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + "Creator of " + ChatColor.DARK_AQUA + args[1] + ChatColor.WHITE + " was set to " + ChatColor.AQUA + args[2]);
	}

	/**
	 * Set the maximum amount of deaths the player has before automatically
	 * leaving the course.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setMaxDeaths(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			player.sendMessage(Static.getParkourString() + "Amount of deaths is not valid.");
			return;
		}

		player.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum deaths was set to " + ChatColor.AQUA + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".MaxDeaths", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Set the mimimum Parkour level required to join a course.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setMinLevel(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			player.sendMessage(Static.getParkourString() + "Minimum level is not valid.");
			return;
		}

		player.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " minimum level requirement was set to " + ChatColor.AQUA + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".MinimumLevel", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * This is the Parkour level the player gets awarded with on course
	 * completion.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setRewardLevel(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			player.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".Level", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + args[1] + "'s reward level was set to " + ChatColor.AQUA + args[2]);
	}

	/**
	 * Toggle the boolean whether or not the course rewards only one time
	 * 
	 * @param args
	 * @param player
	 */
	public static void setRewardOnce(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		if (Parkour.getParkourConfig().getCourseData().getBoolean(args[1].toLowerCase() + ".RewardOnce")) {
			Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".RewardOnce", false);
			player.sendMessage(Static.getParkourString() + args[1] + "'s reward one time was set to " + ChatColor.AQUA + "false");
		} else {
			Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".RewardOnce", true);
			player.sendMessage(Static.getParkourString() + args[1] + "'s reward one time was set to " + ChatColor.AQUA + "true");
		}
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Not to be confused with rewardLevel or rewardPrize, this associates a
	 * Parkour level with a message prefix. A rank is just a visual prefix. E.g.
	 * Level 10: Pro; Level 99: God
	 * 
	 * @param args
	 * @param player
	 */
	public static void setRewardRank(String[] args, Player player) {
		if (!Utils.isNumber(args[1])) {
			player.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		if (args[2] == null || args[2].trim().length() == 0) {
			player.sendMessage(Static.getParkourString() + "Rank is not valid");
			return;
		}

		Parkour.getParkourConfig().getUsersData().set("ServerInfo.Levels." + args[1] + ".Rank", args[2]);
		Parkour.getParkourConfig().saveUsers();
		player.sendMessage(Static.getParkourString() + "Level " + args[1] + "'s rank was set to " + Utils.colour(args[2]));
	}

	/**
	 *
	 * @param args
	 * @param player
	 */
	public static void setRewardParkoins(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			player.sendMessage(Static.getParkourString() + "Parkoins reward needs to be numeric.");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".Parkoins", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + args[1] + "'s parkoins reward was set to " + ChatColor.AQUA + args[2]);
	}

	/**
	 * Sets the Parkours course status to Complete, otherwise the player will
	 * get a notification saying it's still not finished on joining.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setFinish(String[] args, Player player) {
		String courseName = PlayerMethods.getSelected(player.getName());

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".Finished")) {
			player.sendMessage(Static.getParkourString() + ChatColor.AQUA + courseName + ChatColor.WHITE + " has already been set to finished!");
			return;
		}

		Location location = player.getLocation();
		location.setY(location.getBlockY() - 1);
		Block block = location.getBlock();
		block.setType(Static.getParkourBlocks().getFinish());

		Parkour.getParkourConfig().getCourseData().set(courseName + ".Finished", true);
		Parkour.getParkourConfig().saveCourses();

		player.sendMessage(Utils.getTranslation("Parkour.Finish").replace("%COURSE%", courseName));
		Utils.logToFile(courseName + " was set to finished by " + player.getName());
	}

	public static void linkCourse(String[] args, Player player) {
		String selected = PlayerMethods.getSelected(player.getName());

		if (args[1].equalsIgnoreCase("course")) {
			if (!CourseMethods.exist(args[2])) {
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}

			if (Parkour.getParkourConfig().getCourseData().contains(selected + ".LinkedLobby")) {
				player.sendMessage(Static.getParkourString() + "This course is linked to a lobby!");
				return;
			}

			Parkour.getParkourConfig().getCourseData().set(selected + ".LinkedCourse", args[2].toLowerCase());
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is now linked to " + ChatColor.AQUA + args[2]);

		} else if (args[1].equalsIgnoreCase("lobby")) {
			if (!Parkour.getParkourConfig().getConfig().contains("Lobby." + args[2] + ".World")) { // TODO
				player.sendMessage(Static.getParkourString() + "Lobby " + args[2] + " does not exist.");
				return;
			}

			if (Parkour.getParkourConfig().getCourseData().contains(selected + ".LinkedCourse")) {
				player.sendMessage(Static.getParkourString() + "This course is linked to a course!");
				return;
			}

			Parkour.getParkourConfig().getCourseData().set(selected + ".LinkedLobby", args[2]);
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is now linked to " + ChatColor.AQUA + args[2]);

		} else {
			player.sendMessage(Utils.invalidSyntax("Link", "(course / lobby) (courseName / lobbyName)"));
		}

	}

	/**
	 * Resets all the statistics to 0 as if the course was just created. None of
	 * the structure of the course changes, all checkpoints will remain
	 * untouched. All times will deleted from the database.
	 * 
	 * @param courseName
	 */
	public final static void resetCourse(String courseName) {
		if (!exist(courseName))
			return;

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

	public static void rateCourse(String[] args, Player player) {
		String courseName = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".LastPlayed");

		if (!CourseMethods.exist(courseName)) {
			player.sendMessage(Static.getParkourString() + "Invalid course.");
			return;
		}

		if (DatabaseMethods.hasVoted(courseName, player.getName())){
			player.sendMessage(Static.getParkourString() + "You have already voted for " + ChatColor.AQUA + courseName);
			return;
		}

		if (args[0].equalsIgnoreCase("like")) {
			DatabaseMethods.insertVote(courseName, player.getName(), true);
			player.sendMessage(Static.getParkourString() + "You " + ChatColor.GREEN + "liked " + ChatColor.WHITE + courseName); 

		} else if (args[0].equalsIgnoreCase("dislike")) {
			DatabaseMethods.insertVote(courseName, player.getName(), false);
			player.sendMessage(Static.getParkourString() + "You " + ChatColor.RED + "disliked " + ChatColor.WHITE + courseName); 

		}
	}

	public static void linkParkourBlocks(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			return;
		}
		if (!Parkour.getParkourConfig().getConfig().contains("ParkourBlocks." + args[2].toLowerCase())) {
			player.sendMessage(Static.getParkourString() + "ParkourBlocks doesn't exist!");
			return;
		}
		if (Parkour.getParkourConfig().getCourseData().contains(args[1].toLowerCase() + ".ParkourBlocks")) {
			player.sendMessage(Static.getParkourString() + "This course is already linked to a ParkourBlocks, continuing anyway...");
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".ParkourBlocks", args[2].toLowerCase());
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + args[1] + " is now linked to ParkourBlocks " + args[2]);
	}

	public static void challengePlayer(String[] args, Player player) {
		//(course) (player)
		if (!Validation.challengePlayer(args, player))
			return;

		Player target = Bukkit.getPlayer(args[2]);
		String courseName = args[1].toLowerCase();

		target.sendMessage(Utils.getTranslation("Parkour.ChallengeReceive").replace("%PLAYER%", player.getName()).replace("%COURSE%", courseName));
		target.sendMessage(Utils.getTranslation("Parkour.Accept", false));
		player.sendMessage(Utils.getTranslation("Parkour.ChallengeSend").replace("%PLAYER%", target.getName()).replace("%COURSE%", courseName));
		Static.addChallenge(new Challenge(player.getName(), target.getName(), courseName));
	}

	public static void setJoinItem(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (Material.getMaterial(args[2].toUpperCase()) == null) {
			player.sendMessage(Static.getParkourString() + "Invalid material: " + args[2].toUpperCase());
			return;
		}
		if (!Utils.isNumber(args[3])) {
			player.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		int amount = Utils.parseMaterialAmount(args[3]);

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".JoinItemMaterial", args[2].toUpperCase());
		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".JoinItemAmount", amount);
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + "Join item for " + args[1] + " set to " + args[2] + " (" + amount + ")");
	}

	public static void getLeaderboards(String[] args, Player player) {
		if (args.length == 1) {
			Utils.startConversation(player, ConversationType.LEADERBOARD);
			return;
		}
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		
		int limit = 5;
		boolean personal = true;
		
		if (args.length >= 4) {
			if (args[3].equalsIgnoreCase("global")){
				personal = false;
			} else {
				player.sendMessage(Static.getParkourString() + "Defaulting to 'personal', alternative option: 'global'");
			}
		}
		
		if (args.length >= 3) {
			if (!Utils.isNumber(args[2])) {
				player.sendMessage(Static.getParkourString() + "Amount of results needs to be numeric.");
				return;
			}
			limit = Integer.parseInt(args[2]);
		}
		
		if (personal) {
			Utils.displayLeaderboard(DatabaseMethods.getTopPlayerCourseResults(player.getName(), args[1], limit), player);
		} else {
			Utils.displayLeaderboard(DatabaseMethods.getTopCourseResults(args[1], limit), player);
		}
	}

	public static void setCourseMode(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		
		if (args[2].equalsIgnoreCase("freedom")) {
			setCourseMode(args[1], "freedom", player);
			
		} else if (args[2].equalsIgnoreCase("drunk")) {
			setCourseMode(args[1], "drunk", player);
			
		} else if (args[2].equalsIgnoreCase("darkness")) {
			setCourseMode(args[1], "darkness", player);
			
		} else {
			player.sendMessage(Utils.invalidSyntax("setmode", "(course) (freedom / drunk / darkness)"));
		}
	}

	private static void setCourseMode(String courseName, String mode, Player player) {
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Mode", mode);
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Utils.getTranslation("Parkour.SetMode").replace("%COURSE%", courseName).replace("%MODE%", mode));
	}
	
	public static ParkourMode getCourseMode(String courseName) {
		String mode = Parkour.getParkourConfig().getCourseData().getString(courseName + ".Mode");
		
		if ("freedom".equalsIgnoreCase(mode)) {
			return ParkourMode.FREEDOM;
			
		} else if ("drunk".equalsIgnoreCase(mode)) {
			return ParkourMode.DRUNK;
			
		} else if ("darkness".equalsIgnoreCase(mode))
			return ParkourMode.DARKNESS;
		
		return ParkourMode.NONE;
	}
}