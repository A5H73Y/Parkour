package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.command.CommandSender;

public class SetPlayerCommand extends BasicParkourCommand {

	public SetPlayerCommand(Parkour parkour) {
		super(parkour, "setplayer",
				AllowedCommandSender.ANY,
				"setlevel", "setleveladd", "setrank");
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2, 3);
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3)
				&& findPlayer(commandSender, args[2]) != null;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if ("session".equalsIgnoreCase(args[0])) {
			parkour.getPlayerManager().processSetCommand(commandSender, args);
		} else {
			parkour.getPlayerManager().processCommand(commandSender, args[0].replace("set", ""), args[1], args[2]);
		}
	}

	@Override
	public void performConsoleAction(CommandSender commandSender, String[] args) {
		// TODO check this
		parkour.getParkourSessionManager().processCommand(findPlayer(commandSender, args[2]), args[1]);
	}
}
