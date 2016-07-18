package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.StoreGUI;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.SignMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ParkourSignListener implements Listener {

	private final SignMethods sm = new SignMethods();

	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[parkour]") || event.getLine(0).equalsIgnoreCase("[pa]")) {
			Player player = event.getPlayer();

			event.setLine(0, ChatColor.BLACK + "[" + ChatColor.AQUA + "Parkour" + ChatColor.BLACK + "]");

			if (event.getLine(1).equalsIgnoreCase("join") || event.getLine(1).equalsIgnoreCase("j")) {
				sm.joinCourse(event, player);

			} else if (event.getLine(1).equalsIgnoreCase("finish") || event.getLine(1).equalsIgnoreCase("f")) {
				sm.createStandardCourseSign(event, player, "Finish");

			} else if (event.getLine(1).equalsIgnoreCase("lobby") || event.getLine(1).equalsIgnoreCase("l")) {
				sm.joinLobby(event, player);

			} else if (event.getLine(1).equalsIgnoreCase("store") || event.getLine(1).equalsIgnoreCase("s")) {
				sm.createStandardSign(event, player, "Store");

			} else if (event.getLine(1).equalsIgnoreCase("leave")) {
				sm.createStandardSign(event, player, "Leave");

			} else if (event.getLine(1).equalsIgnoreCase("joinall") || event.getLine(1).equalsIgnoreCase("ja")) {
				sm.createStandardSign(event, player, "JoinAll");

			} else if (event.getLine(1).equalsIgnoreCase("effect") || event.getLine(1).equalsIgnoreCase("e")) {
				sm.createEffectSign(event, player, "Effect");

			} else if (event.getLine(1).equalsIgnoreCase("stats") || event.getLine(1).equalsIgnoreCase("s")) {
				sm.createStandardCourseSign(event, player, "Stats");
				
			} else if (event.getLine(1).equalsIgnoreCase("store")) {
				sm.createStandardCourseSign(event, player, "Store");
			
			} else {
				player.sendMessage(Utils.getTranslation("Error.UnknownSignCommand"));
				event.setLine(1, ChatColor.RED + "Unknown cmd");
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
	}

	@EventHandler 
	public void onSignBreak(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if ((event.getClickedBlock().getType() != Material.SIGN) && (event.getClickedBlock().getType() != Material.SIGN_POST) && (event.getClickedBlock().getType() != Material.WALL_SIGN)) return;

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
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)  return;
		if ((event.getClickedBlock().getType() != Material.SIGN) && (event.getClickedBlock().getType() != Material.SIGN_POST) && (event.getClickedBlock().getType() != Material.WALL_SIGN)) return;

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

		if (lines[1].equalsIgnoreCase("Join")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", lines[2]));
				return;
			}

			CourseMethods.joinCourse(event.getPlayer(), lines[2]);

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
				
			event.getPlayer().sendMessage("");
			//TODO Not sure what to do for this :/

		} else if (lines[1].equalsIgnoreCase("leave")) {
			PlayerMethods.playerLeave(event.getPlayer());

		} else if (lines[1].equalsIgnoreCase("finish")) {
			if (lines[2].isEmpty() || !CourseMethods.exist(lines[2])){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.Unknown"));

			} else if (!PlayerMethods.isPlaying(event.getPlayer().getName())){
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NotOnCourse"));

			} else if (!PlayerMethods.getPlayerInfo(event.getPlayer().getName()).getCourse().getName().equals(lines[2])) {
				event.getPlayer().sendMessage(Utils.getTranslation("Error.NotOnCourse"));

			} else {
				PlayerMethods.playerFinish(event.getPlayer());
			}

		} else if (lines[1].equalsIgnoreCase("effect")) {
			applyEffect(lines, event.getPlayer());
			
		} else if (lines[1].equalsIgnoreCase("store")) {
			StoreGUI.openInventory(event.getPlayer());

		} else {
			event.getPlayer().sendMessage(Utils.getTranslation("Error.UnknownSignCommand"));
		}
		event.setCancelled(true);
	}


	private void applyEffect(String[] lines, Player player){
		if (lines[2].equalsIgnoreCase("heal")) {
			Damageable damag = player;
			damag.setHealth(damag.getMaxHealth());
			player.sendMessage(Static.getParkourString() + "Healed!");

		} else if (lines[2].equalsIgnoreCase("jump")) {
			if (Utils.isNumber(lines[3])) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, Integer.parseInt(lines[3])));
				player.sendMessage(Static.getParkourString() + "Jump Effect Applied!");
			} else {
				player.sendMessage(Static.getParkourString() + "Invalid Number");
			}
		} else if (lines[2].equalsIgnoreCase("speed")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 6));
			player.sendMessage(Static.getParkourString() + "Speed Effect Applied!");

		} else if (lines[2].equalsIgnoreCase("fire")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 500, 6));
			player.sendMessage(Static.getParkourString() + "Fire Resistance Applied!");

		} else if (lines[2].equalsIgnoreCase("pain")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 10));
			player.sendMessage(Static.getParkourString() + "Pain Resistance Applied!");

		} else if (lines[2].equalsIgnoreCase("gamemode")) {
			if (lines[3].equalsIgnoreCase("creative")) {
				if (!(player.getGameMode().equals(GameMode.CREATIVE))) {
					player.setGameMode(GameMode.CREATIVE);
					player.sendMessage(Static.getParkourString() + "GameMode set to Creative!");
				}
			} else {
				if (!(player.getGameMode().equals(GameMode.SURVIVAL))) {
					player.setGameMode(GameMode.SURVIVAL);
					player.sendMessage(Static.getParkourString() + "GameMode set to Survival!");
				}
			}
		} else {
			player.sendMessage(Static.getParkourString() + "Unknown Effect!");
		}
	}

}
