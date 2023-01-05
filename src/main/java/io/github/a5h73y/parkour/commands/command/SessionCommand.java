package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPlayerAliasCommand extends BasicParkourCommand {

	public SetPlayerAliasCommand(Parkour parkour) {
		super(parkour, "setlevel",
				AllowedCommandSender.ANY,
				"setleveladd", "setrank");
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getPlayerManager().processCommand(commandSender, args[0].toLowerCase()
				.replace("set", ""), args[1], args[2]);
	}


}
