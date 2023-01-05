package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.command.CommandSender;

public class SessionCommand extends BasicParkourCommand {

	public SessionCommand(Parkour parkour) {
		super(parkour, "session",
				AllowedCommandSender.ANY,
				"manualcheckpoint", "hideall", "leave", "restart");
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if ("session".equalsIgnoreCase(args[0])) {
			parkour.getParkourSessionManager().processCommand(commandSender, args[1]);
		} else {
			parkour.getPlayerManager().processCommand(commandSender, args[0].toLowerCase()
					.replace("set", ""), args[1], args[2]);
		}
	}
}
