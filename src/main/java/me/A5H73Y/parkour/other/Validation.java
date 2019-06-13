package me.A5H73Y.parkour.other;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.course.Course;
import me.A5H73Y.parkour.course.CourseInfo;
import me.A5H73Y.parkour.course.CourseMethods;
import me.A5H73Y.parkour.player.PlayerInfo;
import me.A5H73Y.parkour.player.PlayerMethods;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Validation {

	/**
	 * Validate if the argument is numeric
	 * "1" = true, "Hi" = false
	 *
	 * @param input
	 * @return whether the input is numeric
	 */
	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * Validate if the argument is numeric
	 * "1" = true, "1.0" = true, "Hi" = false
	 *
	 * @param input
	 * @return whether the input is numeric
	 */
	public static boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * Validate if the argument is numeric
	 * "1" = true, "Hi" = false, "-1" = false
	 *
	 * @param input
	 * @return whether the input is numeric
	 */
	public static boolean isPositiveInteger(String input) {
		return isInteger(input) && Integer.parseInt(input) >= 0;
	}

	/**
	 * Validate if the argument is numeric
	 * "1" = true, "Hi" = false, "-1" = false
	 *
	 * @param input
	 * @return whether the input is numeric
	 */
	public static boolean isPositiveDouble(String input) {
		return isDouble(input) && Double.parseDouble(input) >= 0;
	}

	/**
	 * Validate if the input is a populated String
	 * @param input
	 * @return whether the input is a valid String
	 */
	public static boolean isStringValid(String input) {
		return input != null && input.trim().length() != 0;
	}

	/**
	 * Validate course creation
	 * @param courseName
	 * @param player
	 * @return
	 */
	public static boolean courseCreation(String courseName, Player player) {
		if (courseName.length() > 15) {
			player.sendMessage(Static.getParkourString() + "Course name is too long!");
			return false;

		} else if (courseName.contains(".")) {
			player.sendMessage(Static.getParkourString() + "Course name can not contain '.'");
			return false;

		} else if (isInteger(courseName)) {
			player.sendMessage(Static.getParkourString() + "Course name can not only be numeric");
			return false;

		} else if (CourseMethods.exist(courseName)) {
			player.sendMessage(Utils.getTranslation("Error.Exist"));
			return false;
		}

		return true;
	}

	/**
	 * Validate player join course
	 * @param player
	 * @param course
	 * @return
	 */
	public static boolean courseJoining(Player player, Course course) {

		/* Player in wrong world */
		if (Parkour.getSettings().isEnforceWorld()) {
			if (!player.getLocation().getWorld().getName().equals(course.getCurrentCheckpoint().getWorld())) {
				player.sendMessage(Utils.getTranslation("Error.WrongWorld"));
				return false;
			}
		}

		/* Players level isn't high enough */
		int minimumLevel = CourseInfo.getMinimumLevel(course.getName());

		if (minimumLevel > 0) {
			if (!Utils.hasPermissionNoMessage(player,"Parkour.Admin", "MinBypass")
					&& !Utils.hasPermissionNoMessage(player, "Parkour.Level", String.valueOf(minimumLevel))) {
				int currentLevel = PlayerInfo.getParkourLevel(player);

				if (currentLevel < minimumLevel) {
					player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replace("%LEVEL%", String.valueOf(minimumLevel)));
					return false;
				}
			}
		}

		/* Course isn't finished */
		if (!CourseInfo.getFinished(course.getName())) {
			if (Parkour.getPlugin().getConfig().getBoolean("OnJoin.EnforceFinished")) {
				if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Bypass", course.getName())) {
					player.sendMessage(Utils.getTranslation("Error.Finished1"));
					return false;
				}
			} else {
				player.sendMessage(Utils.getTranslation("Error.Finished2"));
			}
		}

		/* Check if player has enough currency to join */
		if (Static.getEconomy()) {
			int joinFee = CourseInfo.getEconomyJoiningFee(course.getName());
			String currencyName = Parkour.getEconomy().currencyNamePlural() == null ?
					"" : " " + Parkour.getEconomy().currencyNamePlural();

			if (joinFee > 0) {
				if (!Parkour.getEconomy().has(player, joinFee)) {
					player.sendMessage(Utils.getTranslation("Economy.Insufficient")
							.replace("%AMOUNT%", joinFee + currencyName)
							.replace("%COURSE%", course.getName()));
					return false;
				} else {
					Parkour.getEconomy().withdrawPlayer(player, joinFee);
					player.sendMessage(Utils.getTranslation("Economy.Fee")
							.replace("%AMOUNT%", joinFee + currencyName)
							.replace("%COURSE%", course.getName()));
				}
			}
		}

		/* Check if the player is allowed to leave the course for another */
		if (Parkour.getPlugin().getConfig().getBoolean("OnCourse.PreventJoiningDifferentCourse")) {
			if (PlayerMethods.isPlaying(player.getName())) {
				player.sendMessage(Utils.getTranslation("Error.JoiningAnotherCourse"));
				return false;
			}
		}

		return true;
	}

	/**
	 * Validate player joining course, without sending messages
	 * @param player
	 * @param courseName
	 * @return
	 */
	public static boolean courseJoiningNoMessages(Player player, String courseName) {
		/* Player in wrong world */
		if (Parkour.getSettings().isEnforceWorld()) {
			if (!player.getLocation().getWorld().getName().equals(CourseInfo.getWorld(courseName))) {
				return false;
			}
		}

		/* Players level isn't high enough */
		int minimumLevel = CourseInfo.getMinimumLevel(courseName);

		if (minimumLevel > 0) {
			if (!Utils.hasPermissionNoMessage(player,"Parkour.Admin", "MinBypass")
					&& !Utils.hasPermissionNoMessage(player, "Parkour.Level", String.valueOf(minimumLevel))) {
				int currentLevel = PlayerInfo.getParkourLevel(player);

				if (currentLevel < minimumLevel) {
					return false;
				}
			}
		}

		/* Course isn't finished */
		if (!CourseInfo.getFinished(courseName)) {
			if (Parkour.getPlugin().getConfig().getBoolean("OnJoin.EnforceFinished")) {
				if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Bypass", courseName)) {
					return false;
				}
			}
		}

		/* Check if player has enough currency to join */
		if (Static.getEconomy()) {
			int joinFee = CourseInfo.getEconomyJoiningFee(courseName);

			return joinFee <= 0 || Parkour.getEconomy().has(player, joinFee);
		}

		return true;
	}

	/**
	 * Validate challenging a player
	 * @param args
	 * @param player
	 * @return
	 */
	public static boolean challengePlayer(String[] args, Player player) {
		String courseName = args[1].toLowerCase();
		String targetPlayerName = args[2];

		if (!CourseMethods.exist(courseName)) {
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			return false;
		}
		if (!PlayerMethods.isPlayerOnline(targetPlayerName)) {
			player.sendMessage(Static.getParkourString() + "This player is not online!");
			return false;
		}
		if (PlayerMethods.isPlaying(player.getName())) {
			player.sendMessage(Static.getParkourString() + "You are already on a course!");
			return false;
		}
		if (PlayerMethods.isPlaying(targetPlayerName)) {
			player.sendMessage(Static.getParkourString() + "This player is already on a course!");
			return false;
		}
		if (player.getName().equalsIgnoreCase(targetPlayerName)) {
			player.sendMessage(Static.getParkourString() + "You can't challenge yourself!");
			return false;
		}

		Player target = Bukkit.getPlayer(targetPlayerName);

		if (!courseJoiningNoMessages(player, courseName)) {
			player.sendMessage(Static.getParkourString() + "You are not able to join this course!");
			return false;
		}
		if (!courseJoiningNoMessages(target, courseName)) {
			player.sendMessage(Static.getParkourString() + "They are not able to join this course!");
			return false;
		}
		if (args.length == 4) {
			String wagerAmount = args[3];

			if (!Static.getEconomy()) {
				player.sendMessage(Static.getParkourString() + "Economy is disabled, no wager will be made.");

			} else if (!isPositiveDouble(wagerAmount)) {
				player.sendMessage(Static.getParkourString() + "Wager must be a positive number.");
				return false;

			} else if (!Parkour.getEconomy().has(player, Double.valueOf(wagerAmount))) {
				player.sendMessage(Static.getParkourString() + "You do not have enough funds for this wager.");
				return false;

			} else if (!Parkour.getEconomy().has(target, Double.valueOf(wagerAmount))) {
				player.sendMessage(Static.getParkourString() + "They do not have enough funds for this wager.");
				return false;
			}
		}

		return true;
	}

	/**
	 * Validate joining a lobby
	 * @param player
	 * @return boolean
	 */
	public static boolean lobbyJoiningSet(Player player) {
		if (Parkour.getPlugin().getConfig().getBoolean("Lobby.Set")) {
			return true;
		}

		if (Utils.hasPermission(player, "Parkour.Admin")) {
			player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set!");
			player.sendMessage(Utils.colour("Type &b'/pa setlobby'&f where you want the lobby to be set."));
		} else {
			player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set! Please tell the Owner!");
		}
		return false;
	}

	/**
	 * Validate joining a custom lobby
	 * @param player
	 * @param lobby
	 * @return
	 */
	public static boolean lobbyJoiningCustom(Player player, String lobby) {
		if (!Parkour.getPlugin().getConfig().contains("Lobby." + lobby + ".World")) {
			player.sendMessage(Static.getParkourString() + "Lobby does not exist!");
			return false;
		}

		int level = Parkour.getPlugin().getConfig().getInt("Lobby." + lobby + ".Level");

		if (level > 0 && !Utils.hasPermissionNoMessage(player, "Parkour.Admin", "MinBypass")) {
			if (PlayerInfo.getParkourLevel(player) < level) {
				player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replace("%LEVEL%", String.valueOf(level)));
				return false;
			}

		}
		return true;
	}

	/**
	 * Validate creating a course checkpoint
	 * @param args
	 * @param player
	 * @return
	 */
	public static boolean createCheckpoint(String[] args, Player player) {
		String selected = PlayerInfo.getSelected(player).toLowerCase();

		if (!CourseMethods.exist(selected)) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
			return false;
		}

		int pointcount = CourseInfo.getCheckpointAmount(selected) + 1;

		if (!(args.length <= 1)) {
			if (!isPositiveInteger(args[1])) {
				player.sendMessage(Static.getParkourString() + "Checkpoint specified is not numeric!");
				return false;
			}
			if (pointcount < Integer.parseInt(args[1])) {
				player.sendMessage(Static.getParkourString() + "This checkpoint does not exist! " + ChatColor.RED + "Creation cancelled.");
				return false;
			}

			pointcount = Integer.parseInt(args[1]);
		}

		if (pointcount < 1) {
			player.sendMessage(Static.getParkourString() + "Invalid checkpoint number.");
			return false;
		}
		return true;
	}

	/**
	 * Validate a course before deleting it
	 * @param courseName
	 * @param player
	 * @return
	 */
	public static boolean deleteCourse(String courseName, Player player) {
		courseName = courseName.toLowerCase();
		List<String> dependentCourses = new ArrayList<>();

		for (String course : CourseInfo.getAllCourses()) {
			String linkedCourse = CourseInfo.getLinkedCourse(courseName);

			if (courseName.equals(linkedCourse)) {
				dependentCourses.add(course);
			}
		}

		if (dependentCourses.size() > 0) {
			player.sendMessage(Static.getParkourString() + "This course can not be deleted as there are dependent courses: " + dependentCourses);
			return false;
		}

		return true;
	}

	/**
	 * Validate a lobby before deleting it
	 * @param courseName
	 * @param player
	 * @return
	 */
	public static boolean deleteLobby(String courseName, Player player) {
		courseName = courseName.toLowerCase();
		List<String> dependentCourses = new ArrayList<>();

		for (String course : CourseInfo.getAllCourses()) {
			String linkedCourse = CourseInfo.getLinkedLobby(courseName);

			if (courseName.equals(linkedCourse)) {
				dependentCourses.add(course);
			}
		}

		if (dependentCourses.size() > 0) {
			player.sendMessage(Static.getParkourString() + "This lobby can not be deleted as there are dependent courses: " + dependentCourses);
			return false;
		}

		return true;
	}

	/**
	 * Validate a kit before deleting it
	 * @param parkourKit
	 * @param player
	 * @return
	 */
	public static boolean deleteParkourKit(String parkourKit, Player player) {
		parkourKit = parkourKit.toLowerCase();
		List<String> dependentCourses = new ArrayList<>();

		for (String course : CourseInfo.getAllCourses()) {
			String linkedKit = CourseInfo.getParkourKit(course);

			if (parkourKit.equals(linkedKit)) {
				dependentCourses.add(course);
			}
		}

		if (dependentCourses.size() > 0) {
			player.sendMessage(Static.getParkourString() + "This ParkourKit can not be deleted as there are dependent courses: " + dependentCourses);
			return false;
		}

		return true;
	}

	/**
	 * Validate the autostart before deleting it
	 * @param courseName
	 * @param coordinates
	 * @param player
	 * @return
	 */
	public static boolean deleteAutoStart(String courseName, String coordinates, Player player) {
		courseName = courseName.toLowerCase();
		if (!Parkour.getParkourConfig().getCourseData().getString("CourseInfo.AutoStart." + coordinates).equalsIgnoreCase(courseName)) {
			player.sendMessage(Static.getParkourString() + "This autostart can not be deleted as it is not linked to course: " + courseName);
			return false;
		}

		return true;
	}

}
