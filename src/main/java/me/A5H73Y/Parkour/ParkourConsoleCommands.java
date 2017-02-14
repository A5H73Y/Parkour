package me.A5H73Y.Parkour;

import org.bukkit.command.CommandSender;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.Backup;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Settings;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ParkourConsoleCommands {

	public static void setLevel(String[] args){

	}

	public static void startBackup() {
		Backup.backupNow(true);
	}

	public static void reloadConfig() {
		Parkour.getParkourConfig().reload();
		Parkour.setSettings(new Settings());
		Static.initiate();
		Utils.log("Config reloaded!");
	}

	public static void recreateCourses() {
		DatabaseMethods.recreateAllCourses();
	}

	public static void setCourseMinimumlevel(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 3))
			return;

		CourseMethods.setMinLevel(args, sender);
	}

	public static void setCourseMaximumDeath(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 3))
			return;

		CourseMethods.setMaxDeaths(args, sender);
	}

	public static void setCourseJoinItem(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 4))
			return;

		CourseMethods.setJoinItem(args, sender);
	} 

	public static void setCourseToRewardOnce(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 2))
			return;

		CourseMethods.setRewardOnce(args, sender);
	}

	public static void setRewardLevel(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 3))
			return;

		CourseMethods.setRewardLevel(args, sender);
	}

	public static void setRewardRank(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 3))
			return;

		CourseMethods.setRewardRank(args, sender);
	}

	public static void setRewardParkoins(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 3))
			return;

		CourseMethods.setRewardParkoins(args, sender);
	}

	public static void displayList(String[] args, CommandSender sender) {
		CourseMethods.displayList(args, sender);
	}
	
	public static void displayParkourBlocks(String[] args, CommandSender sender) {
		Utils.listParkourBlocks(args, sender);
	}

	public static void displayCommands() {
		Utils.log("pa setlevel (player) (level) : Set a players Parkour Level");
		Utils.log("pa backup : Create a backup zip of the Parkour config folder");
		Utils.log("pa reload : Reload the Parkour config");
	}

}
