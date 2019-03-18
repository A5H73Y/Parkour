package me.A5H73Y.Parkour.Utilities;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.potion.PotionEffectType;

public class SignMethods {

	/**
	 * Create a standard Parkour sign, no arguments just display the title
	 *
	 * @param sign
	 * @param player
	 * @param title
	 */
	public void createStandardSign(SignChangeEvent sign, Player player, String title) {
		if (!Utils.hasSignPermission(player, sign, title))
			return;

		sign.setLine(1, title);
		sign.setLine(2, "");
		sign.setLine(3, "-----");
		player.sendMessage(Static.getParkourString() + title + " sign created!");
	}

	public void createStandardCourseSign(SignChangeEvent sign, Player player, String title) {
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
	public boolean createStandardCourseSign(SignChangeEvent sign, Player player, String title, boolean message) {
		if (!Utils.hasSignPermission(player, sign, title))
			return false;

		if (!CourseMethods.exist(sign.getLine(2))) {
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			sign.setLine(2, ChatColor.RED + "Unknown Course!");
			sign.setLine(3, "");
			return false;
		}

		sign.setLine(1, title);

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
	public void createJoinCourseSign(SignChangeEvent sign, Player player) {
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
	public void createLobbyJoinSign(SignChangeEvent sign, Player player) {
		if (!Utils.hasSignPermission(player, sign, "Lobby"))
			return;

		sign.setLine(1, "Lobby");

		if (sign.getLine(2).isEmpty()) {
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
			player.sendMessage(Static.getParkourString() + "Heal Effect sign created!");

		} else if (sign.getLine(2).equalsIgnoreCase("gamemode")) {
			sign.setLine(2, "GameMode");
			if (Utils.doesGameModeEnumExist(sign.getLine(3))) {
				sign.setLine(3, sign.getLine(3).toUpperCase());
				player.sendMessage(Static.getParkourString() + sign.getLine(3) + " GameMode Effect sign created!");
			} else {
				sign.getBlock().breakNaturally();
				player.sendMessage(Static.getParkourString() + "GameMode not recognised.");
			}

		} else {
			String effect = sign.getLine(2).toUpperCase().replace("RESISTANCE", "RESIST").replace("RESIST", "RESISTANCE");
			PotionEffectType potionType = PotionEffectType.getByName(effect);

			if (potionType == null) {
				player.sendMessage(Static.getParkourString() + "Unknown Effect!");
				sign.getBlock().breakNaturally();
				return;
			}

			String[] args = sign.getLine(3).split(":");
			if (args.length != 2) {
				sign.getBlock().breakNaturally();
				player.sendMessage(Static.getParkourString() + "Invalid syntax, must follow '(duration):(strength)' example '1000:6'.");
			} else {
				player.sendMessage(Static.getParkourString() + potionType.getName() + " effect sign created, with a strength of " + args[0] + " and a duration of " + args[1]);
			}
		}
	}

	public void createLeaderboardsSign(SignChangeEvent sign, Player player) {
		if (!createStandardCourseSign(sign, player, "Leaderboards", false))
			return;

		if (!sign.getLine(3).isEmpty()) {
			if (!Utils.isPositiveInteger(sign.getLine(3)))
				sign.setLine(3, "");
		}

		player.sendMessage(Static.getParkourString() + "Leaderboards sign for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
	}

	public void createCheckpointSign(SignChangeEvent sign, Player player, String checkpoint) {
		if (!createStandardCourseSign(sign, player, "Checkpoint", false))
			return;

		if (sign.getLine(3).isEmpty() || !Utils.isPositiveInteger(sign.getLine(3))) {
			sign.getBlock().breakNaturally();
			player.sendMessage(Static.getParkourString() + "Please specify checkpoint on bottom line!");
			return;
		}

		player.sendMessage(Static.getParkourString() + "Checkpoint sign for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
	}
}
