package me.A5H73Y.Parkour.Course;

import java.util.List;
import java.util.Map;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Conversation.ParkourConversation;
import me.A5H73Y.Parkour.Enums.ConversationType;
import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.ParkourKit;
import me.A5H73Y.Parkour.Other.ValidationMethods;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class CourseMethods {

	/**
	 * Check if a course exists on the server
	 * 
	 * @param courseName
	 * @return boolean
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
	 * Retrieve a course based on its unique name.
	 * Everything is populated from this method (all checkpoints & information). 
	 * This should only be used when necessary.
	 * 
	 * @param courseName
	 * @return Course
	 */
	public static Course findByName(String courseName) {
		if (!exist(courseName))
			return null;

		courseName = courseName.toLowerCase();

		Checkpoint checkpoint = CheckpointMethods.getNextCheckpoint(courseName, 0);
		Course course = new Course(courseName, checkpoint);

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".ParkourKit")) {
			String name = Parkour.getParkourConfig().getCourseData().getString(courseName + ".ParkourKit");
			ParkourKit kit = ParkourKit.getParkourKit(name);
			if (kit != null)
				course.setParkourKit(kit);
		}
			
		int maxDeaths = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".MaxDeaths", 0);
		if (maxDeaths > 0)
			course.setMaxDeaths(maxDeaths);

		return course;
	}

	/**
	 * Retrieve a course based on its unique ID.
	 * The ID will be based on where it is in the list of courses, meaning it can change.
	 * Find the current ID using "/pa list courses"
	 * 
	 * @param courseNumber
	 * @return Course
	 */
	public static Course findByNumber(int courseNumber) {
		if (courseNumber <= 0 || courseNumber > Static.getCourses().size())
			return null;

		String courseName = Static.getCourses().get(courseNumber - 1);
		return findByName(courseName);
	}

	/**
	 * Retrieve a course based on the ParkourSession for a player.
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
	 * Create a new Parkour course, given a unique name.
	 * The start of the course is located in courses.yml as checkpoint '0'. 
	 * The world is assumed once the course is joined, so having 2 different checkpoints in 2 different worlds is not an option. 
	 * All course names are stored in a string list, which is also held in memory.
	 * Course is entered into the database so leaderboards can be associated with it.
	 * 
	 * @param args
	 * @param player
	 */
	public static void createCourse(String[] args, Player player) {
		if (!ValidationMethods.courseCreation(args, player))
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

		Static.getCourses().add(name);
		courseData.set("Courses", Static.getCourses());
		Parkour.getParkourConfig().saveCourses();

		PlayerMethods.setSelected(player.getName(), name);

		player.sendMessage(Utils.getTranslation("Parkour.Created").replace("%COURSE%", args[1]));
		DatabaseMethods.insertCourse(name, player.getName());
	}

	/**
	 * This method serves as validation for the player to join the course.
	 * Once validated, the player then joins the course via the PlayerMethods class.
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

		if (!ValidationMethods.courseJoining(player, course))
			return;

		PlayerMethods.playerJoin(player, course);
	}

	/**
	 * Returns whether the course is ready or not.
	 * 
	 * @param courseName
	 * @return boolean
	 */
	public static boolean isReady(String courseName) {
		return Parkour.getParkourConfig().getCourseData().getBoolean(courseName + ".Finished");
	}

	/**
	 * Displays all the information stored about a course. 
	 * Accessed via "/pa stats (course)", will only display applicable information.
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
		FileConfiguration config = Parkour.getParkourConfig().getCourseData();
		FileConfiguration econ = Parkour.getParkourConfig().getEconData();
		ChatColor aqua = ChatColor.AQUA;

		int views = config.getInt(courseName + ".Views");
		int completed = config.getInt(courseName + ".Completed");
		int checkpoints = config.getInt(courseName + ".Points");
		int maxDeaths = config.getInt(courseName + ".MaxDeaths");
		int minLevel = config.getInt(courseName + ".MinimumLevel");
		int rewardLevel = config.getInt(courseName + ".Level");
		int rewardLevelAdd = config.getInt(courseName + ".LevelAdd");
		int XP = config.getInt(courseName + ".XP");
		int parkoins = config.getInt(courseName + ".Parkoins");
		int joinFee = econ.getInt("Price." + courseName + ".JoinFee");
		String linkedLobby = config.getString(courseName + ".LinkedLobby");
		String linkedCourse = config.getString(courseName + ".LinkedCourse");
		String creator = config.getString(courseName + ".Creator");
		boolean finished = config.getBoolean(courseName + ".Finished");
		String parkourKit = config.getString(courseName + ".ParkourKit");
		String mode = config.getString(courseName + ".Mode");

		double completePercent = Math.round(((completed * 1.0 / views) * 100));

		player.sendMessage(Utils.getStandardHeading(Utils.standardizeText(courseName) + " statistics"));

		player.sendMessage("Views: " + aqua + views);
		player.sendMessage("Completed: " + aqua + completed + " times (" + completePercent + "%)");
		player.sendMessage("Checkpoints: " + aqua + checkpoints);
		player.sendMessage("Creator: " + aqua + creator);
		player.sendMessage("Finished: " + aqua + finished);

		if (minLevel > 0)
			player.sendMessage("Required level: " + aqua + minLevel);

		if (maxDeaths > 0)
			player.sendMessage("Max Deaths: " + aqua + maxDeaths);

		if (linkedLobby != null && linkedLobby.length() > 0)
			player.sendMessage("Linked Lobby: " + aqua + linkedLobby);

		if (linkedCourse != null && linkedCourse.length() > 0)
			player.sendMessage("Linked Course: " + aqua + linkedCourse);

		if (rewardLevel > 0)
			player.sendMessage("Level Reward: " + aqua + rewardLevel);
		
		if (rewardLevelAdd > 0)
			player.sendMessage("Level Reward Addon: " + aqua + rewardLevelAdd);

		if (XP > 0)
			player.sendMessage("XP Reward: " + aqua + XP);

		if (parkoins > 0)
			player.sendMessage("Parkoins Reward: " + aqua + parkoins);
		
		if (parkourKit != null && parkourKit.length() > 0)
			player.sendMessage("ParkourKit: " + aqua + parkourKit);
		
		if (mode != null && !"none".equalsIgnoreCase(mode))
			player.sendMessage("Mode: " + aqua + mode);
		
		if (joinFee > 0)
			player.sendMessage("Join Fee: " + aqua + joinFee);

		double likePercent = Math.round(DatabaseMethods.getVotePercent(courseName));

		if (likePercent > 0)
			player.sendMessage("Liked: " + aqua + likePercent + "%");
	}

	/**
	 * Delete a course
	 * Remove all information stored on the server about the course, including all references from the database. 
	 * 
	 * @param courseName
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

	/**
	 * Displays all the Parkour courses or all the players using the plugin.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void displayList(String[] args, CommandSender sender) {
		if (args.length < 2) {
			sender.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
			return;
		}

		if (args[1].equalsIgnoreCase("players")) {
			displayPlaying(sender);

		} else if (args[1].equalsIgnoreCase("courses")) {
			int page = (args.length == 3 && args[2] != null ? Integer.parseInt(args[2]) : 1);
			displayCourses(sender, page);

		} else {
			sender.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
		}
	}

	/**
	 * Display a list of all the players using the Parkour plugin
	 * Will show the course they are on, the amount of times they've died, as well as how long they've been on the course.
	 * 
	 * @param sender
	 */
	private static void displayPlaying(CommandSender sender) {
		if (PlayerMethods.getPlaying().size() == 0) {
			sender.sendMessage(Static.getParkourString() + "Nobody is playing Parkour!");
			return;
		}

		sender.sendMessage(Static.getParkourString() + PlayerMethods.getPlaying().size() + " players using Parkour: ");

		// TODO pages - reuse courses page 
		String playingTemplate = Utils.getTranslation("Parkour.Playing", false);
		for (Map.Entry<String, ParkourSession> entry : PlayerMethods.getPlaying().entrySet()) {
			sender.sendMessage(playingTemplate
					.replace("%PLAYER%", entry.getKey())
					.replace("%COURSE%", entry.getValue().getCourse().getName())
					.replace("%DEATHS%", String.valueOf(entry.getValue().getDeaths()))
					.replace("%TIME%", entry.getValue().displayTime()));
		}
	}

	/**
	 * Display a list of all the Parkour courses on the server
	 * Prints the name and unique course ID, seperated into pages of 8 results.
	 * 
	 * @param sender
	 * @param page
	 */
	private static void displayCourses(CommandSender sender, int page) {
		if (Static.getCourses().size() == 0) {
			sender.sendMessage(Static.getParkourString() + "There are no Parkour courses!");
			return;
		}

		List<String> courseList = Static.getCourses();
		if (page <= 0) {
			sender.sendMessage(Static.getParkourString() + "Please enter a valid page number.");
			return;
		}

		int fromIndex = (page - 1) * 8;
		if (courseList.size() < fromIndex) {
			sender.sendMessage(Static.getParkourString() + "This page doesn't exist.");
			return;
		}

		sender.sendMessage(Static.getParkourString() + courseList.size() + " courses available:");
		List<String> limited = courseList.subList(fromIndex, Math.min(fromIndex + 8, courseList.size()));

		for (int i = 0; i < limited.size(); i++) {
			sender.sendMessage(((fromIndex) + (i + 1)) + ") " + ChatColor.AQUA + limited.get(i));
		}

		sender.sendMessage("== " + page + " / " + ((courseList.size() / 8) + 1) + " ==");
	}

	/**
	 * Start editing a course
	 * Used for functions that do not require a course parameter, such as "/pa checkpoint".
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
	 * Finish editing a course
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
	 * Set the course prize 
	 * Starts a Prize conversation that allows you to configure what the player gets awarded with when the complete the course. 
	 * A course could have every type of Prize if setup.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setPrize(String[] args, Player player) {
		if (!exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		new ParkourConversation(player, ConversationType.COURSEPRIZE).withCourseName(args[1].toLowerCase()).begin();
	}

	/**
	 * Increase the Complete count of the course
	 * 
	 * @param courseName
	 */
	public static void increaseComplete(String courseName) {
		int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Completed");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Completed", completed += 1);
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Increase the amount of views of the course
	 * 
	 * @param courseName
	 */
	public static void increaseView(String courseName) {
		int views = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Views");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Views", views += 1);
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Overwrite the start location of the course.
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
	 * Overwrite the creator of the course
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
	 * Set the maximum amount of deaths a player can accumulate before failing the course.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setMaxDeaths(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			sender.sendMessage(Static.getParkourString() + "Amount of deaths is not valid.");
			return;
		}

		sender.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum deaths was set to " + ChatColor.AQUA + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".MaxDeaths", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Set the mimimum Parkour level required to join the course.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setMinLevel(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			sender.sendMessage(Static.getParkourString() + "Minimum level is not valid.");
			return;
		}

		sender.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " minimum level requirement was set to " + ChatColor.AQUA + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".MinimumLevel", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Set reward level
	 * Reward the player with a Parkour level on course completion.
	 * The original value will not be overwritten if the reward level is smaller.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setRewardLevel(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			sender.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".Level", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
		sender.sendMessage(Static.getParkourString() + args[1] + "'s reward level was set to " + ChatColor.AQUA + args[2]);
	}

	/**
	 * Add reward level
	 * Add value to the player Parkour level on course completion.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setRewardLevelAdd(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			sender.sendMessage(Static.getParkourString() + "Reward level addon needs to be numeric.");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".LevelAdd", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
		sender.sendMessage(Static.getParkourString() + args[1] + "'s reward level addon was set to " + ChatColor.AQUA + args[2]);
	}
	/**
	 * Set whether the player only gets the prize for the first time they complete the course.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setRewardOnce(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		if (Parkour.getParkourConfig().getCourseData().getBoolean(args[1].toLowerCase() + ".RewardOnce")) {
			Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".RewardOnce", false);
			sender.sendMessage(Static.getParkourString() + args[1] + "'s reward one time was set to " + ChatColor.AQUA + "false");
		} else {
			Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".RewardOnce", true);
			sender.sendMessage(Static.getParkourString() + args[1] + "'s reward one time was set to " + ChatColor.AQUA + "true");
		}
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Set a reward rank
	 * Not to be confused with rewardLevel or rewardPrize, this associates a
	 * Parkour level with a message prefix. A rank is just a visual prefix. E.g.
	 * Level 10: Pro; Level 99: God
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setRewardRank(String[] args, CommandSender sender) {
		if (!Utils.isNumber(args[1])) {
			sender.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		if (args[2] == null || args[2].trim().length() == 0) {
			sender.sendMessage(Static.getParkourString() + "Rank is not valid");
			return;
		}

		Parkour.getParkourConfig().getUsersData().set("ServerInfo.Levels." + args[1] + ".Rank", args[2]);
		Parkour.getParkourConfig().saveUsers();
		sender.sendMessage(Static.getParkourString() + "Level " + args[1] + "'s rank was set to " + Utils.colour(args[2]));
	}

	/**
	 * Reward the player with Parkoins on course completions
	 * Parkoins are then spent in the store for unlockables.
	 *
	 * @param args
	 * @param sender
	 */
	public static void setRewardParkoins(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])) {
			sender.sendMessage(Static.getParkourString() + "Parkoins reward needs to be numeric.");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".Parkoins", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
		sender.sendMessage(Static.getParkourString() + args[1] + "'s parkoins reward was set to " + ChatColor.AQUA + args[2]);
	}

	/**
	 * Set the course status to Complete
	 * A player will not be able to join a course if it's not set to finished.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setFinish(String[] args, Player player) {
		String courseName = PlayerMethods.getSelected(player.getName());

		if (isReady(courseName)){
			player.sendMessage(Static.getParkourString() + ChatColor.AQUA + courseName + ChatColor.WHITE + " has already been set to finished!");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(courseName + ".Finished", true);
		Parkour.getParkourConfig().saveCourses();

		player.sendMessage(Utils.getTranslation("Parkour.Finish").replace("%COURSE%", courseName));
		Utils.logToFile(courseName + " was set to finished by " + player.getName());
	}

	/**
	 * Link a course to a lobby or another course on completion
	 * 
	 * @param args
	 * @param player
	 */
	public static void linkCourse(String[] args, Player player) {
		if (args.length < 2) { 
			player.sendMessage(Utils.invalidSyntax("Link", "(course / lobby / reset) (courseName / lobbyName)"));
			return;
		}
		
		String selected = PlayerMethods.getSelected(player.getName());

		if (args.length >= 3 && args[1].equalsIgnoreCase("course")) {
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

		} else if (args.length >= 3 && args[1].equalsIgnoreCase("lobby")) {
			if (!Parkour.getPlugin().getConfig().contains("Lobby." + args[2] + ".World")) { // TODO
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

		} else if (args[1].equalsIgnoreCase("reset")) {	
			Parkour.getParkourConfig().getCourseData().set(selected + ".LinkedLobby", null);
			Parkour.getParkourConfig().getCourseData().set(selected + ".LinkedCourse", null);
			player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is no longer linked.");

		} else {
			player.sendMessage(Utils.invalidSyntax("Link", "(course / lobby / reset) (courseName / lobbyName)"));
		}

	}

	/**
	 * Resets all the statistics to 0 as if the course was just created. 
	 * The structure of the course remains untouched.
	 * All course times will be deleted from the database.
	 * 
	 * @param courseName
	 */
	public final static void resetCourse(String courseName) {
		if (!exist(courseName))
			return;

		courseName = courseName.toLowerCase();
		FileConfiguration config = Parkour.getParkourConfig().getCourseData();

		config.set(courseName + ".Views", 0);
		config.set(courseName + ".Completed", 0);
		config.set(courseName + ".Finished", false);
		config.set(courseName + ".XP", null);
		config.set(courseName + ".Level", null);
		config.set(courseName + ".MinimumLevel", null);
		config.set(courseName + ".LevelAdd", null);
		config.set(courseName + ".MaxDeaths", null);
		config.set(courseName + ".Parkoins", null);
		config.set(courseName + ".LinkedLobby", null);
		config.set(courseName + ".LinkedCourse", null);
		config.set(courseName + ".ParkourKit", null);
		config.set(courseName + ".Mode", null);
		Parkour.getParkourConfig().saveCourses();
		DatabaseMethods.deleteCourseTimes(courseName);
	}

	/**
	 * Rate the course
	 * After completion of a course, the player has the ability to vote whether they liked the course or not.
	 * These are only used for statistics.
	 * 
	 * @param args
	 * @param player
	 */
	public static void rateCourse(String[] args, Player player) {
		String courseName;

		if (args.length > 1) {
			courseName = args[1].toLowerCase();
		} else {
			courseName = PlayerMethods.getLastCompletedCourse(player.getName());
		}

		if (!CourseMethods.exist(courseName)) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return;
		}

		if (!DatabaseMethods.hasPlayerCompleted(player.getName(), courseName)) {
			player.sendMessage(Utils.getTranslation("Error.NotCompleted").replace("%COURSE%", courseName));
			return;
		}

		if (DatabaseMethods.hasVoted(courseName, player.getName())) {
			player.sendMessage(Utils.getTranslation("Error.AlreadyVoted").replace("%COURSE%", courseName));
			return;
		}

		boolean liked = args[0].equalsIgnoreCase("like");
		DatabaseMethods.insertVote(courseName, player.getName(), liked);

		if (liked) {
			player.sendMessage(Static.getParkourString() + "You " + ChatColor.GREEN + "liked " + ChatColor.WHITE + courseName);
		} else {
			player.sendMessage(Static.getParkourString() + "You " + ChatColor.RED + "disliked " + ChatColor.WHITE + courseName);
		}
	}

	/**
	 * Link ParkourKit to a course
	 * Each course can have a different set of ParkourKit.
	 * The name of the ParkourKit set must be specified, then will be applied when a player joins the course.
	 * 
	 * @param args
	 * @param player
	 */
	public static void linkParkourKit(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			return;
		}
		if (!Parkour.getPlugin().getConfig().contains("ParkourKit." + args[2].toLowerCase())) {
			player.sendMessage(Static.getParkourString() + "ParkourKit doesn't exist!");
			return;
		}
		if (Parkour.getParkourConfig().getCourseData().contains(args[1].toLowerCase() + ".ParkourKit")) {
			player.sendMessage(Static.getParkourString() + "This course is already linked to a ParkourKit, continuing anyway...");
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".ParkourKit", args[2].toLowerCase());
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + args[1] + " is now linked to ParkourKit " + args[2]);
	}

	/**
	 * Challenge a player to a course
	 * Invites a player to challenge them in a course, must be confirmed by the recipient before the process initiates.
	 * 
	 * @param args
	 * @param player
	 */
	public static void challengePlayer(String[] args, Player player) {
		if (!ValidationMethods.challengePlayer(args, player))
			return;

		if (!Utils.delayPlayer(player, 10, true))
			return;

		Player target = Bukkit.getPlayer(args[2]);
		String courseName = args[1].toLowerCase();

		target.sendMessage(Utils.getTranslation("Parkour.ChallengeReceive")
				.replace("%PLAYER%", player.getName())
				.replace("%COURSE%", courseName));
		target.sendMessage(Utils.getTranslation("Parkour.Accept", false));
		player.sendMessage(Utils.getTranslation("Parkour.ChallengeSend")
				.replace("%PLAYER%", target.getName())
				.replace("%COURSE%", courseName));
		Challenge.challengePlayer(player.getName(), target.getName(), courseName);
	}

	/**
	 * Set Join Item for a course
	 * Specify the material and the amount for the course, which will then be given to the player once they join.
	 * 
	 * @param args
	 * @param sender
	 */
	public static void setJoinItem(String[] args, CommandSender sender) {
		if (!CourseMethods.exist(args[1])) {
			sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (Material.getMaterial(args[2].toUpperCase()) == null) {
			sender.sendMessage(Static.getParkourString() + "Invalid material: " + args[2].toUpperCase());
			return;
		}
		if (!Utils.isNumber(args[3])) {
			sender.sendMessage(Static.getParkourString() + "Amount needs to be numeric.");
			return;
		}

		int amount = Utils.parseMaterialAmount(args[3]);

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".JoinItemMaterial", args[2].toUpperCase());
		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".JoinItemAmount", amount);
		Parkour.getParkourConfig().saveCourses();
		sender.sendMessage(Static.getParkourString() + "Join item for " + args[1] + " set to " + args[2] + " (" + amount + ")");
	}

	/**
	 * Retrieve and display Leaderboard for a course
	 * Can be specified direction using appropriate commands,
	 * or the Leaderboard conversation can be started, to specify what you want to view based on the options
	 * 
	 * @param args
	 * @param player
	 */
	public static void getLeaderboards(String[] args, Player player) {
		if (!Utils.delayPlayer(player, 3, true))
			return;

		if (args.length == 1) {
			new ParkourConversation(player, ConversationType.LEADERBOARD).begin();
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
			Utils.displayLeaderboard(player, DatabaseMethods.getTopPlayerCourseResults(player.getName(), args[1], limit), args[1]);
		} else {
			Utils.displayLeaderboard(player, DatabaseMethods.getTopCourseResults(args[1], limit), args[1]);
		}
	}

	/**
	 * Set the ParkourMode of a course
	 * Starts a Conversation to set the mode of the course.
	 * 
	 * @param args
	 * @param player
	 */
	public static void setCourseMode(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		new ParkourConversation(player, ConversationType.PARKOURMODE).withCourseName(args[1].toLowerCase()).begin();
	}

	/**
	 * Retrieves the ParkourMode of a course
	 * Will default to NONE if a mode hasn't been specified.
	 * 
	 * @param courseName
	 * @return ParkourMode
	 */
	public static ParkourMode getCourseMode(String courseName) {
		String mode = Parkour.getParkourConfig().getCourseData().getString(courseName + ".Mode");

		if ("freedom".equalsIgnoreCase(mode)) {
			return ParkourMode.FREEDOM;

		} else if ("drunk".equalsIgnoreCase(mode)) {
			return ParkourMode.DRUNK;

		} else if ("darkness".equalsIgnoreCase(mode)) {
			return ParkourMode.DARKNESS;

		} else if ("speedy".equalsIgnoreCase(mode)) {
			return ParkourMode.SPEEDY;

		} else if ("moon".equalsIgnoreCase(mode)) {
			return ParkourMode.MOON;
		}

		return ParkourMode.NONE;
	}

	/**
	 * Return the linked course of the specified course
	 * Will validate it's a valid course before returning
	 * @param courseName
	 * @return linkedCourse
	 */
	public static String getLinkedCourse(String courseName) {
		String linkedCourse = Parkour.getParkourConfig().getCourseData().getString(courseName + ".LinkedCourse");

		if (linkedCourse != null && CourseMethods.exist(linkedCourse))
			return linkedCourse;

		return null;
	}

	/**
	 * Return the linked course of the specified lobby
	 * Will validate it's a valid lobby before returning
	 * @param courseName
	 * @return linkedLobby
	 */
	public static String getLinkedLobby(String courseName) {
		String linkedLobby = Parkour.getParkourConfig().getCourseData().getString(courseName + ".LinkedLobby");

		if (linkedLobby != null && Static.getLobbyList().contains(linkedLobby))
			return linkedLobby;

		return null;
	}
}