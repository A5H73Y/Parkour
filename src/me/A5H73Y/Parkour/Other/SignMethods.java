package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class SignMethods {

	public boolean createStandardSign(SignChangeEvent sign, Player player, String title){
		if (!hasPermission(player, sign, title))
			return false;

		sign.setLine(1, title);
		sign.setLine(2, "");
		sign.setLine(3, "-----");
		player.sendMessage(Static.getParkourString() + title + " sign created!");
		return true;
	}

	public void createStandardCourseSign(SignChangeEvent sign, Player player, String title){
		createStandardCourseSign(sign, player, title, true);
	}

	public boolean createStandardCourseSign(SignChangeEvent sign, Player player, String title, boolean message){
		if (!hasPermission(player, sign, title))
			return false;

		if (!CourseMethods.exist(sign.getLine(2))){
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			sign.setLine(2, ChatColor.RED + "Unknown Course!");
			sign.setLine(3, "");
			return false;
		}

		sign.setLine(1, title);
		sign.setLine(2, sign.getLine(2));

		if (message)
			player.sendMessage(Static.getParkourString() + Static.Daqua + title + Static.White + " sign for " + Static.Aqua + sign.getLine(2) + Static.White + " created!");
		return true;
	}

	public void joinCourse(SignChangeEvent sign, Player player){
		if (!createStandardCourseSign(sign, player, "Join", false))
			return;

		if (sign.getLine(3).equalsIgnoreCase("cj")){
			sign.setLine(1, sign.getLine(1).toString() + " (CJ)");
		} else if (sign.getLine(3).equalsIgnoreCase("c")){
			sign.setLine(1, sign.getLine(1).toString() + " (C)");
		}

		if (Parkour.getParkourConfig().getCourseData().contains(sign.getLine(2).toString() + ".MinimumLevel"))
			sign.setLine(3, ChatColor.RED + "" + Parkour.getParkourConfig().getCourseData().get(sign.getLine(2).toString() + ".MinimumLevel"));

		player.sendMessage(Static.getParkourString() + "Join for " + Static.Aqua + sign.getLine(2).toString() + Static.White + " created!");
	}

	public void joinLobby(SignChangeEvent sign, Player player){
		if (!hasPermission(player, sign, "Lobby"))
			return;

		sign.setLine(1, "Lobby");

		if (sign.getLine(2).isEmpty()){
			sign.setLine(3, "");
			player.sendMessage(Static.getParkourString() + "Lobby shortcut created!");

		} else {
			if (Parkour.getParkourConfig().getConfig().contains("Lobby." + sign.getLine(2))) {
				if (Parkour.getParkourConfig().getConfig().contains("Lobby." + sign.getLine(2).toString() + ".Level")) {
					sign.setLine(3, ChatColor.RED + Parkour.getParkourConfig().getConfig().getString("Lobby." + sign.getLine(2).toString() + ".Level"));
				}
				player.sendMessage(Static.getParkourString() + "Lobby " + sign.getLine(2) + " shortcut created!");
			} else {
				player.sendMessage(Static.getParkourString() + "That custom lobby does not exist!");
				sign.setLine(2, "");
				sign.setLine(3, "-----");
			}
		}

	}

	public void createEffectSign(SignChangeEvent sign, Player player, String title) {
		if (!hasPermission(player, sign, title))
			return;

		sign.setLine(1, "Effect");
		if (sign.getLine(2).equalsIgnoreCase("heal")) {
			sign.setLine(2, "Heal");
			sign.setLine(3, "-----");

		} else if (sign.getLine(2).equalsIgnoreCase("jump")) {
			if (sign.getLine(3).isEmpty() || Utils.isNumber(sign.getLine(3))) {
				player.sendMessage(Static.getParkourString() + "Invalid Jump Height");
				sign.getBlock().breakNaturally();
				return;
			}

			sign.setLine(2, "Jump");
			player.sendMessage(Static.getParkourString() + "Created Jump sign");

		} else if (sign.getLine(2).equalsIgnoreCase("speed")) {
			sign.setLine(2, "Speed");
			sign.setLine(3, "-----");
			player.sendMessage(Static.getParkourString() + "Created Speed sign");

		} else if (sign.getLine(2).equalsIgnoreCase("pain") || sign.getLine(2).equalsIgnoreCase("painresist")) {
			sign.setLine(2, "Pain Resist");
			sign.setLine(3, "-----");
			player.sendMessage(Static.getParkourString() + "Created Pain Resistance sign");

		} else if (sign.getLine(2).equalsIgnoreCase("fire") || sign.getLine(2).equalsIgnoreCase("fireresist")) {
			sign.setLine(2, "Fire Resist");
			sign.setLine(3, "-----");
			player.sendMessage(Static.getParkourString() + "Created Fire Resistance sign");

		} else if (sign.getLine(2).equalsIgnoreCase("gamemode")) {
			sign.setLine(2, "Gamemode");
			if (sign.getLine(3).equalsIgnoreCase("creative") || sign.getLine(3).equalsIgnoreCase("c")) {
				sign.setLine(3, "Creative");
			} else {
				sign.setLine(3, "Survival");
			}
			
		} else {
			sign.setLine(2, ChatColor.RED + "Unknown Effect");
			sign.setLine(3, "");
			player.sendMessage(Static.getParkourString() + "Unknown Effect");
		}
	}

	private boolean hasPermission(Player player, SignChangeEvent sign, String permission){
		if (!Utils.hasPermission(player, "Parkour.Sign", permission)){
			sign.setCancelled(true);
			sign.getBlock().breakNaturally();
			return false;
		}
		return true;
	}
}
