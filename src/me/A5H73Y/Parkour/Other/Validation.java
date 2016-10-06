package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
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

	public static boolean courseJoining(Player player, Course course){
		
		/* Player in wrong world */
		if (Parkour.getSettings().isEnforceWorld()){
			if (!player.getLocation().getWorld().getName().equals(course.getCheckpoint().getWorld())){
				player.sendMessage(Utils.getTranslation("Error.WrongWorld"));
				return false;
			}
		}

		/* Players level isn't high enough */
		if (Parkour.getParkourConfig().getCourseData().contains(course.getName() + ".MinimumLevel")){
			if (!player.hasPermission("Parkour.MinBypass")){
				int minimumLevel = Parkour.getParkourConfig().getCourseData().getInt(course.getName() + ".MinimumLevel");
				int currentLevel = Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level");

				if (currentLevel < minimumLevel){
					player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replace("%LEVEL%", String.valueOf(minimumLevel)));
					return false;
				}
			}
		}

		/* Course isn't finished */
		if (!CourseMethods.isReady(course.getName())){
			if (Parkour.getParkourConfig().getConfig().getBoolean("OnJoin.EnforceFinished")){
				if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Bypass", course.getName())){
					player.sendMessage(Utils.getTranslation("Error.Finished1"));
					return false;
				}
			} else {
				player.sendMessage(Utils.getTranslation("Error.Finished2"));
			}
		}
		
		/* Check if player has enough currency to join */
		if (Static.getEconomy()){
			int joinFee = Parkour.getParkourConfig().getEconData().getInt("Price." + course.getName() + "JoinFee");
			
			if (joinFee > 0){
				if (Parkour.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) < joinFee){
					player.sendMessage(Utils.getTranslation("Economy.Insufficient"));
					return false;
				} else {
					Parkour.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), joinFee);
					player.sendMessage(Utils.getTranslation("Economy.Fee"));
				}
			}
		}

		return true;
	}

}
