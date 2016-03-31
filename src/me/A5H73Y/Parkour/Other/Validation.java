package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.entity.Player;

public class Validation {

	public static boolean courseCreation(String[] args, Player player){
		if (args.length > 2) {
			player.sendMessage(Utils.getTranslation("Error.TooMany"));
			return false;
		} else if (args.length == 1) {
			player.sendMessage(Utils.getTranslation("Error.TooLittle"));
			return false;
		} else if (args[1].length() > 15) {
			player.sendMessage("Course name is too long!");
			return false;
		} else if (args[1].equalsIgnoreCase("TestMode")) {
			player.sendMessage("This name is already taken!");
			return false;
		} else if (args[1].contains(".")) {
			player.sendMessage("Course names can not contain '.'");
			return false;
		} else if (Utils.isNumber(args[1])){
			player.sendMessage("Course name can not only be numeric");
			return false;
		} else if (CourseMethods.exist(args[1])) {
			player.sendMessage(Utils.getTranslation("Error.Exist"));
			return false;
		}

		return true;
	}
	
	public static boolean courseJoining(Player player, String courseName){
		/*
		 * if (getConfig().getBoolean("Other.Use.ForceWorld")){
			if (!(player.getLocation().getWorld().getName().equals(courseData.getString(course + ".World")))){
				player.sendMessage(ParkourString + "You are in the wrong world!");
				return;
			}
		}
		
		if (courseData.contains(course + ".MinimumLevel")){
			if (!(player.hasPermission("Parkour.MinBypass"))){
				int min = courseData.getInt(course + ".MinimumLevel");
				int cur = usersData.getInt("PlayerInfo." + player.getName() + ".Level");

				if (cur >= min){

				}else{
					player.sendMessage(ParkourString + Colour(stringData.getString("Error.RequiredLvl").replaceAll("%LEVEL%", ""+min)));	
					return;
				}
			}
		}
		if (getConfig().getBoolean("Other.Display.FinishedError")) {
				if (courseData.contains(course + ".Finished")) {
				} else {
					if (getConfig().getBoolean("Other.onJoin.forceFinished")) {
						if (!(player.hasPermission("Parkour.Admin"))) {
							player.sendMessage(ParkourString + Colour(stringData.getString("Error.Finished1")));	
							return;
						}
					} else {
						player.sendMessage(ParkourString + Colour(stringData.getString("Error.Finished2")));	
					}
				}
			}

		 */
		
		
		return true;
	}
	
}
