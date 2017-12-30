package me.A5H73Y.Parkour.Utilities;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class SignMethods {

	/**
	 * Create a standard Parkour sign, no arguments just display the title
	 * 
	 * @param sign
	 * @param player
	 * @param title
	 */
	public void createStandardSign(SignChangeEvent sign, Player player, String title){
		if (!Utils.hasSignPermission(player, sign, title))
			return;

		sign.setLine(1, title);
		sign.setLine(2, "");
		sign.setLine(3, "-----");
		player.sendMessage(Static.getParkourString() + title + " sign created!");
	}

	public void createStandardCourseSign(SignChangeEvent sign, Player player, String title){
		createStandardCourseSign(sign, player, title, true);
	}

	/**
	 * Create standard Parkour course sign, argument being the course name
	 * 
	 * @param sign
	 * @param player
	 * @param title
	 * @param message
	 * @return
	 */
	public boolean createStandardCourseSign(SignChangeEvent sign, Player player, String title, boolean message){
		if (!Utils.hasSignPermission(player, sign, title))
			return false;

		if (!CourseMethods.exist(sign.getLine(2))){
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			sign.setLine(2, ChatColor.RED + "Unknown Course!");
			sign.setLine(3, "");
			return false;
		}

		sign.setLine(1, title);
		sign.setLine(2, sign.getLine(2).toLowerCase());

		if (message)
			player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + title + ChatColor.WHITE + " sign for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
		return true;
	}

	/**
	 * Create Course Join Sign
	 * A minimum level requirement will be displayed on the join sign.
	 * @param sign
	 * @param player
	 */
	public void createJoinCourseSign(SignChangeEvent sign, Player player){
		if (!createStandardCourseSign(sign, player, "Join", false))
			return;

		int minimumLevel = CourseInfo.getMinimumLevel(sign.getLine(2));

		if (minimumLevel > 0)
			sign.setLine(3, ChatColor.RED + "" + minimumLevel);

		player.sendMessage(Static.getParkourString() + "Join for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
	}

	/**
	 * Create Lobby join Sign
	 * @param sign
	 * @param player
	 */
	public void createLobbyJoinSign(SignChangeEvent sign, Player player){
		if (!Utils.hasSignPermission(player, sign, "Lobby"))
			return;

		sign.setLine(1, "Lobby");

		if (sign.getLine(2).isEmpty()){
			sign.setLine(3, "");
			player.sendMessage(Static.getParkourString() + "Lobby shortcut created!");

		} else {
			if (Parkour.getPlugin().getConfig().contains("Lobby." + sign.getLine(2))) {
				if (Parkour.getPlugin().getConfig().contains("Lobby." + sign.getLine(2) + ".Level")) {
					sign.setLine(3, ChatColor.RED + Parkour.getPlugin().getConfig().getString("Lobby." + sign.getLine(2) + ".Level"));
				}
				player.sendMessage(Static.getParkourString() + "Lobby " + sign.getLine(2) + " shortcut created!");
			} else {
				player.sendMessage(Static.getParkourString() + "That custom lobby does not exist!");
				sign.setLine(2, "");
				sign.setLine(3, "-----");
			}
		}
	}

	public void createEffectSign(SignChangeEvent sign, Player player) {
		if (!Utils.hasSignPermission(player, sign, "Effect"))
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

	public void createLeaderboardsSign(SignChangeEvent sign, Player player) {
	    if (!createStandardCourseSign(sign, player, "Leaderboards", false))
	        return;

	    if (!sign.getLine(3).isEmpty()) {
	        if (!Utils.isNumber(sign.getLine(3)))
	            sign.setLine(3, "");
        }

        player.sendMessage(Static.getParkourString() + "Leaderboards sign for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
    }
}
