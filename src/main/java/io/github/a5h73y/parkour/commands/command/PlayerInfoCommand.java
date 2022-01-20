package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerInfoCommand extends BasicParkourCommand {

	public PlayerInfoCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "player", "info");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return commandSender instanceof Player || args.length == 3;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		OfflinePlayer targetPlayer;

		if (commandSender instanceof Player && args.length <= 1) {
			targetPlayer = (OfflinePlayer) commandSender;
		} else {
			targetPlayer = Bukkit.getOfflinePlayer(args[1]);
		}

		parkour.getPlayerManager().displayParkourInfo(commandSender, targetPlayer);
	}
}
