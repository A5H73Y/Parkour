package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand extends BasicParkourCommand {

	public PlayerCommand(Parkour parkour) {
		super(parkour, "player",
				AllowedCommandSender.ANY,
				"info");
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
			parkour.getPlayerManager().displayParkourInfo(commandSender,
					args.length <= 1 ? (OfflinePlayer) commandSender : Bukkit.getOfflinePlayer(args[1]));
	}

	@Override
	public void performConsoleAction(CommandSender commandSender, String[] args) {
		parkour.getPlayerManager().displayParkourInfo(commandSender, Bukkit.getOfflinePlayer(args[1]));
	}
}
