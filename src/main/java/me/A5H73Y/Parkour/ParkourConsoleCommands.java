package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Player.PlayerMethods;
import org.bukkit.command.CommandSender;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.Backup;
import me.A5H73Y.Parkour.Other.Help;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;

class ParkourConsoleCommands {

	public static void startBackup() {
		Backup.backupNow(true);
	}

	public static void reloadConfig() {
		Utils.reloadConfig();
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
	
	public static void setRewardLevelAdd(String[] args, CommandSender sender) {
		if (!Utils.validateArgs(sender, args, 3))
			return;

		CourseMethods.setRewardLevelAdd(args, sender);
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
	
	public static void displayParkourKit(String[] args, CommandSender sender) {
		Utils.listParkourKit(args, sender);
	}
	
	public static void displaySettings(CommandSender sender) {
		Help.displaySettings(sender);
	}
	
	public static void displayHelp(String[] args, CommandSender sender) {
		Help.lookupCommandHelp(args, sender);
	}

	public static void displayCommands() {
		Utils.log("pa reload");
		Utils.log("pa recreate");
		Utils.log("pa setminlevel (course) (level)");
		Utils.log("pa setmaxdeath (course) (amount)");
		Utils.log("pa setjoinitem (course) (item) (amount)"); 
		Utils.log("pa rewardonce (course)");
		Utils.log("pa rewardlevel (course) (level)");
		Utils.log("pa rewardrank (level) (rank)");
		Utils.log("pa rewardparkoins (course) (amount)");
		Utils.log("pa setlevel (player) (level)");
		Utils.log("pa setrank (player) (rank)");
		Utils.log("pa list (courses / players)");
		Utils.log("pa listkit (course)");
		Utils.log("pa settings");
		Utils.log("pa help (command)");
		Utils.log("pa backup : Create a backup zip of the Parkour config folder");	
	}

    public static void setPlayerLevel(String[] args, CommandSender sender) {
        if (!Utils.validateArgs(sender, args, 3))
            return;

        PlayerMethods.setLevel(args, sender);
    }

    public static void setPlayerRank(String[] args, CommandSender sender) {
        if (!Utils.validateArgs(sender, args, 3))
            return;

        PlayerMethods.setRank(args, sender);
    }
}
