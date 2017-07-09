package me.A5H73Y.Parkour.Other;

import java.util.ArrayList;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ValidationMethods {

	/**
	 * Validate course creation
	 * @param args
	 * @param player
	 * @return
	 */
	public static boolean courseCreation(String[] args, Player player){
		if (args.length > 2) {
			player.sendMessage(Utils.getTranslation("Error.TooMany"));
			return false;
		} else if (args.length == 1) {
			player.sendMessage(Utils.getTranslation("Error.TooLittle"));
			return false;
		} else if (args[1].length() > 15) {
			player.sendMessage(Static.getParkourString() + "Course name is too long!");
			return false;
		} else if (args[1].contains(".")) {
			player.sendMessage(Static.getParkourString() + "Course name can not contain '.'");
			return false;
		} else if (Utils.isNumber(args[1])){
			player.sendMessage(Static.getParkourString() + "Course name can not only be numeric");
			return false;
		} else if (CourseMethods.exist(args[1])) {
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
	public static boolean courseJoining(Player player, Course course){

		/* Player in wrong world */
		if (Parkour.getSettings().isEnforceWorld()){
			if (!player.getLocation().getWorld().getName().equals(course.getCurrentCheckpoint().getWorld())){
				player.sendMessage(Utils.getTranslation("Error.WrongWorld"));
				return false;
			}
		}

		/* Players level isn't high enough */
		if (Parkour.getParkourConfig().getCourseData().contains(course.getName() + ".MinimumLevel")){
			int minimumLevel = Parkour.getParkourConfig().getCourseData().getInt(course.getName() + ".MinimumLevel");

			if (!player.hasPermission("Parkour.Admin.MinBypass") && !player.hasPermission("Parkour.Level." + minimumLevel)){
				int currentLevel = Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level");

				if (currentLevel < minimumLevel){
					player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replace("%LEVEL%", String.valueOf(minimumLevel)));
					return false;
				}
			}
		}

		/* Course isn't finished */
		if (!CourseMethods.isReady(course.getName())){
			if (Parkour.getPlugin().getConfig().getBoolean("OnJoin.EnforceFinished")){
				if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Bypass", course.getName())){
					player.sendMessage(Utils.getTranslation("Error.Finished1"));
					return false;
				}
			} else {
				player.sendMessage(Utils.getTranslation("Error.Finished2"));
			}
		}

		/* Check if player has enough currency to join */
		if (Static.getEconomy()){
			int joinFee = Parkour.getParkourConfig().getEconData().getInt("Price." + course.getName() + ".JoinFee");
			String currencyName = Parkour.getEconomy().currencyNamePlural() == null ? 
					"" : " " + Parkour.getEconomy().currencyNamePlural();
			
			if (joinFee > 0){
				if (Parkour.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) < joinFee){
					player.sendMessage(Utils.getTranslation("Economy.Insufficient")
							.replace("%AMOUNT%", joinFee + currencyName)
							.replace("%COURSE%", course.getName()));
					return false;
				} else {
					Parkour.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), joinFee);
					player.sendMessage(Utils.getTranslation("Economy.Fee")
							.replace("%AMOUNT%", joinFee + currencyName)
							.replace("%COURSE%", course.getName()));
				}
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
	public static boolean courseJoiningNoMessages(Player player, String courseName){
		/* Player in wrong world */
		if (Parkour.getSettings().isEnforceWorld()){
			if (!player.getLocation().getWorld().getName().equals(Parkour.getParkourConfig().getCourseData().getString(courseName + ".World"))){
				return false;
			}
		}

		/* Players level isn't high enough */
		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".MinimumLevel")){
			int minimumLevel = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".MinimumLevel");

			if (!player.hasPermission("Parkour.Admin.MinBypass") && !player.hasPermission("Parkour.Level." + minimumLevel)){
				int currentLevel = Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level");

				if (currentLevel < minimumLevel){
					return false;
				}
			}
		}

		/* Course isn't finished */
		if (!CourseMethods.isReady(courseName)){
			if (Parkour.getPlugin().getConfig().getBoolean("OnJoin.EnforceFinished")){
				if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Bypass", courseName)){
					return false;
				}
			}
		}

		/* Check if player has enough currency to join */
		if (Static.getEconomy()){
			int joinFee = Parkour.getParkourConfig().getEconData().getInt("Price." + courseName + "JoinFee");

			if (joinFee > 0){
				if (Parkour.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) < joinFee){
					return false;
				} 
			}
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

		if (!CourseMethods.exist(courseName)){
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			return false;
		}
		if (!PlayerMethods.isPlayerOnline(args[2])){
			player.sendMessage(Static.getParkourString() + "This player is not online!");
			return false;
		}
		if (PlayerMethods.isPlaying(args[2])){
			player.sendMessage(Static.getParkourString() + "This player is already playing!");
			return false;
		}
		if (player.getName().equalsIgnoreCase(args[2])){
			player.sendMessage(Static.getParkourString() + "You can't challenge yourself!");
			return false;
		}

		Player target = Bukkit.getPlayer(args[2]);

		if (!ValidationMethods.courseJoiningNoMessages(player, courseName)){
			player.sendMessage(Static.getParkourString() + "You are not able to join this course!");
			return false;
		}
		if (!ValidationMethods.courseJoiningNoMessages(target, courseName)){
			player.sendMessage(Static.getParkourString() + "They are not able to join this course!");
			return false;
		}

		return true;
	}

	/**
	 * Validate joining a lobby
	 * @param player
	 * @return boolean
	 */
	public static boolean lobbyJoiningSet(Player player) {
		if (Parkour.getPlugin().getConfig().getBoolean("Lobby.Set"))
			return true;

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

		if (level > 0 && !player.hasPermission("Parkour.Admin.MinBypass")) {
			if (Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level") < level) {
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
		String selected = PlayerMethods.getSelected(player.getName()).toLowerCase();

		if (!CourseMethods.exist(selected)) {
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
			return false;
		}

		int pointcount = Parkour.getParkourConfig().getCourseData().getInt((selected + ".Points")) + 1;

		if (!(args.length <= 1)) {
			if (!Utils.isNumber(args[1])) {
				player.sendMessage(Static.getParkourString() + "Checkpoint specified is not numeric!");
				return false; 
			}
			if (pointcount < Integer.parseInt(args[1])){
				player.sendMessage(Static.getParkourString() + "This checkpoint does not exist! " + ChatColor.RED + "Creation cancelled.");
				return false;
			}

			pointcount = Integer.parseInt(args[1]);
		}

		if (pointcount < 1){
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
		FileConfiguration courseConfig = Parkour.getParkourConfig().getCourseData();
		List<String> dependantCourses = new ArrayList<>();

		for (String course : Static.getCourses()){
			String linkedCourse = courseConfig.getString(course + ".LinkedCourse");

			if (linkedCourse != null && courseName.equals(linkedCourse)) {
				dependantCourses.add(course);
			}
		}

		if (dependantCourses.size() > 0) {
			player.sendMessage(Static.getParkourString() + "This course can not be deleted as there are dependant courses: " + dependantCourses);
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
		FileConfiguration courseConfig = Parkour.getParkourConfig().getCourseData();
		List<String> dependantCourses = new ArrayList<>();

		for (String course : Static.getCourses()){
			String linkedCourse = courseConfig.getString(course + ".LinkedLobby");

			if (linkedCourse != null && courseName.equals(linkedCourse)) {
				dependantCourses.add(course);
			}
		}

		if (dependantCourses.size() > 0) {
			player.sendMessage(Static.getParkourString() + "This lobby can not be deleted as there are dependant courses: " + dependantCourses);
			return false;
		}

		return true;
	}
}
