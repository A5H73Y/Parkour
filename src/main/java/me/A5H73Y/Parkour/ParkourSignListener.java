package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.SignMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ParkourSignListener implements Listener {

	private final SignMethods sm = new SignMethods();

	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[parkour]") || event.getLine(0).equalsIgnoreCase("[pa]")) {
			Player player = event.getPlayer();

			event.setLine(0, ChatColor.BLACK + "[" + ChatColor.AQUA + "Parkour" + ChatColor.BLACK + "]");

			if (event.getLine(1).equalsIgnoreCase("join") || event.getLine(1).equalsIgnoreCase("j")) {
				sm.createJoinCourseSign(event, player);

			} else if (event.getLine(1).equalsIgnoreCase("finish") || event.getLine(1).equalsIgnoreCase("f")) {
				sm.createStandardCourseSign(event, player, "Finish");

			} else if (event.getLine(1).equalsIgnoreCase("lobby") || event.getLine(1).equalsIgnoreCase("l")) {
				sm.createLobbyJoinSign(event, player);

			} else if (event.getLine(1).equalsIgnoreCase("leave") || event.getLine(1).equalsIgnoreCase("le")) {
				sm.createStandardSign(event, player, "Leave");

			} else if (event.getLine(1).equalsIgnoreCase("effect") || event.getLine(1).equalsIgnoreCase("e")) {
				sm.createEffectSign(event, player, "Effect");

			} else if (event.getLine(1).equalsIgnoreCase("stats") || event.getLine(1).equalsIgnoreCase("s")) {
				sm.createStandardCourseSign(event, player, "Stats");

			} else if (event.getLine(1).equalsIgnoreCase("leaderboards") || event.getLine(1).equalsIgnoreCase("lb")) {
				sm.createStandardCourseSign(event, player, "Leaderboards");

			} else if (event.getLine(1).equalsIgnoreCase("setpoint") || event.getLine(1).equalsIgnoreCase("sp")) {
				//event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(Static.getParkourString() + "SetPoint signs are no longer supported. Please convert to using the new checkpoint system, to learn more enter '/pa help checkpoint'");

			} else {
				player.sendMessage(Utils.getTranslation("Error.UnknownSignCommand"));
				player.sendMessage(Utils.getTranslation("Help.SignCommands"));
				event.setLine(1, ChatColor.RED + "Unknown cmd");
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
	}

	@EventHandler 
	public void onSignBreak(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) 
			return;

		if ((event.getClickedBlock().getType() != Material.SIGN) && (event.getClickedBlock().getType() != Material.SIGN_POST) && (event.getClickedBlock().getType() != Material.WALL_SIGN)) 
			return;

		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Parkour.SignProtection"))
			return;

		String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();

		if (!lines[0].contains(ChatColor.AQUA + "Parkour")) 
			return;

		if (!Utils.hasPermission(event.getPlayer(), "Parkour.Admin")){
			event.getPlayer().sendMessage(Utils.getTranslation("Error.SignProtected"));
			event.setCancelled(true);
			return;
		}

		event.getClickedBlock().breakNaturally();
		event.getPlayer().sendMessage(Static.getParkourString() + "Sign Removed!");
	}

	@EventHandler
	public void onSignInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)  
			return;

		if ((event.getClickedBlock().getType() != Material.SIGN) && (event.getClickedBlock().getType() != Material.SIGN_POST) && (event.getClickedBlock().getType() != Material.WALL_SIGN)) 
			return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		String[] lines = sign.getLines();

		if (!lines[0].contains(ChatColor.AQUA + "Parkour")){
			if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
				return;

			if (!Parkour.getParkourConfig().getConfig().getBoolean("OnCourse.EnforceParkourSigns"))
				return;

			event.getPlayer().sendMessage(Utils.getTranslation("Error.Sign"));
			return;
		}

		if (Parkour.getSettings().isSignPermission() && !Utils.hasPermission(event.getPlayer(), "Parkour.Basic", "Signs"))
			return;

		if (lines[1].equalsIgnoreCase("join")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", lines[2]));
				return;
			}

			CourseMethods.joinCourse(event.getPlayer(), lines[2]);

		} else if (lines[1].equalsIgnoreCase("setpoint")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", lines[2]));
				return;

			} else if (!PlayerMethods.isPlaying(event.getPlayer().getName())){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NotOnCourse"));
				return;	
			}

			ParkourSession session = PlayerMethods.getParkourSession(event.getPlayer().getName());

			if (session.getCheckpoint() == session.getCourse().getCheckpoints())
				return;

			if (session.getCheckpoint() >= Integer.parseInt(lines[3]))
				return;

			if (session.getCheckpoint() + 1 < Integer.parseInt(lines[3]))
				return;

			PlayerMethods.increaseCheckpoint(session, event.getPlayer());

		} else if (lines[1].equalsIgnoreCase("lobby")) {
			if (lines[2].isEmpty()) {
				CourseMethods.joinLobby(new String[0], event.getPlayer());
			} else {
				String[] args = {"", lines[2]};
				CourseMethods.joinLobby(args, event.getPlayer());
			}

		} else if (lines[1].equalsIgnoreCase("stats")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}

			CourseMethods.displayCourseInfo(lines[2], event.getPlayer());

		} else if (lines[1].equalsIgnoreCase("leave")) {
			PlayerMethods.playerLeave(event.getPlayer());

		} else if (lines[1].equalsIgnoreCase("finish")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.Unknown"));

			} else if (!PlayerMethods.isPlaying(event.getPlayer().getName())){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NotOnCourse"));

			} else if (!PlayerMethods.getParkourSession(event.getPlayer().getName()).getCourse().getName().equals(lines[2].toLowerCase())) {
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NotOnCourse"));

			} else {
				PlayerMethods.playerFinish(event.getPlayer());
			}

		} else if (lines[1].equalsIgnoreCase("effect")) {
			PlayerMethods.applyEffect(lines, event.getPlayer());

		} else if (lines[1].equalsIgnoreCase("leaderboards")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.Unknown"));

			} else if (!Utils.delayPlayer(event.getPlayer(), 4, true)) {

			} else {
				Utils.displayLeaderboard(event.getPlayer(), 
						DatabaseMethods.getTopCourseResults(lines[2]));
			}

		} else {
			event.getPlayer().sendMessage(Utils.getTranslation("Error.UnknownSignCommand"));
		}
		event.setCancelled(true);
	}

}
