package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ParkourListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		//TODO
		boolean paDisabled = true; //pl.getConfig().getBoolean("Other.DisableCommands.OnParkour");
		boolean commandIsPa = event.getMessage().startsWith("/pa");

		if (commandIsPa && Static.containsQuestion(player.getName())) {
			if (event.getMessage().startsWith("/pa yes")) {
				String question[] = Static.getQuestion(player.getName()).split(",");
				int command = Integer.parseInt(question[0]);
				String argument = question[1];
				Static.removeQuestion(player.getName());
				
				Utils.Confirm(command, argument, player);
			} else if (event.getMessage().startsWith("/pa no")) {
				player.sendMessage(Static.getParkourString() + "Question cancelled!");
				Static.removeQuestion(player.getName());
			} else {
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Invalid question answer.");
				player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
			}
			event.setCancelled(true);
			return;
		}

		if (!commandIsPa && PlayerMethods.isPlaying(player.getName())) {
			if (paDisabled) {
				if (!(player.hasPermission("Parkour.Admin") || player.hasPermission("Parkour.*"))) {
					boolean allowed = false;
					for (String word : Parkour.getParkourConfig().getConfig().getStringList("Other.Commands.Whitelist")) {
						if (event.getMessage().startsWith("/" + word)) {
							allowed = true;
							break;
						}
					}
					if (allowed == false) {
						event.setCancelled(true);
						player.sendMessage(Utils.getTranslation("Error.Command"));
					}
				}
			}
		}
	}
}
